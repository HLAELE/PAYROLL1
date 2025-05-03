package com.example.payrollmanagementsystem.controller;

import com.example.payrollmanagementsystem.dao.EmployeeDAO;
import com.example.payrollmanagementsystem.model.Employee;
import com.example.payrollmanagementsystem.util.PayslipGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PayslipController {

    @FXML private ComboBox<Employee> employeeComboBox;
    @FXML private Button generatePayslipBtn;
    @FXML private Button backBtn;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    @FXML
    public void handleGeneratePayslip() {
        try {
            Employee employee = employeeComboBox.getValue();

            if (employee == null) {
                showAlert("Error", "Please select an employee to generate the payslip.");
                return;
            }

            PayslipGenerator.generatePayslip(employee.getEmployeeId());

        } catch (Exception e) {
            showAlert("Error", "Failed to generate payslip: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBack() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/payrollmanagementsystem/fxml/AdminDashboardView.fxml"));
        Stage stage = (Stage) backBtn.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void initialize() {
        loadEmployees();
    }

    private void loadEmployees() {
        try {
            List<Employee> employees = employeeDAO.getAllEmployees();
            ObservableList<Employee> employeeList = FXCollections.observableArrayList(employees);
            employeeComboBox.setItems(employeeList);

            employeeComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Employee e) {
                    if (e == null) return "";
                    String[] names = e.getName().split(" ");
                    String firstName = names.length > 0 ? names[0] : "";
                    String lastName = names.length > 1 ? names[1] : "";
                    return String.format("%s %s (%d)", firstName, lastName, e.getEmployeeId());
                }

                @Override
                public Employee fromString(String s) {
                    return null;
                }
            });

        } catch (SQLException e) {
            showAlert("Database Error", e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
