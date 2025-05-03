// SalaryDAO.java
package com.example.payrollmanagementsystem.dao;

import com.example.payrollmanagementsystem.model.Salary;
import com.example.payrollmanagementsystem.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class SalaryDAO {

    // Add a new salary record
    public void addSalary(Salary salary) throws SQLException {
        String sql = "INSERT INTO salaries (employee_id, basic_salary, overtime_pay, gross_salary, net_salary, " +
                "pay_period_start, pay_period_end, payment_date, salary_month) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, salary.getEmployeeId());
            stmt.setDouble(2, salary.getBasicSalary());
            stmt.setDouble(3, salary.getOvertimePay());
            stmt.setDouble(4, salary.getGrossSalary());
            stmt.setDouble(5, salary.getNetSalary());
            stmt.setDate(6, Date.valueOf(salary.getPayPeriodStart()));
            stmt.setDate(7, Date.valueOf(salary.getPayPeriodEnd()));
            stmt.setDate(8, Date.valueOf(salary.getPaymentDate()));
            stmt.setDate(9, Date.valueOf(salary.getSalaryMonth().atDay(1)));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Adding salary failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    salary.setSalaryId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // Retrieve a salary record for a specific employee and month for non-deleted employees
    public Salary getSalaryForMonth(int employeeId, YearMonth month) throws SQLException {
        String sql = "SELECT s.* FROM salaries s " +
                "JOIN employees e ON s.employee_id = e.employee_id " +
                "WHERE s.employee_id = ? AND s.salary_month = ? AND e.deleted_at IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            stmt.setDate(2, Date.valueOf(month.atDay(1)));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapSalaryFromResultSet(rs);
                }
            }
        }
        return null;
    }

    // Retrieve all salary records for non-deleted employees
    public List<Salary> getAllSalaries() throws SQLException {
        List<Salary> salaries = new ArrayList<>();
        String sql = "SELECT s.* FROM salaries s " +
                "JOIN employees e ON s.employee_id = e.employee_id " +
                "WHERE e.deleted_at IS NULL " +
                "ORDER BY s.payment_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                salaries.add(mapSalaryFromResultSet(rs));
            }
        }
        return salaries;
    }

    // Delete a salary record by ID
    public boolean deleteSalaryById(int salaryId) throws SQLException {
        String sql = "DELETE FROM salaries WHERE salary_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, salaryId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    // Update an existing salary record
    public boolean updateSalary(Salary salary) throws SQLException {
        String sql = "UPDATE salaries SET employee_id = ?, basic_salary = ?, overtime_pay = ?, " +
                "gross_salary = ?, net_salary = ?, pay_period_start = ?, pay_period_end = ?, " +
                "payment_date = ?, salary_month = ? WHERE salary_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, salary.getEmployeeId());
            stmt.setDouble(2, salary.getBasicSalary());
            stmt.setDouble(3, salary.getOvertimePay());
            stmt.setDouble(4, salary.getGrossSalary());
            stmt.setDouble(5, salary.getNetSalary());
            stmt.setDate(6, Date.valueOf(salary.getPayPeriodStart()));
            stmt.setDate(7, Date.valueOf(salary.getPayPeriodEnd()));
            stmt.setDate(8, Date.valueOf(salary.getPaymentDate()));
            stmt.setDate(9, Date.valueOf(salary.getSalaryMonth().atDay(1)));
            stmt.setInt(10, salary.getSalaryId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    // Helper method to map ResultSet to Salary model
    private Salary mapSalaryFromResultSet(ResultSet rs) throws SQLException {
        Salary salary = new Salary();
        salary.setSalaryId(rs.getInt("salary_id"));
        salary.setEmployeeId(rs.getInt("employee_id"));
        salary.setBasicSalary(rs.getDouble("basic_salary"));
        salary.setOvertimePay(rs.getDouble("overtime_pay"));
        salary.setGrossSalary(rs.getDouble("gross_salary"));
        salary.setNetSalary(rs.getDouble("net_salary"));
        salary.setPayPeriodStart(rs.getDate("pay_period_start").toLocalDate());
        salary.setPayPeriodEnd(rs.getDate("pay_period_end").toLocalDate());
        salary.setPaymentDate(rs.getDate("payment_date").toLocalDate());
        salary.setSalaryMonth(YearMonth.from(rs.getDate("salary_month").toLocalDate()));
        return salary;
    }
}