package com.campuscare.controller;

import com.campuscare.dao.CategoryDAO;
import com.campuscare.dao.ComplaintDAO;
import com.campuscare.model.Attachment;
import com.campuscare.model.Category;
import com.campuscare.model.Complaint;
import com.campuscare.model.User;
import com.campuscare.service.ComplaintService;
import com.campuscare.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@WebServlet(urlPatterns = {"/complaints", "/complaints/new"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 20    // 20MB
)
public class ComplaintServlet extends HttpServlet {

    private final ComplaintService complaintService = new ComplaintService();
    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String servletPath = request.getServletPath();

        // 1. Complaint Creation Form: GET /complaints/new
        if ("/complaints/new".equals(servletPath)) {
            List<Category> categories = categoryDAO.findAll(true);
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/WEB-INF/views/student/complaint-form.jsp").forward(request, response);
            return;
        }

        // 2. My Complaints List: GET /complaints
        String status = request.getParameter("status");
        String categoryStr = request.getParameter("categoryId");
        String search = request.getParameter("search");
        String pageStr = request.getParameter("page");

        Integer categoryId = null;
        if (categoryStr != null && !categoryStr.trim().isEmpty()) {
            try { categoryId = Integer.parseInt(categoryStr.trim()); } catch (NumberFormatException ignored) {}
        }

        int page = 1;
        int pageSize = 10;
        if (pageStr != null) {
            try { page = Math.max(1, Integer.parseInt(pageStr.trim())); } catch (NumberFormatException ignored) {}
        }
        int offset = (page - 1) * pageSize;

        int totalCount = complaintDAO.countStudentComplaints(user.getUserId(), status, categoryId, search);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        List<Complaint> complaints = complaintDAO.findStudentComplaints(user.getUserId(), status, categoryId, search, offset, pageSize);
        List<Category> categories = categoryDAO.findAll(true);

        request.setAttribute("complaints", complaints);
        request.setAttribute("categories", categories);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("searchQuery", search);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCount", totalCount);

        String submittedTicket = request.getParameter("submitted");
        if (submittedTicket != null) {
            request.setAttribute("successMessage", "Complaint submitted successfully! Your Ticket ID is " + submittedTicket);
        }

        request.getRequestDispatcher("/WEB-INF/views/student/my-complaints.jsp").forward(request, response);
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

        String title = request.getParameter("title");
        String categoryIdStr = request.getParameter("categoryId");
        String location = request.getParameter("location");
        String priority = request.getParameter("priority");
        String description = request.getParameter("description");

        int categoryId = 0;
        try {
            if (categoryIdStr != null) categoryId = Integer.parseInt(categoryIdStr.trim());
        } catch (NumberFormatException ignored) {}

        Attachment attachment = null;
        try {
            String contentType = request.getContentType();
            if (contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
                Part filePart = request.getPart("attachment");
                if (filePart != null && filePart.getSize() > 0) {
                    String submittedFileName = filePart.getSubmittedFileName();
                    if (submittedFileName != null && !submittedFileName.trim().isEmpty()) {
                        if (!ValidationUtil.isAllowedFileExtension(submittedFileName)) {
                            throw new IllegalArgumentException("Only image files (JPG, PNG, GIF) and PDF documents are allowed.");
                        }

                        String cleanOriginalName = ValidationUtil.sanitizeFilename(submittedFileName);
                        String storedName = UUID.randomUUID().toString() + "_" + cleanOriginalName;

                        String uploadDirPath = getServletContext().getRealPath("/uploads");
                        if (uploadDirPath == null) {
                            uploadDirPath = System.getProperty("user.dir") + File.separator + "uploads";
                        }
                        File uploadDir = new File(uploadDirPath);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs();
                        }

                        File targetFile = new File(uploadDir, storedName);
                        try (InputStream input = filePart.getInputStream()) {
                            Files.copy(input, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }

                        attachment = new Attachment();
                        attachment.setOriginalFilename(cleanOriginalName);
                        attachment.setStoredFilename(storedName);
                        attachment.setMimeType(filePart.getContentType());
                        attachment.setSizeBytes(filePart.getSize());
                    }
                }
            }
        } catch (Exception e) {
            forwardWithError(request, response, e.getMessage(), title, categoryId, location, priority, description);
            return;
        }

        try {
            Complaint created = complaintService.createComplaint(user, title, categoryId, location, priority, description, attachment);
            // Post/Redirect/Get
            response.sendRedirect(request.getContextPath() + "/complaints?submitted=" + created.getTicketNumber());
        } catch (Exception e) {
            forwardWithError(request, response, e.getMessage(), title, categoryId, location, priority, description);
        }
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String error,
                                  String title, int categoryId, String location, String priority, String description)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", error);
        request.setAttribute("title", title);
        request.setAttribute("categoryId", categoryId);
        request.setAttribute("location", location);
        request.setAttribute("priority", priority);
        request.setAttribute("description", description);
        request.setAttribute("categories", categoryDAO.findAll(true));
        request.getRequestDispatcher("/WEB-INF/views/student/complaint-form.jsp").forward(request, response);
    }
}
