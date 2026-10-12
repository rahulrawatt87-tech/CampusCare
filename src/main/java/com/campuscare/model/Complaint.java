package com.campuscare.model;

import java.sql.Timestamp;
import java.util.List;

public class Complaint {
    private int complaintId;
    private String ticketNumber;
    private int userId;
    private String studentName;
    private String studentEmail;
    private String studentId;
    private String studentPhone;
    private String title;
    private int categoryId;
    private String categoryName;
    private String location;
    private String description;
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    private String status;   // SUBMITTED, UNDER_REVIEW, ASSIGNED, IN_PROGRESS, RESOLVED, REJECTED, REOPENED
    private Integer assignedDepartmentId;
    private String assignedDepartmentName;
    private Integer assignedStaffId;
    private String assignedStaffName;
    private String rejectionReason;
    private String resolutionNotes;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp resolvedAt;

    // Derived / UI properties
    private boolean overdue;
    private long daysOpen;
    private List<ComplaintUpdate> updates;
    private List<Attachment> attachments;
    private Feedback feedback;

    public Complaint() {}

    public int getComplaintId() { return complaintId; }
    public void setComplaintId(int complaintId) { this.complaintId = complaintId; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentPhone() { return studentPhone; }
    public void setStudentPhone(String studentPhone) { this.studentPhone = studentPhone; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getAssignedDepartmentId() { return assignedDepartmentId; }
    public void setAssignedDepartmentId(Integer assignedDepartmentId) { this.assignedDepartmentId = assignedDepartmentId; }

    public String getAssignedDepartmentName() { return assignedDepartmentName; }
    public void setAssignedDepartmentName(String assignedDepartmentName) { this.assignedDepartmentName = assignedDepartmentName; }

    public Integer getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(Integer assignedStaffId) { this.assignedStaffId = assignedStaffId; }

    public String getAssignedStaffName() { return assignedStaffName; }
    public void setAssignedStaffName(String assignedStaffName) { this.assignedStaffName = assignedStaffName; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Timestamp getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Timestamp resolvedAt) { this.resolvedAt = resolvedAt; }

    public boolean isOverdue() { return overdue; }
    public void setOverdue(boolean overdue) { this.overdue = overdue; }

    public long getDaysOpen() { return daysOpen; }
    public void setDaysOpen(long daysOpen) { this.daysOpen = daysOpen; }

    public List<ComplaintUpdate> getUpdates() { return updates; }
    public void setUpdates(List<ComplaintUpdate> updates) { this.updates = updates; }

    public List<Attachment> getAttachments() { return attachments; }
    public void setAttachments(List<Attachment> attachments) { this.attachments = attachments; }

    public Feedback getFeedback() { return feedback; }
    public void setFeedback(Feedback feedback) { this.feedback = feedback; }

    public boolean isResolved() {
        return "RESOLVED".equalsIgnoreCase(this.status);
    }

    public boolean isRejected() {
        return "REJECTED".equalsIgnoreCase(this.status);
    }

    public boolean isOpen() {
        return !isResolved() && !isRejected();
    }

    public String getStatusBadgeClass() {
        if ("SUBMITTED".equalsIgnoreCase(status)) return "badge-submitted";
        if ("UNDER_REVIEW".equalsIgnoreCase(status)) return "badge-under-review";
        if ("ASSIGNED".equalsIgnoreCase(status)) return "badge-assigned";
        if ("IN_PROGRESS".equalsIgnoreCase(status)) return "badge-in-progress";
        if ("RESOLVED".equalsIgnoreCase(status)) return "badge-resolved";
        if ("REJECTED".equalsIgnoreCase(status)) return "badge-rejected";
        if ("REOPENED".equalsIgnoreCase(status)) return "badge-reopened";
        return "badge-secondary";
    }

    public String getPriorityBadgeClass() {
        if ("URGENT".equalsIgnoreCase(priority)) return "priority-urgent";
        if ("HIGH".equalsIgnoreCase(priority)) return "priority-high";
        if ("MEDIUM".equalsIgnoreCase(priority)) return "priority-medium";
        return "priority-low";
    }

    public String getFormattedStatus() {
        if (status == null) return "";
        switch (status.toUpperCase()) {
            case "SUBMITTED": return "Submitted";
            case "UNDER_REVIEW": return "Under review";
            case "ASSIGNED": return "Assigned";
            case "IN_PROGRESS": return "In progress";
            case "RESOLVED": return "Resolved";
            case "REJECTED": return "Rejected";
            case "REOPENED": return "Reopened";
            default: return status;
        }
    }
}
