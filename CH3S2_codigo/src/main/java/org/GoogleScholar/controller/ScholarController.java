package org.GoogleScholar.controller;

import com.google.gson.*;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.GoogleScholar.model.Author;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScholarController {

    public static class ArticleFirst {
        public final String title;
        public final List<AuthorRef> authors;
        public ArticleFirst(String title, List<AuthorRef> authors) { this.title = title; this.authors = authors; }
    }
    public static class AuthorRef {
        public final String name;
        public final String authorId;     // puede venir null
        public final String profileLink;  // puede venir null
        public AuthorRef(String name, String authorId, String profileLink) {
            this.name = name; this.authorId = authorId; this.profileLink = profileLink;
        }
    }

    private final String apiKey;

    public ScholarController() {
        this.apiKey = System.getenv("SERPAPI_KEY");
        if (apiKey == null || apiKey.isBlank()) throw new IllegalStateException("Falta SERPAPI_KEY.");
    }

    // 1) Busca resultados y escoge el primero que SÍ tiene autores
    public ArticleFirst fetchFirstArticleAndAuthors(String query) throws Exception {
        String url = "https://serpapi.com/search.json"
                + "?engine=google_scholar"
                + "&q=" + URLEncoder.encode(query, StandardCharsets.UTF_8)
                + "&hl=en&num=20&start=0"
                + "&api_key=" + apiKey;

        String json = httpGet(url);
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonArray results = root.has("organic_results") ? root.getAsJsonArray("organic_results") : new JsonArray();
        if (results.size() == 0) throw new RuntimeException("Sin resultados para: " + query);

        for (JsonElement el : results) {
            if (!el.isJsonObject()) continue;
            JsonObject item = el.getAsJsonObject();
            List<AuthorRef> authors = extractAuthors(item);
            if (!authors.isEmpty()) {
                String articleTitle = getString(item, "title", "(sin título)");
                return new ArticleFirst(articleTitle, authors);
            }
        }
        String firstTitle = getString(results.get(0).getAsJsonObject(), "title", "(sin título)");
        return new ArticleFirst(firstTitle, new ArrayList<>());
    }

    // 2) Enriquecer autor: usar author_id si existe; si no, sacarlo del link
    public Author enrichAuthor(AuthorRef ref, String articleTitle) throws Exception {
        String aId = ref.authorId;
        if ((aId == null || aId.isBlank()) && ref.profileLink != null) {
            aId = extractAuthorIdFromLink(ref.profileLink); // intenta user=...
        }

        if (aId != null && !aId.isBlank()) {
            // tenemos ID → pedimos detalle y sacamos 'citations'
            AuthorDetails det = fetchAuthorDetails(aId);
            String profileUrl = "https://scholar.google.com/citations?user=" + aId;
            return new Author(det.name != null ? det.name : ref.name, aId, det.citations, articleTitle, profileUrl);
        }

        // Sin ID: guardamos lo que tenemos
        return new Author(ref.name, null, null, articleTitle, null);
    }

    // ---------- Helpers ----------

    private static class AuthorDetails {
        String name; Integer citations; Integer hIndex; Integer i10;
    }

    /** Llama al endpoint de autor y extrae name + citations (y otros por si los quieres usar después). */
    private AuthorDetails fetchAuthorDetails(String authorId) throws Exception {
        String url = "https://serpapi.com/search.json"
                + "?engine=google_scholar_author"
                + "&author_id=" + URLEncoder.encode(authorId, StandardCharsets.UTF_8)
                + "&hl=en"
                + "&api_key=" + apiKey;

        String json = httpGet(url);
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        AuthorDetails d = new AuthorDetails();

        if (root.has("author") && root.get("author").isJsonObject()) {
            JsonObject a = root.getAsJsonObject("author");
            d.name = getString(a, "name", null);
        }

        if (root.has("cited_by") && root.get("cited_by").isJsonObject()) {
            JsonObject cb = root.getAsJsonObject("cited_by");

            // A) Camino directo (si existe)
            if (cb.has("citations") && cb.get("citations").isJsonObject()) {
                JsonObject cits = cb.getAsJsonObject("citations");
                if (cits.has("all") && cits.get("all").isJsonPrimitive()) {
                    try { d.citations = cits.get("all").getAsInt(); } catch (Exception ignored) {}
                }
            }
            if (cb.has("h_index") && cb.get("h_index").isJsonObject()) {
                JsonObject hi = cb.getAsJsonObject("h_index");
                if (hi.has("all") && hi.get("all").isJsonPrimitive()) {
                    try { d.hIndex = hi.get("all").getAsInt(); } catch (Exception ignored) {}
                }
            }
            if (cb.has("i10_index") && cb.get("i10_index").isJsonObject()) {
                JsonObject i10 = cb.getAsJsonObject("i10_index");
                if (i10.has("all") && i10.get("all").isJsonPrimitive()) {
                    try { d.i10 = i10.get("all").getAsInt(); } catch (Exception ignored) {}
                }
            }

            // B) Camino alterno (algunos payloads traen 'table')
            if (d.citations == null && cb.has("table") && cb.get("table").isJsonArray()) {
                JsonArray table = cb.getAsJsonArray("table");
                for (JsonElement rowEl : table) {
                    if (!rowEl.isJsonObject()) continue;
                    JsonObject row = rowEl.getAsJsonObject();
                    if (row.has("citations")) {
                        JsonObject cits = row.getAsJsonObject("citations");
                        if (cits.has("all")) { try { d.citations = cits.get("all").getAsInt(); } catch (Exception ignored) {} }
                    }
                    if (row.has("h_index")) {
                        JsonObject hi = row.getAsJsonObject("h_index");
                        if (hi.has("all")) { try { d.hIndex = hi.get("all").getAsInt(); } catch (Exception ignored) {} }
                    }
                    if (row.has("i10_index")) {
                        JsonObject i10 = row.getAsJsonObject("i10_index");
                        if (i10.has("all")) { try { d.i10 = i10.get("all").getAsInt(); } catch (Exception ignored) {} }
                    }
                }
            }
        }

        return d;
    }

    /** Extrae candidatos a autores desde distintas ubicaciones. */
    private List<AuthorRef> extractAuthors(JsonObject item) {
        List<AuthorRef> out = new ArrayList<>();

        // A) Campo 'authors'
        if (item.has("authors") && item.get("authors").isJsonArray()) {
            JsonArray arr = item.getAsJsonArray("authors");
            for (JsonElement ael : arr) {
                if (!ael.isJsonObject()) continue;
                JsonObject a = ael.getAsJsonObject();
                String name = getString(a, "name", null);
                String aid  = getString(a, "author_id", null);
                String link = getString(a, "link", null);
                if (name != null) out.add(new AuthorRef(name, aid, link));
            }
            if (!out.isEmpty()) return out;
        }

        // B) publication_info.authors
        if (item.has("publication_info") && item.get("publication_info").isJsonObject()) {
            JsonObject pub = item.getAsJsonObject("publication_info");
            if (pub.has("authors") && pub.get("authors").isJsonArray()) {
                JsonArray arr = pub.getAsJsonArray("authors");
                for (JsonElement ael : arr) {
                    if (!ael.isJsonObject()) continue;
                    JsonObject a = ael.getAsJsonObject();
                    String name = getString(a, "name", null);
                    String aid  = getString(a, "author_id", null);
                    String link = getString(a, "link", null);
                    if (name != null) out.add(new AuthorRef(name, aid, link));
                }
                if (!out.isEmpty()) return out;
            }

            // C) Heurística desde summary
            String summary = getString(pub, "summary", null);
            if (summary != null) {
                for (String n : parseAuthorsFromSummary(summary)) out.add(new AuthorRef(n, null, null));
            }
        }

        return out;
    }

    /** Parseo simple de "by A, B and C - ..." */
    private List<String> parseAuthorsFromSummary(String summary) {
        List<String> names = new ArrayList<>();
        String s = summary;
        if (s.toLowerCase(Locale.ROOT).startsWith("by ")) s = s.substring(3);
        int cut = s.indexOf(" - ");
        if (cut > 0) s = s.substring(0, cut);
        s = s.replace(" and ", ",");
        for (String part : s.split(",")) {
            String n = part.trim();
            if (n.length() >= 2 && n.length() <= 120) names.add(n);
        }
        return names;
    }

    /** Extrae el parámetro user (author_id) de un link de perfil. */
    private String extractAuthorIdFromLink(String link) {
        try {
            URI uri = new URI(link);
            String q = uri.getQuery();
            if (q == null) return null;
            for (String p : q.split("&")) {
                int i = p.indexOf('=');
                if (i <= 0) continue;
                String k = URLDecoder.decode(p.substring(0, i), StandardCharsets.UTF_8);
                String v = URLDecoder.decode(p.substring(i + 1), StandardCharsets.UTF_8);
                if ("user".equals(k)) return v;
            }
        } catch (Exception ignored) {}
        return null;
    }

    // --- HTTP / JSON ---
    private String httpGet(String url) throws Exception {
        try (CloseableHttpClient http = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            try (ClassicHttpResponse resp = http.executeOpen(null, get, null)) {
                int status = resp.getCode();
                String body = resp.getEntity() != null ? EntityUtils.toString(resp.getEntity()) : "";
                if (status != 200) throw new RuntimeException("HTTP " + status + ": " + body);
                return body;
            }
        }
    }
    private static String getString(JsonObject o, String key, String def) {
        return (o.has(key) && o.get(key).isJsonPrimitive()) ? o.get(key).getAsString() : def;
    }
}
