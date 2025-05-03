package com.example.payrollmanagementsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminDashboardController {

    @FXML private Button manageEmployeesBtn;
    @FXML private Button calculateSalaryBtn;
    @FXML private Button generatePayslipBtn;
    @FXML private Button viewReportsBtn;
    @FXML private Button userManagementBtn;
    @FXML private Button logoutBtn;
    @FXML private TextField employeeIdTextField;

    @FXML
    private void handleManageEmployees() throws IOException {
        loadView("ManageEmployeesView.fxml");
    }

    @FXML
    private void handleCalculateSalary() throws IOException {
        loadView("CalculateSalaryView.fxml");
    }

    @FXML
    private void handleGeneratePayslip() throws IOException {
        loadView("GeneratePayslipView.fxml");
    }

    @FXML
    private void handleViewReports() throws IOException {
        loadView("ReportsView.fxml");
    }

    @FXML
    private void handleUserManagement() throws IOException {
        loadView("UserManagementView.fxml");
    }

    @FXML
    private void handleLogout() throws IOException {
        loadView("LoginView.fxml");
    }

    @FXML
    private void handleViewEmployeeDetails(ActionEvent event) {
        try {
            if (employeeIdTextField.getText().isEmpty()) {
                showAlert("Input Error", "Please enter an employee ID");
                return;
            }

            int employeeId = Integer.parseInt(employeeIdTextField.getText());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/payrollmanagementsystem/fxml/ViewEmployeeDetailsView.fxml"));
            Parent root = loader.load();

            ViewEmployeeDetailsController controller = loader.getController();
            controller.loadEmployeeDetails(employeeId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Employee Details");
            stage.centerOnScreen();
            stage.show();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid numeric ID");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load employee details view");
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/payrollmanagementsystem/fxml/" + fxmlFile));
        Parent root = loader.load();
        Stage stage = (Stage) manageEmployeesBtn.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
