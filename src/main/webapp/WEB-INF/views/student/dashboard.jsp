<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Student Dashboard — CampusCare" scope="request"/>
<c:set var="activeNav" value="dashboard" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<div class="app-wrapper">
    <jsp:include page="/WEB-INF/views/shared/sidebar.jsp"/>

    <main class="main-content">
        <!-- Top Greeting Bar -->
        <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
            <div>
                <h2 class="fw-bold mb-1"><c:out value="${greeting}"/>, <c:out value="${sessionScope.currentUser.fullName.split(' ')[0]}"/></h2>
                <p class="text-muted small mb-0">Here is the latest activity on your campus service requests.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/complaints/new" class="btn btn-cc-primary">
                    <i class="bi bi-plus-lg me-1"></i> New complaint
                </a>
            </div>
        </div>

        <!-- Metric Stat Cards (Matches Mockup 9.2) -->
        <div class="row g-3 mb-4">
            <div class="col-6 col-lg-3">
                <div class="metric-card">
                    <div class="metric-title">Total complaints</div>
                    <div class="d-flex align-items-center justify-content-between">
                        <div class="metric-value"><fmt:formatNumber minIntegerDigits="2" value="${stats.totalComplaints}"/></div>
                        <i class="bi bi-folder2-open fs-3 text-muted"></i>
                    </div>
                </div>
            </div>

            <div class="col-6 col-lg-3">
                <div class="metric-card">
                    <div class="metric-title">In progress</div>
                    <div class="d-flex align-items-center justify-content-between">
                        <div class="metric-value"><fmt:formatNumber minIntegerDigits="2" value="${stats.inProgressCount}"/></div>
                        <span class="metric-indicator live"></span>
                    </div>
                </div>
            </div>

            <div class="col-6 col-lg-3">
                <div class="metric-card">
                    <div class="metric-title">Resolved</div>
                    <div class="d-flex align-items-center justify-content-between">
                        <div class="metric-value"><fmt:formatNumber minIntegerDigits="2" value="${stats.resolvedCount}"/></div>
                        <i class="bi bi-check-circle-fill text-success fs-3"></i>
                    </div>
                </div>
            </div>

            <div class="col-6 col-lg-3">
                <div class="metric-card">
                    <div class="metric-title">Needs attention</div>
                    <div class="d-flex align-items-center justify-content-between">
                        <div class="metric-value"><fmt:formatNumber minIntegerDigits="2" value="${stats.overdueCount}"/></div>
                        <span class="metric-indicator alert"></span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Recent Complaints Table -->
        <div class="cc-card">
            <div class="cc-card-header">
                <h5 class="cc-card-title">Recent complaints</h5>
                <a href="${pageContext.request.contextPath}/complaints" class="text-secondary small fw-semibold">
                    View all <i class="bi bi-arrow-right"></i>
                </a>
            </div>

            <div class="table-responsive">
                <c:choose>
                    <c:when test="${not empty recentComplaints}">
                        <table class="cc-table">
                            <thead>
                                <tr>
                                    <th>Ticket</th>
                                    <th>Issue</th>
                                    <th>Submitted</th>
                                    <th>Status</th>
                                    <th class="text-end">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="c" items="${recentComplaints}">
                                    <tr>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/complaints/${c.ticketNumber}" class="ticket-code">
                                                <c:out value="${c.ticketNumber}"/>
                                            </a>
                                        </td>
                                        <td>
                                            <div class="fw-semibold text-dark"><c:out value="${c.title}"/></div>
                                            <div class="text-muted small"><i class="bi bi-geo-alt me-1"></i><c:out value="${c.location}"/></div>
                                        </td>
                                        <td>
                                            <div class="text-muted small">
                                                <fmt:formatDate value="${c.createdAt}" pattern="dd MMM yyyy"/>
                                            </div>
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
                                                View details
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
                            <div class="empty-state-title">No complaints lodged yet</div>
                            <div class="empty-state-desc">You haven't submitted any campus service requests. If you notice any infrastructure or service issue, submit a ticket.</div>
                            <a href="${pageContext.request.contextPath}/complaints/new" class="btn btn-cc-primary">
                                <i class="bi bi-plus-lg me-1"></i> Submit your first complaint
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
