package com.example.payrollmanagementsystem.controller;

import com.example.payrollmanagementsystem.dao.UserDAO;
import com.example.payrollmanagementsystem.model.User;
import com.example.payrollmanagementsystem.util.PasswordHashing;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML private VBox loginForm;
    @FXML private VBox registerForm;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerLink;
    @FXML private Label errorMessage;
    @FXML private Label successMessage;

    @FXML private TextField registerUsernameField;
    @FXML private PasswordField registerPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> registerRoleComboBox;
    @FXML private Button registerButton;
    @FXML private Hyperlink backToLoginLink;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll("Admin", "Employee");
        registerRoleComboBox.getItems().addAll("Admin", "Employee");
        registerForm.setVisible(false); // Initially hide registration form
        errorMessage.setVisible(false); // Hide error initially
        successMessage.setVisible(false); // Hide success initially
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String selectedRole = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || selectedRole == null) {
            showError("Please fill all fields");
            return;
        }

        try {
            User user = userDAO.getUserByUsername(username);

            if (user == null || user.getPasswordHash() == null) {
                showError("Invalid credentials");
                return;
            }

            // Verify password
            if (!userDAO.verifyUserCredentials(username, password)) {
                showError("Invalid credentials");
                return;
            }

            if (!selectedRole.equals(user.getRole())) {
                showError("Please select your correct role: " + user.getRole());
                return;
            }

            redirectBasedOnRole(user);

        } catch (Exception e) {
            showError("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void redirectBasedOnRole(User user) {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader();

            if ("Admin".equals(user.getRole())) {
                loader.setLocation(getClass().getResource("/com/example/payrollmanagementsystem/fxml/AdminDashboardView.fxml"));
            } else {
                loader.setLocation(getClass().getResource("/com/example/payrollmanagementsystem/fxml/EmployeeDashboardView.fxml"));
            }

            Parent root = loader.load();

            // In your login controller where you handle successful login
            if ("Employee".equals(user.getRole())) {
                EmployeeDashboardController dashboardController = loader.getController();
                dashboardController.setEmployeeId(user.getEmployeeId()); // Make sure you're passing the correct ID
            }

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load dashboard");
        }
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(true);
        successMessage.setVisible(false);
    }

    private void showSuccess(String message) {
        successMessage.setText(message);
        successMessage.setVisible(true);
        errorMessage.setVisible(false);
    }

    @FXML
    private void handleRegisterLinkAction(ActionEvent event) {
        loginForm.setVisible(false);
        registerForm.setVisible(true);
    }

    @FXML
    private void handleBackToLoginLinkAction(ActionEvent event) {
        registerForm.setVisible(false);
        loginForm.setVisible(true);
        errorMessage.setVisible(false);
        successMessage.setVisible(false);
    }

    @FXML
    private void handleRegisterButtonAction(ActionEvent event) {
        String username = registerUsernameField.getText().trim();
        String password = registerPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String selectedRole = registerRoleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || selectedRole == null) {
            showError("Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        User existingUser = userDAO.getUserByUsername(username);
        if (existingUser != null) {
            showError("Username already exists.");
            return;
        }

        String passwordHash = PasswordHashing.hashPassword(password);
        User newUser = new User(username, password, selectedRole); // Raw password passed initially
        newUser.setPasswordHash(passwordHash);

        boolean registrationSuccess = userDAO.registerUser(newUser);

        if (registrationSuccess) {
            showSuccess("Registration successful!");
            registerUsernameField.clear();
            registerPasswordField.clear();
            confirmPasswordField.clear();
            registerRoleComboBox.getSelectionModel().clearSelection();

            loginForm.setVisible(true);
            registerForm.setVisible(false);
        } else {
            showError("Registration failed. Please try again.");
        }
    }
}
