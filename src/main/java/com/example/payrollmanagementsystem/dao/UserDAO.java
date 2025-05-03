package com.example.payrollmanagementsystem.dao;

import com.example.payrollmanagementsystem.model.User;
import com.example.payrollmanagementsystem.util.DBConnection;
import com.example.payrollmanagementsystem.util.PasswordHashing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private static final String GET_USER_BY_USERNAME =
            "SELECT u.*, e.employee_id FROM users u LEFT JOIN employees e ON u.user_id = e.user_id WHERE u.username = ? LIMIT 1";
    private static final String INSERT_USER = "INSERT INTO users (username, passwordHash, role) VALUES (?, ?, ?)";
    private static final String GET_ALL_USERS = "SELECT u.*, e.employee_id FROM users u LEFT JOIN employees e ON u.user_id = e.user_id";
    private static final String UPDATE_USER = "UPDATE users SET username = ?, passwordHash = ?, role = ? WHERE user_id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    private static final String GET_ALL_USER_IDS = "SELECT user_id FROM users";  // New constant

    public User getUserByUsername(String username) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_USER_BY_USERNAME)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("passwordHash"),
                        rs.getString("role"),
                        rs.getInt("employee_id")  // Added employeeId
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }
        return null;
    }

    public boolean verifyUserCredentials(String username, String password) {
        User user = getUserByUsername(username);
        if (user == null) {
            System.out.println("User not found: " + username);
            return false;
        }

        System.out.println("Stored hash: " + user.getPasswordHash());
        System.out.println("Input password: " + password);

        boolean verified = PasswordHashing.verifyPassword(password, user.getPasswordHash());
        System.out.println("Verification result: " + verified);
        return verified;
    }

    public boolean registerUser(User user) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash()); // Now contains the hash
            stmt.setString(3, user.getRole());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_USERS);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("passwordHash"),
                        rs.getString("role"),
                        rs.getInt("employee_id")  // Added employeeId
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean updateUser(User user) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_USER)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole());
            stmt.setInt(4, user.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USER)) {

            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // New method to fetch all user IDs
    public List<Integer> getAllUserIds() throws SQLException {
        List<Integer> userIds = new ArrayList<>();
        String sql = "SELECT user_id FROM users";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                userIds.add(rs.getInt("user_id"));
            }
        }
        return userIds;
    }
}
