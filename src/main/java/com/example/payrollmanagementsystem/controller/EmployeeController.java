package com.example.payrollmanagementsystem.controller;

import com.example.payrollmanagementsystem.dao.EmployeeDAO;
import com.example.payrollmanagementsystem.dao.UserDAO;
import com.example.payrollmanagementsystem.model.Employee;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class EmployeeController {

    @FXML
    private VBox formPane;
    @FXML
    private TextField employeeNameField;
    @FXML
    private TextField employeeDepartmentField;
    @FXML
    private TextField employeePositionField;
    @FXML
    private TextField employeeSalaryField;
    @FXML
    private TextField employeeIdField;
    @FXML
    private TableView<Employee> employeeTableView;
    @FXML
    private TableColumn<Employee, Integer> employeeIdColumn;
    @FXML
    private TableColumn<Employee, String> employeeNameColumn;
    @FXML
    private TableColumn<Employee, String> employeeDepartmentColumn;
    @FXML
    private TableColumn<Employee, String> employeePositionColumn;
    @FXML
    private TableColumn<Employee, Double> employeeSalaryColumn;
    @FXML
    private ComboBox<Integer> userIdComboBox;
    @FXML
    private Button backBtn;

    private final EmployeeDAO employeeDAO;
    private final UserDAO userDAO;

    public EmployeeController() {
        this.employeeDAO = new EmployeeDAO();
        this.userDAO = new UserDAO();
    }

    @FXML
    public void initialize() {
        initializeTableColumns();
        loadEmployeeTable();
        setupRowSelection();

        if (userIdComboBox != null) {
            populateUserIds();
        }
    }

    private void initializeTableColumns() {
        employeeIdColumn.setCellValueFactory(cellData -> cellData.getValue().employeeIdProperty().asObject());
        employeeNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        employeeDepartmentColumn.setCellValueFactory(cellData -> cellData.getValue().departmentProperty());
        employeePositionColumn.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
        employeeSalaryColumn.setCellValueFactory(cellData -> cellData.getValue().basicSalaryProperty().asObject());
    }

    private void loadEmployeeTable() {
        try {
            List<Employee> employees = employeeDAO.getAllEmployees();
            employeeTableView.getItems().setAll(employees);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load employee data.");
        }
    }

    private void setupRowSelection() {
        employeeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedEmployee) -> {
            if (selectedEmployee != null) {
                populateFields(selectedEmployee);
            }
        });
    }

    private void populateFields(Employee employee) {
        employeeNameField.setText(employee.getName());
        employeeDepartmentField.setText(employee.getDepartment());
        employeePositionField.setText(employee.getPosition());
        employeeSalaryField.setText(String.valueOf(employee.getBasicSalary()));
        employeeIdField.setText(String.valueOf(employee.getEmployeeId()));

        if (userIdComboBox != null) {
            userIdComboBox.setValue(employee.getUserId());
        }
    }

    @FXML
    private void handleAddEmployee(ActionEvent event) {
        formPane.setVisible(true);
        formPane.setManaged(true);
        clearFields();
        employeeIdField.clear(); // Ensure ID field is clear for new employee
    }

    @FXML
    private void handleUpdateEmployee(ActionEvent event) {
        Employee selectedEmployee = employeeTableView.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an employee to update.");
            return;
        }
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    @FXML
    private void handleSaveEmployee(ActionEvent event) {
        if (!validateInput()) return;

        try {
            if (employeeIdField.getText().isEmpty()) {
                // Adding new employee
                Employee employee = new Employee(
                        employeeNameField.getText(),
                        employeeDepartmentField.getText(),
                        employeePositionField.getText(),
                        Double.parseDouble(employeeSalaryField.getText()),
                        40 // Default working hours
                );

                if (userIdComboBox.getValue() != null) {
                    employee.setUserId(userIdComboBox.getValue());
                }

                int generatedId = employeeDAO.addEmployee(employee);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee added successfully with ID: " + generatedId);
            } else {
                // Updating existing employee
                Employee employee = new Employee(
                        Integer.parseInt(employeeIdField.getText()),
                        employeeNameField.getText(),
                        employeeDepartmentField.getText(),
                        employeePositionField.getText(),
                        Double.parseDouble(employeeSalaryField.getText()),
                        40
                );

                if (userIdComboBox.getValue() != null) {
                    employee.setUserId(userIdComboBox.getValue());
                }

                employeeDAO.updateEmployee(employee);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee updated successfully.");
            }

            loadEmployeeTable();
            formPane.setVisible(false);
            formPane.setManaged(false);
            clearFields();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save employee: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteEmployee(ActionEvent event) {
        Employee selectedEmployee = employeeTableView.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an employee to delete.");
            return;
        }

        try {
            employeeDAO.deleteEmployee(selectedEmployee.getEmployeeId());
            loadEmployeeTable();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Employee deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not delete employee.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        formPane.setVisible(false);
        formPane.setManaged(false);
        clearFields();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/payrollmanagementsystem/fxml/AdminDashboardView.fxml"));
            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not return to dashboard.");
        }
    }

    private void populateUserIds() {
        try {
            List<Integer> userIds = userDAO.getAllUserIds();
            userIdComboBox.getItems().clear();
            userIdComboBox.getItems().addAll(userIds);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load user IDs.");
        }
    }

    private boolean validateInput() {
        if (employeeNameField.getText().isEmpty() ||
                employeeDepartmentField.getText().isEmpty() ||
                employeePositionField.getText().isEmpty() ||
                employeeSalaryField.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Missing Fields", "Please fill in all fields.");
            return false;
        }

        try {
            Double.parseDouble(employeeSalaryField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Salary must be a valid number.");
            return false;
        }

        return true;
    }

    private void clearFields() {
        employeeNameField.clear();
        employeeDepartmentField.clear();
        employeePositionField.clear();
        employeeSalaryField.clear();
        employeeIdField.clear();

        if (userIdComboBox != null) {
            userIdComboBox.getSelectionModel().clearSelection();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
