<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Manage Complaints — CampusCare" scope="request"/>
<c:set var="activeNav" value="complaint-queue" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<div class="app-wrapper">
    <jsp:include page="/WEB-INF/views/shared/sidebar.jsp"/>

    <main class="main-content">
        <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
            <div>
                <h2 class="fw-bold mb-1">Manage complaints</h2>
                <p class="text-muted small mb-0">Search, triage, assign departments, and manage status transitions across all campus tickets.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/admin/reports/export.csv?status=${selectedStatus}&categoryId=${selectedCategoryId}&priority=${selectedPriority}&deptId=${selectedDeptId}&search=${searchQuery}&overdue=${overdueOnly}" class="btn btn-outline-secondary btn-sm">
                    <i class="bi bi-file-earmark-arrow-down me-1"></i> Export to CSV
                </a>
            </div>
        </div>

        <!-- Filter Bar -->
        <div class="cc-card p-3 mb-4">
            <form action="${pageContext.request.contextPath}/admin/complaints" method="get" class="row g-2 align-items-center">
                <div class="col-md-3">
                    <div class="input-group">
                        <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-search"></i></span>
                        <input type="text" class="form-control border-start-0" name="search"
                               placeholder="Search ticket, title, student..."
                               value="<c:out value='${searchQuery != null ? searchQuery : \"\"}'/>">
                    </div>
                </div>

                <div class="col-md-2">
                    <select class="form-select" name="status">
                        <option value="">All Statuses</option>
                        <option value="SUBMITTED" ${'SUBMITTED'.equalsIgnoreCase(selectedStatus) ? 'selected' : ''}>Submitted</option>
                        <option value="UNDER_REVIEW" ${'UNDER_REVIEW'.equalsIgnoreCase(selectedStatus) ? 'selected' : ''}>Under review</option>
                        <option value="ASSIGNED" ${'ASSIGNED'.equalsIgnoreCase(selectedStatus) ? 'selected' : ''}>Assigned</option>
                        <option value="IN_PROGRESS" ${'IN_PROGRESS'.equalsIgnoreCase(selectedStatus) ? 'selected' : ''}>In progress</option>
                        <option value="RESOLVED" ${'RESOLVED'.equalsIgnoreCase(selectedStatus) ? 'selected' : ''}>Resolved</option>
                        <option value="REJECTED" ${'REJECTED'.equalsIgnoreCase(selectedStatus) ? 'selected' : ''}>Rejected</option>
                        <option value="REOPENED" ${'REOPENED'.equalsIgnoreCase(selectedStatus) ? 'selected' : ''}>Reopened</option>
                    </select>
                </div>

                <div class="col-md-2">
                    <select class="form-select" name="categoryId">
                        <option value="">All Categories</option>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.categoryId}" ${selectedCategoryId == cat.categoryId ? 'selected' : ''}>
                                <c:out value="${cat.categoryName}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-2">
                    <select class="form-select" name="priority">
                        <option value="">All Priorities</option>
                        <option value="LOW" ${'LOW'.equalsIgnoreCase(selectedPriority) ? 'selected' : ''}>Low</option>
                        <option value="MEDIUM" ${'MEDIUM'.equalsIgnoreCase(selectedPriority) ? 'selected' : ''}>Medium</option>
                        <option value="HIGH" ${'HIGH'.equalsIgnoreCase(selectedPriority) ? 'selected' : ''}>High</option>
                        <option value="URGENT" ${'URGENT'.equalsIgnoreCase(selectedPriority) ? 'selected' : ''}>Urgent</option>
                    </select>
                </div>

                <div class="col-md-2">
                    <select class="form-select" name="deptId">
                        <option value="">All Departments</option>
                        <c:forEach var="d" items="${departments}">
                            <option value="${d.departmentId}" ${selectedDeptId == d.departmentId ? 'selected' : ''}>
                                <c:out value="${d.departmentName}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-1 d-flex gap-1">
                    <button type="submit" class="btn btn-cc-primary w-100" title="Apply Filters"><i class="bi bi-funnel"></i></button>
                    <c:if test="${not empty searchQuery || not empty selectedStatus || not empty selectedCategoryId || not empty selectedPriority || not empty selectedDeptId || overdueOnly}">
                        <a href="${pageContext.request.contextPath}/admin/complaints" class="btn btn-outline-secondary" title="Clear Filters"><i class="bi bi-x-lg"></i></a>
                    </c:if>
                </div>

                <div class="col-12 mt-2">
                    <div class="form-check form-check-inline small">
                        <input class="form-check-input" type="checkbox" id="overdueCheck" name="overdue" value="true" ${overdueOnly ? 'checked' : ''} onchange="this.form.submit()">
                        <label class="form-check-label text-danger fw-semibold" for="overdueCheck">
                            <i class="bi bi-clock-history"></i> Show Overdue Tickets Only
                        </label>
                    </div>
                </div>
            </form>
        </div>

        <!-- Table Card -->
        <div class="cc-card">
            <div class="table-responsive">
                <c:choose>
                    <c:when test="${not empty complaints}">
                        <table class="cc-table">
                            <thead>
                                <tr>
                                    <th>Ticket</th>
                                    <th>Student</th>
                                    <th>Issue</th>
                                    <th>Category</th>
                                    <th>Priority</th>
                                    <th>Department</th>
                                    <th>Date</th>
                                    <th>Status</th>
                                    <th class="text-end">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="c" items="${complaints}">
                                    <tr>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/complaints/${c.ticketNumber}" class="ticket-code">
                                                <c:out value="${c.ticketNumber}"/>
                                            </a>
                                        </td>
                                        <td>
                                            <div class="fw-semibold text-dark"><c:out value="${c.studentName}"/></div>
                                            <div class="text-muted small font-monospace"><c:out value="${c.studentId != null ? c.studentId : ''}"/></div>
                                        </td>
                                        <td>
                                            <div class="fw-semibold text-dark"><c:out value="${c.title}"/></div>
                                            <div class="text-muted small"><i class="bi bi-geo-alt me-1"></i><c:out value="${c.location}"/></div>
                                        </td>
                                        <td>
                                            <span class="text-muted small"><c:out value="${c.categoryName}"/></span>
                                        </td>
                                        <td>
                                            <span class="badge-priority ${c.priorityBadgeClass}"><c:out value="${c.priority}"/></span>
                                        </td>
                                        <td>
                                            <span class="small fw-semibold text-secondary">
                                                <c:out value="${c.assignedDepartmentName != null ? c.assignedDepartmentName : 'Unassigned'}"/>
                                            </span>
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
                                                <span class="badge-overdue ms-1"><i class="bi bi-exclamation-circle"></i></span>
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

                        <!-- Pagination -->
                        <c:if test="${totalPages > 1}">
                            <div class="p-3 border-top d-flex justify-content-between align-items-center">
                                <span class="text-muted small">Showing page ${currentPage} of ${totalPages} (${totalCount} total)</span>
                                <nav>
                                    <ul class="pagination pagination-sm mb-0">
                                        <c:forEach begin="1" end="${totalPages}" var="p">
                                            <li class="page-item ${currentPage == p ? 'active' : ''}">
                                                <a class="page-link" href="${pageContext.request.contextPath}/admin/complaints?page=${p}&status=${selectedStatus}&categoryId=${selectedCategoryId}&priority=${selectedPriority}&deptId=${selectedDeptId}&search=${searchQuery}&overdue=${overdueOnly}">${p}</a>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </nav>
                            </div>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <i class="bi bi-inbox empty-state-icon"></i>
                            <div class="empty-state-title">No complaints match criteria</div>
                            <div class="empty-state-desc">Try clearing or adjusting your search parameters.</div>
                            <a href="${pageContext.request.contextPath}/admin/complaints" class="btn btn-outline-secondary btn-sm">Clear filters</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
