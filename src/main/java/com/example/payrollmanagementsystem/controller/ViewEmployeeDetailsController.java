package com.example.payrollmanagementsystem.controller;

import com.example.payrollmanagementsystem.dao.EmployeeDAO;
import com.example.payrollmanagementsystem.model.Employee;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.sql.SQLException;

public class ViewEmployeeDetailsController {

    @FXML private Label nameLabel;
    @FXML private Label departmentLabel;
    @FXML private Label positionLabel;
    @FXML private Label salaryLabel;
    @FXML private Label workingHoursLabel;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    /**
     * Loads and displays employee details for the given employee ID
     * @param employeeId The ID of the employee to display
     */
    public void loadEmployeeDetails(int employeeId) {
        try {
            Employee employee = employeeDAO.getEmployeeById(employeeId);
            if (employee != null) {
                nameLabel.setText("Name: " + employee.getName());
                departmentLabel.setText("Department: " + employee.getDepartment());
                positionLabel.setText("Position: " + employee.getPosition());
                salaryLabel.setText(String.format("Salary: $%.2f", employee.getBasicSalary()));
                workingHoursLabel.setText("Working Hours: " + employee.getWorkingHours() + " hours");
            } else {
                clearLabels();
                showAlert("Not Found", "No employee found with ID: " + employeeId);
            }
        } catch (SQLException e) {
            clearLabels();
            showAlert("Database Error", "Could not load employee data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearLabels() {
        nameLabel.setText("Name: N/A");
        departmentLabel.setText("Department: N/A");
        positionLabel.setText("Position: N/A");
        salaryLabel.setText("Salary: N/A");
        workingHoursLabel.setText("Working Hours: N/A");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
