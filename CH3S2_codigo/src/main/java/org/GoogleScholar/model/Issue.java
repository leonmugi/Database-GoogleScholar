package org.GoogleScholar.model;

public class Issue {
    private final String authorQuery;
    private final String articleTitle; // puede ser null en errores HTTP
    private final String issue;        // descripción
    private final Integer httpStatus;  // opcional
    private final String rawJson;      // opcional

    public Issue(String authorQuery, String articleTitle, String issue, Integer httpStatus, String rawJson) {
        this.authorQuery = authorQuery;
        this.articleTitle = articleTitle;
        this.issue = issue;
        this.httpStatus = httpStatus;
        this.rawJson = rawJson;
    }

    public String getAuthorQuery() { return authorQuery; }
    public String getArticleTitle() { return articleTitle; }
    public String getIssue() { return issue; }
    public Integer getHttpStatus() { return httpStatus; }
    public String getRawJson() { return rawJson; }
}
