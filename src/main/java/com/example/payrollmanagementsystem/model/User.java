package com.example.payrollmanagementsystem.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.example.payrollmanagementsystem.util.PasswordHashing;

public class User {
    private IntegerProperty id;
    private StringProperty username;
    private StringProperty passwordHash;
    private StringProperty role;
    private IntegerProperty employeeId; // New field to link to employees.employee_id

    // Constructor with ID and employeeId (for existing users)
    public User(int id, String username, String passwordHash, String role, int employeeId) {
        this.id = new SimpleIntegerProperty(id);
        this.username = new SimpleStringProperty(username);
        this.passwordHash = new SimpleStringProperty(passwordHash);
        this.role = new SimpleStringProperty(role);
        this.employeeId = new SimpleIntegerProperty(employeeId);
    }

    // Constructor without ID and employeeId (for new registration)
    // Hashes the password immediately during object creation
    public User(String username, String password, String role) {
        this(0, username, PasswordHashing.hashPassword(password), role, 0);
        // Hashes the password immediately during object creation
    }

    // Getters
    public int getId() {
        return id.get();
    }

    public String getUsername() {
        return username.get();
    }

    public String getPasswordHash() {
        return passwordHash.get();
    }

    public String getRole() {
        return role.get();
    }

    public int getEmployeeId() {
        return employeeId.get();
    }

    // Property getters (needed for JavaFX TableView)
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty passwordHashProperty() {
        return passwordHash;
    }

    public StringProperty roleProperty() {
        return role;
    }

    public IntegerProperty employeeIdProperty() {
        return employeeId;
    }

    // Setters
    public void setId(int id) {
        this.id.set(id);
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash.set(passwordHash);
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId.set(employeeId);
    }

    // Method to establish a connection to the database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/payroll_system",  // Replace 'yourdb' with your actual database name
                "username",  // Replace 'username' with your MySQL username
                "Enigma21"   // Replace 'password' with your MySQL password
        );
    }
}
