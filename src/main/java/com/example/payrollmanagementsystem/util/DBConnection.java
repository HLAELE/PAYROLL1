package com.example.payrollmanagementsystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/payroll_system?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "59921347";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // Load the MySQL driver
                connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                System.out.println("Database connected successfully!"); // Confirmation message
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC driver not found!");
                throw new SQLException("MySQL JDBC driver not found!", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed."); // Confirmation message
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        // Simple test to check the connection
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("Successfully established a test connection.");
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Failed to establish a test connection: " + e.getMessage());
        }
    }
}