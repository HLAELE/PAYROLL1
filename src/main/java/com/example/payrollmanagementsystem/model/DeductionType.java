package com.example.payrollmanagementsystem.model;

public class DeductionType {
    private int deductionTypeId;
    private String name;
    private String description;

    // Getters and Setters
    public int getDeductionTypeId() { return deductionTypeId; }
    public void setDeductionTypeId(int deductionTypeId) { this.deductionTypeId = deductionTypeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}