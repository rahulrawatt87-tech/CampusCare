package com.campuscare.dao;

import com.campuscare.model.Complaint;
import com.campuscare.model.DashboardStats;
import com.campuscare.util.DBConnection;

import java.sql.*;
import java.time.Year;
import java.util.*;

public class ComplaintDAO {

    /**
     * Generates a unique, sequential ticket number: CC-{YEAR}-{000000}
     */
    public synchronized String generateNextTicketNumber(Connection conn) throws SQLException {
        int currentYear = Year.now().getValue();
        String prefix = "CC-" + currentYear + "-";
        String sql = "SELECT ticket_number FROM complaints WHERE ticket_number LIKE ? ORDER BY complaint_id DESC LIMIT 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String lastTicket = rs.getString("ticket_number");
                    try {
                        String numPart = lastTicket.substring(prefix.length());
                        int nextNum = Integer.parseInt(numPart) + 1;
                        return String.format("%s%06d", prefix, nextNum);
                    } catch (Exception ignored) {}
                }
            }
        }
        return String.format("%s000101", prefix);
    }

    public boolean createComplaint(Complaint c, Connection conn) throws SQLException {
        boolean manageConn = (conn == null);
        Connection activeConn = manageConn ? DBConnection.getConnection() : conn;

        try {
            if (c.getTicketNumber() == null || c.getTicketNumber().trim().isEmpty()) {
                c.setTicketNumber(generateNextTicketNumber(activeConn));
            }

            String sql = "INSERT INTO complaints (ticket_number, user_id, title, category_id, location, description, " +
                         "priority, status, assigned_department_id, assigned_staff_id, created_at, updated_at) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

            try (PreparedStatement ps = activeConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, c.getTicketNumber());
                ps.setInt(2, c.getUserId());
                ps.setString(3, c.getTitle());
                ps.setInt(4, c.getCategoryId());
                ps.setString(5, c.getLocation());
                ps.setString(6, c.getDescription());
                ps.setString(7, c.getPriority());
                ps.setString(8, c.getStatus() != null ? c.getStatus() : "SUBMITTED");
                
                if (c.getAssignedDepartmentId() != null && c.getAssignedDepartmentId() > 0) {
                    ps.setInt(9, c.getAssignedDepartmentId());
                } else {
                    ps.setNull(9, Types.INTEGER);
                }

                if (c.getAssignedStaffId() != null && c.getAssignedStaffId() > 0) {
                    ps.setInt(10, c.getAssignedStaffId());
                } else {
                    ps.setNull(10, Types.INTEGER);
                }

                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            c.setComplaintId(rs.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } finally {
            if (manageConn && activeConn != null) {
                activeConn.close();
            }
        }
        return false;
    }

    public Complaint findById(int complaintId) {
        String sql = baseQuery() + " WHERE c.complaint_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, complaintId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapComplaint(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Complaint findByTicketNumber(String ticketNumber) {
        String sql = baseQuery() + " WHERE LOWER(c.ticket_number) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticketNumber.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapComplaint(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Complaint> findStudentComplaints(int userId, String status, Integer categoryId, String search, int offset, int limit) {
        List<Complaint> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(baseQuery()).append(" WHERE c.user_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append(" AND c.status = ? ");
            params.add(status.trim());
        }
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND c.category_id = ? ");
            params.add(categoryId);
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (LOWER(c.title) LIKE ? OR LOWER(c.ticket_number) LIKE ? OR LOWER(c.location) LIKE ?) ");
            String s = "%" + search.trim().toLowerCase() + "%";
            params.add(s);
            params.add(s);
            params.add(s);
        }

        sql.append(" ORDER BY c.created_at DESC LIMIT ? OFFSET ?");
        params.add(limit > 0 ? limit : 50);
        params.add(offset >= 0 ? offset : 0);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapComplaint(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countStudentComplaints(int userId, String status, Integer categoryId, String search) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM complaints c WHERE c.user_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append(" AND c.status = ? ");
            params.add(status.trim());
        }
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND c.category_id = ? ");
            params.add(categoryId);
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (LOWER(c.title) LIKE ? OR LOWER(c.ticket_number) LIKE ? OR LOWER(c.location) LIKE ?) ");
            String s = "%" + search.trim().toLowerCase() + "%";
            params.add(s);
            params.add(s);
            params.add(s);
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Complaint> findComplaints(String status, Integer categoryId, String priority,
                                         Integer deptId, String search, Boolean overdueOnly,
                                         int offset, int limit) {
        List<Complaint> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(baseQuery()).append(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append(" AND c.status = ? ");
            params.add(status.trim());
        }
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND c.category_id = ? ");
            params.add(categoryId);
        }
        if (priority != null && !priority.trim().isEmpty() && !"ALL".equalsIgnoreCase(priority)) {
            sql.append(" AND c.priority = ? ");
            params.add(priority.trim());
        }
        if (deptId != null && deptId > 0) {
            sql.append(" AND c.assigned_department_id = ? ");
            params.add(deptId);
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (LOWER(c.title) LIKE ? OR LOWER(c.ticket_number) LIKE ? OR LOWER(c.location) LIKE ? OR LOWER(u.full_name) LIKE ?) ");
            String s = "%" + search.trim().toLowerCase() + "%";
            params.add(s);
            params.add(s);
            params.add(s);
            params.add(s);
        }
        if (Boolean.TRUE.equals(overdueOnly)) {
            sql.append(" AND c.status NOT IN ('RESOLVED', 'REJECTED') AND ( " +
                       "(c.priority = 'URGENT' AND TIMESTAMPDIFF(HOUR, c.created_at, NOW()) > 24) OR " +
                       "(c.priority = 'HIGH' AND TIMESTAMPDIFF(HOUR, c.created_at, NOW()) > 48) OR " +
                       "(c.priority = 'MEDIUM' AND TIMESTAMPDIFF(HOUR, c.created_at, NOW()) > 72) OR " +
                       "(c.priority = 'LOW' AND TIMESTAMPDIFF(HOUR, c.created_at, NOW()) > 120)) ");
        }

        sql.append(" ORDER BY c.created_at DESC LIMIT ? OFFSET ?");
        params.add(limit > 0 ? limit : 50);
        params.add(offset >= 0 ? offset : 0);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapComplaint(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countComplaints(String status, Integer categoryId, String priority,
                               Integer deptId, String search, Boolean overdueOnly) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM complaints c " +
                "JOIN users u ON c.user_id = u.user_id " +
                "WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append(" AND c.status = ? ");
            params.add(status.trim());
        }
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND c.category_id = ? ");
            params.add(categoryId);
        }
        if (priority != null && !priority.trim().isEmpty() && !"ALL".equalsIgnoreCase(priority)) {
            sql.append(" AND c.priority = ? ");
            params.add(priority.trim());
        }
        if (deptId != null && deptId > 0) {
            sql.append(" AND c.assigned_department_id = ? ");
            params.add(deptId);
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (LOWER(c.title) LIKE ? OR LOWER(c.ticket_number) LIKE ? OR LOWER(c.location) LIKE ? OR LOWER(u.full_name) LIKE ?) ");
            String s = "%" + search.trim().toLowerCase() + "%";
            params.add(s);
            params.add(s);
            params.add(s);
            params.add(s);
        }
        if (Boolean.TRUE.equals(overdueOnly)) {
            sql.append(" AND c.status NOT IN ('RESOLVED', 'REJECTED') AND ( " +
                       "(c.priority = 'URGENT' AND TIMESTAMPDIFF(HOUR, c.created_at, NOW()) > 24) OR " +
                       "(c.priority = 'HIGH' AND TIMESTAMPDIFF(HOUR, c.created_at, NOW()) > 48) OR " +
                       "(c.priority = 'MEDIUM' AND TIMESTAMPDIFF(HOUR, c.created_at, NOW()) > 72) OR " +
                       "(c.priority = 'LOW' AND TIMESTAMPDIFF(HOUR, c.created_at, NOW()) > 120)) ");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Complaint> findStaffComplaints(int staffUserId, Integer departmentId, String status, String search) {
        List<Complaint> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(baseQuery()).append(" WHERE (c.assigned_staff_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(staffUserId);

        if (departmentId != null && departmentId > 0) {
            sql.append(" OR (c.assigned_department_id = ? AND c.assigned_staff_id IS NULL)) ");
            params.add(departmentId);
        } else {
            sql.append(") ");
        }

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append(" AND c.status = ? ");
            params.add(status.trim());
        }

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (LOWER(c.title) LIKE ? OR LOWER(c.ticket_number) LIKE ? OR LOWER(c.location) LIKE ?) ");
            String s = "%" + search.trim().toLowerCase() + "%";
            params.add(s);
            params.add(s);
            params.add(s);
        }

        sql.append(" ORDER BY c.created_at DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapComplaint(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStatus(int complaintId, String newStatus, String resolutionNotes,
                                String rejectionReason, Timestamp resolvedAt, Connection conn) throws SQLException {
        boolean manageConn = (conn == null);
        Connection activeConn = manageConn ? DBConnection.getConnection() : conn;

        try {
            String sql = "UPDATE complaints SET status = ?, resolution_notes = ?, rejection_reason = ?, " +
                         "resolved_at = ?, updated_at = NOW() WHERE complaint_id = ?";
            try (PreparedStatement ps = activeConn.prepareStatement(sql)) {
                ps.setString(1, newStatus);
                ps.setString(2, resolutionNotes);
                ps.setString(3, rejectionReason);
                ps.setTimestamp(4, resolvedAt);
                ps.setInt(5, complaintId);
                return ps.executeUpdate() > 0;
            }
        } finally {
            if (manageConn && activeConn != null) {
                activeConn.close();
            }
        }
    }

    public boolean updateAssignment(int complaintId, Integer deptId, Integer staffId, Connection conn) throws SQLException {
        boolean manageConn = (conn == null);
        Connection activeConn = manageConn ? DBConnection.getConnection() : conn;

        try {
            String sql = "UPDATE complaints SET assigned_department_id = ?, assigned_staff_id = ?, updated_at = NOW() " +
                         "WHERE complaint_id = ?";
            try (PreparedStatement ps = activeConn.prepareStatement(sql)) {
                if (deptId != null && deptId > 0) ps.setInt(1, deptId); else ps.setNull(1, Types.INTEGER);
                if (staffId != null && staffId > 0) ps.setInt(2, staffId); else ps.setNull(2, Types.INTEGER);
                ps.setInt(3, complaintId);
                return ps.executeUpdate() > 0;
            }
        } finally {
            if (manageConn && activeConn != null) {
                activeConn.close();
            }
        }
    }

    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        try (Connection conn = DBConnection.getConnection()) {
            // General counts
            String sql = "SELECT " +
                    "COUNT(*) AS total, " +
                    "SUM(CASE WHEN status = 'SUBMITTED' THEN 1 ELSE 0 END) AS submitted, " +
                    "SUM(CASE WHEN status = 'UNDER_REVIEW' THEN 1 ELSE 0 END) AS under_review, " +
                    "SUM(CASE WHEN status = 'ASSIGNED' THEN 1 ELSE 0 END) AS assigned, " +
                    "SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS in_progress, " +
                    "SUM(CASE WHEN status = 'RESOLVED' THEN 1 ELSE 0 END) AS resolved, " +
                    "SUM(CASE WHEN status = 'REJECTED' THEN 1 ELSE 0 END) AS rejected, " +
                    "SUM(CASE WHEN status = 'REOPENED' THEN 1 ELSE 0 END) AS reopened, " +
                    "SUM(CASE WHEN priority = 'URGENT' AND status NOT IN ('RESOLVED','REJECTED') THEN 1 ELSE 0 END) AS urgent_open, " +
                    "SUM(CASE WHEN status NOT IN ('RESOLVED','REJECTED') AND ( " +
                    "  (priority='URGENT' AND TIMESTAMPDIFF(HOUR, created_at, NOW()) > 24) OR " +
                    "  (priority='HIGH' AND TIMESTAMPDIFF(HOUR, created_at, NOW()) > 48) OR " +
                    "  (priority='MEDIUM' AND TIMESTAMPDIFF(HOUR, created_at, NOW()) > 72) OR " +
                    "  (priority='LOW' AND TIMESTAMPDIFF(HOUR, created_at, NOW()) > 120) " +
                    ") THEN 1 ELSE 0 END) AS overdue " +
                    "FROM complaints";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalComplaints(rs.getInt("total"));
                    stats.setSubmittedCount(rs.getInt("submitted"));
                    stats.setUnderReviewCount(rs.getInt("under_review"));
                    stats.setAssignedCount(rs.getInt("assigned"));
                    stats.setInProgressCount(rs.getInt("in_progress"));
                    stats.setResolvedCount(rs.getInt("resolved"));
                    stats.setRejectedCount(rs.getInt("rejected"));
                    stats.setReopenedCount(rs.getInt("reopened"));
                    stats.setUrgentCount(rs.getInt("urgent_open"));
                    stats.setOverdueCount(rs.getInt("overdue"));
                }
            }

            // Average resolution time in hours
            String sqlAvg = "SELECT AVG(TIMESTAMPDIFF(HOUR, created_at, resolved_at)) AS avg_hours " +
                            "FROM complaints WHERE status = 'RESOLVED' AND resolved_at IS NOT NULL";
            try (PreparedStatement ps = conn.prepareStatement(sqlAvg);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setAvgResolutionTimeHours(rs.getDouble("avg_hours"));
                }
            }

            // Category breakdown
            String sqlCat = "SELECT cat.category_name, COUNT(c.complaint_id) as count " +
                            "FROM categories cat LEFT JOIN complaints c ON cat.category_id = c.category_id " +
                            "GROUP BY cat.category_name ORDER BY count DESC";
            try (PreparedStatement ps = conn.prepareStatement(sqlCat);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.getCategoryDistribution().put(rs.getString("category_name"), rs.getInt("count"));
                }
            }

            // Status breakdown
            String sqlStat = "SELECT status, COUNT(*) as count FROM complaints GROUP BY status";
            try (PreparedStatement ps = conn.prepareStatement(sqlStat);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.getStatusDistribution().put(rs.getString("status"), rs.getInt("count"));
                }
            }

            // Feedback summary
            String sqlFeed = "SELECT AVG(rating) as avg_rating, COUNT(*) as total_feedback FROM feedback";
            try (PreparedStatement ps = conn.prepareStatement(sqlFeed);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setAvgFeedbackRating(rs.getDouble("avg_rating"));
                    stats.setTotalFeedbackCount(rs.getInt("total_feedback"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public DashboardStats getStudentStats(int userId) {
        DashboardStats stats = new DashboardStats();
        String sql = "SELECT " +
                "COUNT(*) AS total, " +
                "SUM(CASE WHEN status IN ('SUBMITTED','UNDER_REVIEW','ASSIGNED') THEN 1 ELSE 0 END) AS submitted, " +
                "SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS in_progress, " +
                "SUM(CASE WHEN status = 'RESOLVED' THEN 1 ELSE 0 END) AS resolved, " +
                "SUM(CASE WHEN status NOT IN ('RESOLVED','REJECTED') AND ( " +
                "  (priority='URGENT' AND TIMESTAMPDIFF(HOUR, created_at, NOW()) > 24) OR " +
                "  (priority='HIGH' AND TIMESTAMPDIFF(HOUR, created_at, NOW()) > 48) OR " +
                "  (priority='MEDIUM' AND TIMESTAMPDIFF(HOUR, created_at, NOW()) > 72) OR " +
                "  (priority='LOW' AND TIMESTAMPDIFF(HOUR, created_at, NOW()) > 120) " +
                ") THEN 1 ELSE 0 END) AS overdue " +
                "FROM complaints WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalComplaints(rs.getInt("total"));
                    stats.setUnderReviewCount(rs.getInt("submitted"));
                    stats.setInProgressCount(rs.getInt("in_progress"));
                    stats.setResolvedCount(rs.getInt("resolved"));
                    stats.setOverdueCount(rs.getInt("overdue"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    private String baseQuery() {
        return "SELECT c.*, " +
               "u.full_name AS student_name, u.email AS student_email, u.student_id AS student_reg_id, u.phone AS student_phone, " +
               "cat.category_name, " +
               "dept.department_name AS assigned_department_name, " +
               "staff.full_name AS assigned_staff_name, " +
               "CASE WHEN c.status NOT IN ('RESOLVED', 'REJECTED') AND ( " +
               "  (c.priority = 'URGENT' AND TIMESTAMPDIFF(HOUR, c.created_at, NOW()) > 24) OR " +
               "  (c.priority = 'HIGH' AND TIMESTAMPDIFF(HOUR, c.created_at, NOW()) > 48) OR " +
               "  (c.priority = 'MEDIUM' AND TIMESTAMPDIFF(HOUR, c.created_at, NOW()) > 72) OR " +
               "  (c.priority = 'LOW' AND TIMESTAMPDIFF(HOUR, c.created_at, NOW()) > 120) " +
               ") THEN 1 ELSE 0 END AS is_overdue, " +
               "TIMESTAMPDIFF(DAY, c.created_at, NOW()) AS days_open " +
               "FROM complaints c " +
               "JOIN users u ON c.user_id = u.user_id " +
               "JOIN categories cat ON c.category_id = cat.category_id " +
               "LEFT JOIN departments dept ON c.assigned_department_id = dept.department_id " +
               "LEFT JOIN users staff ON c.assigned_staff_id = staff.user_id ";
    }

    private Complaint mapComplaint(ResultSet rs) throws SQLException {
        Complaint c = new Complaint();
        c.setComplaintId(rs.getInt("complaint_id"));
        c.setTicketNumber(rs.getString("ticket_number"));
        c.setUserId(rs.getInt("user_id"));
        c.setStudentName(rs.getString("student_name"));
        c.setStudentEmail(rs.getString("student_email"));
        c.setStudentId(rs.getString("student_reg_id"));
        c.setStudentPhone(rs.getString("student_phone"));
        c.setTitle(rs.getString("title"));
        c.setCategoryId(rs.getInt("category_id"));
        c.setCategoryName(rs.getString("category_name"));
        c.setLocation(rs.getString("location"));
        c.setDescription(rs.getString("description"));
        c.setPriority(rs.getString("priority"));
        c.setStatus(rs.getString("status"));
        
        int deptId = rs.getInt("assigned_department_id");
        c.setAssignedDepartmentId(rs.wasNull() ? null : deptId);
        c.setAssignedDepartmentName(rs.getString("assigned_department_name"));

        int staffId = rs.getInt("assigned_staff_id");
        c.setAssignedStaffId(rs.wasNull() ? null : staffId);
        c.setAssignedStaffName(rs.getString("assigned_staff_name"));

        c.setRejectionReason(rs.getString("rejection_reason"));
        c.setResolutionNotes(rs.getString("resolution_notes"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));
        c.setResolvedAt(rs.getTimestamp("resolved_at"));

        c.setOverdue(rs.getInt("is_overdue") == 1);
        c.setDaysOpen(rs.getLong("days_open"));
        return c;
    }
}
