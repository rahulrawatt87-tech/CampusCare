package com.campuscare.controller;

import com.campuscare.service.AuthService;
import com.campuscare.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
            return;
        }

        request.setAttribute("accountType", "user");
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accountType = request.getParameter("accountType");
        if (accountType == null || accountType.trim().isEmpty()) {
            accountType = "user";
        }

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (password == null || !password.equals(confirmPassword)) {
            preserveAttributes(request, accountType, fullName, email, phone);
            request.setAttribute("errorMessage", "Passwords do not match. Please verify and re-enter password.");
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        if ("admin".equalsIgnoreCase(accountType)) {
            String secretKey = request.getParameter("secretKey");
            String error = authService.registerAdmin(fullName, email, phone, password, secretKey);
            if (error != null) {
                preserveAttributes(request, accountType, fullName, email, phone);
                request.setAttribute("secretKey", secretKey);
                request.setAttribute("errorMessage", error);
                request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/login?msg=registered");
            }
        } else {
            // Student or Faculty
            String role = request.getParameter("role"); // "STUDENT" or "FACULTY"
            if (!"FACULTY".equalsIgnoreCase(role)) {
                role = "STUDENT";
            }
            String idNumber = request.getParameter("idNumber");
            String academicBranch = request.getParameter("academicBranch");
            String courseYear = request.getParameter("courseYear");

            String error = authService.registerUser(fullName, email, idNumber, academicBranch, courseYear, phone, password, role);
            if (error != null) {
                preserveAttributes(request, accountType, fullName, email, phone);
                request.setAttribute("role", role);
                request.setAttribute("idNumber", idNumber);
                request.setAttribute("academicBranch", academicBranch);
                request.setAttribute("courseYear", courseYear);
                request.setAttribute("errorMessage", error);
                request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/login?msg=registered");
            }
        }
    }

    private void preserveAttributes(HttpServletRequest request, String accountType,
                                    String fullName, String email, String phone) {
        request.setAttribute("accountType", accountType);
        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);
    }
}
