// src/main/java/org/GoogleScholar/model/AuthorRepository.java
package org.GoogleScholar.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository: isolates SQL details.
 * - saveAll(): batch insert with INSERT IGNORE (unique index prevents duplicates).
 * - findAll(): read up to 500 recent rows for the Swing table.
 */
public class AuthorRepository {

    public void saveAll(List<Author> authors) throws SQLException {
        String sql = "INSERT IGNORE INTO authors " +
                "(author_name, author_id, citations, article_title, profile_url) " +
                "VALUES (?,?,?,?,?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (Author a : authors) {
                ps.setString(1, a.getName());
                ps.setString(2, a.getAuthorId());
                if (a.getCitations() == null) ps.setNull(3, Types.INTEGER);
                else ps.setInt(3, a.getCitations());
                ps.setString(4, a.getArticleTitle());
                ps.setString(5, a.getProfileUrl());
                ps.addBatch();
            }
            ps.executeBatch(); // single roundtrip to DB
        }
    }

    /** Pull a small snapshot to display in the Swing table (sorted by newest id). */
    public List<Author> findAll() throws SQLException {
        String sql = """
            SELECT author_name, author_id, citations, article_title, profile_url
            FROM authors
            ORDER BY id DESC
            LIMIT 500
        """;
        List<Author> list = new ArrayList<>();
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Author(
                        rs.getString("author_name"),
                        rs.getString("author_id"),
                        rs.getObject("citations") == null ? null : rs.getInt("citations"),
                        rs.getString("article_title"),
                        rs.getString("profile_url")
                ));
            }
        }
        return list;
    }
}
