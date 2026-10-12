<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Staff Dashboard — CampusCare" scope="request"/>
<c:set var="activeNav" value="dashboard" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<div class="app-wrapper">
    <jsp:include page="/WEB-INF/views/shared/sidebar.jsp"/>

    <main class="main-content">
        <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
            <div>
                <h2 class="fw-bold mb-1">Staff workspace</h2>
                <p class="text-muted small mb-0">Tickets routed to your department (<c:out value="${sessionScope.currentUser.departmentName != null ? sessionScope.currentUser.departmentName : 'All Departments'}"/>).</p>
            </div>
            <div>
                <span class="badge bg-light text-dark border p-2">
                    <i class="bi bi-person-badge me-1"></i> <c:out value="${sessionScope.currentUser.fullName}"/>
                </span>
            </div>
        </div>

        <!-- Metric Cards -->
        <div class="row g-3 mb-4">
            <div class="col-6 col-lg-3">
                <div class="metric-card">
                    <div class="metric-title">Department Tickets</div>
                    <div class="metric-value"><fmt:formatNumber minIntegerDigits="2" value="${totalCount}"/></div>
                </div>
            </div>

            <div class="col-6 col-lg-3">
                <div class="metric-card">
                    <div class="metric-title">In Progress</div>
                    <div class="metric-value text-warning"><fmt:formatNumber minIntegerDigits="2" value="${inProgressCount}"/></div>
                </div>
            </div>

            <div class="col-6 col-lg-3">
                <div class="metric-card">
                    <div class="metric-title">Resolved</div>
                    <div class="metric-value text-success"><fmt:formatNumber minIntegerDigits="2" value="${resolvedCount}"/></div>
                </div>
            </div>

            <div class="col-6 col-lg-3">
                <div class="metric-card">
                    <div class="metric-title">Overdue Attention</div>
                    <div class="metric-value text-danger"><fmt:formatNumber minIntegerDigits="2" value="${overdueCount}"/></div>
                </div>
            </div>
        </div>

        <!-- Filter Card -->
        <div class="cc-card p-3 mb-4">
            <form action="${pageContext.request.contextPath}/staff/dashboard" method="get" class="row g-2 align-items-center">
                <div class="col-md-7">
                    <div class="input-group">
                        <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-search"></i></span>
                        <input type="text" class="form-control border-start-0" name="search"
                               placeholder="Search ticket code, title or location..."
                               value="<c:out value='${searchQuery != null ? searchQuery : \"\"}'/>">
                    </div>
                </div>

                <div class="col-md-3">
                    <select class="form-select" name="status">
                        <option value="">All Statuses</option>
                        <option value="ASSIGNED" ${'ASSIGNED'.equalsIgnoreCase(selectedStatus) ? 'selected' : ''}>Assigned (New)</option>
                        <option value="IN_PROGRESS" ${'IN_PROGRESS'.equalsIgnoreCase(selectedStatus) ? 'selected' : ''}>In progress</option>
                        <option value="RESOLVED" ${'RESOLVED'.equalsIgnoreCase(selectedStatus) ? 'selected' : ''}>Resolved</option>
                    </select>
                </div>

                <div class="col-md-2 d-flex gap-1">
                    <button type="submit" class="btn btn-cc-primary w-100"><i class="bi bi-funnel me-1"></i> Filter</button>
                    <c:if test="${not empty searchQuery || not empty selectedStatus}">
                        <a href="${pageContext.request.contextPath}/staff/dashboard" class="btn btn-outline-secondary"><i class="bi bi-x-lg"></i></a>
                    </c:if>
                </div>
            </form>
        </div>

        <!-- Assigned Tickets Table -->
        <div class="cc-card">
            <div class="cc-card-header">
                <h5 class="cc-card-title">Assigned Tickets</h5>
                <span class="text-muted small">${assignedComplaints.size()} tickets found</span>
            </div>
            <div class="table-responsive">
                <c:choose>
                    <c:when test="${not empty assignedComplaints}">
                        <table class="cc-table">
                            <thead>
                                <tr>
                                    <th>Ticket</th>
                                    <th>Issue</th>
                                    <th>Student</th>
                                    <th>Priority</th>
                                    <th>Status</th>
                                    <th>Submitted</th>
                                    <th class="text-end">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="c" items="${assignedComplaints}">
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
                                            <div class="small text-dark"><c:out value="${c.studentName}"/></div>
                                            <div class="text-muted small"><c:out value="${c.studentPhone != null ? c.studentPhone : ''}"/></div>
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
                                        <td>
                                            <div class="text-muted small">
                                                <fmt:formatDate value="${c.createdAt}" pattern="dd MMM yyyy"/>
                                            </div>
                                        </td>
                                        <td class="text-end">
                                            <a href="${pageContext.request.contextPath}/complaints/${c.ticketNumber}" class="btn btn-sm btn-outline-secondary">
                                                Process Ticket
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <i class="bi bi-check2-circle empty-state-icon text-success"></i>
                            <div class="empty-state-title">No assigned tickets</div>
                            <div class="empty-state-desc">Your department queue is clear or no tickets match the current filter.</div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
