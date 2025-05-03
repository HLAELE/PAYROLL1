package com.example.payrollmanagementsystem.util;

import com.example.payrollmanagementsystem.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PasswordUpdater {

    public static void main(String[] args) {
        String usernameToUpdate = "Typex";
        String plainPassword = "Enigma";
        String hashedPassword = PasswordHashing.hashPassword(plainPassword);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET password = ? WHERE username = ?")) {
            preparedStatement.setString(1, hashedPassword);
            preparedStatement.setString(2, usernameToUpdate);
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Password for user '" + usernameToUpdate + "' updated successfully.");
            } else {
                System.out.println("User '" + usernameToUpdate + "' not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}