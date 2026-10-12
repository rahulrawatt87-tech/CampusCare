package com.campuscare.controller;

import com.campuscare.dao.DepartmentDAO;
import com.campuscare.model.Department;
import com.campuscare.model.User;
import com.campuscare.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/departments")
public class DepartmentServlet extends HttpServlet {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User admin = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (admin == null || !admin.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<Department> departments = departmentDAO.findAll(false);
        request.setAttribute("departments", departments);

        String msg = request.getParameter("msg");
        if ("created".equals(msg)) {
            request.setAttribute("successMessage", "New department created successfully.");
        } else if ("updated".equals(msg)) {
            request.setAttribute("successMessage", "Department updated successfully.");
        }

        request.getRequestDispatcher("/WEB-INF/views/admin/departments.jsp").forward(request, response);
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
        String name = request.getParameter("departmentName");
        String description = request.getParameter("description");
        String email = request.getParameter("contactEmail");
        String phone = request.getParameter("contactPhone");
        boolean active = "true".equalsIgnoreCase(request.getParameter("active")) || "on".equalsIgnoreCase(request.getParameter("active"));

        if (!ValidationUtil.isNotEmpty(name)) {
            request.setAttribute("errorMessage", "Department name is required.");
            doGet(request, response);
            return;
        }

        if ("update".equalsIgnoreCase(action)) {
            int deptId = Integer.parseInt(request.getParameter("departmentId"));
            Department dept = new Department(deptId, name, description, email, phone, active, null);
            departmentDAO.updateDepartment(dept);
            response.sendRedirect(request.getContextPath() + "/admin/departments?msg=updated");
        } else {
            Department dept = new Department(0, name, description, email, phone, true, null);
            departmentDAO.createDepartment(dept);
            response.sendRedirect(request.getContextPath() + "/admin/departments?msg=created");
        }
    }
}
