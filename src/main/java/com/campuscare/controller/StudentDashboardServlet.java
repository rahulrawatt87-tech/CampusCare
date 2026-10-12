package com.campuscare.controller;

import com.campuscare.dao.ComplaintDAO;
import com.campuscare.dao.NotificationDAO;
import com.campuscare.model.Complaint;
import com.campuscare.model.DashboardStats;
import com.campuscare.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User student = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (student == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Compute greeting based on time of day
        int hour = LocalTime.now().getHour();
        String greeting = (hour < 12) ? "Good morning" : (hour < 17) ? "Good afternoon" : "Good evening";
        request.setAttribute("greeting", greeting);

        // Fetch stats
        DashboardStats stats = complaintDAO.getStudentStats(student.getUserId());
        request.setAttribute("stats", stats);

        // Fetch recent complaints (top 5)
        List<Complaint> recentComplaints = complaintDAO.findStudentComplaints(student.getUserId(), null, null, null, 0, 5);
        request.setAttribute("recentComplaints", recentComplaints);

        // Fetch unread notifications count
        int unreadNotifs = notificationDAO.getUnreadCount(student.getUserId());
        request.setAttribute("unreadNotifs", unreadNotifs);

        request.getRequestDispatcher("/WEB-INF/views/student/dashboard.jsp").forward(request, response);
    }
}
