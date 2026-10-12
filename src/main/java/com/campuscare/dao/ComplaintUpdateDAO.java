package com.campuscare.dao;

import com.campuscare.model.ComplaintUpdate;
import com.campuscare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintUpdateDAO {

    public boolean addUpdate(ComplaintUpdate update, Connection conn) throws SQLException {
        boolean manageConn = (conn == null);
        Connection activeConn = manageConn ? DBConnection.getConnection() : conn;

        try {
            String sql = "INSERT INTO complaint_updates (complaint_id, updated_by, event_type, old_status, new_status, " +
                         "comment, visibility, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
            try (PreparedStatement ps = activeConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, update.getComplaintId());
                ps.setInt(2, update.getUpdatedBy());
                ps.setString(3, update.getEventType());
                ps.setString(4, update.getOldStatus());
                ps.setString(5, update.getNewStatus());
                ps.setString(6, update.getComment());
                ps.setString(7, update.getVisibility() != null ? update.getVisibility() : "PUBLIC");

                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            update.setUpdateId(rs.getInt(1));
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

    public List<ComplaintUpdate> findByComplaintId(int complaintId, boolean includeInternal) {
        List<ComplaintUpdate> list = new ArrayList<>();
        String sql = "SELECT cu.*, u.full_name AS author_name, u.role AS author_role " +
                     "FROM complaint_updates cu " +
                     "JOIN users u ON cu.updated_by = u.user_id " +
                     "WHERE cu.complaint_id = ? " +
                     (includeInternal ? "" : "AND cu.visibility = 'PUBLIC' ") +
                     "ORDER BY cu.created_at ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, complaintId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ComplaintUpdate u = new ComplaintUpdate();
                    u.setUpdateId(rs.getInt("update_id"));
                    u.setComplaintId(rs.getInt("complaint_id"));
                    u.setUpdatedBy(rs.getInt("updated_by"));
                    u.setAuthorName(rs.getString("author_name"));
                    u.setAuthorRole(rs.getString("author_role"));
                    u.setEventType(rs.getString("event_type"));
                    u.setOldStatus(rs.getString("old_status"));
                    u.setNewStatus(rs.getString("new_status"));
                    u.setComment(rs.getString("comment"));
                    u.setVisibility(rs.getString("visibility"));
                    u.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
