// ReportDAO.java
package com.example.payrollmanagementsystem.dao;

import com.example.payrollmanagementsystem.model.Report;
import com.example.payrollmanagementsystem.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO {

    // Get total payroll expenses for non-deleted employees
    public double getTotalPayrollExpenses() {
        double total = 0;
        String query = "SELECT SUM(s.net_salary) AS total " +
                "FROM salaries s " +
                "JOIN employees e ON s.employee_id = e.employee_id " +
                "WHERE e.deleted_at IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    // Get payroll grouped by department for non-deleted employees
    public List<Report> getPayrollByDepartment() {
        List<Report> reports = new ArrayList<>();
        String query = "SELECT e.department AS department, SUM(s.net_salary) AS total " +
                "FROM employees e " +
                "JOIN salaries s ON e.employee_id = s.employee_id " +
                "WHERE e.deleted_at IS NULL " +
                "GROUP BY e.department";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String department = rs.getString("department");
                double total = rs.getDouble("total");
                reports.add(new Report(department, total, null, null, "Department"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reports;
    }

    // Get monthly salary trends for non-deleted employees - CORRECTED
    public List<Report> getMonthlySalaryTrends() {
        List<Report> reports = new ArrayList<>();
        String query = "SELECT m.month_name AS month, COALESCE(SUM(s.net_salary), 0) AS total " +
                "FROM months m " +
                "LEFT JOIN salaries s ON m.month_id = MONTH(s.salary_month) " +
                "JOIN employees e ON s.employee_id = e.employee_id " +
                "WHERE e.deleted_at IS NULL " +
                "GROUP BY m.month_id, m.month_name " +
                "ORDER BY m.month_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String month = rs.getString("month");
                double total = rs.getDouble("total");
                reports.add(new Report(month, total, null, null, "Month"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reports;
    }

    // Get payroll summary statistics for non-deleted employees
    public Map<String, Double> getPayrollSummary() {
        Map<String, Double> summary = new LinkedHashMap<>();
        String query = "SELECT " +
                "SUM(s.net_salary) AS total, " +
                "AVG(s.net_salary) AS average, " +
                "SUM(s.overtime_pay) AS overtime " +
                "FROM salaries s " +
                "JOIN employees e ON s.employee_id = e.employee_id " +
                "WHERE e.deleted_at IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                summary.put("Total Payroll", rs.getDouble("total"));
                summary.put("Average Salary", rs.getDouble("average"));
                summary.put("Overtime Costs", rs.getDouble("overtime"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return summary;
    }

    // Get department with highest payroll for non-deleted employees
    public String getHighestPayingDept() {
        String department = null;
        String query = "SELECT e.department " +
                "FROM employees e " +
                "JOIN salaries s ON e.employee_id = s.employee_id " +
                "WHERE e.deleted_at IS NULL " +
                "GROUP BY e.department " +
                "ORDER BY SUM(s.net_salary) DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                department = rs.getString("department");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return department;
    }
}
