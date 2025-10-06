package org.GoogleScholar.model;

public class Article {
    private final String authorQuery;
    private final String articleId;
    private final String title;
    private final String authors;
    private final String publicationDate;
    private final String abstractText;
    private final String link;
    private final String keywords;
    private final Integer citedBy;

    public Article(String authorQuery, String articleId, String title, String authors,
                   String publicationDate, String abstractText, String link,
                   String keywords, Integer citedBy) {
        this.authorQuery = authorQuery;
        this.articleId = articleId;
        this.title = title;
        this.authors = authors;
        this.publicationDate = publicationDate;
        this.abstractText = abstractText;
        this.link = link;
        this.keywords = keywords;
        this.citedBy = citedBy;
    }

    public String getAuthorQuery()     { return authorQuery; }
    public String getArticleId()       { return articleId; }
    public String getTitle()           { return title; }
    public String getAuthors()         { return authors; }
    public String getPublicationDate() { return publicationDate; }
    public String getAbstractText()    { return abstractText; }
    public String getLink()            { return link; }
    public String getKeywords()        { return keywords; }
    public Integer getCitedBy()        { return citedBy; }
}
