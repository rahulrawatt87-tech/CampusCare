package com.campuscare.controller;

import com.campuscare.dao.NotificationDAO;
import com.campuscare.model.Notification;
import com.campuscare.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/notifications", "/notifications/read", "/notifications/read-all"})
public class NotificationServlet extends HttpServlet {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<Notification> notifications = notificationDAO.getUserNotifications(user.getUserId(), 50);
        int unreadCount = notificationDAO.getUnreadCount(user.getUserId());

        request.setAttribute("notifications", notifications);
        request.setAttribute("unreadCount", unreadCount);

        request.getRequestDispatcher("/WEB-INF/views/shared/notifications.jsp").forward(request, response);
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

        String servletPath = request.getServletPath();

        if ("/notifications/read-all".equals(servletPath)) {
            notificationDAO.markAllAsRead(user.getUserId());
        } else if ("/notifications/read".equals(servletPath)) {
            String idStr = request.getParameter("notificationId");
            if (idStr != null) {
                try {
                    int id = Integer.parseInt(idStr.trim());
                    notificationDAO.markAsRead(id, user.getUserId());
                } catch (NumberFormatException ignored) {}
            }
        }

        String redirect = request.getParameter("redirect");
        if (redirect != null && !redirect.trim().isEmpty()) {
            response.sendRedirect(redirect);
        } else {
            response.sendRedirect(request.getContextPath() + "/notifications");
        }
    }
}
