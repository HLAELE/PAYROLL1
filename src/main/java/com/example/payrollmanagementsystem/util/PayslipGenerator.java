package com.example.payrollmanagementsystem.util;

import com.example.payrollmanagementsystem.dao.EmployeeDAO;
import com.example.payrollmanagementsystem.dao.PayslipDAO;
import com.example.payrollmanagementsystem.model.Employee;
import com.example.payrollmanagementsystem.model.Payslip;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class PayslipGenerator {

    private static EmployeeDAO employeeDAO = new EmployeeDAO();
    private static PayslipDAO payslipDAO = new PayslipDAO();

    public static void generatePayslip(int employeeId) {
        try {
            // Verify employee exists
            if (!employeeDAO.employeeExists(employeeId)) {
                showAlert(Alert.AlertType.ERROR, "Invalid Employee", "No employee found with ID: " + employeeId);
                return;
            }

            // Fetch Employee
            Employee employee = employeeDAO.getEmployeeById(employeeId);

            // Calculate salary details
            double basicSalary = employee.getBasicSalary();
            double overtimePay = calculateOvertime(employee);
            double grossSalary = basicSalary + overtimePay;
            double taxDeduction = calculateTax(grossSalary);
            double insuranceDeduction = calculateInsurance(grossSalary);
            double otherDeductions = calculateOtherDeductions(employee);
            double netSalary = grossSalary - taxDeduction - insuranceDeduction - otherDeductions;

            // Create Payslip
            Payslip payslip = new Payslip();
            payslip.setEmployeeId(employeeId);
            payslip.setBasicSalary(basicSalary);
            payslip.setOvertimePay(overtimePay);
            payslip.setGrossSalary(grossSalary);
            payslip.setTaxDeduction(taxDeduction);
            payslip.setInsuranceDeduction(insuranceDeduction);
            payslip.setOtherDeductions(otherDeductions);
            payslip.setNetSalary(netSalary);
            payslip.setPayPeriod(LocalDate.now());
            payslip.setGeneratedOn(LocalDateTime.now());

            payslipDAO.savePayslip(payslip);

            exportPayslipToPDF(employee, payslip);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Payslip generated successfully for employee ID: " + employeeId);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to generate payslip: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "File Error", "Failed to save payslip PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void exportPayslipToPDF(Employee employee, Payslip payslip) throws IOException {
        Stage primaryStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Payslip PDF");
        fileChooser.setInitialFileName(String.format("payslip_%d_%s.pdf",
                employee.getEmployeeId(),
                YearMonth.from(payslip.getPayPeriod()).format(DateTimeFormatter.ofPattern("yyyyMM"))));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file == null) {
            throw new IOException("No file selected");
        }

        try (PdfWriter writer = new PdfWriter(file);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph("VOGUX C")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(18));
            document.add(new Paragraph("\"Fell the confidance, be you.\"")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic()
                    .setFontSize(12));
            document.add(new Paragraph("\n"));

            // Payslip generation date
            document.add(new Paragraph("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10)
                    .setItalic());
            document.add(new Paragraph("\n"));

            // Employee Information
            Table infoTable = new Table(new float[]{1, 3});
            infoTable.setWidth(UnitValue.createPercentValue(100));
            addTableRow(infoTable, "Employee ID:", String.valueOf(employee.getEmployeeId()));
            addTableRow(infoTable, "Name:", employee.getName());
            addTableRow(infoTable, "Department:", employee.getDepartment());
            addTableRow(infoTable, "Position:", employee.getPosition());
            addTableRow(infoTable, "Pay Period:", YearMonth.from(payslip.getPayPeriod()).format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // Earnings
            document.add(new Paragraph("EARNINGS").setBold());
            Table earningsTable = new Table(new float[]{3, 1});
            earningsTable.setWidth(UnitValue.createPercentValue(100));
            addTableRow(earningsTable, "Basic Salary:", formatCurrency(payslip.getBasicSalary()));
            addTableRow(earningsTable, "Overtime Pay:", formatCurrency(payslip.getOvertimePay()));
            addTableRow(earningsTable, "Gross Salary:", formatCurrency(payslip.getGrossSalary()));
            document.add(earningsTable);
            document.add(new Paragraph("\n"));

            // Deductions
            document.add(new Paragraph("DEDUCTIONS").setBold());
            Table deductionsTable = new Table(new float[]{3, 1});
            deductionsTable.setWidth(UnitValue.createPercentValue(100));
            addTableRow(deductionsTable, "Tax Deduction:", formatCurrency(payslip.getTaxDeduction()));
            addTableRow(deductionsTable, "Insurance Deduction:", formatCurrency(payslip.getInsuranceDeduction()));
            addTableRow(deductionsTable, "Other Deductions:", formatCurrency(payslip.getOtherDeductions()));
            addTableRow(deductionsTable, "Total Deductions:",
                    formatCurrency(payslip.getTaxDeduction() + payslip.getInsuranceDeduction() + payslip.getOtherDeductions()));
            document.add(deductionsTable);
            document.add(new Paragraph("\n"));

            // Net Salary
            Paragraph netSalary = new Paragraph("NET SALARY: " + formatCurrency(payslip.getNetSalary()))
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(netSalary);
        }
    }

    private static void addTableRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold()));
        table.addCell(new Cell().add(new Paragraph(value)));
    }

    private static String formatCurrency(double amount) {
        return String.format("M%,.2f", amount);
    }

    private static double calculateOvertime(Employee employee) {
        // Example logic for overtime: 5% of basic salary
        return employee.getBasicSalary() * 0.05;
    }

    private static double calculateTax(double grossSalary) {
        // Example tax: 15%
        return grossSalary * 0.15;
    }

    private static double calculateInsurance(double grossSalary) {
        // Example insurance: 5%
        return grossSalary * 0.05;
    }

    private static double calculateOtherDeductions(Employee employee) {
        // Example fixed other deductions
        return 500.0; // Fixed M500 deduction for now
    }

    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
