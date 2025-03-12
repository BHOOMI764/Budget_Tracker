package com.budgeting;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/budgeting_db?currentSchema=public"; 
    private static final String USER = "postgres";  
    private static final String PASSWORD = "bhoomi90#";  
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            LOGGER.log(Level.INFO, "Connected to the database successfully!");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "PostgreSQL JDBC Driver not found!", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection failed!", e);
        }
        return conn;
    }

    public void fetchData(JTable table) {
        String query = "SELECT * FROM budget_entries";
        try (Connection con = connect();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0); // Clear existing data

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("name"),
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    rs.getDate("date")
                });
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL execution failed!", e);
        }
    }

    public void addExpense(String name, double amount, String category, Date date) {
        String sql = "INSERT INTO budget_entries (name, amount, category, date) VALUES (?, ?, ?, ?)";
        try (Connection con = connect();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, category);
            pstmt.setDate(4, date);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding expense!", e);
        }
    }

    public void updateExpense(String name, double amount, String category, Date date) {
        String sql = "UPDATE budget_entries SET amount=?, category=?, date=? WHERE name=?";
        try (Connection con = connect();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, category);
            pstmt.setDate(3, date);
            pstmt.setString(4, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating expense!", e);
        }
    }

    public void deleteExpense(String name) {
        String sql = "DELETE FROM budget_entries WHERE name=?";
        try (Connection con = connect();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting expense!", e);
        }
    }
}
