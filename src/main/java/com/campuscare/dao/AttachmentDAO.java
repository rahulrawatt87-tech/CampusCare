package com.campuscare.dao;

import com.campuscare.model.Attachment;
import com.campuscare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttachmentDAO {

    public boolean createAttachment(Attachment a, Connection conn) throws SQLException {
        boolean manageConn = (conn == null);
        Connection activeConn = manageConn ? DBConnection.getConnection() : conn;

        try {
            String sql = "INSERT INTO attachments (complaint_id, uploaded_by, stored_filename, original_filename, " +
                         "mime_type, size_bytes, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";
            try (PreparedStatement ps = activeConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, a.getComplaintId());
                ps.setInt(2, a.getUploadedBy());
                ps.setString(3, a.getStoredFilename());
                ps.setString(4, a.getOriginalFilename());
                ps.setString(5, a.getMimeType());
                ps.setLong(6, a.getSizeBytes());

                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            a.setAttachmentId(rs.getInt(1));
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

    public List<Attachment> findByComplaintId(int complaintId) {
        List<Attachment> list = new ArrayList<>();
        String sql = "SELECT * FROM attachments WHERE complaint_id = ? ORDER BY created_at ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, complaintId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAttachment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Attachment findById(int attachmentId) {
        String sql = "SELECT * FROM attachments WHERE attachment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attachmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAttachment(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Attachment mapAttachment(ResultSet rs) throws SQLException {
        Attachment a = new Attachment();
        a.setAttachmentId(rs.getInt("attachment_id"));
        a.setComplaintId(rs.getInt("complaint_id"));
        a.setUploadedBy(rs.getInt("uploaded_by"));
        a.setStoredFilename(rs.getString("stored_filename"));
        a.setOriginalFilename(rs.getString("original_filename"));
        a.setMimeType(rs.getString("mime_type"));
        a.setSizeBytes(rs.getLong("size_bytes"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        return a;
    }
}
