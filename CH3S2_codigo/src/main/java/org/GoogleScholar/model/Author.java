// src/main/java/org/GoogleScholar/model/Author.java
package org.GoogleScholar.model;

/**
 * Plain Model object representing what we store in MySQL:
 * - name (always)
 * - authorId (Scholar's "user" id) if available
 * - citations (total) if available
 * - articleTitle (the article whose authors we extracted)
 * - profileUrl (direct link to the Scholar profile if we resolved authorId)
 */
public class Author {
    private final String name;
    private final String authorId;
    private final Integer citations;
    private final String articleTitle;
    private final String profileUrl;

    public Author(String name, String authorId, Integer citations, String articleTitle, String profileUrl) {
        this.name = name;
        this.authorId = authorId;
        this.citations = citations;
        this.articleTitle = articleTitle;
        this.profileUrl = profileUrl;
    }

    public String getName() { return name; }
    public String getAuthorId() { return authorId; }
    public Integer getCitations() { return citations; }
    public String getArticleTitle() { return articleTitle; }
    public String getProfileUrl() { return profileUrl; }
}
