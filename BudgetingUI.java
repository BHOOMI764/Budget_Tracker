package com.budgeting;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;

public class BudgetingUI {
    private JFrame frame;
    private JTable table;
    private JTextField txtName, txtAmount, txtCategory, txtDate;
    private DatabaseConnection db;

    public BudgetingUI() {
        db = new DatabaseConnection();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Budgeting App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        table = new JTable(new DefaultTableModel(
            new Object[]{"Name", "Amount", "Category", "Date"}, 0
        ));
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Name:"));
        txtName = new JTextField();
        inputPanel.add(txtName);
        
        inputPanel.add(new JLabel("Amount:"));
        txtAmount = new JTextField();
        inputPanel.add(txtAmount);
        
        inputPanel.add(new JLabel("Category:"));
        txtCategory = new JTextField();
        inputPanel.add(txtCategory);
        
        inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        txtDate = new JTextField();
        inputPanel.add(txtDate);
        
        frame.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnFetch = new JButton("Fetch Data");

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addExpense();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateExpense();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteExpense();
            }
        });

        btnFetch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                db.fetchData(table);
            }
        });

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnFetch);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void addExpense() {
        String name = txtName.getText();
        double amount = Double.parseDouble(txtAmount.getText());
        String category = txtCategory.getText();
        Date date = Date.valueOf(txtDate.getText());

        db.addExpense(name, amount, category, date);
        db.fetchData(table);
        clearFields();
    }

    private void updateExpense() {
        String name = txtName.getText();
        double amount = Double.parseDouble(txtAmount.getText());
        String category = txtCategory.getText();
        Date date = Date.valueOf(txtDate.getText());

        db.updateExpense(name, amount, category, date);
        db.fetchData(table);
        clearFields();
    }

    private void deleteExpense() {
        String name = txtName.getText();
        db.deleteExpense(name);
        db.fetchData(table);
        clearFields();
    }

    private void clearFields() {
        txtName.setText("");
        txtAmount.setText("");
        txtCategory.setText("");
        txtDate.setText("");
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BudgetingUI());
    }
}
