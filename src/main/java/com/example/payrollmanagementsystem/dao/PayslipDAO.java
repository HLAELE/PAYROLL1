package com.example.payrollmanagementsystem.dao;

import com.example.payrollmanagementsystem.model.Employee;
import com.example.payrollmanagementsystem.model.Payslip;
import com.example.payrollmanagementsystem.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PayslipDAO {

    // Get payslip by employee ID with JOIN to fetch employee name
    public Payslip getPayslipByEmployeeId(int employeeId) throws SQLException {
        String sql = "SELECT p.*, e.first_name, e.last_name " +
                "FROM payslips p " +
                "JOIN employees e ON p.employee_id = e.employee_id " +
                "WHERE p.employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Payslip payslip = new Payslip();
                payslip.setPayslipId(rs.getInt("payslip_id"));
                payslip.setEmployeeId(rs.getInt("employee_id"));
                payslip.setSalaryId(rs.getInt("salary_id"));
                payslip.setBasicSalary(rs.getDouble("basic_salary"));
                payslip.setOvertimePay(rs.getDouble("overtime_pay"));
                payslip.setGrossSalary(rs.getDouble("gross_salary"));
                payslip.setTaxDeduction(rs.getDouble("tax_deduction"));
                payslip.setInsuranceDeduction(rs.getDouble("insurance_deduction"));
                payslip.setOtherDeductions(rs.getDouble("other_deductions"));
                payslip.setNetSalary(rs.getDouble("net_salary"));

                Date payPeriodDate = rs.getDate("pay_period");
                if (payPeriodDate != null) {
                    payslip.setPayPeriod(payPeriodDate.toLocalDate());
                }

                Timestamp generatedOn = rs.getTimestamp("generated_on");
                if (generatedOn != null) {
                    payslip.setGeneratedOn(generatedOn.toLocalDateTime());
                }

                payslip.setFilePath(rs.getString("file_path"));

                // Set employee name
                Employee employee = new Employee();
                employee.setName(rs.getString("first_name") + " " + rs.getString("last_name"));
                payslip.setEmployee(employee);

                return payslip;
            }

            return null;
        }
    }

    // Generate payslip and save it to database
    public Payslip generatePayslip(int employeeId, int salaryId) throws Exception {
        Connection conn = DBConnection.getConnection();

        String employeeQuery = "SELECT e.first_name, e.last_name, e.department, e.position, s.* " +
                "FROM employees e JOIN salaries s ON e.employee_id = s.employee_id " +
                "WHERE e.employee_id = ? AND s.salary_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(employeeQuery);
        pstmt.setInt(1, employeeId);
        pstmt.setInt(2, salaryId);
        ResultSet rs = pstmt.executeQuery();

        if (!rs.next()) {
            throw new Exception("Employee or Salary not found.");
        }

        String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
        double basic = rs.getDouble("basic_salary");
        double overtime = rs.getDouble("overtime_pay");
        double gross = rs.getDouble("gross_salary");
        double tax = rs.getDouble("tax_deduction");
        double insurance = rs.getDouble("insurance_deduction");
        double other = rs.getDouble("other_deductions");
        double net = rs.getDouble("net_salary");

        String filePath = "payslips/payslip_" + employeeId + ".txt";

        String insertQuery = "INSERT INTO payslips (employee_id, salary_id, basic_salary, overtime_pay, gross_salary, " +
                "tax_deduction, insurance_deduction, other_deductions, net_salary, pay_period, generated_on, file_path) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURDATE(), NOW(), ?)";

        PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
        insertStmt.setInt(1, employeeId);
        insertStmt.setInt(2, salaryId);
        insertStmt.setDouble(3, basic);
        insertStmt.setDouble(4, overtime);
        insertStmt.setDouble(5, gross);
        insertStmt.setDouble(6, tax);
        insertStmt.setDouble(7, insurance);
        insertStmt.setDouble(8, other);
        insertStmt.setDouble(9, net);
        insertStmt.setString(10, filePath);
        insertStmt.executeUpdate();

        ResultSet keys = insertStmt.getGeneratedKeys();
        int payslipId = 0;
        if (keys.next()) {
            payslipId = keys.getInt(1);
        }

        Payslip payslip = new Payslip();
        payslip.setPayslipId(payslipId);
        payslip.setEmployeeId(employeeId);
        payslip.setSalaryId(salaryId);
        payslip.setBasicSalary(basic);
        payslip.setOvertimePay(overtime);
        payslip.setGrossSalary(gross);
        payslip.setTaxDeduction(tax);
        payslip.setInsuranceDeduction(insurance);
        payslip.setOtherDeductions(other);
        payslip.setNetSalary(net);
        payslip.setPayPeriod(LocalDate.now());
        payslip.setGeneratedOn(LocalDateTime.now());
        payslip.setFilePath(filePath);

        Employee employee = new Employee();
        employee.setName(fullName);
        payslip.setEmployee(employee);

        return payslip;
    }

    // Save payslip (alternative method with custom Payslip object input)
    public void savePayslip(Payslip payslip) throws SQLException {
        String sql = "INSERT INTO payslips (employee_id, basic_salary, overtime_pay, gross_salary, " +
                "tax_deduction, insurance_deduction, other_deductions, net_salary, pay_period, generated_on) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, payslip.getEmployeeId());
            pstmt.setDouble(2, payslip.getBasicSalary());
            pstmt.setDouble(3, payslip.getOvertimePay());
            pstmt.setDouble(4, payslip.getGrossSalary());
            pstmt.setDouble(5, payslip.getTaxDeduction());
            pstmt.setDouble(6, payslip.getInsuranceDeduction());
            pstmt.setDouble(7, payslip.getOtherDeductions());
            pstmt.setDouble(8, payslip.getNetSalary());
            pstmt.setDate(9, Date.valueOf(payslip.getPayPeriod()));
            pstmt.setTimestamp(10, Timestamp.valueOf(payslip.getGeneratedOn()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating payslip failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    payslip.setPayslipId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // Check if a payslip exists for an employee
    public boolean payslipExists(int employeeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM payslips WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}
