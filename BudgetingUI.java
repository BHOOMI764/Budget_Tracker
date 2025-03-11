package com.budgeting;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BudgetingUI {
    private static final String URL = "jdbc:postgresql://localhost:5432/budgeting_db"; 
    private static final String USER = "postgres";  
    private static final String PASSWORD = "bhoomi90#";  

    private JFrame frame;
    private JTextField txtName, txtAmount, txtCategory, txtDate;
    private JButton btnAdd, btnUpdate, btnDelete;
    private JTable table;
    private DefaultTableModel model;

    public BudgetingUI() {
        frame = new JFrame("Budgeting App");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(new JLabel("Name:")); txtName = new JTextField(); panel.add(txtName);
        panel.add(new JLabel("Amount:")); txtAmount = new JTextField(); panel.add(txtAmount);
        panel.add(new JLabel("Category:")); txtCategory = new JTextField(); panel.add(txtCategory);
        panel.add(new JLabel("Date (YYYY-MM-DD):")); txtDate = new JTextField(); panel.add(txtDate);
        frame.add(panel, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel();
        btnAdd = new JButton("Add"); btnUpdate = new JButton("Update"); btnDelete = new JButton("Delete");
        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); btnPanel.add(btnDelete);
        frame.add(btnPanel, BorderLayout.SOUTH);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Amount", "Category", "Date"}, 0);
        table = new JTable(model);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();

        btnAdd.addActionListener(e -> addEntry());
        btnUpdate.addActionListener(e -> updateEntry());
        btnDelete.addActionListener(e -> deleteEntry());

        frame.setVisible(true);
    }

    private void loadData() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM budget_entries ORDER BY date DESC")) {
        
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getDouble("amount"),
                                          rs.getString("category"), rs.getDate("date")});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading data: " + ex.getMessage());
        }
    }

    private void addEntry() {
        if (txtName.getText().isEmpty() || txtAmount.getText().isEmpty() || txtCategory.getText().isEmpty() || txtDate.getText().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields are required!");
            return;
        }
        String sql = "INSERT INTO budget_entries (name, amount, category, date) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, txtName.getText().trim());
            pstmt.setDouble(2, Double.parseDouble(txtAmount.getText().trim()));
            pstmt.setString(3, txtCategory.getText().trim());
            pstmt.setDate(4, Date.valueOf(txtDate.getText().trim()));

            pstmt.executeUpdate();
            loadData();
            JOptionPane.showMessageDialog(frame, "Entry added successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error adding entry: " + ex.getMessage());
        }
    }

    private void updateEntry() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Select a row to update.");
            return;
        }

        int id = (int) table.getValueAt(selectedRow, 0);
        String sql = "UPDATE budget_entries SET name=?, amount=?, category=?, date=? WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, txtName.getText().trim());
            pstmt.setDouble(2, Double.parseDouble(txtAmount.getText().trim()));
            pstmt.setString(3, txtCategory.getText().trim());
            pstmt.setDate(4, Date.valueOf(txtDate.getText().trim()));
            pstmt.setInt(5, id);

            pstmt.executeUpdate();
            loadData();
            JOptionPane.showMessageDialog(frame, "Entry updated successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error updating entry: " + ex.getMessage());
        }
    }

    private void deleteEntry() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Select a row to delete.");
            return;
        }

        int id = (int) table.getValueAt(selectedRow, 0);
        String sql = "DELETE FROM budget_entries WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            loadData();
            JOptionPane.showMessageDialog(frame, "Entry deleted successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error deleting entry: " + ex.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BudgetingUI::new);
    }
}
