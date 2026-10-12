<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Staff & Technicians — CampusCare" scope="request"/>
<c:set var="activeNav" value="staff" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<div class="app-wrapper">
    <jsp:include page="/WEB-INF/views/shared/sidebar.jsp"/>

    <main class="main-content">
        <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
            <div>
                <h2 class="fw-bold mb-1">Staff & Technicians directory</h2>
                <p class="text-muted small mb-0">Authorized service department personnel provisioned to address and resolve campus tickets.</p>
            </div>
            <div>
                <button type="button" class="btn btn-cc-primary btn-sm" data-bs-toggle="modal" data-bs-target="#newStaffModal">
                    <i class="bi bi-person-plus-fill me-1"></i> Add Staff Member
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

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show small" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                <c:out value="${errorMessage}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="alert alert-info small d-flex align-items-center gap-2 mb-4" role="alert">
            <i class="bi bi-shield-check fs-5 text-primary"></i>
            <div>
                <strong>Security Architecture:</strong> Staff and technician accounts cannot self-register from the public sign-up page.
                Only verified Administrators have authorization to provision service personnel.
            </div>
        </div>

        <div class="cc-card">
            <div class="table-responsive">
                <table class="cc-table align-middle">
                    <thead>
                        <tr>
                            <th>Staff Member</th>
                            <th>Contact Info</th>
                            <th>Assigned Service Department</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty staffMembers}">
                                <tr>
                                    <td colspan="6" class="text-center py-4 text-muted">
                                        <i class="bi bi-people display-6 d-block mb-2 text-secondary opacity-50"></i>
                                        No staff members provisioned yet. Click "Add Staff Member" to add maintenance technicians.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="s" items="${staffMembers}">
                                    <tr>
                                        <td>
                                            <div class="d-flex align-items-center gap-2">
                                                <div class="rounded-circle bg-primary-subtle text-primary fw-bold d-flex align-items-center justify-content-center" style="width:34px;height:34px;font-size:0.85rem;">
                                                    <c:out value="${s.fullName.substring(0, 1)}"/>
                                                </div>
                                                <div>
                                                    <div class="fw-bold text-dark"><c:out value="${s.fullName}"/></div>
                                                    <div class="text-muted small">ID: #<c:out value="${s.userId}"/></div>
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <div class="small text-dark"><i class="bi bi-envelope me-1 text-muted"></i><c:out value="${s.email}"/></div>
                                            <div class="text-muted small"><i class="bi bi-telephone me-1 text-muted"></i><c:out value="${not empty s.phone ? s.phone : 'N/A'}"/></div>
                                        </td>
                                        <td>
                                            <span class="badge bg-light text-dark border px-2 py-1">
                                                <i class="bi bi-tools me-1 text-primary"></i>
                                                <c:out value="${not empty s.departmentName ? s.departmentName : 'General Maintenance'}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <span class="badge bg-secondary-subtle text-secondary border">STAFF</span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${s.active}">
                                                    <span class="badge bg-success-subtle text-success border border-success-subtle">
                                                        <i class="bi bi-check-circle me-1"></i>Active
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-danger-subtle text-danger border border-danger-subtle">
                                                        <i class="bi bi-x-circle me-1"></i>Disabled
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-end">
                                            <form action="${pageContext.request.contextPath}/admin/staff" method="post" class="d-inline m-0">
                                                <input type="hidden" name="action" value="toggle">
                                                <input type="hidden" name="userId" value="${s.userId}">
                                                <input type="hidden" name="status" value="${!s.active}">
                                                <button type="submit" class="btn btn-sm ${s.active ? 'btn-outline-danger' : 'btn-outline-success'}">
                                                    <c:out value="${s.active ? 'Deactivate' : 'Activate'}"/>
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

<!-- Add Staff Member Modal -->
<div class="modal fade" id="newStaffModal" tabindex="-1" aria-labelledby="newStaffModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/admin/staff" method="post">
                <input type="hidden" name="action" value="add">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold" id="newStaffModalLabel">
                        <i class="bi bi-person-plus text-primary me-2"></i>Provision New Staff Member
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="staffFullName" class="form-label small fw-semibold text-secondary">Full Name *</label>
                        <input type="text" class="form-control" id="staffFullName" name="fullName"
                               placeholder="e.g. Robert Davis" required
                               value="<c:out value='${prevFullName != null ? prevFullName : \"\"}'/>">
                    </div>

                    <div class="mb-3">
                        <label for="staffEmail" class="form-label small fw-semibold text-secondary">Official Staff Email *</label>
                        <input type="email" class="form-control" id="staffEmail" name="email"
                               placeholder="robert.electric@campuscare.edu" required
                               value="<c:out value='${prevEmail != null ? prevEmail : \"\"}'/>">
                        <div class="form-text">Used by the technician to log in and receive ticket assignments.</div>
                    </div>

                    <div class="mb-3">
                        <label for="staffDept" class="form-label small fw-semibold text-secondary">Assigned Service Department *</label>
                        <select class="form-select" id="staffDept" name="departmentId" required>
                            <option value="">Choose service maintenance unit...</option>
                            <c:forEach var="dept" items="${departments}">
                                <option value="${dept.departmentId}" ${prevDepartmentId == dept.departmentId ? 'selected' : ''}>
                                    <c:out value="${dept.departmentName}"/>
                                </option>
                            </c:forEach>
                        </select>
                        <div class="form-text">Complaints submitted to this service category will be assigned to this staff member.</div>
                    </div>

                    <div class="mb-3">
                        <label for="staffPhone" class="form-label small fw-semibold text-secondary">Contact Phone</label>
                        <input type="tel" class="form-control" id="staffPhone" name="phone"
                               placeholder="+1-555-0199"
                               value="<c:out value='${prevPhone != null ? prevPhone : \"\"}'/>">
                    </div>

                    <div class="mb-3">
                        <label for="staffPassword" class="form-label small fw-semibold text-secondary">Initial Password *</label>
                        <input type="password" class="form-control" id="staffPassword" name="password"
                               placeholder="Minimum 6 characters" minlength="6" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-cc-primary">Provision Staff Account</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
