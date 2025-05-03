package com.example.payrollmanagementsystem.model;

public class Deduction {
    private int deductionId;
    private int deductionTypeId;
    private String name;
    private double amount;

    // Getters and Setters
    public int getDeductionId() { return deductionId; }
    public void setDeductionId(int deductionId) { this.deductionId = deductionId; }

    public int getDeductionTypeId() { return deductionTypeId; }
    public void setDeductionTypeId(int deductionTypeId) { this.deductionTypeId = deductionTypeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}