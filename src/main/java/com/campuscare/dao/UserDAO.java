package com.campuscare.dao;

import com.campuscare.model.User;
import com.campuscare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findByEmail(String email) {
        String sql = "SELECT u.*, d.department_name FROM users u " +
                     "LEFT JOIN departments d ON u.department_id = d.department_id " +
                     "WHERE LOWER(u.email) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findById(int userId) {
        String sql = "SELECT u.*, d.department_name FROM users u " +
                     "LEFT JOIN departments d ON u.department_id = d.department_id " +
                     "WHERE u.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?) LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean studentIdExists(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) return false;
        String sql = "SELECT 1 FROM users WHERE LOWER(student_id) = LOWER(?) LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createUser(User user) {
        String sql = "INSERT INTO users (full_name, email, student_id, academic_branch, department_id, course_year, phone, password_hash, role, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail().trim().toLowerCase());
            ps.setString(3, user.getStudentId() != null && !user.getStudentId().trim().isEmpty() ? user.getStudentId().trim() : null);
            ps.setString(4, user.getAcademicBranch() != null && !user.getAcademicBranch().trim().isEmpty() ? user.getAcademicBranch().trim() : null);
            if (user.getDepartmentId() != null && user.getDepartmentId() > 0) {
                ps.setInt(5, user.getDepartmentId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setString(6, user.getCourseYear());
            ps.setString(7, user.getPhone());
            ps.setString(8, user.getPasswordHash());
            ps.setString(9, user.getRole() != null ? user.getRole().toUpperCase() : "STUDENT");
            ps.setBoolean(10, user.isActive());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateProfile(User user) {
        String sql = "UPDATE users SET full_name = ?, phone = ?, course_year = ?, academic_branch = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getCourseYear());
            ps.setString(4, user.getAcademicBranch());
            ps.setInt(5, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(int userId, String newHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean toggleUserStatus(int userId, boolean active) {
        String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<User> findAllStaff() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, d.department_name FROM users u " +
                     "LEFT JOIN departments d ON u.department_id = d.department_id " +
                     "WHERE u.role IN ('STAFF', 'ADMIN') AND u.is_active = TRUE ORDER BY u.full_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> findAllStaffMembers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, d.department_name FROM users u " +
                     "LEFT JOIN departments d ON u.department_id = d.department_id " +
                     "WHERE u.role = 'STAFF' ORDER BY d.department_name, u.full_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> findStaffByDepartment(int departmentId) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, d.department_name FROM users u " +
                     "LEFT JOIN departments d ON u.department_id = d.department_id " +
                     "WHERE u.role IN ('STAFF', 'ADMIN') AND (u.department_id = ? OR u.role = 'ADMIN') AND u.is_active = TRUE ORDER BY u.full_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setStudentId(rs.getString("student_id"));
        u.setAcademicBranch(rs.getString("academic_branch"));
        int deptId = rs.getInt("department_id");
        u.setDepartmentId(rs.wasNull() ? null : deptId);
        u.setDepartmentName(rs.getString("department_name"));
        u.setCourseYear(rs.getString("course_year"));
        u.setPhone(rs.getString("phone"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        u.setActive(rs.getBoolean("is_active"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }
}
