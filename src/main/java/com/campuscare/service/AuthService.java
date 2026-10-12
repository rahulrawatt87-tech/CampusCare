package com.campuscare.service;

import com.campuscare.dao.NotificationDAO;
import com.campuscare.dao.UserDAO;
import com.campuscare.model.Notification;
import com.campuscare.model.User;
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

    public String registerStudent(String fullName, String email, String studentId,
                                  Integer deptId, String courseYear, String phone, String password) {
        if (!ValidationUtil.isNotEmpty(fullName) || fullName.trim().length() < 2) {
            return "Please provide a valid full name (minimum 2 characters).";
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return "Please provide a valid email address.";
        }
        if (!ValidationUtil.isNotEmpty(studentId)) {
            return "Student ID is required for registration.";
        }
        if (!ValidationUtil.isNotEmpty(password) || password.length() < 6) {
            return "Password must be at least 6 characters in length.";
        }

        if (userDAO.emailExists(email)) {
            return "An account with this email address already exists.";
        }
        if (userDAO.studentIdExists(studentId)) {
            return "An account with this Student ID already exists.";
        }

        String hash = PasswordUtil.hashPassword(password);
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setStudentId(studentId.trim());
        user.setDepartmentId(deptId);
        user.setCourseYear(courseYear != null ? courseYear.trim() : null);
        user.setPhone(phone != null ? phone.trim() : null);
        user.setPasswordHash(hash);
        user.setRole("STUDENT");
        user.setActive(true);

        boolean success = userDAO.createUser(user);
        if (success) {
            try {
                Notification notif = new Notification();
                notif.setUserId(user.getUserId());
                notif.setMessage("Welcome to CampusCare! You can now report campus issues and track their progress.");
                notificationDAO.createNotification(notif, null);
            } catch (Exception ignored) {}
            return null; // Null means success (no error message)
        } else {
            return "Failed to register student. Please try again or contact administration.";
        }
    }
}
