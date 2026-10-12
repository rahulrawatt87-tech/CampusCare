package com.campuscare.controller;

import com.campuscare.dao.AttachmentDAO;
import com.campuscare.dao.ComplaintDAO;
import com.campuscare.model.Attachment;
import com.campuscare.model.Complaint;
import com.campuscare.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/attachments/*")
public class AttachmentServlet extends HttpServlet {

    private final AttachmentDAO attachmentDAO = new AttachmentDAO();
    private final ComplaintDAO complaintDAO = new ComplaintDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authentication required.");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int attachmentId = 0;
        try {
            attachmentId = Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Attachment attachment = attachmentDAO.findById(attachmentId);
        if (attachment == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Attachment not found");
            return;
        }

        Complaint complaint = complaintDAO.findById(attachment.getComplaintId());
        if (complaint == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Authorization checks
        if (currentUser.isStudent() && complaint.getUserId() != currentUser.getUserId()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You cannot view this attachment.");
            return;
        }

        if (currentUser.isStaff()) {
            boolean isAssigned = (complaint.getAssignedStaffId() != null && complaint.getAssignedStaffId() == currentUser.getUserId());
            boolean isDept = (currentUser.getDepartmentId() != null && complaint.getAssignedDepartmentId() != null &&
                    complaint.getAssignedDepartmentId().intValue() == currentUser.getDepartmentId().intValue());
            if (!isAssigned && !isDept) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied.");
                return;
            }
        }

        String uploadDirPath = getServletContext().getRealPath("/uploads");
        if (uploadDirPath == null) {
            uploadDirPath = System.getProperty("user.dir") + File.separator + "uploads";
        }

        File file = new File(uploadDirPath, attachment.getStoredFilename());
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File does not exist on disk.");
            return;
        }

        response.setContentType(attachment.getMimeType() != null ? attachment.getMimeType() : "application/octet-stream");
        response.setContentLengthLong(file.length());
        response.setHeader("Content-Disposition", "inline; filename=\"" + attachment.getOriginalFilename() + "\"");

        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}
