package com.example.payrollmanagementsystem.controller;

import com.example.payrollmanagementsystem.dao.*;
import com.example.payrollmanagementsystem.model.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.*;
import java.util.*;

public class SalaryController {

    // Form Controls
    @FXML private ComboBox<Employee> employeeComboBox;
    @FXML private TextField basicSalaryField, regularHoursField;
    @FXML private CheckBox overtimeCheckbox;
    @FXML private HBox overtimeFieldsContainer;
    @FXML private TextField overtimeHoursField, overtimeMinutesField;
    @FXML private ComboBox<String> deductionTypeComboBox;
    @FXML private ListView<String> deductionsListView;
    @FXML private DatePicker salaryMonthPicker; // Added DatePicker

    // Result Labels
    @FXML private Label automaticDeductionsLabel, additionalDeductionsLabel;
    @FXML private Label netSalaryLabel, summaryLabel;

    // Buttons
    @FXML private Button backBtn, calculateBtn, saveBtn, addDeductionBtn;

    // Data
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final SalaryDAO salaryDAO = new SalaryDAO();
    private double basicSalary = 0;
    private double overtimePay = 0;
    private double automaticDeductions = 0;
    private double additionalDeductionsTotal = 0;
    private final Map<String, Double> deductionPresets = Map.of(
            "Tax (15%)", 0.15,
            "Insurance (5%)", 0.05,
            "Loan (M200)", 200.0,
            "Advance (M150)", 150.0,
            "Penalty (M100)", 100.0,
            "Union Dues (M50)", 50.0,
            "Health Fund (M75)", 75.0,
            "Parking Fee (M30)", 30.0
    );

    @FXML
    public void initialize() {
        setupEmployeeComboBox();
        setupDeductionComboBox();
        setupFormValidation();
        setupBackButton();
        setupButtonActions();
        overtimeFieldsContainer.managedProperty().bind(overtimeCheckbox.selectedProperty());
        overtimeFieldsContainer.visibleProperty().bind(overtimeCheckbox.selectedProperty());
    }

    private void setupEmployeeComboBox() {
        try {
            employeeComboBox.setItems(FXCollections.observableArrayList(employeeDAO.getAllEmployees()));
            employeeComboBox.setConverter(new StringConverter<Employee>() {
                @Override public String toString(Employee e) {
                    return e != null ? e.getName() + " (" + e.getDepartment() + ")" : "";
                }
                @Override public Employee fromString(String s) { return null; }
            });

            employeeComboBox.valueProperty().addListener((obs, oldVal, employee) -> {
                if (employee != null) {
                    basicSalary = employee.getBasicSalary();
                    basicSalaryField.setText(String.format("M%.2f", basicSalary));
                    regularHoursField.setText(String.valueOf(employee.getWorkingHours()));
                    calculateAutomaticDeductions();
                    updateResults();
                }
            });
        } catch (Exception e) {
            showAlert("Error", "Failed to load employees: " + e.getMessage());
        }
    }

    private void setupDeductionComboBox() {
        deductionTypeComboBox.setItems(FXCollections.observableArrayList(deductionPresets.keySet()));
    }

