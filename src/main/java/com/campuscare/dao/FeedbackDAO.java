package com.campuscare.dao;

import com.campuscare.model.Feedback;
import com.campuscare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    public boolean createFeedback(Feedback f) {
        String sql = "INSERT INTO feedback (complaint_id, user_id, rating, comment, created_at) " +
                     "VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, f.getComplaintId());
            ps.setInt(2, f.getUserId());
            ps.setInt(3, f.getRating());
            ps.setString(4, f.getComment());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        f.setFeedbackId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Feedback findByComplaintId(int complaintId) {
        String sql = "SELECT f.*, u.full_name AS student_name, c.ticket_number " +
                     "FROM feedback f " +
                     "JOIN users u ON f.user_id = u.user_id " +
                     "JOIN complaints c ON f.complaint_id = c.complaint_id " +
                     "WHERE f.complaint_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, complaintId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Feedback f = new Feedback();
                    f.setFeedbackId(rs.getInt("feedback_id"));
                    f.setComplaintId(rs.getInt("complaint_id"));
                    f.setTicketNumber(rs.getString("ticket_number"));
                    f.setUserId(rs.getInt("user_id"));
                    f.setStudentName(rs.getString("student_name"));
                    f.setRating(rs.getInt("rating"));
                    f.setComment(rs.getString("comment"));
                    f.setCreatedAt(rs.getTimestamp("created_at"));
                    return f;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Feedback> findAll(int limit) {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT f.*, u.full_name AS student_name, c.ticket_number " +
                     "FROM feedback f " +
                     "JOIN users u ON f.user_id = u.user_id " +
                     "JOIN complaints c ON f.complaint_id = c.complaint_id " +
                     "ORDER BY f.created_at DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit > 0 ? limit : 50);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Feedback f = new Feedback();
                    f.setFeedbackId(rs.getInt("feedback_id"));
                    f.setComplaintId(rs.getInt("complaint_id"));
                    f.setTicketNumber(rs.getString("ticket_number"));
                    f.setUserId(rs.getInt("user_id"));
                    f.setStudentName(rs.getString("student_name"));
                    f.setRating(rs.getInt("rating"));
                    f.setComment(rs.getString("comment"));
                    f.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(f);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
