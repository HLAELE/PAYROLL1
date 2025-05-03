package com.example.payrollmanagementsystem.controller;

import com.example.payrollmanagementsystem.dao.UserDAO;
import com.example.payrollmanagementsystem.model.User;
import com.example.payrollmanagementsystem.util.PasswordHashing;  // Import the PasswordHashing utility
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Button registerButton;

    @FXML
    private Hyperlink loginLink;

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList("Admin", "Employee"));
        roleComboBox.setPromptText("Select Role");
    }

    @FXML
    private void handleRegisterAction(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Passwords do not match.");
            return;
        }

        // Hash the password before saving
        String hashedPassword = PasswordHashing.hashPassword(password);
        boolean success = userDAO.registerUser(new User(username, hashedPassword, role));
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "User registered successfully!");
            navigateToLogin(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Registration failed. Username might already exist.");
        }
    }

    @FXML
    private void handleLoginLinkAction(ActionEvent event) {
        navigateToLogin(event);
    }

    private void navigateToLogin(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/example/payrollmanagementsystem/fxml/LoginView.fxml"));
            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to load login page.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