    private void setupFormValidation() {
        // Numeric validation for overtime fields
        overtimeHoursField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                overtimeHoursField.setText(oldVal);
            }
        });

        overtimeMinutesField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[0-5]?\\d?")) {
                overtimeMinutesField.setText(oldVal);
            }
        });
    }

    private void setupBackButton() {
        backBtn.setOnAction(e -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/payrollmanagementsystem/fxml/AdminDashboardView.fxml"));
                Stage stage = (Stage) backBtn.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException ex) {
                showAlert("Error", "Failed to load dashboard: " + ex.getMessage());
            }
        });
    }

    private void setupButtonActions() {
        calculateBtn.setOnAction(e -> handleCalculate());
        saveBtn.setOnAction(e -> handleSave());
        addDeductionBtn.setOnAction(e -> handleAddDeduction());
    }

    @FXML
    private void handleCalculate() {
        if (!validateInputs()) return;

        calculateOvertime();
        calculateAutomaticDeductions();
        updateResults();
    }

    @FXML
    private void handleAddDeduction() {
        String selected = deductionTypeComboBox.getValue();
        if (selected == null) {
            showAlert("Error", "Please select a deduction type");
            return;
        }

        double amount = calculateDeductionAmount(selected);
        deductionsListView.getItems().add(String.format("%s: M%.2f", selected, amount));
        additionalDeductionsTotal += amount;
        updateResults();
    }

    private double calculateDeductionAmount(String deduction) {
        if (deduction.contains("Tax")) return basicSalary * deductionPresets.get(deduction);
        if (deduction.contains("Insurance")) return basicSalary * deductionPresets.get(deduction);
        return deductionPresets.getOrDefault(deduction, 0.0);
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        if (employeeComboBox.getValue() == null) {
            errors.append("- Please select an employee\n");
        }

        if (salaryMonthPicker.getValue() == null) {
            errors.append("- Please select the salary month\n");
        }

        if (overtimeCheckbox.isSelected()) {
            if (overtimeHoursField.getText().isEmpty() && overtimeMinutesField.getText().isEmpty()) {
                errors.append("- Please enter overtime hours/minutes\n");
            } else {
                try {
                    int hours = overtimeHoursField.getText().isEmpty() ? 0 : Integer.parseInt(overtimeHoursField.getText());
                    int minutes = overtimeMinutesField.getText().isEmpty() ? 0 : Integer.parseInt(overtimeMinutesField.getText());
                    if (minutes > 59) {
                        errors.append("- Minutes must be between 0-59\n");
                    }
                } catch (NumberFormatException e) {
                    errors.append("- Invalid overtime values\n");
                }
            }
        }

        if (errors.length() > 0) {
            showAlert("Validation Error", errors.toString());
            return false;
        }
        return true;
    }

    private void calculateOvertime() {
        try {
            int hours = overtimeHoursField.getText().isEmpty() ? 0 : Integer.parseInt(overtimeHoursField.getText());
            int minutes = overtimeMinutesField.getText().isEmpty() ? 0 : Integer.parseInt(overtimeMinutesField.getText());
            overtimePay = (hours + (minutes / 60.0)) * 50; // M50/hour rate
        } catch (NumberFormatException e) {
            overtimePay = 0;
        }
    }

    private void calculateAutomaticDeductions() {
        automaticDeductions = (basicSalary * 0.15) + (basicSalary * 0.05); // 15% tax + 5% insurance
    }

    private void updateResults() {
        double grossSalary = basicSalary + overtimePay;
        double totalDeductions = automaticDeductions + additionalDeductionsTotal;
        double netSalary = grossSalary - totalDeductions;

        automaticDeductionsLabel.setText(String.format("Tax & Insurance: M%.2f", automaticDeductions));
        additionalDeductionsLabel.setText(String.format("Additional Deductions: M%.2f", additionalDeductionsTotal));
        netSalaryLabel.setText(String.format("M%.2f", netSalary));

        summaryLabel.setText(String.format(
                "Salary Summary:\n\nGross Salary: M%.2f\n- Automatic Deductions: M%.2f\n- Additional Deductions: M%.2f\n\nNet Payable: M%.2f",
                grossSalary, automaticDeductions, additionalDeductionsTotal, netSalary
        ));
    }

    @FXML
    private void handleSave() {
        if (!validateInputs()) return;

        try {
            Employee employee = employeeComboBox.getValue();
            LocalDate selectedDate = salaryMonthPicker.getValue();
            YearMonth salaryMonth = YearMonth.from(selectedDate);

            Salary salary = new Salary();
            salary.setEmployeeId(employee.getEmployeeId());
            salary.setBasicSalary(basicSalary);
            salary.setOvertimePay(overtimePay);
            salary.setGrossSalary(basicSalary + overtimePay);
            salary.setNetSalary(basicSalary + overtimePay - automaticDeductions - additionalDeductionsTotal);
            salary.setPaymentDate(LocalDate.now());
            salary.setPayPeriodStart(LocalDate.now().withDayOfMonth(1));
            salary.setPayPeriodEnd(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
            salary.setSalaryMonth(salaryMonth); // Set the salary month

            salaryDAO.addSalary(salary);
            showAlert("Success", "Salary record saved successfully for " + employee.getName() + " for " + salaryMonth);
            resetForm();
        } catch (Exception e) {
            showAlert("Error", "Failed to save salary record: " + e.getMessage());
        }
    }

    private void resetForm() {
        employeeComboBox.getSelectionModel().clearSelection();
        basicSalaryField.clear();
        regularHoursField.clear();
        overtimeCheckbox.setSelected(false);
        overtimeHoursField.clear();
        overtimeMinutesField.clear();
        deductionsListView.getItems().clear();
        salaryMonthPicker.setValue(null); // Clear the date picker
        additionalDeductionsTotal = 0;
        updateResults();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}