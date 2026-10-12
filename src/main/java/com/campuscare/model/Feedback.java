package com.campuscare.model;

import java.sql.Timestamp;

public class Feedback {
    private int feedbackId;
    private int complaintId;
    private String ticketNumber;
    private int userId;
    private String studentName;
    private int rating; // 1 to 5
    private String comment;
    private Timestamp createdAt;

    public Feedback() {}

    public Feedback(int feedbackId, int complaintId, String ticketNumber, int userId,
                    String studentName, int rating, String comment, Timestamp createdAt) {
        this.feedbackId = feedbackId;
        this.complaintId = complaintId;
        this.ticketNumber = ticketNumber;
        this.userId = userId;
        this.studentName = studentName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public int getFeedbackId() { return feedbackId; }
    public void setFeedbackId(int feedbackId) { this.feedbackId = feedbackId; }

    public int getComplaintId() { return complaintId; }
    public void setComplaintId(int complaintId) { this.complaintId = complaintId; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
