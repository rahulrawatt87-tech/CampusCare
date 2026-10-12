package com.campuscare.service;

import com.campuscare.dao.ComplaintDAO;
import com.campuscare.dao.FeedbackDAO;
import com.campuscare.model.Complaint;
import com.campuscare.model.DashboardStats;
import com.campuscare.model.Feedback;
import com.campuscare.util.ValidationUtil;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

public class ReportService {

    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    public DashboardStats getAggregateReportData() {
        return complaintDAO.getDashboardStats();
    }

    public List<Feedback> getAllFeedback(int limit) {
        return feedbackDAO.findAll(limit);
    }

    /**
     * Exports complaints to CSV format.
     * Applies formula injection protection using ValidationUtil.escapeCsv().
     */
    public void exportComplaintsToCsv(PrintWriter writer, String status, Integer categoryId,
                                      String priority, Integer deptId, String search, Boolean overdueOnly) {
        // Retrieve filtered complaints (up to 5000 rows for export)
        List<Complaint> list = complaintDAO.findComplaints(status, categoryId, priority, deptId, search, overdueOnly, 0, 5000);

        // CSV Header
        writer.println("Ticket Number,Title,Category,Priority,Status,Student Name,Student ID,Location,Assigned Department,Assigned Staff,Submitted Date,Resolved Date,Is Overdue");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Complaint c : list) {
            StringBuilder sb = new StringBuilder();
            sb.append(ValidationUtil.escapeCsv(c.getTicketNumber())).append(",");
            sb.append(ValidationUtil.escapeCsv(c.getTitle())).append(",");
            sb.append(ValidationUtil.escapeCsv(c.getCategoryName())).append(",");
            sb.append(ValidationUtil.escapeCsv(c.getPriority())).append(",");
            sb.append(ValidationUtil.escapeCsv(c.getStatus())).append(",");
            sb.append(ValidationUtil.escapeCsv(c.getStudentName())).append(",");
            sb.append(ValidationUtil.escapeCsv(c.getStudentId())).append(",");
            sb.append(ValidationUtil.escapeCsv(c.getLocation())).append(",");
            sb.append(ValidationUtil.escapeCsv(c.getAssignedDepartmentName() != null ? c.getAssignedDepartmentName() : "Unassigned")).append(",");
            sb.append(ValidationUtil.escapeCsv(c.getAssignedStaffName() != null ? c.getAssignedStaffName() : "Unassigned")).append(",");
            sb.append(ValidationUtil.escapeCsv(c.getCreatedAt() != null ? sdf.format(c.getCreatedAt()) : "")).append(",");
            sb.append(ValidationUtil.escapeCsv(c.getResolvedAt() != null ? sdf.format(c.getResolvedAt()) : "")).append(",");
            sb.append(c.isOverdue() ? "YES" : "NO");
            writer.println(sb.toString());
        }
    }
}
