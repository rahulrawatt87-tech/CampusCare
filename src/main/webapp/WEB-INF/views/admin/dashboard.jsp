<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Administration Overview — CampusCare" scope="request"/>
<c:set var="activeNav" value="dashboard" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<div class="app-wrapper">
    <jsp:include page="/WEB-INF/views/shared/sidebar.jsp"/>

    <main class="main-content">
        <!-- Top bar -->
        <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
            <div>
                <h2 class="fw-bold mb-1">Administration overview</h2>
                <p class="text-muted small mb-0">Review new reports, assign work and monitor resolution across campus departments.</p>
            </div>
            <div class="d-flex gap-2">
                <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-outline-secondary btn-sm">
                    <i class="bi bi-bar-chart me-1"></i> Reports & Analytics
                </a>
                <a href="${pageContext.request.contextPath}/admin/complaints" class="btn btn-cc-primary btn-sm">
                    <i class="bi bi-inbox me-1"></i> Full Complaint Queue
                </a>
            </div>
        </div>

        <!-- 4 Metric Cards (Matches Mockup 9.4) -->
        <div class="row g-3 mb-4">
            <div class="col-6 col-lg-3">
                <div class="metric-card">
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <span class="metric-title mb-0">New</span>
                        <span class="badge bg-primary-subtle text-primary" style="font-size:0.68rem;">LIVE</span>
                    </div>
                    <div class="metric-value"><fmt:formatNumber minIntegerDigits="2" value="${stats.submittedCount}"/></div>
                </div>
            </div>

            <div class="col-6 col-lg-3">
                <div class="metric-card">
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <span class="metric-title mb-0">In review</span>
                        <span class="badge bg-purple-subtle text-purple" style="font-size:0.68rem; background-color:#EDE9FE; color:#7C3AED;">LIVE</span>
                    </div>
                    <div class="metric-value"><fmt:formatNumber minIntegerDigits="2" value="${stats.underReviewCount}"/></div>
                </div>
            </div>

            <div class="col-6 col-lg-3">
                <div class="metric-card">
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <span class="metric-title mb-0">Overdue</span>
                        <span class="badge bg-danger-subtle text-danger" style="font-size:0.68rem;">LIVE</span>
                    </div>
                    <div class="metric-value text-danger"><fmt:formatNumber minIntegerDigits="2" value="${stats.overdueCount}"/></div>
                </div>
            </div>

            <div class="col-6 col-lg-3">
                <div class="metric-card">
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <span class="metric-title mb-0">Resolved</span>
                        <span class="badge bg-success-subtle text-success" style="font-size:0.68rem;">LIVE</span>
                    </div>
                    <div class="metric-value text-success"><fmt:formatNumber minIntegerDigits="2" value="${stats.resolvedCount}"/></div>
                </div>
            </div>
        </div>

        <div class="row g-4">
            <!-- Left Area: Complaint Queue (Matches Mockup 9.4) -->
            <div class="col-lg-8">
                <div class="cc-card">
                    <div class="cc-card-header">
                        <h5 class="cc-card-title">Complaint queue</h5>
                        <a href="${pageContext.request.contextPath}/admin/complaints" class="small fw-semibold text-secondary">
                            View all tickets <i class="bi bi-arrow-right"></i>
                        </a>
                    </div>

                    <div class="table-responsive">
                        <c:choose>
                            <c:when test="${not empty recentQueue}">
                                <table class="cc-table">
                                    <thead>
                                        <tr>
                                            <th>Ticket</th>
                                            <th>Complaint</th>
                                            <th>Priority</th>
                                            <th>Status</th>
                                            <th class="text-end">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="c" items="${recentQueue}">
                                            <tr>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/complaints/${c.ticketNumber}" class="ticket-code">
                                                        <c:out value="${c.ticketNumber}"/>
                                                    </a>
                                                </td>
                                                <td>
                                                    <div class="fw-semibold text-dark"><c:out value="${c.title}"/></div>
                                                    <div class="text-muted small"><c:out value="${c.categoryName}"/> • <c:out value="${c.studentName}"/></div>
                                                </td>
                                                <td>
                                                    <span class="badge-priority ${c.priorityBadgeClass}"><c:out value="${c.priority}"/></span>
                                                </td>
                                                <td>
                                                    <span class="badge-status ${c.statusBadgeClass}">
                                                        <c:out value="${c.formattedStatus}"/>
                                                    </span>
                                                    <c:if test="${c.overdue}">
                                                        <span class="badge-overdue ms-1"><i class="bi bi-clock-history"></i> Overdue</span>
                                                    </c:if>
                                                </td>
                                                <td class="text-end">
                                                    <a href="${pageContext.request.contextPath}/complaints/${c.ticketNumber}" class="btn btn-sm btn-outline-secondary">
                                                        Process
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <i class="bi bi-inbox empty-state-icon"></i>
                                    <div class="empty-state-title">No complaints in the queue</div>
                                    <div class="empty-state-desc">All student tickets have been resolved or no submissions are currently pending.</div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Right Area: Category Distribution & Needs Attention (Matches Mockup 9.4) -->
            <div class="col-lg-4">
                
                <!-- Complaints by category -->
                <div class="cc-card mb-4">
                    <div class="cc-card-header">
                        <h6 class="cc-card-title">Complaints by category</h6>
                    </div>
                    <div class="cc-card-body">
                        <c:forEach var="entry" items="${stats.categoryDistribution}">
                            <div class="mb-3">
                                <div class="d-flex justify-content-between small fw-semibold mb-1">
                                    <span><c:out value="${entry.key}"/></span>
                                    <span class="text-muted">${entry.value}</span>
                                </div>
                                <div class="progress" style="height: 6px;">
                                    <div class="progress-bar" role="progressbar"
                                         style="width: ${stats.totalComplaints > 0 ? (entry.value * 100 / stats.totalComplaints) : 0}%; background-color: var(--cc-primary);"></div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <!-- Needs attention / Overdue queue -->
                <div class="cc-card">
                    <div class="cc-card-header bg-danger-subtle border-danger-subtle">
                        <h6 class="cc-card-title text-danger mb-0">
                            <i class="bi bi-exclamation-octagon-fill me-1"></i> Needs attention
                        </h6>
                    </div>
                    <div class="cc-card-body p-0">
                        <c:choose>
                            <c:when test="${not empty overdueList}">
                                <ul class="list-group list-group-flush">
                                    <c:forEach var="ov" items="${overdueList}">
                                        <li class="list-group-item p-3">
                                            <div class="d-flex justify-content-between align-items-center mb-1">
                                                <a href="${pageContext.request.contextPath}/complaints/${ov.ticketNumber}" class="ticket-code small">
                                                    <c:out value="${ov.ticketNumber}"/>
                                                </a>
                                                <span class="badge-overdue">Overdue</span>
                                            </div>
                                            <div class="fw-semibold small text-truncate"><c:out value="${ov.title}"/></div>
                                            <div class="text-muted" style="font-size:0.75rem;">
                                                Open for ${ov.daysOpen} days • <c:out value="${ov.assignedDepartmentName != null ? ov.assignedDepartmentName : 'Unassigned'}"/>
                                            </div>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:when>
                            <c:otherwise>
                                <div class="p-4 text-center text-muted small">
                                    <i class="bi bi-check2-all text-success fs-3 d-block mb-1"></i>
                                    No overdue tickets at this moment.
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
