package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private final String URL = System.getenv("URL");
    private final String user = System.getenv("USER");
    private final String password = System.getenv("PASSWORD");

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, user, password);
    }
}
