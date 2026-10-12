package com.campuscare.model;

import java.sql.Timestamp;

public class ComplaintUpdate {
    private int updateId;
    private int complaintId;
    private int updatedBy;
    private String authorName;
    private String authorRole;
    private String eventType; // CREATED, STATUS_CHANGE, ASSIGNED, INTERNAL_NOTE, COMMENT, RESOLVED, REJECTED, REOPENED
    private String oldStatus;
    private String newStatus;
    private String comment;
    private String visibility; // PUBLIC, INTERNAL
    private Timestamp createdAt;

    public ComplaintUpdate() {}

    public ComplaintUpdate(int updateId, int complaintId, int updatedBy, String authorName, String authorRole,
                           String eventType, String oldStatus, String newStatus, String comment,
                           String visibility, Timestamp createdAt) {
        this.updateId = updateId;
        this.complaintId = complaintId;
        this.updatedBy = updatedBy;
        this.authorName = authorName;
        this.authorRole = authorRole;
        this.eventType = eventType;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.comment = comment;
        this.visibility = visibility;
        this.createdAt = createdAt;
    }

    public int getUpdateId() { return updateId; }
    public void setUpdateId(int updateId) { this.updateId = updateId; }

    public int getComplaintId() { return complaintId; }
    public void setComplaintId(int complaintId) { this.complaintId = complaintId; }

    public int getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(int updatedBy) { this.updatedBy = updatedBy; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorRole() { return authorRole; }
    public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isInternal() {
        return "INTERNAL".equalsIgnoreCase(this.visibility);
    }
}
