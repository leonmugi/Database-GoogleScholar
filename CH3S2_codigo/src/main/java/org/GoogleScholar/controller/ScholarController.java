package org.GoogleScholar.controller;

import com.google.gson.*;
import org.GoogleScholar.model.Article;
import org.GoogleScholar.model.Issue;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Sprint 3 controller: fetch top articles per author name with validation and logging. */
public class ScholarController {

    private final String apiKey;
    private final List<Issue> issues = new ArrayList<>();

    // Placeholders (en español)
    private static final String ID_MISSING   = "No se encontró id";
    private static final String DATE_MISSING = "No se encontró fecha";
    private static final String ABS_MISSING  = "No se encontró abstracto";
    private static final String KW_MISSING   = "No se encontraron keywords";

    public ScholarController() {
        this.apiKey = System.getenv("SERPAPI_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Missing SERPAPI_KEY environment variable.");
        }
    }

    /** Devuelve y “vacía” la lista de incidencias acumuladas. */
    public List<Issue> drainIssues() {
        List<Issue> copy = new ArrayList<>(issues);
        issues.clear();
        return copy;
    }

    /** Obtiene hasta 'limit' artículos del autor (saltando items sin autores) con validación. */
    public List<Article> fetchTopArticlesByAuthorName(String authorName, int limit) throws Exception {
        List<Article> out = new ArrayList<>();
        final int pageSize = 10;
        int start = 0;
        int safetyPages = 0;

        while (out.size() < limit && safetyPages < 3) {
            String q = "\"" + authorName + "\"";
            String url = "https://serpapi.com/search.json"
                    + "?engine=google_scholar"
                    + "&q=" + URLEncoder.encode(q, StandardCharsets.UTF_8)
                    + "&hl=en"
                    + "&num=" + pageSize
                    + "&start=" + start
                    + "&api_key=" + apiKey;

            String json;
            try {
                json = httpGetWithRetry(url, 3, 600);
            } catch (HttpError e) {
                // Registra incidente de red/HTTP
                issues.add(new Issue(authorName, null,
                        "HTTP error " + e.getStatusCode(), e.getStatusCode(), null));
                System.err.println("HTTP " + e.getStatusCode() + " for " + url);
                break; // aborta esta página
            }

            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray results = root.has("organic_results") ? root.getAsJsonArray("organic_results") : new JsonArray();

            for (JsonElement el : results) {
                if (out.size() >= limit) break;
                JsonObject item = el.getAsJsonObject();

                // Autores: si no hay, lo saltamos (y registramos)
                String authorsCsv = extractAuthorsCsv(item);
                if (authorsCsv == null || authorsCsv.isBlank()) {
                    issues.add(new Issue(authorName, getString(item, "title", "(sin título)"),
                            "Artículo sin autores", null, trimJson(item.toString())));
                    continue;
                }

                String title          = getString(item, "title", "(sin título)");
                String link           = getString(item, "link", null);
                String articleIdRaw   = getString(item, "result_id", null);
                String publicationRaw = extractYear(item);
                String abstractRaw    = getString(item, "snippet", null);
                Integer citedByRaw    = extractCitedBy(item);
                String keywordsRaw    = computeKeywords(abstractRaw != null ? abstractRaw : title, authorsCsv);

                // Placeholders si faltan
                String articleId    = articleIdRaw    == null ? ID_MISSING   : articleIdRaw;
                String publication  = publicationRaw  == null ? DATE_MISSING : publicationRaw;
                String abstractText = abstractRaw     == null ? ABS_MISSING  : abstractRaw;
                String keywords     = keywordsRaw     == null ? KW_MISSING   : keywordsRaw;
                Integer citedBy     = citedByRaw == null ? -1 : citedByRaw;

                // Incidencias por campo faltante
                if (articleIdRaw == null)    issues.add(new Issue(authorName, title, "Falta article_id", null, null));
                if (publicationRaw == null)  issues.add(new Issue(authorName, title, "Falta publication_date", null, null));
                if (abstractRaw == null)     issues.add(new Issue(authorName, title, "Falta abstract", null, null));
                if (keywordsRaw == null)     issues.add(new Issue(authorName, title, "Falta keywords (computadas)", null, null));
                if (citedByRaw == null)      issues.add(new Issue(authorName, title, "Faltan citas", null, null));

                out.add(new Article(
                        authorName, articleId, title, authorsCsv, publication,
                        abstractText, link, keywords, citedBy
                ));
            }

            start += pageSize;
            safetyPages++;
        }

        return out;
    }

    // --------------------- HTTP helper con reintentos ---------------------

    /** Excepción para envolver estado HTTP. */
    private static class HttpError extends Exception {
        private final int statusCode;
        HttpError(int code, String msg) { super(msg); this.statusCode = code; }
        int getStatusCode() { return statusCode; }
    }

    /** GET con validación de status y reintento simple (backoff ms). */
    private String httpGetWithRetry(String url, int attempts, long backoffMs) throws HttpError {
        int tryNo = 0;
        while (true) {
            tryNo++;
            try (CloseableHttpClient http = HttpClients.createDefault()) {
                HttpGet get = new HttpGet(url);
                try (CloseableHttpResponse resp = http.execute(get)) {
                    int status = resp.getCode();
                    String body = resp.getEntity() != null ? EntityUtils.toString(resp.getEntity()) : "";
                    if (status != 200) {
                        if (tryNo < attempts) {
                            System.err.println("HTTP " + status + " → retry " + tryNo + "/" + attempts);
                            sleep(backoffMs * tryNo);
                            continue;
                        }
                        throw new HttpError(status, body);
                    }
                    // Log “verde”
                    System.out.println("HTTP 200 ✓");
                    return body;
                }
            } catch (HttpError e) {
                throw e;
            } catch (Exception ex) {
                if (tryNo < attempts) {
                    System.err.println("Network error → retry " + tryNo + "/" + attempts + " :: " + ex.getMessage());
                    sleep(backoffMs * tryNo);
                    continue;
                }
                throw new HttpError(-1, ex.getMessage());
            }
        }
    }

    private void sleep(long ms) { try { Thread.sleep(ms); } catch (InterruptedException ignored) {} }

    // --------------------- JSON helpers ---------------------

    private static String getString(JsonObject o, String key, String def) {
        return (o.has(key) && o.get(key).isJsonPrimitive()) ? o.get(key).getAsString() : def;
    }

    private String extractAuthorsCsv(JsonObject item) {
        List<String> names = new ArrayList<>();

        if (item.has("authors") && item.get("authors").isJsonArray()) {
            for (JsonElement ael : item.getAsJsonArray("authors")) {
                if (ael.isJsonObject()) {
                    String name = getString(ael.getAsJsonObject(), "name", null);
                    if (name != null && !name.isBlank()) names.add(name);
                }
            }
        }

        if (names.isEmpty() && item.has("publication_info") && item.get("publication_info").isJsonObject()) {
            JsonObject pub = item.getAsJsonObject("publication_info");
            if (pub.has("authors") && pub.get("authors").isJsonArray()) {
                for (JsonElement ael : pub.getAsJsonArray("authors")) {
                    if (ael.isJsonObject()) {
                        String name = getString(ael.getAsJsonObject(), "name", null);
                        if (name != null && !name.isBlank()) names.add(name);
                    }
                }
            }
        }

        return names.isEmpty() ? null : String.join(", ", names);
    }

    private String extractYear(JsonObject item) {
        if (item.has("publication_info") && item.get("publication_info").isJsonObject()) {
            String summary = getString(item.getAsJsonObject("publication_info"), "summary", null);
            if (summary != null) {
                var m = java.util.regex.Pattern.compile("(19|20)\\d{2}").matcher(summary);
                if (m.find()) return m.group();
            }
        }
        return null;
    }

    private Integer extractCitedBy(JsonObject item) {
        try {
            if (item.has("cited_by") && item.get("cited_by").isJsonObject()) {
                JsonObject cb = item.getAsJsonObject("cited_by");
                if (cb.has("value")) return cb.get("value").getAsInt();
            } else if (item.has("inline_links") && item.get("inline_links").isJsonObject()) {
                JsonObject il = item.getAsJsonObject("inline_links");
                if (il.has("cited_by") && il.get("cited_by").isJsonObject()) {
                    JsonObject cb = il.getAsJsonObject("cited_by");
                    if (cb.has("total")) return cb.get("total").getAsInt();
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    // --- keywords: top 3 palabras frecuentes (sin stopwords y sin apellidos de autores) ---
    private static final Set<String> STOP = Set.of(
            "the","and","for","with","that","this","from","have","has","are","was","were","will",
            "into","over","under","between","about","after","before","until","while","more","most",
            "can","may","might","should","could","would","than","such","using","used","use","based",
            "on","in","at","by","to","of","a","an","as","is","it","be","we","our","their","its",
            "not","no","yes","new","study","results","paper","review","article","case","cases"
    );

    private String computeKeywords(String text, String authorsCsv) {
        if (text == null || text.isBlank()) return null;

        Set<String> ban = new HashSet<>(STOP);
        if (authorsCsv != null) {
            for (String part : authorsCsv.split("[, ]+")) {
                if (part.length() >= 3) ban.add(part.toLowerCase());
            }
        }

        Map<String,Integer> freq = new HashMap<>();
        for (String raw : text.toLowerCase().replaceAll("[^a-z0-9 ]", " ").split("\\s+")) {
            if (raw.length() < 4) continue;
            if (ban.contains(raw)) continue;
            if (raw.matches("\\d+")) continue;
            freq.merge(raw, 1, Integer::sum);
        }

        return freq.entrySet().stream()
                .sorted((a,b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .reduce((a,b) -> a + ", " + b)
                .orElse(null);
    }

    private String trimJson(String j) {
        if (j == null) return null;
        return j.length() > 800 ? j.substring(0, 800) + "…" : j;
    }
}