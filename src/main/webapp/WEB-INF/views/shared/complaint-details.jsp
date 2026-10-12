<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Complaint ${complaint.ticketNumber} — CampusCare" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<div class="app-wrapper">
    <jsp:include page="/WEB-INF/views/shared/sidebar.jsp"/>

    <main class="main-content">
        <!-- Top Navigation / Breadcrumb -->
        <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
            <div>
                <div class="d-flex align-items-center gap-2 mb-1">
                    <span class="ticket-code fs-5"><c:out value="${complaint.ticketNumber}"/></span>
                    <span class="badge-status ${complaint.statusBadgeClass}">
                        <c:out value="${complaint.formattedStatus}"/>
                    </span>
                    <c:if test="${complaint.overdue}">
                        <span class="badge-overdue"><i class="bi bi-clock-history"></i> Overdue</span>
                    </c:if>
                </div>
                <div class="text-muted small">
                    Submitted on <fmt:formatDate value="${complaint.createdAt}" pattern="dd MMM yyyy, hh:mm a"/>
                </div>
            </div>

            <!-- Action Buttons based on User Role -->
            <div class="d-flex flex-wrap gap-2">
                <c:if test="${sessionScope.currentUser.admin || sessionScope.currentUser.staff}">
                    <!-- Add Note Button -->
                    <button type="button" class="btn btn-outline-secondary btn-sm" data-bs-toggle="modal" data-bs-target="#commentModal">
                        <i class="bi bi-chat-left-text me-1"></i> Add Note
                    </button>
                    <!-- Status Change Button -->
                    <button type="button" class="btn btn-cc-secondary btn-sm" data-bs-toggle="modal" data-bs-target="#statusModal">
                        <i class="bi bi-arrow-repeat me-1"></i> Update Status
                    </button>
                </c:if>

                <c:if test="${sessionScope.currentUser.admin}">
                    <!-- Admin Assign Button -->
                    <button type="button" class="btn btn-cc-primary btn-sm" data-bs-toggle="modal" data-bs-target="#assignModal">
                        <i class="bi bi-person-plus me-1"></i> Assign
                    </button>
                </c:if>

                <c:if test="${sessionScope.currentUser.student && complaint.resolved}">
                    <!-- Reopen Button for Student -->
                    <button type="button" class="btn btn-outline-danger btn-sm" data-bs-toggle="modal" data-bs-target="#reopenModal">
                        <i class="bi bi-arrow-counterclockwise me-1"></i> Reopen Ticket
                    </button>
                </c:if>
            </div>
        </div>

        <!-- Alert messages -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show small" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>
                <c:out value="${successMessage}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show small" role="alert">
                <i class="bi bi-exclamation-circle-fill me-2"></i>
                <c:out value="${errorMessage}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="row g-4">
            <!-- Left Column: Details & Progress History -->
            <div class="col-lg-8">
                
                <!-- Issue Details Card -->
                <div class="cc-card mb-4">
                    <div class="cc-card-body">
                        <h4 class="fw-bold mb-3"><c:out value="${complaint.title}"/></h4>

                        <div class="row g-3 mb-4 py-2 bg-light rounded-3 px-2 border">
                            <div class="col-sm-4">
                                <span class="text-muted small d-block">Category</span>
                                <span class="fw-semibold"><c:out value="${complaint.categoryName}"/></span>
                            </div>
                            <div class="col-sm-4">
                                <span class="text-muted small d-block">Location</span>
                                <span class="fw-semibold"><i class="bi bi-geo-alt me-1 text-danger"></i><c:out value="${complaint.location}"/></span>
                            </div>
                            <div class="col-sm-4">
                                <span class="text-muted small d-block">Assigned To</span>
                                <span class="fw-semibold">
                                    <c:out value="${complaint.assignedDepartmentName != null ? complaint.assignedDepartmentName : 'Unassigned'}"/>
                                    <c:if test="${not empty complaint.assignedStaffName}">
                                        <span class="text-muted small d-block">(<c:out value="${complaint.assignedStaffName}"/>)</span>
                                    </c:if>
                                </span>
                            </div>
                        </div>

                        <div class="mb-4">
                            <h6 class="fw-bold text-secondary small text-uppercase mb-2">Description</h6>
                            <p class="text-dark" style="white-space: pre-wrap; line-height: 1.6;"><c:out value="${complaint.description}"/></p>
                        </div>

                        <!-- Resolution Notes if Resolved -->
                        <c:if test="${complaint.resolved && not empty complaint.resolutionNotes}">
                            <div class="p-3 mb-4 bg-success-subtle border border-success-subtle rounded-3">
                                <div class="d-flex align-items-center gap-2 mb-1 text-success fw-bold">
                                    <i class="bi bi-check-circle-fill"></i> Official Resolution Details
                                </div>
                                <p class="mb-0 small text-dark"><c:out value="${complaint.resolutionNotes}"/></p>
                                <div class="text-muted" style="font-size:0.75rem; margin-top:0.4rem;">
                                    Resolved on <fmt:formatDate value="${complaint.resolvedAt}" pattern="dd MMM yyyy, hh:mm a"/>
                                </div>
                            </div>
                        </c:if>

                        <!-- Rejection Reason if Rejected -->
                        <c:if test="${complaint.rejected && not empty complaint.rejectionReason}">
                            <div class="p-3 mb-4 bg-danger-subtle border border-danger-subtle rounded-3">
                                <div class="d-flex align-items-center gap-2 mb-1 text-danger fw-bold">
                                    <i class="bi bi-x-circle-fill"></i> Rejection Reason
                                </div>
                                <p class="mb-0 small text-dark"><c:out value="${complaint.rejectionReason}"/></p>
                            </div>
                        </c:if>

                        <!-- Attachments Section -->
                        <c:if test="${not empty complaint.attachments}">
                            <div class="mt-3 pt-3 border-top">
                                <h6 class="fw-bold text-secondary small text-uppercase mb-2">Attached Documents / Images</h6>
                                <div class="d-flex flex-wrap gap-2">
                                    <c:forEach var="att" items="${complaint.attachments}">
                                        <a href="${pageContext.request.contextPath}/attachments/${att.attachmentId}" target="_blank"
                                           class="btn btn-sm btn-light border d-flex align-items-center gap-2 py-2 px-3">
                                            <i class="bi bi-paperclip fs-5 text-primary"></i>
                                            <div class="text-start">
                                                <div class="fw-semibold text-truncate" style="max-width:200px;"><c:out value="${att.originalFilename}"/></div>
                                                <div class="text-muted" style="font-size:0.72rem;">${att.formattedSize}</div>
                                            </div>
                                        </a>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>

                <!-- Progress History / Audit Timeline (Matches Mockup 9.5) -->
                <div class="cc-card">
                    <div class="cc-card-header">
                        <h5 class="cc-card-title"><i class="bi bi-clock-history me-2"></i>Progress history</h5>
                        <span class="text-muted small">${complaint.updates.size()} events recorded</span>
                    </div>
                    <div class="cc-card-body">
                        <ul class="timeline">
                            <c:forEach var="u" items="${complaint.updates}">
                                <li class="timeline-item">
                                    <div class="timeline-marker ${u.newStatus == 'RESOLVED' ? 'resolved' : u.newStatus == 'REJECTED' ? 'rejected' : u.internal ? 'internal' : ''}"></div>
                                    <div class="timeline-header">
                                        <div class="timeline-title">
                                            <c:choose>
                                                <c:when test="${u.eventType == 'CREATED'}">
                                                    Complaint submitted
                                                </c:when>
                                                <c:when test="${u.eventType == 'STATUS_CHANGE'}">
                                                    Status changed to <c:out value="${u.newStatus}"/>
                                                </c:when>
                                                <c:when test="${u.eventType == 'ASSIGNED'}">
                                                    Assigned to department
                                                </c:when>
                                                <c:when test="${u.eventType == 'INTERNAL_NOTE'}">
                                                    <span class="badge bg-warning-subtle text-warning-emphasis border">Internal Staff Note</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:out value="${u.eventType}"/>
                                                </c:otherwise>
                                            </c:choose>
                                            <span class="text-muted small fw-normal ms-1">by <c:out value="${u.authorName}"/> (<c:out value="${u.authorRole}"/>)</span>
                                        </div>
                                        <div class="timeline-time">
                                            <fmt:formatDate value="${u.createdAt}" pattern="dd MMM • hh:mm a"/>
                                        </div>
                                    </div>
                                    <c:if test="${not empty u.comment}">
                                        <div class="timeline-body ${u.internal ? 'internal' : ''}">
                                            <c:out value="${u.comment}"/>
                                        </div>
                                    </c:if>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>

                <!-- Student Feedback Form (If Resolved & Student Owner) -->
                <c:if test="${complaint.resolved && sessionScope.currentUser.student}">
                    <div class="cc-card">
                        <div class="cc-card-header">
                            <h5 class="cc-card-title"><i class="bi bi-star-fill text-warning me-2"></i>Resolution Feedback</h5>
                        </div>
                        <div class="cc-card-body">
                            <c:choose>
                                <c:when test="${not empty complaint.feedback}">
                                    <div class="p-3 bg-light rounded border">
                                        <div class="d-flex align-items-center gap-2 mb-2">
                                            <div class="text-warning fs-5">
                                                <c:forEach begin="1" end="${complaint.feedback.rating}">★</c:forEach>
                                                <c:forEach begin="${complaint.feedback.rating + 1}" end="5">☆</c:forEach>
                                            </div>
                                            <span class="fw-bold">${complaint.feedback.rating} / 5 stars</span>
                                        </div>
                                        <p class="text-muted mb-0 small"><c:out value="${complaint.feedback.comment}"/></p>
                                        <div class="text-muted" style="font-size:0.75rem; margin-top:0.4rem;">
                                            Submitted on <fmt:formatDate value="${complaint.feedback.createdAt}" pattern="dd MMM yyyy"/>
                                        </div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <p class="text-muted small">How was your experience with the resolution? Please rate the support provided.</p>
                                    <form action="${pageContext.request.contextPath}/complaints/${complaint.ticketNumber}/feedback" method="post">
                                        <input type="hidden" name="csrfToken" value="${csrfToken != null ? csrfToken : sessionScope.CSRF_TOKEN}">

                                        <div class="mb-3">
                                            <label class="form-label small fw-semibold text-secondary d-block">Rating *</label>
                                            <div class="star-rating">
                                                <input type="radio" id="star5" name="rating" value="5" required /><label for="star5" title="5 stars">★</label>
                                                <input type="radio" id="star4" name="rating" value="4" /><label for="star4" title="4 stars">★</label>
                                                <input type="radio" id="star3" name="rating" value="3" /><label for="star3" title="3 stars">★</label>
                                                <input type="radio" id="star2" name="rating" value="2" /><label for="star2" title="2 stars">★</label>
                                                <input type="radio" id="star1" name="rating" value="1" /><label for="star1" title="1 star">★</label>
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label for="feedbackComment" class="form-label small fw-semibold text-secondary">Comments (optional)</label>
                                            <textarea class="form-control" id="feedbackComment" name="comment" rows="3" placeholder="Share your feedback on the timeliness, quality, or professionalism of the repair..."></textarea>
                                        </div>

                                        <button type="submit" class="btn btn-cc-primary btn-sm">
                                            <i class="bi bi-send me-1"></i> Submit Feedback
                                        </button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:if>

            </div>

            <!-- Right Column: Ticket Metadata Card (Matches Mockup 9.5) -->
            <div class="col-lg-4">
                <div class="cc-card">
                    <div class="cc-card-header">
                        <h6 class="cc-card-title">Ticket information</h6>
                    </div>
                    <div class="cc-card-body p-0">
                        <ul class="list-group list-group-flush small">
                            <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                <span class="text-muted">Priority</span>
                                <span class="badge-priority ${complaint.priorityBadgeClass}"><c:out value="${complaint.priority}"/></span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                <span class="text-muted">Submitted</span>
                                <span class="fw-semibold text-dark"><fmt:formatDate value="${complaint.createdAt}" pattern="dd MMM yyyy"/></span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                <span class="text-muted">Last update</span>
                                <span class="fw-semibold text-dark"><fmt:formatDate value="${complaint.updatedAt}" pattern="dd MMM yyyy, hh:mm a"/></span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                <span class="text-muted">Owner</span>
                                <span class="fw-semibold text-dark"><c:out value="${complaint.studentName}"/></span>
                            </li>
                            <c:if test="${sessionScope.currentUser.admin || sessionScope.currentUser.staff}">
                                <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                    <span class="text-muted">Student ID</span>
                                    <span class="font-monospace text-dark"><c:out value="${complaint.studentId != null ? complaint.studentId : 'N/A'}"/></span>
                                </li>
                                <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                    <span class="text-muted">Student Email</span>
                                    <span class="text-dark"><c:out value="${complaint.studentEmail}"/></span>
                                </li>
                            </c:if>
                            <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                <span class="text-muted">Department</span>
                                <span class="fw-semibold text-secondary"><c:out value="${complaint.assignedDepartmentName != null ? complaint.assignedDepartmentName : 'Unassigned'}"/></span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                <span class="text-muted">Days Open</span>
                                <span class="fw-semibold text-dark">${complaint.daysOpen} days</span>
                            </li>
                        </ul>
                    </div>
                </div>

                <!-- Assistance Card -->
                <div class="cc-card bg-light p-3 border">
                    <h6 class="fw-bold mb-1 small text-secondary"><i class="bi bi-question-circle me-1"></i> Need Assistance?</h6>
                    <p class="text-muted small mb-0">For emergency safety issues, contact Campus Security at extension <strong>100</strong> immediately.</p>
                </div>
            </div>
        </div>
    </main>
