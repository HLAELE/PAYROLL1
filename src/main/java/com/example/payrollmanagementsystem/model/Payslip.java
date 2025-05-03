package com.example.payrollmanagementsystem.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Payslip {
    private int payslipId;
    private int employeeId;
    private int salaryId;
    private LocalDateTime generatedOn;
    private String filePath;
    private LocalDate payPeriod;
    private double basicSalary;
    private double overtimePay;
    private double grossSalary;
    private double taxDeduction;
    private double insuranceDeduction;
    private double otherDeductions;
    private double netSalary;
    private Employee employee;
    private List<Deduction> deductions;

    // Getters and Setters
    public int getPayslipId() {
        return payslipId;
    }

    public void setPayslipId(int payslipId) {
        this.payslipId = payslipId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(int salaryId) {
        this.salaryId = salaryId;
    }

    public LocalDateTime getGeneratedOn() {
        return generatedOn;
    }

    public void setGeneratedOn(LocalDateTime generatedOn) {
        this.generatedOn = generatedOn;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDate getPayPeriod() {
        return payPeriod;
    }

    public void setPayPeriod(LocalDate payPeriod) {
        this.payPeriod = payPeriod;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public double getOvertimePay() {
        return overtimePay;
    }

    public void setOvertimePay(double overtimePay) {
        this.overtimePay = overtimePay;
    }

    public double getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(double grossSalary) {
        this.grossSalary = grossSalary;
    }

    public double getTaxDeduction() {
        return taxDeduction;
    }

    public void setTaxDeduction(double taxDeduction) {
        this.taxDeduction = taxDeduction;
    }

    public double getInsuranceDeduction() {
        return insuranceDeduction;
    }

    public void setInsuranceDeduction(double insuranceDeduction) {
        this.insuranceDeduction = insuranceDeduction;
    }

    public double getOtherDeductions() {
        return otherDeductions;
    }

    public void setOtherDeductions(double otherDeductions) {
        this.otherDeductions = otherDeductions;
    }

    public double getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(double netSalary) {
        this.netSalary = netSalary;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public List<Deduction> getDeductions() {
        return deductions;
    }

    public void setDeductions(List<Deduction> deductions) {
        this.deductions = deductions;
    }

    public String getEmployeeName() {
        return employee != null ? employee.getName() : "Unknown Employee";
    }

    public double getTotalDeductions() {
        return taxDeduction + insuranceDeduction + otherDeductions;
    }

    @Override
    public String toString() {
        return String.format(
                "Payslip ID: %d\nEmployee: %s\nBasic Salary: $%.2f\nOvertime: $%.2f\nGross Salary: $%.2f\n" +
                        "Tax Deduction: $%.2f\nInsurance: $%.2f\nOther Deductions: $%.2f\nNet Salary: $%.2f",
                payslipId, getEmployeeName(), basicSalary, overtimePay, grossSalary,
                taxDeduction, insuranceDeduction, otherDeductions, netSalary
        );
    }
}
