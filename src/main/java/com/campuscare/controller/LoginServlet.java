package com.campuscare.controller;

import com.campuscare.model.User;
import com.campuscare.service.AuthService;
import com.campuscare.util.CsrfUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            User user = (User) session.getAttribute("currentUser");
            redirectToDashboard(user, request, response);
            return;
        }

        String error = request.getParameter("error");
        if ("auth_required".equals(error)) {
            request.setAttribute("errorMessage", "Please log in to access this page.");
        } else if ("session_expired".equals(error)) {
            request.setAttribute("errorMessage", "Your session has expired. Please log in again.");
        }

        String msg = request.getParameter("msg");
        if ("registered".equals(msg)) {
            request.setAttribute("successMessage", "Account created successfully! Please sign in with your credentials.");
        } else if ("logged_out".equals(msg)) {
            request.setAttribute("successMessage", "You have been logged out securely.");
        }

        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = authService.authenticate(email, password);

        if (user != null) {
            // Prevent Session Fixation: invalidate old session and generate a new session ID
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("currentUser", user);
            newSession.setMaxInactiveInterval(30 * 60); // 30 minutes inactivity timeout
            CsrfUtil.refreshToken(newSession);

            redirectToDashboard(user, request, response);
        } else {
            request.setAttribute("errorMessage", "Invalid email address or password. Please try again.");
            request.setAttribute("enteredEmail", email != null ? email.trim() : "");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }

    private void redirectToDashboard(User user, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String ctx = request.getContextPath();
        if (user.isAdmin()) {
            response.sendRedirect(ctx + "/admin/dashboard");
        } else if (user.isStaff()) {
            response.sendRedirect(ctx + "/staff/dashboard");
        } else {
            response.sendRedirect(ctx + "/student/dashboard");
        }
    }
}
