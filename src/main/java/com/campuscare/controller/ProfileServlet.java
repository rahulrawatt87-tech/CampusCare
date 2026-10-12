package com.campuscare.controller;

import com.campuscare.dao.DepartmentDAO;
import com.campuscare.dao.UserDAO;
import com.campuscare.model.Department;
import com.campuscare.model.User;
import com.campuscare.util.PasswordUtil;
import com.campuscare.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Refresh user info from database
        User freshUser = userDAO.findById(user.getUserId());
        session.setAttribute("currentUser", freshUser);

        List<Department> departments = departmentDAO.findAll(true);
        request.setAttribute("departments", departments);
        request.setAttribute("user", freshUser);

        String msg = request.getParameter("msg");
        if ("profile_updated".equals(msg)) {
            request.setAttribute("successMessage", "Profile details updated successfully.");
        } else if ("password_changed".equals(msg)) {
            request.setAttribute("successMessage", "Your password has been changed successfully.");
        }

        request.getRequestDispatcher("/WEB-INF/views/shared/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        if ("change_password".equals(action)) {
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");

            User dbUser = userDAO.findById(user.getUserId());
            if (!PasswordUtil.checkPassword(currentPassword, dbUser.getPasswordHash())) {
                request.setAttribute("errorMessage", "Current password is incorrect.");
                doGet(request, response);
                return;
            }

            if (!ValidationUtil.isNotEmpty(newPassword) || newPassword.length() < 6) {
                request.setAttribute("errorMessage", "New password must be at least 6 characters.");
                doGet(request, response);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                request.setAttribute("errorMessage", "New passwords do not match.");
                doGet(request, response);
                return;
            }

            String newHash = PasswordUtil.hashPassword(newPassword);
            userDAO.updatePassword(user.getUserId(), newHash);
            response.sendRedirect(request.getContextPath() + "/profile?msg=password_changed");
            return;
        }

        // Update profile
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String courseYear = request.getParameter("courseYear");
        String deptIdStr = request.getParameter("departmentId");

        if (!ValidationUtil.isNotEmpty(fullName) || fullName.trim().length() < 2) {
            request.setAttribute("errorMessage", "Full name must be at least 2 characters.");
            doGet(request, response);
            return;
        }

        Integer deptId = null;
        if (deptIdStr != null && !deptIdStr.trim().isEmpty()) {
            try { deptId = Integer.parseInt(deptIdStr.trim()); } catch (NumberFormatException ignored) {}
        }

        User updateObj = new User();
        updateObj.setUserId(user.getUserId());
        updateObj.setFullName(fullName.trim());
        updateObj.setPhone(phone != null ? phone.trim() : null);
        updateObj.setCourseYear(courseYear != null ? courseYear.trim() : null);
        updateObj.setDepartmentId(deptId);

        userDAO.updateProfile(updateObj);
        response.sendRedirect(request.getContextPath() + "/profile?msg=profile_updated");
    }
}
