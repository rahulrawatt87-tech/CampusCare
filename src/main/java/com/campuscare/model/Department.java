package com.campuscare.model;

import java.sql.Timestamp;

public class Department {
    private int departmentId;
    private String departmentName;
    private String description;
    private String contactEmail;
    private String contactPhone;
    private boolean active;
    private Timestamp createdAt;
    
    // Aggregates for dashboards
    private int complaintCount;
    private int staffCount;

    public Department() {}

    public Department(int departmentId, String departmentName, String description,
                      String contactEmail, String contactPhone, boolean active, Timestamp createdAt) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.description = description;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.active = active;
        this.createdAt = createdAt;
    }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public int getComplaintCount() { return complaintCount; }
    public void setComplaintCount(int complaintCount) { this.complaintCount = complaintCount; }

    public int getStaffCount() { return staffCount; }
    public void setStaffCount(int staffCount) { this.staffCount = staffCount; }
}
