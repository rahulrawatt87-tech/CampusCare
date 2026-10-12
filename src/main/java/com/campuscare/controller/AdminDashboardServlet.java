package com.campuscare.controller;

import com.campuscare.dao.ComplaintDAO;
import com.campuscare.dao.DepartmentDAO;
import com.campuscare.model.Complaint;
import com.campuscare.model.DashboardStats;
import com.campuscare.model.Department;
import com.campuscare.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAO();
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

        // Live stats aggregates from DB
        DashboardStats stats = complaintDAO.getDashboardStats();
        request.setAttribute("stats", stats);

        // Recent complaint queue (top 8)
        List<Complaint> recentQueue = complaintDAO.findComplaints(null, null, null, null, null, null, 0, 8);
        request.setAttribute("recentQueue", recentQueue);

        // Overdue items needing attention
        List<Complaint> overdueList = complaintDAO.findComplaints(null, null, null, null, null, true, 0, 5);
        request.setAttribute("overdueList", overdueList);

        // Departments for quick filters
        List<Department> departments = departmentDAO.findAll(true);
        request.setAttribute("departments", departments);

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}
