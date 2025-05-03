package com.example.payrollmanagementsystem.model;

/**
 * Report is a versatile model representing analytical data for charts and summaries.
 * It supports extra metadata such as percentages, colors, and categories.
 */
public class Report {
    private String label;        // E.g., Department name or Month
    private double value;        // Numeric value (e.g., total salary)
    private Double percentage;   // Optional: Used for pie charts or summaries
    private String colorHex;     // Optional: UI color suggestion (e.g., "#3498db")
    private String category;     // Optional: Type (e.g., "Department", "Month")

    // Basic Constructor
    public Report(String label, double value) {
        this.label = label;
        this.value = value;
    }

    // Extended Constructor
    public Report(String label, double value, Double percentage, String colorHex, String category) {
        this.label = label;
        this.value = value;
        this.percentage = percentage;
        this.colorHex = colorHex;
        this.category = category;
    }

    // Getters and Setters
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Report{" +
                "label='" + label + '\'' +
                ", value=" + value +
                ", percentage=" + percentage +
                ", colorHex='" + colorHex + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
