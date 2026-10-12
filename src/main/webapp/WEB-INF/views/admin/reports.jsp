<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Reports & Analytics — CampusCare" scope="request"/>
<c:set var="activeNav" value="reports" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<div class="app-wrapper">
    <jsp:include page="/WEB-INF/views/shared/sidebar.jsp"/>

    <main class="main-content">
        <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
            <div>
                <h2 class="fw-bold mb-1">Reports & analytics</h2>
                <p class="text-muted small mb-0">System metrics, resolution lead time, feedback averages and exportable data.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/admin/reports/export.csv" class="btn btn-cc-primary btn-sm">
                    <i class="bi bi-file-earmark-spreadsheet me-1"></i> Export All Complaints (CSV)
                </a>
            </div>
        </div>

        <!-- Metric KPI Cards -->
        <div class="row g-3 mb-4">
            <div class="col-6 col-lg-3">
                <div class="metric-card">
                    <div class="metric-title">Total Lodged</div>
                    <div class="metric-value"><fmt:formatNumber minIntegerDigits="2" value="${stats.totalComplaints}"/></div>
                </div>
            </div>

            <div class="col-6 col-lg-3">
                <div class="metric-card">
                    <div class="metric-title">Avg. Resolution Lead Time</div>
                    <div class="metric-value fs-2">
                        <c:choose>
                            <c:when test="${stats.avgResolutionTimeHours > 0}">
                                <fmt:formatNumber maxFractionDigits="1" value="${stats.avgResolutionTimeHours}"/> <span class="fs-6 text-muted">hrs</span>
                            </c:when>
                            <c:otherwise>N/A</c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <div class="col-6 col-lg-3">
                <div class="metric-card">
                    <div class="metric-title">Student Satisfaction</div>
                    <div class="d-flex align-items-center gap-2">
                        <div class="metric-value fs-2 text-warning">
                            <c:choose>
                                <c:when test="${stats.avgFeedbackRating > 0}">
                                    <fmt:formatNumber maxFractionDigits="1" value="${stats.avgFeedbackRating}"/> <span class="fs-6 text-dark">★</span>
                                </c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>
                        </div>
                        <span class="text-muted small">(${stats.totalFeedbackCount} reviews)</span>
                    </div>
                </div>
            </div>

            <div class="col-6 col-lg-3">
                <div class="metric-card">
                    <div class="metric-title">SLA Overdue Rate</div>
                    <div class="metric-value fs-2 text-danger">
                        <c:choose>
                            <c:when test="${stats.totalComplaints > 0}">
                                <fmt:formatNumber maxFractionDigits="1" value="${(stats.overdueCount * 100.0) / stats.totalComplaints}"/>%
                            </c:when>
                            <c:otherwise>0%</c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>

        <!-- Charts Row -->
        <div class="row g-4 mb-4">
            <!-- Category Distribution Bar Chart -->
            <div class="col-lg-7">
                <div class="cc-card h-100">
                    <div class="cc-card-header">
                        <h6 class="cc-card-title"><i class="bi bi-bar-chart-fill me-2 text-secondary"></i>Complaints by Category</h6>
                    </div>
                    <div class="cc-card-body">
                        <canvas id="categoryChart" style="max-height: 280px;"></canvas>
                    </div>
                </div>
            </div>

            <!-- Status Breakdown Doughnut Chart -->
            <div class="col-lg-5">
                <div class="cc-card h-100">
                    <div class="cc-card-header">
                        <h6 class="cc-card-title"><i class="bi bi-pie-chart-fill me-2 text-primary"></i>Status Distribution</h6>
                    </div>
                    <div class="cc-card-body d-flex align-items-center justify-content-center">
                        <canvas id="statusChart" style="max-height: 250px; max-width: 250px;"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- Student Feedback Reviews Table -->
        <div class="cc-card">
            <div class="cc-card-header">
                <h5 class="cc-card-title"><i class="bi bi-chat-heart-fill text-danger me-2"></i>Recent Student Feedback & Ratings</h5>
                <span class="text-muted small">${feedbackList.size()} ratings recorded</span>
            </div>
            <div class="table-responsive">
                <c:choose>
                    <c:when test="${not empty feedbackList}">
                        <table class="cc-table">
                            <thead>
                                <tr>
                                    <th>Ticket</th>
                                    <th>Student</th>
                                    <th>Rating</th>
                                    <th>Feedback Comments</th>
                                    <th>Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="f" items="${feedbackList}">
                                    <tr>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/complaints/${f.ticketNumber}" class="ticket-code">
                                                <c:out value="${f.ticketNumber}"/>
                                            </a>
                                        </td>
                                        <td><c:out value="${f.studentName}"/></td>
                                        <td>
                                            <span class="text-warning fw-bold">
                                                <c:forEach begin="1" end="${f.rating}">★</c:forEach>
                                                <c:forEach begin="${f.rating + 1}" end="5">☆</c:forEach>
                                            </span>
                                            <span class="ms-1 small text-dark">(${f.rating}/5)</span>
                                        </td>
                                        <td>
                                            <div class="small text-dark"><c:out value="${f.comment != null ? f.comment : 'No written comment'}"/></div>
                                        </td>
                                        <td>
                                            <div class="text-muted small">
                                                <fmt:formatDate value="${f.createdAt}" pattern="dd MMM yyyy"/>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state py-4">
                            <i class="bi bi-star empty-state-icon text-muted"></i>
                            <div class="empty-state-title">No feedback received yet</div>
                            <div class="empty-state-desc">Once students verify resolved complaints, their ratings and reviews will show here.</div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>
</div>

<!-- Chart.js Scripts Initialization -->
<script>
document.addEventListener("DOMContentLoaded", function () {
    // 1. Category Bar Chart
    const catLabels = [];
    const catData = [];
    <c:forEach var="entry" items="${stats.categoryDistribution}">
        catLabels.push("<c:out value='${entry.key}'/>");
        catData.push(${entry.value});
    </c:forEach>

    const ctxCat = document.getElementById('categoryChart');
    if (ctxCat && catLabels.length > 0) {
        new Chart(ctxCat, {
            type: 'bar',
            data: {
                labels: catLabels,
                datasets: [{
                    label: 'Complaints',
                    data: catData,
                    backgroundColor: '#176B72',
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    y: { beginAtZero: true, ticks: { precision: 0 } }
                }
            }
        });
    }

    // 2. Status Doughnut Chart
    const statusLabels = [];
    const statusData = [];
    <c:forEach var="entry" items="${stats.statusDistribution}">
        statusLabels.push("<c:out value='${entry.key}'/>");
        statusData.push(${entry.value});
    </c:forEach>

    const ctxStat = document.getElementById('statusChart');
    if (ctxStat && statusLabels.length > 0) {
        new Chart(ctxStat, {
            type: 'doughnut',
            data: {
                labels: statusLabels,
                datasets: [{
                    data: statusData,
                    backgroundColor: ['#0284C7', '#7C3AED', '#0D9488', '#D97706', '#16A34A', '#DC2626', '#4F46E5']
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });
    }
});
</script>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
