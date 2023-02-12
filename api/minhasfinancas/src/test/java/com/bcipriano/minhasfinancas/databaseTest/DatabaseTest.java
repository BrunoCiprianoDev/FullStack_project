package com.bcipriano.minhasfinancas.databaseTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@SpringBootTest
public class DatabaseTest {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Test
    public void testConnection() {

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Successfully connected to the database.");

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT 1");
            assertTrue(resultSet.next());
            System.out.println("Successfully executed SELECT 1 statement.");
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database: " + e.getMessage());
            assertTrue(false);
        }
    }
}