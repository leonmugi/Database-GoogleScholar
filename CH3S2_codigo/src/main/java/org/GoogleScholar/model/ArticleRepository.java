package org.GoogleScholar.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleRepository {

    public void saveAll(List<Article> articles) throws SQLException {
        if (articles == null || articles.isEmpty()) return;
        String sql = "INSERT IGNORE INTO articles " +
                "(author_query, article_id, title, authors, publication_date, abstract, link, keywords, cited_by) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (Article a : articles) {
                ps.setString(1, a.getAuthorQuery());
                ps.setString(2, a.getArticleId());
                ps.setString(3, a.getTitle());
                ps.setString(4, a.getAuthors());
                ps.setString(5, a.getPublicationDate());
                ps.setString(6, a.getAbstractText());
                ps.setString(7, a.getLink());
                ps.setString(8, a.getKeywords());
                if (a.getCitedBy() == null) ps.setNull(9, Types.INTEGER);
                else ps.setInt(9, a.getCitedBy());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public List<Article> findAll() throws SQLException {
        String sql = """
            SELECT author_query, article_id, title, authors, publication_date, abstract, link, keywords, cited_by
            FROM articles
            ORDER BY id DESC
            LIMIT 100
        """;
        List<Article> out = new ArrayList<>();
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Article(
                        rs.getString("author_query"),
                        rs.getString("article_id"),
                        rs.getString("title"),
                        rs.getString("authors"),
                        rs.getString("publication_date"),
                        rs.getString("abstract"),
                        rs.getString("link"),
                        rs.getString("keywords"),
                        rs.getObject("cited_by") == null ? null : rs.getInt("cited_by")
                ));
            }
        }
        return out;
    }
}
