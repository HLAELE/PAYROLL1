package com.example.payrollmanagementsystem.dao;

import com.example.payrollmanagementsystem.model.Employee;
import com.example.payrollmanagementsystem.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    private static Employee currentEmployee;  // Static variable to store the logged-in employee

    // This method should be called after the user successfully logs in
    public static void setCurrentEmployee(Employee employee) {
        currentEmployee = employee;
    }

    // This method will return the currently logged-in employee
    public static Employee getCurrentEmployee() {
        return currentEmployee;
    }

    // addEmployee: returns the generated employee ID
    public int addEmployee(Employee employee) throws SQLException {
        String sql = "INSERT INTO employees (user_id, first_name, last_name, department, position, basic_salary, working_hours) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Split the name into first and last names
        String[] names = employee.getName().split(" ");
        String firstName = names.length > 0 ? names[0] : "";
        String lastName = names.length > 1 ? names[1] : "";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, employee.getUserId());
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, employee.getDepartment());
            pstmt.setString(5, employee.getPosition());
            pstmt.setDouble(6, employee.getBasicSalary());
            pstmt.setInt(7, employee.getWorkingHours());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating employee failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated employee ID
                } else {
                    throw new SQLException("Creating employee failed, no ID obtained.");
                }
            }
        }
    }

    // Another version (optional) using a DatabaseConnection class if different from DBConnection
    public int addEmployeeUsingDatabaseConnection(Employee employee) throws SQLException {
        String sql = "INSERT INTO employees (user_id, first_name, last_name, department, position, basic_salary, working_hours) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();  // NOTE: DatabaseConnection, not DBConnection
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, employee.getUserId());
            // You can manually set first name, last name, etc. if available separately
            // Otherwise, you can modify according to your structure

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating employee failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated ID
                } else {
                    throw new SQLException("Creating employee failed, no ID obtained.");
                }
            }
        }
    }

    // Update employee
    public void updateEmployee(Employee employee) throws SQLException {
        String sql = "UPDATE employees SET first_name = ?, last_name = ?, department = ?, " +
                "position = ?, basic_salary = ?, working_hours = ? WHERE employee_id = ?";

        String[] names = employee.getName().split(" ");
        String firstName = names.length > 0 ? names[0] : "";
        String lastName = names.length > 1 ? names[1] : "";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, employee.getDepartment());
            stmt.setString(4, employee.getPosition());
            stmt.setDouble(5, employee.getBasicSalary());
            stmt.setInt(6, employee.getWorkingHours());
            stmt.setInt(7, employee.getEmployeeId());
            stmt.executeUpdate();
        }
    }

    // Soft delete employee
    public void deleteEmployee(int employeeId) throws SQLException {
        String sql = "UPDATE employees SET deleted_at = ? WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, employeeId);
            stmt.executeUpdate();
        }
    }

    // Get all employees
    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT employee_id, user_id, CONCAT(first_name, ' ', last_name) AS full_name, " +
                "department, position, basic_salary, working_hours, deleted_at " +
                "FROM employees WHERE deleted_at IS NULL";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Employee employee = new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("full_name"),
                        rs.getString("department"),
                        rs.getString("position"),
                        rs.getDouble("basic_salary"),
                        rs.getInt("working_hours")
                );
                employee.setUserId(rs.getInt("user_id"));
                Timestamp deletedAtTimestamp = rs.getTimestamp("deleted_at");
                employee.setDeletedAt(deletedAtTimestamp != null ? deletedAtTimestamp.toLocalDateTime() : null);
                employees.add(employee);
            }
        }
        return employees;
    }

    // Get employee by ID
    public Employee getEmployeeById(int id) throws SQLException {
        String sql = "SELECT employee_id, user_id, CONCAT(first_name, ' ', last_name) AS full_name, " +
                "department, position, basic_salary, working_hours, deleted_at " +
                "FROM employees WHERE employee_id = ? AND deleted_at IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Employee employee = new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("full_name"),
                        rs.getString("department"),
                        rs.getString("position"),
                        rs.getDouble("basic_salary"),
                        rs.getInt("working_hours")
                );
                employee.setUserId(rs.getInt("user_id"));
                Timestamp deletedAtTimestamp = rs.getTimestamp("deleted_at");
                employee.setDeletedAt(deletedAtTimestamp != null ? deletedAtTimestamp.toLocalDateTime() : null);
                return employee;
            }
        }
        return null;
    }

    // Check if employee exists
    public boolean employeeExists(int employeeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM employees WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Get employee by User ID
    public Employee getEmployeeByUserId(int userId) throws SQLException {
        String sql = "SELECT employee_id, user_id, first_name, last_name, department, position, basic_salary, working_hours " +
                "FROM employees WHERE user_id = ? AND deleted_at IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Employee employee = new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("department"),
                        rs.getString("position"),
                        rs.getDouble("basic_salary"),
                        rs.getInt("working_hours")
                );
                employee.setUserId(rs.getInt("user_id"));
                return employee;
            }
        }
        return null;
    }

    // Get employee by full name
    public Employee getEmployeeByName(String name) throws SQLException {
        String sql = "SELECT employee_id, user_id, CONCAT(first_name, ' ', last_name) AS full_name, " +
                "department, position, basic_salary, working_hours, deleted_at " +
                "FROM employees WHERE CONCAT(first_name, ' ', last_name) = ? AND deleted_at IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Employee employee = new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("full_name"),
                        rs.getString("department"),
                        rs.getString("position"),
                        rs.getDouble("basic_salary"),
                        rs.getInt("working_hours")
                );
                employee.setUserId(rs.getInt("user_id"));
                Timestamp deletedAtTimestamp = rs.getTimestamp("deleted_at");
                employee.setDeletedAt(deletedAtTimestamp != null ? deletedAtTimestamp.toLocalDateTime() : null);
                return employee;
            }
        }
        return null;
    }
}
