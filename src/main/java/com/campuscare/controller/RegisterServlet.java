package com.campuscare.controller;

import com.campuscare.dao.DepartmentDAO;
import com.campuscare.model.Department;
import com.campuscare.service.AuthService;
import com.campuscare.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final AuthService authService = new AuthService();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
            return;
        }

        List<Department> departments = departmentDAO.findAll(true);
        request.setAttribute("departments", departments);
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String studentId = request.getParameter("studentId");
        String deptIdStr = request.getParameter("departmentId");
        String courseYear = request.getParameter("courseYear");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        Integer deptId = null;
        if (deptIdStr != null && !deptIdStr.trim().isEmpty()) {
            try {
                deptId = Integer.parseInt(deptIdStr.trim());
            } catch (NumberFormatException ignored) {}
        }

        if (password == null || !password.equals(confirmPassword)) {
            List<Department> departments = departmentDAO.findAll(true);
            request.setAttribute("departments", departments);
            request.setAttribute("errorMessage", "Passwords do not match. Please verify and try again.");
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("studentId", studentId);
            request.setAttribute("departmentId", deptId);
            request.setAttribute("courseYear", courseYear);
            request.setAttribute("phone", phone);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        String error = authService.registerStudent(fullName, email, studentId, deptId, courseYear, phone, password);

        if (error != null) {
            List<Department> departments = departmentDAO.findAll(true);
            request.setAttribute("departments", departments);
            request.setAttribute("errorMessage", error);
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("studentId", studentId);
            request.setAttribute("departmentId", deptId);
            request.setAttribute("courseYear", courseYear);
            request.setAttribute("phone", phone);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login?msg=registered");
        }
    }
}
