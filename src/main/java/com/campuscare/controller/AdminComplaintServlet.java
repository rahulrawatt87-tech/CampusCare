package com.campuscare.controller;

import com.campuscare.dao.CategoryDAO;
import com.campuscare.dao.ComplaintDAO;
import com.campuscare.dao.DepartmentDAO;
import com.campuscare.dao.UserDAO;
import com.campuscare.model.Category;
import com.campuscare.model.Complaint;
import com.campuscare.model.Department;
import com.campuscare.model.User;
import com.campuscare.service.ComplaintService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/complaints/*")
public class AdminComplaintServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final ComplaintService complaintService = new ComplaintService();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User admin = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (admin == null || !admin.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String status = request.getParameter("status");
        String categoryStr = request.getParameter("categoryId");
        String priority = request.getParameter("priority");
        String deptStr = request.getParameter("deptId");
        String search = request.getParameter("search");
        String overdueStr = request.getParameter("overdue");
        String pageStr = request.getParameter("page");

        Integer categoryId = null;
        if (categoryStr != null && !categoryStr.trim().isEmpty()) {
            try { categoryId = Integer.parseInt(categoryStr.trim()); } catch (NumberFormatException ignored) {}
        }

        Integer deptId = null;
        if (deptStr != null && !deptStr.trim().isEmpty()) {
            try { deptId = Integer.parseInt(deptStr.trim()); } catch (NumberFormatException ignored) {}
        }

        Boolean overdueOnly = "true".equalsIgnoreCase(overdueStr);

        int page = 1;
        int pageSize = 15;
        if (pageStr != null) {
            try { page = Math.max(1, Integer.parseInt(pageStr.trim())); } catch (NumberFormatException ignored) {}
        }
        int offset = (page - 1) * pageSize;

        int totalCount = complaintDAO.countComplaints(status, categoryId, priority, deptId, search, overdueOnly);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        List<Complaint> complaints = complaintDAO.findComplaints(status, categoryId, priority, deptId, search, overdueOnly, offset, pageSize);
        List<Department> departments = departmentDAO.findAll(true);
        List<Category> categories = categoryDAO.findAll(true);
        List<User> staffList = userDAO.findAllStaff();

        request.setAttribute("complaints", complaints);
        request.setAttribute("departments", departments);
        request.setAttribute("categories", categories);
        request.setAttribute("staffList", staffList);

        request.setAttribute("selectedStatus", status);
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("selectedPriority", priority);
        request.setAttribute("selectedDeptId", deptId);
        request.setAttribute("searchQuery", search);
        request.setAttribute("overdueOnly", overdueOnly);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCount", totalCount);

        request.getRequestDispatcher("/WEB-INF/views/admin/complaint-queue.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User actor = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (actor == null || (!actor.isAdmin() && !actor.isStaff())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.sendRedirect(request.getContextPath() + "/admin/complaints");
            return;
        }

        String[] parts = pathInfo.split("/");
        if (parts.length < 3) {
            response.sendRedirect(request.getContextPath() + "/admin/complaints");
            return;
        }

        String ticketNumber = parts[1];
        String action = parts[2];

        try {
            Complaint complaint = complaintDAO.findByTicketNumber(ticketNumber);
            if (complaint == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Complaint not found");
                return;
            }

            if ("assign".equals(action)) {
                int deptId = Integer.parseInt(request.getParameter("departmentId"));
                String staffIdStr = request.getParameter("staffId");
                Integer staffId = (staffIdStr != null && !staffIdStr.trim().isEmpty()) ? Integer.parseInt(staffIdStr.trim()) : null;
                String comment = request.getParameter("comment");

                complaintService.assignComplaint(complaint, deptId, staffId, comment, actor);
                response.sendRedirect(request.getContextPath() + "/complaints/" + ticketNumber + "?msg=status_updated");
                return;
            }

            if ("status".equals(action)) {
                String targetStatus = request.getParameter("status");
                String comment = request.getParameter("comment");
                String rejectionReason = request.getParameter("rejectionReason");
                String resolutionNotes = request.getParameter("resolutionNotes");

                complaintService.changeStatus(complaint, targetStatus, comment, rejectionReason, resolutionNotes, actor);
                response.sendRedirect(request.getContextPath() + "/complaints/" + ticketNumber + "?msg=status_updated");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/admin/complaints");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/complaints/" + ticketNumber + "?error=" + e.getMessage());
        }
    }
}
