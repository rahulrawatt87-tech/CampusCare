package com.campuscare.service;

import com.campuscare.dao.*;
import com.campuscare.model.*;
import com.campuscare.util.DBConnection;
import com.campuscare.util.ValidationUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ComplaintService {

    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final ComplaintUpdateDAO updateDAO = new ComplaintUpdateDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final FeedbackDAO feedbackDAO = new FeedbackDAO();
    private final AttachmentDAO attachmentDAO = new AttachmentDAO();

    /**
     * Submits a new complaint with transaction safety.
     * Inserts complaint, initial history update, and notification.
     */
    public Complaint createComplaint(User student, String title, int categoryId, String location,
                                     String priority, String description, Attachment attachment) throws Exception {
        if (!ValidationUtil.isNotEmpty(title) || title.length() < 3 || title.length() > 200) {
            throw new IllegalArgumentException("Title must be between 3 and 200 characters.");
        }
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Please select a valid category.");
        }
        if (!ValidationUtil.isNotEmpty(location) || location.length() < 2 || location.length() > 150) {
            throw new IllegalArgumentException("Location must be between 2 and 150 characters.");
        }
        if (!ValidationUtil.isValidPriority(priority)) {
            priority = "MEDIUM";
        }
        if (!ValidationUtil.isNotEmpty(description) || description.length() < 10) {
            throw new IllegalArgumentException("Description must be at least 10 characters.");
        }

        Category cat = categoryDAO.findById(categoryId);
        if (cat == null) {
            throw new IllegalArgumentException("Invalid category selected.");
        }

        Complaint c = new Complaint();
        c.setUserId(student.getUserId());
        c.setTitle(title.trim());
        c.setCategoryId(categoryId);
        c.setLocation(location.trim());
        c.setDescription(description.trim());
        c.setPriority(priority.toUpperCase());
        c.setStatus("SUBMITTED");

        // Suggest or pre-assign default department if defined for category
        if (cat.getDefaultDepartmentId() != null && cat.getDefaultDepartmentId() > 0) {
            c.setAssignedDepartmentId(cat.getDefaultDepartmentId());
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Create complaint
                boolean created = complaintDAO.createComplaint(c, conn);
                if (!created) {
                    throw new SQLException("Failed to insert complaint record.");
                }

                // 2. Insert initial timeline audit update
                ComplaintUpdate update = new ComplaintUpdate();
                update.setComplaintId(c.getComplaintId());
                update.setUpdatedBy(student.getUserId());
                update.setEventType("CREATED");
                update.setOldStatus(null);
                update.setNewStatus("SUBMITTED");
                update.setComment("Complaint submitted: Ticket created successfully.");
                update.setVisibility("PUBLIC");
                updateDAO.addUpdate(update, conn);

                // 3. Save attachment if provided
                if (attachment != null) {
                    attachment.setComplaintId(c.getComplaintId());
                    attachment.setUploadedBy(student.getUserId());
                    attachmentDAO.createAttachment(attachment, conn);
                }

                // 4. Create confirmation notification for student
                Notification notif = new Notification();
                notif.setUserId(student.getUserId());
                notif.setComplaintId(c.getComplaintId());
                notif.setMessage("Complaint ticket " + c.getTicketNumber() + " created successfully: " + c.getTitle());
                notificationDAO.createNotification(notif, conn);

                conn.commit();
                return c;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Retrieves full complaint details with timeline and attachments.
     * Enforces privacy and ownership rules.
     */
    public Complaint getComplaintWithDetails(String ticketNumber, User currentUser) throws IllegalAccessException {
        Complaint c = complaintDAO.findByTicketNumber(ticketNumber);
        if (c == null) {
            return null;
        }

        // Access check
        if (currentUser.isComplainant() && c.getUserId() != currentUser.getUserId()) {
            throw new IllegalAccessException("Access Denied: You cannot view another user's complaint.");
        }

        if (currentUser.isStaff()) {
            boolean isStaffAssigned = (c.getAssignedStaffId() != null && c.getAssignedStaffId() == currentUser.getUserId());
            boolean isDeptAssigned = (currentUser.getDepartmentId() != null &&
                    c.getAssignedDepartmentId() != null &&
                    c.getAssignedDepartmentId().intValue() == currentUser.getDepartmentId().intValue());
            if (!isStaffAssigned && !isDeptAssigned) {
                // Staff only sees their department's tickets
                throw new IllegalAccessException("Access Denied: You are not authorized to view this department's ticket.");
            }
        }

        boolean canSeeInternalNotes = currentUser.isAdmin() || currentUser.isStaff();
        c.setUpdates(updateDAO.findByComplaintId(c.getComplaintId(), canSeeInternalNotes));
        c.setAttachments(attachmentDAO.findByComplaintId(c.getComplaintId()));
        c.setFeedback(feedbackDAO.findByComplaintId(c.getComplaintId()));

        return c;
    }

    /**
     * Updates complaint status with validation, audit logging, and notification.
     */
    public boolean changeStatus(Complaint complaint, String targetStatus, String comment,
                                String rejectionReason, String resolutionNotes, User actor) throws Exception {
        targetStatus = targetStatus.toUpperCase().trim();
        String currentStatus = complaint.getStatus().toUpperCase();

        validateStatusTransition(currentStatus, targetStatus, actor, rejectionReason, resolutionNotes);

        Timestamp resolvedAt = null;
        if ("RESOLVED".equals(targetStatus)) {
            resolvedAt = new Timestamp(System.currentTimeMillis());
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Update complaint row
                boolean ok = complaintDAO.updateStatus(complaint.getComplaintId(), targetStatus,
                        resolutionNotes, rejectionReason, resolvedAt, conn);
                if (!ok) {
                    throw new SQLException("Failed to update complaint status.");
                }

                // 2. Add history record
                ComplaintUpdate update = new ComplaintUpdate();
                update.setComplaintId(complaint.getComplaintId());
                update.setUpdatedBy(actor.getUserId());
                update.setEventType("STATUS_CHANGE");
                update.setOldStatus(currentStatus);
                update.setNewStatus(targetStatus);
                
                String historyComment = comment;
                if ("RESOLVED".equals(targetStatus) && (historyComment == null || historyComment.trim().isEmpty())) {
                    historyComment = "Resolved: " + resolutionNotes;
                } else if ("REJECTED".equals(targetStatus) && (historyComment == null || historyComment.trim().isEmpty())) {
                    historyComment = "Rejected: " + rejectionReason;
                }
                update.setComment(historyComment);
                update.setVisibility("PUBLIC");
                updateDAO.addUpdate(update, conn);

                // 3. Notify complaint owner (student)
                Notification notif = new Notification();
                notif.setUserId(complaint.getUserId());
                notif.setComplaintId(complaint.getComplaintId());
                notif.setMessage("Complaint " + complaint.getTicketNumber() + " status updated to " +
                        formatStatusLabel(targetStatus) + (historyComment != null ? ": " + historyComment : ""));
                notificationDAO.createNotification(notif, conn);

                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Assigns department and staff member to a complaint.
     */
    public boolean assignComplaint(Complaint complaint, Integer deptId, Integer staffId,
                                   String comment, User actor) throws Exception {
        if (!actor.isAdmin()) {
            throw new IllegalAccessException("Only administrators can assign complaints.");
        }
        if (deptId == null || deptId <= 0) {
            throw new IllegalArgumentException("Department is required for assignment.");
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Update complaint assignment
                boolean ok = complaintDAO.updateAssignment(complaint.getComplaintId(), deptId, staffId, conn);
                if (!ok) {
                    throw new SQLException("Failed to update assignment.");
                }

                // Also if status was SUBMITTED or UNDER_REVIEW, move to ASSIGNED
                String newStatus = complaint.getStatus();
                if ("SUBMITTED".equalsIgnoreCase(newStatus) || "UNDER_REVIEW".equalsIgnoreCase(newStatus)) {
                    newStatus = "ASSIGNED";
                    complaintDAO.updateStatus(complaint.getComplaintId(), "ASSIGNED", null, null, null, conn);
                }

                // 2. Timeline update
                ComplaintUpdate update = new ComplaintUpdate();
                update.setComplaintId(complaint.getComplaintId());
                update.setUpdatedBy(actor.getUserId());
                update.setEventType("ASSIGNED");
                update.setOldStatus(complaint.getStatus());
                update.setNewStatus(newStatus);
                update.setComment(comment != null && !comment.trim().isEmpty() ? comment : "Assigned to department.");
                update.setVisibility("PUBLIC");
                updateDAO.addUpdate(update, conn);

                // 3. Notification to student
                Notification notifStudent = new Notification();
                notifStudent.setUserId(complaint.getUserId());
                notifStudent.setComplaintId(complaint.getComplaintId());
                notifStudent.setMessage("Complaint " + complaint.getTicketNumber() + " has been assigned to department.");
                notificationDAO.createNotification(notifStudent, conn);

                // 4. Notification to assigned staff member if designated
                if (staffId != null && staffId > 0) {
                    Notification notifStaff = new Notification();
                    notifStaff.setUserId(staffId);
                    notifStaff.setComplaintId(complaint.getComplaintId());
                    notifStaff.setMessage("You have been assigned ticket " + complaint.getTicketNumber() + ": " + complaint.getTitle());
                    notificationDAO.createNotification(notifStaff, conn);
                }

                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Adds an internal staff note or public comment.
     */
    public boolean addComment(Complaint complaint, String comment, boolean isInternal, User actor) throws Exception {
        if (!ValidationUtil.isNotEmpty(comment)) {
            throw new IllegalArgumentException("Comment text cannot be empty.");
        }

        ComplaintUpdate update = new ComplaintUpdate();
        update.setComplaintId(complaint.getComplaintId());
        update.setUpdatedBy(actor.getUserId());
        update.setEventType(isInternal ? "INTERNAL_NOTE" : "COMMENT");
        update.setOldStatus(complaint.getStatus());
        update.setNewStatus(complaint.getStatus());
        update.setComment(comment.trim());
        update.setVisibility(isInternal ? "INTERNAL" : "PUBLIC");

        boolean ok = updateDAO.addUpdate(update, null);
        if (ok && !isInternal && actor.getUserId() != complaint.getUserId()) {
            // Notify student if a staff/admin left a public comment
            Notification notif = new Notification();
            notif.setUserId(complaint.getUserId());
            notif.setComplaintId(complaint.getComplaintId());
            notif.setMessage("New comment on complaint " + complaint.getTicketNumber() + ": " + comment.trim());
            notificationDAO.createNotification(notif, null);
        }
        return ok;
    }

    /**
     * Student feedback submission for a resolved complaint.
     */
    public boolean submitFeedback(Complaint complaint, int rating, String comment, User student) throws Exception {
        if (complaint.getUserId() != student.getUserId()) {
            throw new IllegalAccessException("You can only submit feedback for your own complaint.");
        }
        if (!"RESOLVED".equalsIgnoreCase(complaint.getStatus())) {
            throw new IllegalStateException("Feedback can only be provided for resolved complaints.");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        if (feedbackDAO.findByComplaintId(complaint.getComplaintId()) != null) {
            throw new IllegalStateException("Feedback has already been submitted for this complaint.");
        }

        Feedback f = new Feedback();
        f.setComplaintId(complaint.getComplaintId());
        f.setUserId(student.getUserId());
        f.setRating(rating);
        f.setComment(comment != null ? comment.trim() : null);

        return feedbackDAO.createFeedback(f);
    }

    /**
     * Reopens a resolved complaint.
     */
    public boolean reopenComplaint(Complaint complaint, String reason, User actor) throws Exception {
        if (!"RESOLVED".equalsIgnoreCase(complaint.getStatus())) {
            throw new IllegalStateException("Only resolved complaints can be reopened.");
        }
        if (!ValidationUtil.isNotEmpty(reason)) {
            throw new IllegalArgumentException("A reason is required to reopen a complaint.");
        }

        // Complainant owner or Admin can reopen
        if (actor.isComplainant() && actor.getUserId() != complaint.getUserId()) {
            throw new IllegalAccessException("You cannot reopen another user's complaint.");
        }

        return changeStatus(complaint, "REOPENED", "Complaint reopened: " + reason.trim(), null, null, actor);
    }

    private void validateStatusTransition(String current, String target, User actor,
                                           String rejectionReason, String resolutionNotes) {
        if (current.equals(target)) {
            throw new IllegalArgumentException("Complaint is already in " + target + " status.");
        }

        if ("REJECTED".equals(target)) {
            if (!actor.isAdmin()) {
                throw new IllegalArgumentException("Only administrators can reject complaints.");
            }
            if (!ValidationUtil.isNotEmpty(rejectionReason)) {
                throw new IllegalArgumentException("Rejection reason is required.");
            }
            if ("RESOLVED".equals(current)) {
                throw new IllegalArgumentException("A resolved complaint cannot be rejected.");
            }
            return;
        }

        if ("RESOLVED".equals(target)) {
            if (actor.isComplainant()) {
                throw new IllegalArgumentException("Complainants cannot mark complaints as resolved.");
            }
            if (!ValidationUtil.isNotEmpty(resolutionNotes)) {
                throw new IllegalArgumentException("Resolution notes are required when marking a complaint as resolved.");
            }
            return;
        }

        if ("REOPENED".equals(target)) {
            if (!"RESOLVED".equals(current)) {
                throw new IllegalArgumentException("Only resolved complaints can be reopened.");
            }
            return;
        }

        if ("UNDER_REVIEW".equals(target)) {
            if (!actor.isAdmin()) {
                throw new IllegalArgumentException("Only administrators can move tickets to Under Review.");
            }
            return;
        }

        if ("ASSIGNED".equals(target)) {
            if (!actor.isAdmin()) {
                throw new IllegalArgumentException("Only administrators can move tickets to Assigned.");
            }
            return;
        }

        if ("IN_PROGRESS".equals(target)) {
            if (actor.isComplainant()) {
                throw new IllegalArgumentException("Complainants cannot change complaint to In Progress.");
            }
            return;
        }
    }

    private String formatStatusLabel(String status) {
        switch (status) {
            case "SUBMITTED": return "Submitted";
            case "UNDER_REVIEW": return "Under Review";
            case "ASSIGNED": return "Assigned";
            case "IN_PROGRESS": return "In Progress";
            case "RESOLVED": return "Resolved";
            case "REJECTED": return "Rejected";
            case "REOPENED": return "Reopened";
            default: return status;
        }
    }
}
