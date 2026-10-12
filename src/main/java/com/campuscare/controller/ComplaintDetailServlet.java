package com.campuscare.controller;

import com.campuscare.dao.DepartmentDAO;
import com.campuscare.dao.UserDAO;
import com.campuscare.model.Complaint;
import com.campuscare.model.Department;
import com.campuscare.model.User;
import com.campuscare.service.ComplaintService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/complaints/*")
public class ComplaintDetailServlet extends HttpServlet {

    private final ComplaintService complaintService = new ComplaintService();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/complaints");
            return;
        }

        // Parse ticket number, e.g. "/CC-2026-000101"
        String[] parts = pathInfo.split("/");
        String ticketNumber = parts[1];

        try {
            Complaint complaint = complaintService.getComplaintWithDetails(ticketNumber, currentUser);
            if (complaint == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Complaint ticket not found: " + ticketNumber);
                return;
            }

            // Load departments and staff for admin/staff modals
            if (currentUser.isAdmin()) {
                List<Department> departments = departmentDAO.findAll(true);
                List<User> staffList = userDAO.findAllStaff();
                request.setAttribute("departments", departments);
                request.setAttribute("staffList", staffList);
            }

            request.setAttribute("complaint", complaint);

            String actionMsg = request.getParameter("msg");
            if ("feedback_saved".equals(actionMsg)) {
                request.setAttribute("successMessage", "Thank you! Your feedback has been recorded.");
            } else if ("status_updated".equals(actionMsg)) {
                request.setAttribute("successMessage", "Status has been updated successfully.");
            } else if ("reopened".equals(actionMsg)) {
                request.setAttribute("successMessage", "Complaint has been reopened for investigation.");
            } else if ("comment_added".equals(actionMsg)) {
                request.setAttribute("successMessage", "Comment added to complaint history.");
            }

            request.getRequestDispatcher("/WEB-INF/views/shared/complaint-details.jsp").forward(request, response);
        } catch (IllegalAccessException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.sendRedirect(request.getContextPath() + "/complaints");
            return;
        }

        String[] parts = pathInfo.split("/");
        if (parts.length < 2) {
            response.sendRedirect(request.getContextPath() + "/complaints");
            return;
        }

        String ticketNumber = parts[1];
        String subAction = (parts.length > 2) ? parts[2] : "";

        try {
            Complaint complaint = complaintService.getComplaintWithDetails(ticketNumber, currentUser);
            if (complaint == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Complaint not found");
                return;
            }

            if ("feedback".equals(subAction)) {
                int rating = Integer.parseInt(request.getParameter("rating"));
                String comment = request.getParameter("comment");
                complaintService.submitFeedback(complaint, rating, comment, currentUser);
                response.sendRedirect(request.getContextPath() + "/complaints/" + ticketNumber + "?msg=feedback_saved");
                return;
            }

            if ("reopen".equals(subAction)) {
                String reason = request.getParameter("reopenReason");
                complaintService.reopenComplaint(complaint, reason, currentUser);
                response.sendRedirect(request.getContextPath() + "/complaints/" + ticketNumber + "?msg=reopened");
                return;
            }

            if ("comment".equals(subAction)) {
                String comment = request.getParameter("comment");
                boolean isInternal = "true".equalsIgnoreCase(request.getParameter("isInternal"));
                if (isInternal && currentUser.isStudent()) {
                    isInternal = false; // Students cannot create internal notes
                }
                complaintService.addComment(complaint, comment, isInternal, currentUser);
                response.sendRedirect(request.getContextPath() + "/complaints/" + ticketNumber + "?msg=comment_added");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/complaints/" + ticketNumber);
        } catch (IllegalAccessException | IllegalStateException | IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Operation failed: " + e.getMessage());
        }
    }
}
