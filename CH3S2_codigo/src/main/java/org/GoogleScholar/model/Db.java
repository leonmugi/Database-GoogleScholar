package org.GoogleScholar.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    // Ajusta host/puerto si es necesario
    private static final String URL  = System.getenv().getOrDefault("DB_URL",
            "jdbc:mysql://localhost:3306/scholar?useSSL=false&allowPublicKeyRetrieval=true");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "scholar_user");
    private static final String PASS = System.getenv().getOrDefault("DB_PASS", "StrongPassword!");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