</div>

<!-- MODALS -->

<!-- 1. Assignment Modal (Admin only) -->
<c:if test="${sessionScope.currentUser.admin}">
    <div class="modal fade" id="assignModal" tabindex="-1" aria-labelledby="assignModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/admin/complaints/${complaint.ticketNumber}/assign" method="post">
                    <input type="hidden" name="csrfToken" value="${csrfToken != null ? csrfToken : sessionScope.CSRF_TOKEN}">
                    <div class="modal-header">
                        <h5 class="modal-title fw-bold" id="assignModalLabel">Assign Complaint</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label small fw-semibold">Department *</label>
                            <select class="form-select" name="departmentId" id="assignDeptSelect" required>
                                <option value="">Select Department...</option>
                                <c:forEach var="dept" items="${departments}">
                                    <option value="${dept.departmentId}" ${complaint.assignedDepartmentId == dept.departmentId ? 'selected' : ''}>
                                        <c:out value="${dept.departmentName}"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label small fw-semibold">Assign Staff Member (Optional)</label>
                            <select class="form-select" name="staffId" id="assignStaffSelect">
                                <option value="">Unassigned (Department Pool)</option>
                                <c:forEach var="st" items="${staffList}">
                                    <option value="${st.userId}" data-dept="${st.departmentId}" ${complaint.assignedStaffId == st.userId ? 'selected' : ''}>
                                        <c:out value="${st.fullName}"/> (<c:out value="${st.departmentName != null ? st.departmentName : 'All'}"/>)
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label small fw-semibold">Assignment Note</label>
                            <textarea class="form-control" name="comment" rows="2" placeholder="e.g. Assigned to Electrical Maintenance for fan motor replacement."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-cc-primary">Save Assignment</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</c:if>

