package com.campuscare.service;

import com.campuscare.dao.NotificationDAO;
import com.campuscare.dao.UserDAO;
import com.campuscare.model.Notification;
import com.campuscare.model.User;
import com.campuscare.util.ConfigUtil;
import com.campuscare.util.PasswordUtil;
import com.campuscare.util.ValidationUtil;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    public User authenticate(String email, String plainPassword) {
        if (!ValidationUtil.isValidEmail(email) || !ValidationUtil.isNotEmpty(plainPassword)) {
            return null;
        }

        User user = userDAO.findByEmail(email);
        if (user == null || !user.isActive()) {
            return null;
        }

        if (PasswordUtil.checkPassword(plainPassword, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    /**
     * Backward-compatible registration for students.
     */
    public String registerStudent(String fullName, String email, String studentId,
                                  Integer deptId, String courseYear, String phone, String password) {
        return registerUser(fullName, email, studentId, null, courseYear, phone, password, "STUDENT");
    }

    /**
     * Public registration for Students and Faculty members.
     */
    public String registerUser(String fullName, String email, String idNumber,
                               String academicBranch, String courseYear, String phone,
                               String password, String role) {
        if (!ValidationUtil.isNotEmpty(fullName) || fullName.trim().length() < 2) {
            return "Please provide a valid full name (minimum 2 characters).";
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return "Please provide a valid email address.";
        }
        if (!ValidationUtil.isNotEmpty(idNumber)) {
            return "ID / Roll Number is required for registration.";
        }
        if (!ValidationUtil.isNotEmpty(password) || password.length() < 6) {
            return "Password must be at least 6 characters in length.";
        }

        String assignedRole = "FACULTY".equalsIgnoreCase(role) ? "FACULTY" : "STUDENT";

        if (userDAO.emailExists(email)) {
            return "An account with this email address already exists.";
        }
        if (userDAO.studentIdExists(idNumber)) {
            return "An account with this " + (assignedRole.equals("FACULTY") ? "Faculty ID" : "Student Roll No.") + " already exists.";
        }

        String hash = PasswordUtil.hashPassword(password);
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setStudentId(idNumber.trim());
        user.setAcademicBranch(academicBranch != null && !academicBranch.trim().isEmpty() ? academicBranch.trim() : null);
        user.setCourseYear(courseYear != null ? courseYear.trim() : null);
        user.setPhone(phone != null ? phone.trim() : null);
        user.setPasswordHash(hash);
        user.setRole(assignedRole);
        user.setActive(true);

        boolean success = userDAO.createUser(user);
        if (success) {
            try {
                Notification notif = new Notification();
                notif.setUserId(user.getUserId());
                notif.setMessage("Welcome to CampusCare! You can now report campus issues and track their progress.");
                notificationDAO.createNotification(notif, null);
            } catch (Exception ignored) {}
            return null; // Success
        } else {
            return "Failed to register account. Please try again or contact administration.";
        }
    }

    /**
     * Verified Administrator registration requiring institutional secret key.
     */
    public String registerAdmin(String fullName, String email, String phone, String password, String secretKey) {
        if (!ValidationUtil.isNotEmpty(secretKey)) {
            return "Institutional Verification Key is required to register an administrator account.";
        }
        String configuredKey = ConfigUtil.getAdminRegistrationKey();
        if (!configuredKey.equals(secretKey.trim())) {
            return "Verification Failed: Invalid Institutional Verification Key. Only authorized campus administration officials can register.";
        }

        if (!ValidationUtil.isNotEmpty(fullName) || fullName.trim().length() < 2) {
            return "Please provide a valid full name (minimum 2 characters).";
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return "Please provide a valid institutional email address.";
        }
        if (!ValidationUtil.isNotEmpty(password) || password.length() < 6) {
            return "Password must be at least 6 characters in length.";
        }

        if (userDAO.emailExists(email)) {
            return "An account with this email address already exists.";
        }

        String hash = PasswordUtil.hashPassword(password);
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPhone(phone != null ? phone.trim() : null);
        user.setPasswordHash(hash);
        user.setRole("ADMIN");
        user.setActive(true);

        boolean success = userDAO.createUser(user);
        if (success) {
            try {
                Notification notif = new Notification();
                notif.setUserId(user.getUserId());
                notif.setMessage("Welcome to CampusCare Administration! You have full access to manage complaints, departments, and staff.");
                notificationDAO.createNotification(notif, null);
            } catch (Exception ignored) {}
            return null; // Success
        } else {
            return "Failed to register administrator account. Please try again.";
        }
    }

    /**
     * Provisioning of staff / technicians directly by an Administrator.
     */
    public String createStaff(String fullName, String email, Integer departmentId,
                              String phone, String password, User actor) {
        if (actor == null || !actor.isAdmin()) {
            return "Unauthorized: Only administrators can add department staff.";
        }

        if (!ValidationUtil.isNotEmpty(fullName) || fullName.trim().length() < 2) {
            return "Please provide a valid full name for the staff member.";
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return "Please provide a valid email address.";
        }
        if (departmentId == null || departmentId <= 0) {
            return "Please assign the staff member to a valid service department.";
        }
        if (!ValidationUtil.isNotEmpty(password) || password.length() < 6) {
            return "Initial password must be at least 6 characters in length.";
        }

        if (userDAO.emailExists(email)) {
            return "An account with this email address already exists.";
        }

        String hash = PasswordUtil.hashPassword(password);
        User staff = new User();
        staff.setFullName(fullName.trim());
        staff.setEmail(email.trim().toLowerCase());
        staff.setDepartmentId(departmentId);
        staff.setPhone(phone != null ? phone.trim() : null);
        staff.setPasswordHash(hash);
        staff.setRole("STAFF");
        staff.setActive(true);

        boolean success = userDAO.createUser(staff);
        if (success) {
            try {
                Notification notif = new Notification();
                notif.setUserId(staff.getUserId());
                notif.setMessage("Welcome to CampusCare Staff Portal. You have been assigned to handle department maintenance tickets.");
                notificationDAO.createNotification(notif, null);
            } catch (Exception ignored) {}
            return null; // Success
        } else {
            return "Failed to add staff member. Please check details and try again.";
        }
    }
}
