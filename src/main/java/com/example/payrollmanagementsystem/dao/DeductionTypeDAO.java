package com.example.payrollmanagementsystem.dao;

import com.example.payrollmanagementsystem.model.DeductionType;
import com.example.payrollmanagementsystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeductionTypeDAO {
    public List<DeductionType> getAllDeductionTypes() throws SQLException {
        List<DeductionType> deductionTypes = new ArrayList<>();
        String sql = "SELECT * FROM deduction_types ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                DeductionType type = new DeductionType();
                type.setDeductionTypeId(rs.getInt("deduction_type_id"));
                type.setName(rs.getString("name"));
                type.setDescription(rs.getString("description"));

                deductionTypes.add(type);
            }
        }

        return deductionTypes;
    }

    public DeductionType getDeductionTypeById(int id) throws SQLException {
        String sql = "SELECT * FROM deduction_types WHERE deduction_type_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                DeductionType type = new DeductionType();
                type.setDeductionTypeId(rs.getInt("deduction_type_id"));
                type.setName(rs.getString("name"));
                type.setDescription(rs.getString("description"));

                return type;
            }
        }

        return null;
    }

    public void addDeductionType(DeductionType deductionType) throws SQLException {
        String sql = "INSERT INTO deduction_types (name, description) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, deductionType.getName());
            stmt.setString(2, deductionType.getDescription());
            stmt.executeUpdate();
        }
    }

    public void updateDeductionType(DeductionType deductionType) throws SQLException {
        String sql = "UPDATE deduction_types SET name = ?, description = ? WHERE deduction_type_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, deductionType.getName());
            stmt.setString(2, deductionType.getDescription());
            stmt.setInt(3, deductionType.getDeductionTypeId());
            stmt.executeUpdate();
        }
    }

    public void deleteDeductionType(int deductionTypeId) throws SQLException {
        String sql = "DELETE FROM deduction_types WHERE deduction_type_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, deductionTypeId);
            stmt.executeUpdate();
        }
    }
}