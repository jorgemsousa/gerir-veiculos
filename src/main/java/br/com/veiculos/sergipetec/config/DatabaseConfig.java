package br.com.veiculos.sergipetec.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final Dotenv dotenv = Dotenv.load();

    private static final String URL = "jdbc:postgresql://"
            + dotenv.get("DB_HOST") + ":"
            + dotenv.get("DB_PORT") + "/"
            + dotenv.get("DB_NAME");

    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
