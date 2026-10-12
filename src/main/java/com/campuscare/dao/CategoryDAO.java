package com.campuscare.dao;

import com.campuscare.model.Category;
import com.campuscare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> findAll(boolean onlyActive) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT c.*, d.department_name AS default_department_name " +
                     "FROM categories c " +
                     "LEFT JOIN departments d ON c.default_department_id = d.department_id " +
                     (onlyActive ? "WHERE c.is_active = TRUE " : "") +
                     "ORDER BY c.category_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Category findById(int id) {
        String sql = "SELECT c.*, d.department_name AS default_department_name " +
                     "FROM categories c " +
                     "LEFT JOIN departments d ON c.default_department_id = d.department_id " +
                     "WHERE c.category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCategory(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createCategory(Category c) {
        String sql = "INSERT INTO categories (category_name, description, default_department_id, is_active) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getCategoryName());
            ps.setString(2, c.getDescription());
            if (c.getDefaultDepartmentId() != null && c.getDefaultDepartmentId() > 0) {
                ps.setInt(3, c.getDefaultDepartmentId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setBoolean(4, c.isActive());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setCategoryId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Category mapCategory(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setCategoryId(rs.getInt("category_id"));
        c.setCategoryName(rs.getString("category_name"));
        c.setDescription(rs.getString("description"));
        int deptId = rs.getInt("default_department_id");
        c.setDefaultDepartmentId(rs.wasNull() ? null : deptId);
        c.setDefaultDepartmentName(rs.getString("default_department_name"));
        c.setActive(rs.getBoolean("is_active"));
        return c;
    }
}
