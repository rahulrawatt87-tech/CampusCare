<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Campus Departments — CampusCare" scope="request"/>
<c:set var="activeNav" value="departments" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<div class="app-wrapper">
    <jsp:include page="/WEB-INF/views/shared/sidebar.jsp"/>

    <main class="main-content">
        <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
            <div>
                <h2 class="fw-bold mb-1">Campus departments</h2>
                <p class="text-muted small mb-0">Configure service departments, contact information, and active status.</p>
            </div>
            <div>
                <button type="button" class="btn btn-cc-primary btn-sm" data-bs-toggle="modal" data-bs-target="#newDeptModal">
                    <i class="bi bi-plus-lg me-1"></i> Add Department
                </button>
            </div>
        </div>

        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show small" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>
                <c:out value="${successMessage}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="cc-card">
            <div class="table-responsive">
                <table class="cc-table">
                    <thead>
                        <tr>
                            <th>Department Name</th>
                            <th>Description</th>
                            <th>Contact</th>
                            <th>Staff Assigned</th>
                            <th>Complaints Handled</th>
                            <th>Status</th>
                            <th class="text-end">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="d" items="${departments}">
                            <tr>
                                <td>
                                    <div class="fw-bold text-dark"><c:out value="${d.departmentName}"/></div>
                                </td>
                                <td>
                                    <div class="text-muted small text-truncate" style="max-width:280px;"><c:out value="${d.description}"/></div>
                                </td>
                                <td>
                                    <div class="small text-dark"><c:out value="${d.contactEmail}"/></div>
                                    <div class="text-muted small"><c:out value="${d.contactPhone}"/></div>
                                </td>
                                <td>
                                    <span class="badge bg-light text-dark border">${d.staffCount} Staff</span>
                                </td>
                                <td>
                                    <span class="badge bg-primary-subtle text-primary border border-primary-subtle">${d.complaintCount} Tickets</span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${d.active}">
                                            <span class="badge bg-success-subtle text-success border border-success-subtle">Active</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary-subtle text-secondary border">Inactive</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-end">
                                    <button type="button" class="btn btn-sm btn-outline-secondary"
                                            onclick="editDept('${d.departmentId}', '${d.departmentName}', '${d.description}', '${d.contactEmail}', '${d.contactPhone}', ${d.active})">
                                        Edit
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

<!-- Add Department Modal -->
<div class="modal fade" id="newDeptModal" tabindex="-1" aria-labelledby="newDeptModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/admin/departments" method="post">
                <input type="hidden" name="csrfToken" value="${csrfToken != null ? csrfToken : sessionScope.CSRF_TOKEN}">
                <input type="hidden" name="action" value="create">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold" id="newDeptModalLabel">Add Campus Department</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label small fw-semibold">Department Name *</label>
                        <input type="text" class="form-control" name="departmentName" required placeholder="e.g. Sports Facilities & Gym">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-semibold">Description</label>
                        <textarea class="form-control" name="description" rows="2" placeholder="Responsibilities and scope..."></textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-semibold">Contact Email</label>
                        <input type="email" class="form-control" name="contactEmail" placeholder="dept@campuscare.edu">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-semibold">Contact Phone</label>
                        <input type="text" class="form-control" name="contactPhone" placeholder="+1-555-0155">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-cc-primary">Create Department</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Edit Department Modal -->
<div class="modal fade" id="editDeptModal" tabindex="-1" aria-labelledby="editDeptModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/admin/departments" method="post">
                <input type="hidden" name="csrfToken" value="${csrfToken != null ? csrfToken : sessionScope.CSRF_TOKEN}">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="departmentId" id="editDeptId">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold" id="editDeptModalLabel">Edit Department</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label small fw-semibold">Department Name *</label>
                        <input type="text" class="form-control" name="departmentName" id="editDeptName" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-semibold">Description</label>
                        <textarea class="form-control" name="description" id="editDeptDesc" rows="2"></textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-semibold">Contact Email</label>
                        <input type="email" class="form-control" name="contactEmail" id="editDeptEmail">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-semibold">Contact Phone</label>
                        <input type="text" class="form-control" name="contactPhone" id="editDeptPhone">
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" name="active" id="editDeptActive">
                        <label class="form-check-label small" for="editDeptActive">Active Department (Accepts new tickets)</label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-cc-primary">Save Changes</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function editDept(id, name, desc, email, phone, active) {
    document.getElementById('editDeptId').value = id;
    document.getElementById('editDeptName').value = name;
    document.getElementById('editDeptDesc').value = desc;
    document.getElementById('editDeptEmail').value = email;
    document.getElementById('editDeptPhone').value = phone;
    document.getElementById('editDeptActive').checked = active;
    new bootstrap.Modal(document.getElementById('editDeptModal')).show();
}
</script>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
