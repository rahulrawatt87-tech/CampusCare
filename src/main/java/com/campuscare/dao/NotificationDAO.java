package com.campuscare.dao;

import com.campuscare.model.Notification;
import com.campuscare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public boolean createNotification(Notification n, Connection conn) throws SQLException {
        boolean manageConn = (conn == null);
        Connection activeConn = manageConn ? DBConnection.getConnection() : conn;

        try {
            String sql = "INSERT INTO notifications (user_id, complaint_id, message, is_read, created_at) " +
                         "VALUES (?, ?, ?, FALSE, NOW())";
            try (PreparedStatement ps = activeConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, n.getUserId());
                if (n.getComplaintId() != null && n.getComplaintId() > 0) {
                    ps.setInt(2, n.getComplaintId());
                } else {
                    ps.setNull(2, Types.INTEGER);
                }
                ps.setString(3, n.getMessage());

                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            n.setNotificationId(rs.getInt(1));
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

    public List<Notification> getUserNotifications(int userId, int limit) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT n.*, c.ticket_number FROM notifications n " +
                     "LEFT JOIN complaints c ON n.complaint_id = c.complaint_id " +
                     "WHERE n.user_id = ? ORDER BY n.created_at DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit > 0 ? limit : 20);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification n = new Notification();
                    n.setNotificationId(rs.getInt("notification_id"));
                    n.setUserId(rs.getInt("user_id"));
                    int cId = rs.getInt("complaint_id");
                    n.setComplaintId(rs.wasNull() ? null : cId);
                    n.setTicketNumber(rs.getString("ticket_number"));
                    n.setMessage(rs.getString("message"));
                    n.setRead(rs.getBoolean("is_read"));
                    n.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(n);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean markAsRead(int notificationId, int userId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE notification_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean markAllAsRead(int userId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
