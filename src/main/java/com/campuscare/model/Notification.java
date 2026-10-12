package com.campuscare.model;

import java.sql.Timestamp;

public class Notification {
    private int notificationId;
    private int userId;
    private Integer complaintId;
    private String ticketNumber;
    private String message;
    private boolean read;
    private Timestamp createdAt;

    public Notification() {}

    public Notification(int notificationId, int userId, Integer complaintId, String ticketNumber,
                        String message, boolean read, Timestamp createdAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.complaintId = complaintId;
        this.ticketNumber = ticketNumber;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
    }

    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getComplaintId() { return complaintId; }
    public void setComplaintId(Integer complaintId) { this.complaintId = complaintId; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
