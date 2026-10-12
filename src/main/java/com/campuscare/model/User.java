package com.campuscare.model;

import java.sql.Timestamp;

public class User {
    private int userId;
    private String fullName;
    private String email;
    private String studentId;
    private String academicBranch; // e.g. Computer Science & Engineering
    private Integer departmentId; // Service department for Staff
    private String departmentName;
    private String courseYear; // Year for student or Designation for faculty
    private String phone;
    private String passwordHash;
    private String role; // STUDENT, FACULTY, ADMIN, STAFF
    private boolean active;
    private Timestamp createdAt;

    public User() {}

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getAcademicBranch() { return academicBranch; }
    public void setAcademicBranch(String academicBranch) { this.academicBranch = academicBranch; }

    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getCourseYear() { return courseYear; }
    public void setCourseYear(String courseYear) { this.courseYear = courseYear; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isStudent() {
        return "STUDENT".equalsIgnoreCase(this.role);
    }

    public boolean isFaculty() {
        return "FACULTY".equalsIgnoreCase(this.role);
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    public boolean isStaff() {
        return "STAFF".equalsIgnoreCase(this.role);
    }

    public boolean isComplainant() {
        return isStudent() || isFaculty();
    }
}
