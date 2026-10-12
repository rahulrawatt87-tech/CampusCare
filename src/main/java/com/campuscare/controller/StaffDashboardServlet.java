package com.campuscare.controller;

import com.campuscare.dao.ComplaintDAO;
import com.campuscare.model.Complaint;
import com.campuscare.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/staff/dashboard", "/staff/assigned"})
public class StaffDashboardServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User staff = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (staff == null || (!staff.isStaff() && !staff.isAdmin())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String status = request.getParameter("status");
        String search = request.getParameter("search");

        List<Complaint> assignedComplaints = complaintDAO.findStaffComplaints(
                staff.getUserId(), staff.getDepartmentId(), status, search);

        int totalCount = assignedComplaints.size();
        long inProgressCount = assignedComplaints.stream().filter(c -> "IN_PROGRESS".equalsIgnoreCase(c.getStatus())).count();
        long pendingCount = assignedComplaints.stream().filter(c -> "ASSIGNED".equalsIgnoreCase(c.getStatus())).count();
        long resolvedCount = assignedComplaints.stream().filter(Complaint::isResolved).count();
        long overdueCount = assignedComplaints.stream().filter(Complaint::isOverdue).count();

        request.setAttribute("assignedComplaints", assignedComplaints);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("inProgressCount", inProgressCount);
        request.setAttribute("pendingCount", pendingCount);
        request.setAttribute("resolvedCount", resolvedCount);
        request.setAttribute("overdueCount", overdueCount);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("searchQuery", search);

        request.getRequestDispatcher("/WEB-INF/views/staff/dashboard.jsp").forward(request, response);
    }
}
