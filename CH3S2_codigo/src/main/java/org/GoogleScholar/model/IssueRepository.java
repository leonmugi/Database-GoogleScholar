package org.GoogleScholar.model;

import java.sql.*;
import java.util.List;

public class IssueRepository {
    public void saveAll(List<Issue> list) throws SQLException {
        if (list == null || list.isEmpty()) return;
        String sql = "INSERT INTO ingest_issues (author_query, article_title, issue, http_status, raw_json) " +
                "VALUES (?,?,?,?,?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (Issue i : list) {
                ps.setString(1, i.getAuthorQuery());
                ps.setString(2, i.getArticleTitle());
                ps.setString(3, i.getIssue());
                if (i.getHttpStatus() == null) ps.setNull(4, Types.INTEGER);
                else ps.setInt(4, i.getHttpStatus());
                ps.setString(5, i.getRawJson());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
