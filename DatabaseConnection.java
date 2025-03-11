package com.budgeting;

import java.sql.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/budgeting_db?currentSchema=public"; 
    private static final String USER = "postgres";  
    private static final String PASSWORD = "bhoomi90#";  
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    public static Connection connect() {
        Connection conn = null;
        try {
            // Load PostgreSQL JDBC Driver
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            LOGGER.log(Level.INFO, "Connected to the database successfully!");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "PostgreSQL JDBC Driver not found!", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection failed!", e);
        }
        return conn;
    }

    public static void fetchData() {
        try (Connection conn = connect()) {
            if (conn != null) {
                String sql = "SELECT * FROM budget_entries";  // Your SQL query
                System.out.println("Executing Query: " + sql); // Debugging

                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {
                        // Example: Print first column
                        System.out.println("Row: " + rs.getString(1));
                    }
                }
            } else {
                LOGGER.log(Level.WARNING, "Failed to establish database connection.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL execution failed!", e);
        }
    }

    public static void main(String[] args) {
        fetchData();  // Call method to execute query
    }
}