<!-- 2. Status Update Modal (Admin & Staff) -->
<c:if test="${sessionScope.currentUser.admin || sessionScope.currentUser.staff}">
    <div class="modal fade" id="statusModal" tabindex="-1" aria-labelledby="statusModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/admin/complaints/${complaint.ticketNumber}/status" method="post" class="confirm-status-form">
                    <input type="hidden" name="csrfToken" value="${csrfToken != null ? csrfToken : sessionScope.CSRF_TOKEN}">
                    <div class="modal-header">
                        <h5 class="modal-title fw-bold" id="statusModalLabel">Update Complaint Status</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label small fw-semibold">New Status *</label>
                            <select class="form-select" name="status" id="targetStatusSelect" required onchange="toggleStatusFields(this.value)">
                                <option value="">Select target status...</option>
                                <c:if test="${sessionScope.currentUser.admin}">
                                    <option value="UNDER_REVIEW">Under Review</option>
                                    <option value="ASSIGNED">Assigned</option>
                                </c:if>
                                <option value="IN_PROGRESS">In Progress</option>
                                <option value="RESOLVED">Resolved</option>
                                <c:if test="${sessionScope.currentUser.admin}">
                                    <option value="REJECTED">Rejected</option>
                                </c:if>
                            </select>
                        </div>

                        <!-- Resolution Notes Field (Required if Resolved) -->
                        <div class="mb-3 d-none" id="resolutionNotesGroup">
                            <label class="form-label small fw-semibold text-success">Resolution Notes *</label>
                            <textarea class="form-control border-success" name="resolutionNotes" rows="3" placeholder="Explain the actions taken to repair or resolve this complaint..."></textarea>
                            <div class="form-text">Visible to the student and recorded in the audit trail.</div>
                        </div>

                        <!-- Rejection Reason Field (Required if Rejected) -->
                        <div class="mb-3 d-none" id="rejectionReasonGroup">
                            <label class="form-label small fw-semibold text-danger">Rejection Reason *</label>
                            <textarea class="form-control border-danger" name="rejectionReason" rows="3" placeholder="State why this ticket is being rejected (e.g. Duplicate issue, Out of scope)..."></textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label small fw-semibold">Status Update Comment</label>
                            <textarea class="form-control" name="comment" rows="2" placeholder="Brief note explaining current action taken..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-cc-primary">Apply Status Change</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
    function toggleStatusFields(val) {
        const resGroup = document.getElementById('resolutionNotesGroup');
        const rejGroup = document.getElementById('rejectionReasonGroup');
        if (resGroup) resGroup.classList.toggle('d-none', val !== 'RESOLVED');
        if (rejGroup) rejGroup.classList.toggle('d-none', val !== 'REJECTED');
    }
    </script>
