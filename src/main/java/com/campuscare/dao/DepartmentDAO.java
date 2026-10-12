package com.campuscare.dao;

import com.campuscare.model.Department;
import com.campuscare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public List<Department> findAll(boolean onlyActive) {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT d.*, " +
                     "(SELECT COUNT(*) FROM complaints c WHERE c.assigned_department_id = d.department_id) AS complaint_count, " +
                     "(SELECT COUNT(*) FROM users u WHERE u.department_id = d.department_id AND u.role IN ('STAFF','ADMIN')) AS staff_count " +
                     "FROM departments d " +
                     (onlyActive ? "WHERE d.is_active = TRUE " : "") +
                     "ORDER BY d.department_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Department d = mapDepartment(rs);
                d.setComplaintCount(rs.getInt("complaint_count"));
                d.setStaffCount(rs.getInt("staff_count"));
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Department findById(int id) {
        String sql = "SELECT d.*, " +
                     "(SELECT COUNT(*) FROM complaints c WHERE c.assigned_department_id = d.department_id) AS complaint_count, " +
                     "(SELECT COUNT(*) FROM users u WHERE u.department_id = d.department_id) AS staff_count " +
                     "FROM departments d WHERE d.department_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Department d = mapDepartment(rs);
                    d.setComplaintCount(rs.getInt("complaint_count"));
                    d.setStaffCount(rs.getInt("staff_count"));
                    return d;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createDepartment(Department d) {
        String sql = "INSERT INTO departments (department_name, description, contact_email, contact_phone, is_active) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getDepartmentName().trim());
            ps.setString(2, d.getDescription());
            ps.setString(3, d.getContactEmail());
            ps.setString(4, d.getContactPhone());
            ps.setBoolean(5, d.isActive());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        d.setDepartmentId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDepartment(Department d) {
        String sql = "UPDATE departments SET department_name = ?, description = ?, contact_email = ?, contact_phone = ?, is_active = ? " +
                     "WHERE department_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getDepartmentName().trim());
            ps.setString(2, d.getDescription());
            ps.setString(3, d.getContactEmail());
            ps.setString(4, d.getContactPhone());
            ps.setBoolean(5, d.isActive());
            ps.setInt(6, d.getDepartmentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Department mapDepartment(ResultSet rs) throws SQLException {
        Department d = new Department();
        d.setDepartmentId(rs.getInt("department_id"));
        d.setDepartmentName(rs.getString("department_name"));
        d.setDescription(rs.getString("description"));
        d.setContactEmail(rs.getString("contact_email"));
        d.setContactPhone(rs.getString("contact_phone"));
        d.setActive(rs.getBoolean("is_active"));
        d.setCreatedAt(rs.getTimestamp("created_at"));
        return d;
    }
}
