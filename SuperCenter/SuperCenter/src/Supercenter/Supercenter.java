package Supercenter;

import java.awt.Font;
import java.awt.EventQueue;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Supercenter {

    private JFrame frame;
    private JTable table;
    private JComboBox<String> comboTableSelection;
    private Connection con;
    private PreparedStatement pst;
    private ResultSet rs;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Supercenter window = new Supercenter();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Supercenter() {
        initialize();
        connect();
    }

    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/Supercenter", "root", "password");
            JOptionPane.showMessageDialog(null, "Connected to the database!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error connecting to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTable(String tableName) {
        try {
            if (!isValidTable(tableName)) {
                JOptionPane.showMessageDialog(null, "Invalid table selected.");
                return;
            }
            String query = "SELECT * FROM " + tableName;
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();

            DefaultTableModel model = buildTableModel(rs);
            table.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading table: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    private boolean isValidTable(String tableName) {
        String[] validTables = {"Suppliers", "Products", "Customers", "Transactions"};
        for (String valid : validTables) {
            if (valid.equalsIgnoreCase(tableName)) {
                return true;
            }
        }
        return false;
    }

    private DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = meta.getColumnName(i);
        }

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        while (rs.next()) {
            Object[] rowData = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                rowData[i - 1] = rs.getObject(i);
            }
            model.addRow(rowData);
        }
        return model;
    }

    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblTitle = new JLabel("Supercenter Database Manager");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTitle.setBounds(230, 10, 340, 40);
        frame.getContentPane().add(lblTitle);

        comboTableSelection = new JComboBox<>(new String[]{"Suppliers", "Products", "Customers", "Transactions"});
        comboTableSelection.setBounds(10, 70, 200, 30);
        frame.getContentPane().add(comboTableSelection);

        JButton btnLoad = new JButton("Load Table");
        btnLoad.setBounds(220, 70, 150, 30);
        frame.getContentPane().add(btnLoad);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 120, 760, 300);
        frame.getContentPane().add(scrollPane);

        table = new JTable();
        scrollPane.setViewportView(table);

        btnLoad.addActionListener(e -> loadTable(comboTableSelection.getSelectedItem().toString()));

        JButton btnCreate = new JButton("Create");
        btnCreate.setBounds(10, 450, 150, 30);
        frame.getContentPane().add(btnCreate);

        JButton btnEdit = new JButton("Edit");
        btnEdit.setBounds(170, 450, 150, 30);
        frame.getContentPane().add(btnEdit);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(330, 450, 150, 30);
        frame.getContentPane().add(btnDelete);

        btnCreate.addActionListener(e -> createRecord());
        btnEdit.addActionListener(e -> editRecord());
        btnDelete.addActionListener(e -> deleteRecord());
    }

    private void createRecord() {
        String tableName = comboTableSelection.getSelectedItem().toString();
        String columns, values;
        try {
            switch (tableName) {
                case "Suppliers":
                    columns = "name, contact_info, country";
                    values = "?, ?, ?";
                    pst = con.prepareStatement("INSERT INTO Suppliers (" + columns + ") VALUES (" + values + ")");
                    pst.setString(1, JOptionPane.showInputDialog("Enter supplier name:"));
                    pst.setString(2, JOptionPane.showInputDialog("Enter contact info:"));
                    pst.setString(3, JOptionPane.showInputDialog("Enter country:"));
                    break;
                case "Products":
                    columns = "name, price, category, supplier_id";
                    values = "?, ?, ?, ?";
                    pst = con.prepareStatement("INSERT INTO Products (" + columns + ") VALUES (" + values + ")");
                    pst.setString(1, JOptionPane.showInputDialog("Enter product name:"));
                    pst.setDouble(2, Double.parseDouble(JOptionPane.showInputDialog("Enter price:")));
                    pst.setString(3, JOptionPane.showInputDialog("Enter category:"));
                    pst.setInt(4, Integer.parseInt(JOptionPane.showInputDialog("Enter supplier ID:")));
                    break;
                case "Customers":
                    columns = "first_name, last_name, email, loyalty_points";
                    values = "?, ?, ?, ?";
                    pst = con.prepareStatement("INSERT INTO Customers (" + columns + ") VALUES (" + values + ")");
                    pst.setString(1, JOptionPane.showInputDialog("Enter first name:"));
                    pst.setString(2, JOptionPane.showInputDialog("Enter last name:"));
                    pst.setString(3, JOptionPane.showInputDialog("Enter email:"));
                    pst.setInt(4, Integer.parseInt(JOptionPane.showInputDialog("Enter loyalty points:")));
                    break;
                case "Transactions":
                    columns = "customer_id, product_id, quantity";
                    values = "?, ?, ?";
                    pst = con.prepareStatement("INSERT INTO Transactions (" + columns + ") VALUES (" + values + ")");
                    pst.setInt(1, Integer.parseInt(JOptionPane.showInputDialog("Enter customer ID:")));
                    pst.setInt(2, Integer.parseInt(JOptionPane.showInputDialog("Enter product ID:")));
                    pst.setInt(3, Integer.parseInt(JOptionPane.showInputDialog("Enter quantity:")));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid table selected.");
            }
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Record added successfully!");
            loadTable(tableName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error adding record: " + e.getMessage());
        }
    }
    private void editRecord() {
        int selectedRow = table.getSelectedRow(); // Get the selected row
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "No row selected. Please select a record to edit.");
            return;
        }

        // Get the current table name
        String tableName = comboTableSelection.getSelectedItem().toString();
        String primaryKey = table.getValueAt(selectedRow, 0).toString(); // Assumes primary key is in the first column

        // Define primary key column for each table explicitly
        String primaryKeyColumn = "";
        switch (tableName) {
            case "Suppliers":
                primaryKeyColumn = "supplier_id"; // Explicitly set primary key for Suppliers
                break;
            case "Products":
                primaryKeyColumn = "product_id"; // Explicitly set primary key for Products
                break;
            case "Customers":
                primaryKeyColumn = "customer_id"; // Explicitly set primary key for Customers
                break;
            case "Transactions":
                primaryKeyColumn = "transaction_id"; // Explicitly set primary key for Transactions
                break;
            default:
                JOptionPane.showMessageDialog(null, "Unknown table selected.");
                return;
        }

        try {
            switch (tableName) {
                case "Suppliers":
                    String newName = JOptionPane.showInputDialog("Edit Supplier Name:", table.getValueAt(selectedRow, 1));
                    String newContactInfo = JOptionPane.showInputDialog("Edit Contact Info:", table.getValueAt(selectedRow, 2));
                    String newCountry = JOptionPane.showInputDialog("Edit Country:", table.getValueAt(selectedRow, 3));

                    String updateSuppliers = "UPDATE Suppliers SET name = ?, contact_info = ?, country = ? WHERE " + primaryKeyColumn + " = ?";
                    pst = con.prepareStatement(updateSuppliers);
                    pst.setString(1, newName);
                    pst.setString(2, newContactInfo);
                    pst.setString(3, newCountry);
                    pst.setInt(4, Integer.parseInt(primaryKey));
                    break;

                case "Products":
                    String newProductName = JOptionPane.showInputDialog("Edit Product Name:", table.getValueAt(selectedRow, 1));
                    double newPrice = Double.parseDouble(JOptionPane.showInputDialog("Edit Price:", table.getValueAt(selectedRow, 2)));
                    String newCategory = JOptionPane.showInputDialog("Edit Category:", table.getValueAt(selectedRow, 3));
                    int newSupplierID = Integer.parseInt(JOptionPane.showInputDialog("Edit Supplier ID:", table.getValueAt(selectedRow, 4)));

                    String updateProducts = "UPDATE Products SET name = ?, price = ?, category = ?, supplier_id = ? WHERE " + primaryKeyColumn + " = ?";
                    pst = con.prepareStatement(updateProducts);
                    pst.setString(1, newProductName);
                    pst.setDouble(2, newPrice);
                    pst.setString(3, newCategory);
                    pst.setInt(4, newSupplierID);
                    pst.setInt(5, Integer.parseInt(primaryKey));
                    break;

                case "Customers":
                    String newFirstName = JOptionPane.showInputDialog("Edit First Name:", table.getValueAt(selectedRow, 1));
                    String newLastName = JOptionPane.showInputDialog("Edit Last Name:", table.getValueAt(selectedRow, 2));
                    String newEmail = JOptionPane.showInputDialog("Edit Email:", table.getValueAt(selectedRow, 3));
                    int newLoyaltyPoints = Integer.parseInt(JOptionPane.showInputDialog("Edit Loyalty Points:", table.getValueAt(selectedRow, 4)));

                    String updateCustomers = "UPDATE Customers SET first_name = ?, last_name = ?, email = ?, loyalty_points = ? WHERE " + primaryKeyColumn + " = ?";
                    pst = con.prepareStatement(updateCustomers);
                    pst.setString(1, newFirstName);
                    pst.setString(2, newLastName);
                    pst.setString(3, newEmail);
                    pst.setInt(4, newLoyaltyPoints);
                    pst.setInt(5, Integer.parseInt(primaryKey));
                    break;

                case "Transactions":
                    // Prompt user to edit specific fields, excluding 'date'
                    int newCustomerID = Integer.parseInt(JOptionPane.showInputDialog("Edit Customer ID:", table.getValueAt(selectedRow, 1)));
                    int newProductID = Integer.parseInt(JOptionPane.showInputDialog("Edit Product ID:", table.getValueAt(selectedRow, 2)));
                    int newQuantity = Integer.parseInt(JOptionPane.showInputDialog("Edit Quantity:", table.getValueAt(selectedRow, 3)));

                    // Update the 'Transactions' table, excluding the 'date' field
                    String updateTransactions = "UPDATE Transactions SET customer_id = ?, product_id = ?, quantity = ? WHERE " + primaryKeyColumn + " = ?";
                    pst = con.prepareStatement(updateTransactions);
                    pst.setInt(1, newCustomerID);
                    pst.setInt(2, newProductID);
                    pst.setInt(3, newQuantity);
                    pst.setInt(4, Integer.parseInt(primaryKey));
                    

                    // Execute the update
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Record updated successfully!");
                    break;

            }

            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Record updated successfully!");
            loadTable(tableName); // Reload the table data after update
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating record: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }


    private void deleteRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "No row selected. Please select a record to delete.");
            return;
        }

        // Get the selected table name and primary key
        String tableName = comboTableSelection.getSelectedItem().toString();
        String primaryKey = table.getValueAt(selectedRow, 0).toString();

        // Define primary key column for each table
        String primaryKeyColumn = "";
        switch (tableName) {
            case "Products":
                primaryKeyColumn = "product_id"; // Assuming the primary key for Products is 'product_id'
                break;
            case "Customers":
                primaryKeyColumn = "customer_id"; // Assuming the primary key for Customers is 'customer_id'
                break;
            case "Transactions":
                primaryKeyColumn = "transaction_id"; // Assuming the primary key for Transactions is 'transaction_id'
                break;
            case "Suppliers":
                primaryKeyColumn = "supplier_id"; // Assuming the primary key for Transactions is 'transaction_id'
                break;
            // Add cases for other tables if needed
            default:
                JOptionPane.showMessageDialog(null, "Unknown table: " + tableName);
                return;
        }

        try {
            String deleteQuery = "DELETE FROM " + tableName + " WHERE " + primaryKeyColumn + " = ?";
            pst = con.prepareStatement(deleteQuery);
            pst.setInt(1, Integer.parseInt(primaryKey));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Record deleted successfully!");
            loadTable(tableName);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting record: " + e.getMessage());
        }
    }

}
