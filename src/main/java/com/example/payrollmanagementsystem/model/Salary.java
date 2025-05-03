package com.example.payrollmanagementsystem.model;

import java.time.LocalDate;
import java.time.YearMonth;

public class Salary {
    private int salaryId;
    private int employeeId;
    private double basicSalary;
    private double overtimePay;
    private double grossSalary;
    private double netSalary;
    private LocalDate paymentDate;
    private YearMonth salaryMonth;
    private LocalDate payPeriodStart;  // Add this field
    private LocalDate payPeriodEnd;  // Add this field

    // Getters and Setters
    public int getSalaryId() { return salaryId; }
    public void setSalaryId(int salaryId) { this.salaryId = salaryId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }

    public double getOvertimePay() { return overtimePay; }
    public void setOvertimePay(double overtimePay) { this.overtimePay = overtimePay; }

    public double getGrossSalary() { return grossSalary; }
    public void setGrossSalary(double grossSalary) { this.grossSalary = grossSalary; }

    public double getNetSalary() { return netSalary; }
    public void setNetSalary(double netSalary) { this.netSalary = netSalary; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public YearMonth getSalaryMonth() { return salaryMonth; }
    public void setSalaryMonth(YearMonth salaryMonth) {
        this.salaryMonth = salaryMonth;
    }

    // Setter for payPeriodStart
    public void setPayPeriodStart(LocalDate payPeriodStart) {
        this.payPeriodStart = payPeriodStart;
    }

    // Setter for payPeriodEnd
    public void setPayPeriodEnd(LocalDate payPeriodEnd) {
        this.payPeriodEnd = payPeriodEnd;
    }

    // Derived fields
    public LocalDate getPayPeriodStart() {
        return payPeriodStart != null ? payPeriodStart : (salaryMonth != null ? salaryMonth.atDay(1) : null);
    }

    public LocalDate getPayPeriodEnd() {
        return payPeriodEnd != null ? payPeriodEnd : (salaryMonth != null ? salaryMonth.atEndOfMonth() : null);
    }
}
