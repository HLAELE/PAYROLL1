package com.example.payrollmanagementsystem.controller;

import com.example.payrollmanagementsystem.dao.UserDAO;
import com.example.payrollmanagementsystem.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserManagementController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField roleField;
    @FXML private Button addUserButton;
    @FXML private Button updateUserButton;
    @FXML private Button deleteUserButton;
    @FXML private Button backButton;

    private final UserDAO userDAO = new UserDAO();
    private User selectedUser;

    @FXML
    public void initialize() {
        // Initialize table columns
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());

        loadUsers();

        // Set up table selection listener
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedUser = newSelection;
            if (selectedUser != null) {
                usernameField.setText(selectedUser.getUsername());
                roleField.setText(selectedUser.getRole());
            }
        });
    }

    @FXML
    private void handleAddUser(MouseEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleField.getText().trim();

        if (!validateInputs(username, password, role)) {
            return;
        }

        User newUser = new User(username, password, role);
        if (userDAO.registerUser(newUser)) {
            showAlert(AlertType.INFORMATION, "Success", "User added successfully!");
            loadUsers();
            clearFields();
        } else {
            showAlert(AlertType.ERROR, "Error", "Failed to add user. Username might already exist.");
        }
    }

    @FXML
    private void handleUpdateUser(MouseEvent event) {
        if (selectedUser == null) {
            showAlert(AlertType.WARNING, "No Selection", "Please select a user to update.");
            return;
        }

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleField.getText().trim();

        if (!validateInputs(username, password, role)) {
            return;
        }

        User updatedUser = new User(username, password, role);
        updatedUser.setId(selectedUser.getId());

        if (userDAO.updateUser(updatedUser)) {
            showAlert(AlertType.INFORMATION, "Success", "User updated successfully!");
            loadUsers();
            clearFields();
        } else {
            showAlert(AlertType.ERROR, "Error", "Failed to update user.");
        }
    }

    @FXML
    private void handleDeleteUser(MouseEvent event) {
        if (selectedUser == null) {
            showAlert(AlertType.WARNING, "No Selection", "Please select a user to delete.");
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete User: " + selectedUser.getUsername());
        alert.setContentText("Are you sure you want to delete this user?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (userDAO.deleteUser(selectedUser.getId())) {
                showAlert(AlertType.INFORMATION, "Success", "User deleted successfully!");
                loadUsers();
                clearFields();
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to delete user.");
            }
        }
    }

    private boolean validateInputs(String username, String password, String role) {
        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            showAlert(AlertType.WARNING, "Validation Error", "All fields must be filled out.");
            return false;
        }

        if (!username.matches("[a-zA-Z]+")) {
            showAlert(AlertType.WARNING, "Invalid Username", "Username must contain only letters.");
            return false;
        }

        if (!role.matches("Admin|Employee")) {
            showAlert(AlertType.WARNING, "Invalid Role", "Role must be either 'Admin' or 'Employee'.");
            return false;
        }

        return true;
    }

    private void loadUsers() {
        List<User> users = userDAO.getAllUsers();
        userTable.getItems().setAll(users);
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        roleField.clear();
        selectedUser = null;
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBackButtonClick(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/payrollmanagementsystem/fxml/AdminDashboardView.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}