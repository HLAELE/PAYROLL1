package com.example.payrollmanagementsystem.model;

import java.time.LocalDateTime;

public class PayrollDeduction {
    private int payrollDeductionId;
    private int salaryId;
    private int deductionTypeId;
    private double deductionAmount;
    private LocalDateTime createdAt;

    // Getters and Setters
    public int getPayrollDeductionId() { return payrollDeductionId; }
    public void setPayrollDeductionId(int payrollDeductionId) { this.payrollDeductionId = payrollDeductionId; }

    public int getSalaryId() { return salaryId; }
    public void setSalaryId(int salaryId) { this.salaryId = salaryId; }

    public int getDeductionTypeId() { return deductionTypeId; }
    public void setDeductionTypeId(int deductionTypeId) { this.deductionTypeId = deductionTypeId; }

    public double getDeductionAmount() { return deductionAmount; }
    public void setDeductionAmount(double deductionAmount) { this.deductionAmount = deductionAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}