</c:if>

<!-- 3. Add Note Modal -->
<div class="modal fade" id="commentModal" tabindex="-1" aria-labelledby="commentModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/complaints/${complaint.ticketNumber}/comment" method="post">
                <input type="hidden" name="csrfToken" value="${csrfToken != null ? csrfToken : sessionScope.CSRF_TOKEN}">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold" id="commentModalLabel">Add Note / Comment</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label small fw-semibold">Comment *</label>
                        <textarea class="form-control" name="comment" rows="4" required placeholder="Type update, investigation findings, or questions..."></textarea>
                    </div>
                    <c:if test="${sessionScope.currentUser.admin || sessionScope.currentUser.staff}">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="isInternal" value="true" id="internalCheck">
                            <label class="form-check-label small" for="internalCheck">
                                <strong>Internal Staff Note</strong> (Visible only to staff & admin, hidden from student)
                            </label>
                        </div>
                    </c:if>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-cc-primary">Post Note</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- 4. Reopen Modal (Student / Admin) -->
<c:if test="${complaint.resolved}">
    <div class="modal fade" id="reopenModal" tabindex="-1" aria-labelledby="reopenModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/complaints/${complaint.ticketNumber}/reopen" method="post">
                    <input type="hidden" name="csrfToken" value="${csrfToken != null ? csrfToken : sessionScope.CSRF_TOKEN}">
                    <div class="modal-header">
                        <h5 class="modal-title fw-bold text-danger" id="reopenModalLabel">Reopen Resolved Complaint</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p class="text-muted small">If the problem was not adequately solved or has recurred, describe what remains broken.</p>
                        <div class="mb-3">
                            <label class="form-label small fw-semibold">Reason for Reopening *</label>
                            <textarea class="form-control" name="reopenReason" rows="3" required placeholder="State why the issue is still unresolved..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-danger">Confirm Reopen</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
