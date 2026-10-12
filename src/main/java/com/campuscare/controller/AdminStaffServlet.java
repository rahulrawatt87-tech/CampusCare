package com.campuscare.controller;

import com.campuscare.dao.DepartmentDAO;
import com.campuscare.dao.UserDAO;
import com.campuscare.model.Department;
import com.campuscare.model.User;
import com.campuscare.service.AuthService;
import com.campuscare.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/staff")
public class AdminStaffServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User admin = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (admin == null || !admin.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<User> staffMembers = userDAO.findAllStaffMembers();
        List<Department> departments = departmentDAO.findAll(true);

        request.setAttribute("staffMembers", staffMembers);
        request.setAttribute("departments", departments);
        request.setAttribute("activeNav", "staff");

        String msg = request.getParameter("msg");
        if ("added".equals(msg)) {
            request.setAttribute("successMessage", "New staff member provisioned successfully.");
        } else if ("status_updated".equals(msg)) {
            request.setAttribute("successMessage", "Staff account active status has been updated.");
        }

        request.getRequestDispatcher("/WEB-INF/views/admin/staff.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User admin = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (admin == null || !admin.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = request.getParameter("action");

        if ("toggle".equals(action)) {
            String userIdStr = request.getParameter("userId");
            String statusStr = request.getParameter("status");
            try {
                int targetUserId = Integer.parseInt(userIdStr);
                boolean newStatus = Boolean.parseBoolean(statusStr);
                userDAO.toggleUserStatus(targetUserId, newStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/admin/staff?msg=status_updated");
            return;
        }

        // Default action is "add"
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String deptIdStr = request.getParameter("departmentId");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

        Integer deptId = null;
        if (ValidationUtil.isNotEmpty(deptIdStr)) {
            try {
                deptId = Integer.parseInt(deptIdStr.trim());
            } catch (NumberFormatException ignored) {}
        }

        String error = authService.createStaff(fullName, email, deptId, phone, password, admin);

        if (error != null) {
            List<User> staffMembers = userDAO.findAllStaffMembers();
            List<Department> departments = departmentDAO.findAll(true);
            request.setAttribute("staffMembers", staffMembers);
            request.setAttribute("departments", departments);
            request.setAttribute("activeNav", "staff");
            request.setAttribute("errorMessage", error);
            request.setAttribute("prevFullName", fullName);
            request.setAttribute("prevEmail", email);
            request.setAttribute("prevDepartmentId", deptId);
            request.setAttribute("prevPhone", phone);
            request.getRequestDispatcher("/WEB-INF/views/admin/staff.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/staff?msg=added");
        }
    }
}
