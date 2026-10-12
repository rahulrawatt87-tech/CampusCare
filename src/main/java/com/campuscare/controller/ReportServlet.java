package com.campuscare.controller;

import com.campuscare.dao.CategoryDAO;
import com.campuscare.dao.DepartmentDAO;
import com.campuscare.model.Category;
import com.campuscare.model.DashboardStats;
import com.campuscare.model.Department;
import com.campuscare.model.Feedback;
import com.campuscare.model.User;
import com.campuscare.service.ReportService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/reports", "/admin/reports/export.csv"})
public class ReportServlet extends HttpServlet {

    private final ReportService reportService = new ReportService();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User admin = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (admin == null || !admin.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String servletPath = request.getServletPath();

        // 1. CSV Export: /admin/reports/export.csv
        if ("/admin/reports/export.csv".equals(servletPath)) {
            String status = request.getParameter("status");
            String catStr = request.getParameter("categoryId");
            String priority = request.getParameter("priority");
            String deptStr = request.getParameter("deptId");
            String search = request.getParameter("search");
            Boolean overdueOnly = "true".equalsIgnoreCase(request.getParameter("overdue"));

            Integer categoryId = (catStr != null && !catStr.trim().isEmpty()) ? Integer.parseInt(catStr.trim()) : null;
            Integer deptId = (deptStr != null && !deptStr.trim().isEmpty()) ? Integer.parseInt(deptStr.trim()) : null;

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"campuscare_complaints_" + timestamp + ".csv\"");

            PrintWriter writer = response.getWriter();
            reportService.exportComplaintsToCsv(writer, status, categoryId, priority, deptId, search, overdueOnly);
            writer.flush();
            return;
        }

        // 2. Interactive Analytics Report: /admin/reports
        DashboardStats stats = reportService.getAggregateReportData();
        List<Feedback> feedbackList = reportService.getAllFeedback(20);
        List<Department> departments = departmentDAO.findAll(true);
        List<Category> categories = categoryDAO.findAll(true);

        request.setAttribute("stats", stats);
        request.setAttribute("feedbackList", feedbackList);
        request.setAttribute("departments", departments);
        request.setAttribute("categories", categories);

        request.getRequestDispatcher("/WEB-INF/views/admin/reports.jsp").forward(request, response);
    }
}
