package com.example.payrollmanagementsystem.controller;

import com.example.payrollmanagementsystem.dao.EmployeeDAO;
import com.example.payrollmanagementsystem.dao.PayslipDAO;
import com.example.payrollmanagementsystem.model.Employee;
import com.example.payrollmanagementsystem.model.Payslip;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class EmployeeDashboardController {

    @FXML private Label employeeNameLabel;
    @FXML private Label employeeDepartmentLabel;
    @FXML private Label employeePositionLabel;
    @FXML private TextField employeeIdField;
    @FXML private Label errorLabel;
    @FXML private TextField idInputField;
    @FXML private Button viewButton;
    @FXML private TextField employeeIdTextField;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final PayslipDAO payslipDAO = new PayslipDAO();
    private int currentEmployeeId;

    public void setEmployeeId(int id) {
        this.currentEmployeeId = id;
        loadEmployeeDetails();
        employeeIdField.setText(String.valueOf(currentEmployeeId));
    }

    private void loadEmployeeDetails() {
        try {
            Employee employee = employeeDAO.getEmployeeById(currentEmployeeId);
            if (employee != null) {
                employeeNameLabel.setText("Name: " + employee.getName());
                employeeDepartmentLabel.setText("Department: " + employee.getDepartment());
                employeePositionLabel.setText("Position: " + employee.getPosition());
            }
        } catch (SQLException e) {
            showError("Failed to load employee details");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewPayslipButton(ActionEvent event) {
        try {
            int id = Integer.parseInt(employeeIdField.getText().trim());
            viewPayslip(id);
        } catch (NumberFormatException e) {
            showError("Invalid ID format. Please enter a valid employee ID");
        }
    }

    @FXML
    private void handleViewDetailsButton(ActionEvent event) {
        try {
            int id = Integer.parseInt(employeeIdField.getText().trim());
            viewEmployeeDetails(id);
        } catch (NumberFormatException e) {
            showError("Invalid ID format. Please enter a valid employee ID");
        }
    }

    private void viewPayslip(int employeeId) {
        try {
            Payslip payslip = payslipDAO.getPayslipByEmployeeId(employeeId);
            if (payslip != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Payslip Details");
                alert.setHeaderText("Payslip Information for Employee ID: " + employeeId);
                alert.setContentText(payslip.toString());
                alert.showAndWait();
            } else {
                showError("No payslip found for employee ID: " + employeeId);
            }
        } catch (Exception e) {
            showError("Error while fetching payslip");
            e.printStackTrace();
        }
    }

    private void viewEmployeeDetails(int employeeId) {
        try {
            Employee employee = employeeDAO.getEmployeeById(employeeId);
            if (employee != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Employee Details");
                alert.setHeaderText("Details for Employee ID: " + employeeId);
                alert.setContentText(
                        "Name: " + employee.getName() + "\n" +
                                "Department: " + employee.getDepartment() + "\n" +
                                "Position: " + employee.getPosition() + "\n" +
                                "Salary: $" + employee.getBasicSalary() + "\n" +
                                "Working Hours: " + employee.getWorkingHours()
                );
                alert.showAndWait();
            } else {
                showError("No employee found with ID: " + employeeId);
            }
        } catch (SQLException e) {
            showError("Database error while fetching employee details");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Stage stage = (Stage) employeeIdField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/payrollmanagementsystem/fxml/LoginView.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Failed to logout");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @FXML
    public void initialize() {
        currentEmployeeId = getCurrentEmployeeId();

        if (viewButton != null) {
            viewButton.setOnAction(event -> {
                try {
                    int viewId = Integer.parseInt(idInputField.getText().trim());
                    if (employeeDAO.employeeExists(viewId)) {
                        viewEmployeeDetails(viewId);
                    } else {
                        showAlert("Invalid ID", "The employee ID does not exist.");
                    }
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter a valid numeric ID.");
                } catch (Exception e) {
                    showAlert("Error", "An error occurred while processing the request.");
                    e.printStackTrace();
                }
            });
        }
    }

    private int getCurrentEmployeeId() {
        return 1; // Replace with actual logic tied to login session
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ✅ Fixed method name to match ViewPayslipController
    @FXML
    private void handleViewPayslip(ActionEvent event) {
        try {
            int employeeId = Integer.parseInt(employeeIdTextField.getText().trim());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/payrollmanagementsystem/fxml/ViewPayslipView.fxml"));
            Parent root = loader.load();

            ViewPayslipController controller = loader.getController();
            controller.loadPayslipDetails(employeeId); // Changed from loadPayslip() to loadPayslipDetails()

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Employee Payslip");
            stage.show();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid numeric ID");
        } catch (IOException e) {
            showAlert("Error", "Could not load payslip view");
            e.printStackTrace();
        }
    }
}