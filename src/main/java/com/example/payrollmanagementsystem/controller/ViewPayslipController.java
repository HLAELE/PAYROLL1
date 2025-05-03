package com.example.payrollmanagementsystem.controller;

import com.example.payrollmanagementsystem.dao.EmployeeDAO;
import com.example.payrollmanagementsystem.dao.PayslipDAO;
import com.example.payrollmanagementsystem.model.Employee;
import com.example.payrollmanagementsystem.model.Payslip;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.sql.SQLException;
import java.time.LocalDate;

public class ViewPayslipController {

    @FXML
    private Label payslipDetailsLabel;

    private final PayslipDAO payslipDAO = new PayslipDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    /**
     * Call this method after loading the FXML,
     * passing the employeeId whose payslip you want to display.
     */
    public void loadPayslipDetails(int employeeId) {
        try {
            // First check if employee exists
            if (!employeeDAO.employeeExists(employeeId)) {
                showAlert(Alert.AlertType.ERROR, "Invalid Employee ID", "No employee found with ID: " + employeeId);
                payslipDetailsLabel.setText("Error: Invalid Employee ID");
                return;
            }

            // Then check for payslip
            Payslip payslip = payslipDAO.getPayslipByEmployeeId(employeeId);
            if (payslip == null) {
                showAlert(Alert.AlertType.INFORMATION, "No Payslip",
                        "No payslip exists for employee ID: " + employeeId +
                                "\nPlease generate a payslip first.");
                payslipDetailsLabel.setText("No payslip generated yet");
                return;
            }

            // Display the payslip
            displayPayslip(payslip);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load payslip: " + e.getMessage());
            payslipDetailsLabel.setText("Error loading payslip");
            e.printStackTrace();
        }
    }

    private void displayPayslip(Payslip payslip) {
        double totalDeductions = payslip.getTaxDeduction()
                + payslip.getInsuranceDeduction()
                + payslip.getOtherDeductions();

        // Try to get employee name if available
        String employeeName = "Unknown";
        Employee employee = payslip.getEmployee();
        if (employee != null && employee.getName() != null) {
            employeeName = employee.getName();
        }

        String details = String.format(
                "=== PAYSLIP ===\n\n" +
                        "Employee: %s (ID: %d)\n" +
                        "Pay Period: %s\n\n" +
                        "Basic Salary: M%.2f\n" +
                        "Overtime Pay: M%.2f\n" +
                        "Gross Salary: $%.2f\n\n" +
                        "=== DEDUCTIONS ===\n" +
                        "Tax: $%.2f\n" +
                        "Insurance: M%.2f\n" +
                        "Other: $%.2f\n" +
                        "Total Deductions: M%.2f\n\n" +
                        "NET SALARY: M%.2f\n\n" +
                        "Generated On: %s",
                employeeName,
                payslip.getEmployeeId(),
                payslip.getPayPeriod() != null ? payslip.getPayPeriod().toString() : "N/A",
                payslip.getBasicSalary(),
                payslip.getOvertimePay(),
                payslip.getGrossSalary(),
                payslip.getTaxDeduction(),
                payslip.getInsuranceDeduction(),
                payslip.getOtherDeductions(),
                totalDeductions,
                payslip.getNetSalary(),
                payslip.getGeneratedOn() != null ? payslip.getGeneratedOn().toString() : LocalDate.now().toString()
        );

        payslipDetailsLabel.setText(details);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
