package com.example.payrollmanagementsystem.model;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class Employee {
    private final IntegerProperty employeeId;
    private final StringProperty name;
    private final StringProperty department;
    private final StringProperty position;
    private final DoubleProperty basicSalary;
    private final IntegerProperty workingHours;
    private final IntegerProperty userId;
    private LocalDateTime deletedAt;

    // ✅ No-argument constructor
    public Employee() {
        this.employeeId = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.department = new SimpleStringProperty();
        this.position = new SimpleStringProperty();
        this.basicSalary = new SimpleDoubleProperty();
        this.workingHours = new SimpleIntegerProperty();
        this.userId = new SimpleIntegerProperty();
    }

    // ✅ Constructor with all fields (for updating existing employee)
    public Employee(int employeeId, String name, String department, String position, double basicSalary, int workingHours) {
        this.employeeId = new SimpleIntegerProperty(employeeId);
        this.name = new SimpleStringProperty(name);
        this.department = new SimpleStringProperty(department);
        this.position = new SimpleStringProperty(position);
        this.basicSalary = new SimpleDoubleProperty(basicSalary);
        this.workingHours = new SimpleIntegerProperty(workingHours);
        this.userId = new SimpleIntegerProperty(); // Assuming userId can be set later
    }

    // ✅ Constructor for new employee (without employeeId)
    public Employee(String name, String department, String position, double basicSalary, int workingHours) {
        this.employeeId = new SimpleIntegerProperty(); // Auto-generated ID
        this.name = new SimpleStringProperty(name);
        this.department = new SimpleStringProperty(department);
        this.position = new SimpleStringProperty(position);
        this.basicSalary = new SimpleDoubleProperty(basicSalary);
        this.workingHours = new SimpleIntegerProperty(workingHours);
        this.userId = new SimpleIntegerProperty(); // Assuming userId can be set later
    }

    // Getters and Setters

    public int getEmployeeId() {
        return employeeId.get();
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId.set(employeeId);
    }

    public IntegerProperty employeeIdProperty() {
        return employeeId;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getDepartment() {
        return department.get();
    }

    public void setDepartment(String department) {
        this.department.set(department);
    }

    public StringProperty departmentProperty() {
        return department;
    }

    public String getPosition() {
        return position.get();
    }

    public void setPosition(String position) {
        this.position.set(position);
    }

    public StringProperty positionProperty() {
        return position;
    }

    public double getBasicSalary() {
        return basicSalary.get();
    }

    public void setBasicSalary(double basicSalary) {
        this.basicSalary.set(basicSalary);
    }

    public DoubleProperty basicSalaryProperty() {
        return basicSalary;
    }

    public int getWorkingHours() {
        return workingHours.get();
    }

    public void setWorkingHours(int workingHours) {
        this.workingHours.set(workingHours);
    }

    public IntegerProperty workingHoursProperty() {
        return workingHours;
    }

    public int getUserId() {
        return userId.get();
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }

    // Add setter and getter for deletedAt
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
