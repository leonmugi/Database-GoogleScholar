package org.GoogleScholar.model;

public class Author {
    private final String name;
    private final String authorId;     // user=... de Scholar
    private final Integer citations;   // total de citas (all)
    private final String articleTitle;
    private final String profileUrl;   // link al perfil (si lo tenemos)

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
