package com.campuscare.model;

import java.util.HashMap;
import java.util.Map;

public class DashboardStats {
    private int totalComplaints;
    private int submittedCount;
    private int underReviewCount;
    private int assignedCount;
    private int inProgressCount;
    private int resolvedCount;
    private int rejectedCount;
    private int reopenedCount;
    private int overdueCount;
    private int urgentCount;
    
    private double avgResolutionTimeHours;
    private double avgFeedbackRating;
    private int totalFeedbackCount;

    private Map<String, Integer> categoryDistribution = new HashMap<>();
    private Map<String, Integer> statusDistribution = new HashMap<>();
    private Map<String, Integer> departmentDistribution = new HashMap<>();

    public DashboardStats() {}

    public int getTotalComplaints() { return totalComplaints; }
    public void setTotalComplaints(int totalComplaints) { this.totalComplaints = totalComplaints; }

    public int getSubmittedCount() { return submittedCount; }
    public void setSubmittedCount(int submittedCount) { this.submittedCount = submittedCount; }

    public int getUnderReviewCount() { return underReviewCount; }
    public void setUnderReviewCount(int underReviewCount) { this.underReviewCount = underReviewCount; }

    public int getAssignedCount() { return assignedCount; }
    public void setAssignedCount(int assignedCount) { this.assignedCount = assignedCount; }

    public int getInProgressCount() { return inProgressCount; }
    public void setInProgressCount(int inProgressCount) { this.inProgressCount = inProgressCount; }

    public int getResolvedCount() { return resolvedCount; }
    public void setResolvedCount(int resolvedCount) { this.resolvedCount = resolvedCount; }

    public int getRejectedCount() { return rejectedCount; }
    public void setRejectedCount(int rejectedCount) { this.rejectedCount = rejectedCount; }

    public int getReopenedCount() { return reopenedCount; }
    public void setReopenedCount(int reopenedCount) { this.reopenedCount = reopenedCount; }

    public int getOverdueCount() { return overdueCount; }
    public void setOverdueCount(int overdueCount) { this.overdueCount = overdueCount; }

    public int getUrgentCount() { return urgentCount; }
    public void setUrgentCount(int urgentCount) { this.urgentCount = urgentCount; }

    public double getAvgResolutionTimeHours() { return avgResolutionTimeHours; }
    public void setAvgResolutionTimeHours(double avgResolutionTimeHours) { this.avgResolutionTimeHours = avgResolutionTimeHours; }

    public double getAvgFeedbackRating() { return avgFeedbackRating; }
    public void setAvgFeedbackRating(double avgFeedbackRating) { this.avgFeedbackRating = avgFeedbackRating; }

    public int getTotalFeedbackCount() { return totalFeedbackCount; }
    public void setTotalFeedbackCount(int totalFeedbackCount) { this.totalFeedbackCount = totalFeedbackCount; }

    public Map<String, Integer> getCategoryDistribution() { return categoryDistribution; }
    public void setCategoryDistribution(Map<String, Integer> categoryDistribution) { this.categoryDistribution = categoryDistribution; }

    public Map<String, Integer> getStatusDistribution() { return statusDistribution; }
    public void setStatusDistribution(Map<String, Integer> statusDistribution) { this.statusDistribution = statusDistribution; }

    public Map<String, Integer> getDepartmentDistribution() { return departmentDistribution; }
    public void setDepartmentDistribution(Map<String, Integer> departmentDistribution) { this.departmentDistribution = departmentDistribution; }

    public int getActivePendingCount() {
        return submittedCount + underReviewCount + assignedCount + inProgressCount;
    }
}
