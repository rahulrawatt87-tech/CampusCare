<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="My Profile — CampusCare" scope="request"/>
<c:set var="activeNav" value="profile" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<div class="app-wrapper">
    <jsp:include page="/WEB-INF/views/shared/sidebar.jsp"/>

    <main class="main-content">
        <div class="mb-4">
            <h2 class="fw-bold mb-1">My profile</h2>
            <p class="text-muted small mb-0">Manage your account information and login security credentials.</p>
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
                <i class="bi bi-exclamation-circle-fill me-2"></i>
                <c:out value="${errorMessage}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="row g-4">
            <!-- Profile Info Form -->
            <div class="col-lg-7">
                <div class="cc-card">
                    <div class="cc-card-header">
                        <h5 class="cc-card-title"><i class="bi bi-person-lines-fill me-2"></i>Personal Details</h5>
                    </div>
                    <div class="cc-card-body">
                        <form action="${pageContext.request.contextPath}/profile" method="post">
                            <input type="hidden" name="csrfToken" value="${csrfToken != null ? csrfToken : sessionScope.CSRF_TOKEN}">
                            <input type="hidden" name="action" value="update_profile">

                            <div class="mb-3">
                                <label class="form-label small fw-semibold text-secondary">College Email</label>
                                <input type="email" class="form-control bg-light" value="<c:out value='${user.email}'/>" readonly disabled>
                                <div class="form-text">Primary college identity email cannot be altered directly.</div>
                            </div>

                            <c:if test="${user.student}">
                                <div class="mb-3">
                                    <label class="form-label small fw-semibold text-secondary">Student ID / Registration</label>
                                    <input type="text" class="form-control bg-light font-monospace" value="<c:out value='${user.studentId}'/>" readonly disabled>
                                </div>
                            </c:if>

                            <div class="mb-3">
                                <label class="form-label small fw-semibold text-secondary">Full Name *</label>
                                <input type="text" class="form-control" name="fullName" value="<c:out value='${user.fullName}'/>" required>
                            </div>

                            <div class="mb-3">
                                <label class="form-label small fw-semibold text-secondary">Contact Phone</label>
                                <input type="tel" class="form-control" name="phone" value="<c:out value='${user.phone != null ? user.phone : \"\"}'/>">
                            </div>

                            <div class="mb-3">
                                <label class="form-label small fw-semibold text-secondary">Department</label>
                                <select class="form-select" name="departmentId">
                                    <option value="">Select Department...</option>
                                    <c:forEach var="dept" items="${departments}">
                                        <option value="${dept.departmentId}" ${user.departmentId == dept.departmentId ? 'selected' : ''}>
                                            <c:out value="${dept.departmentName}"/>
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <c:if test="${user.student}">
                                <div class="mb-3">
                                    <label class="form-label small fw-semibold text-secondary">Course & Year</label>
                                    <input type="text" class="form-control" name="courseYear" value="<c:out value='${user.courseYear != null ? user.courseYear : \"\"}'/>" placeholder="e.g. B.Tech CSE - 3rd Year">
                                </div>
                            </c:if>

                            <div class="pt-3 border-top">
                                <button type="submit" class="btn btn-cc-primary">Save Profile Changes</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Password Change Form -->
            <div class="col-lg-5">
                <div class="cc-card">
                    <div class="cc-card-header">
                        <h5 class="cc-card-title"><i class="bi bi-shield-lock-fill me-2"></i>Change Password</h5>
                    </div>
                    <div class="cc-card-body">
                        <form action="${pageContext.request.contextPath}/profile" method="post">
                            <input type="hidden" name="csrfToken" value="${csrfToken != null ? csrfToken : sessionScope.CSRF_TOKEN}">
                            <input type="hidden" name="action" value="change_password">

                            <div class="mb-3">
                                <label class="form-label small fw-semibold text-secondary">Current Password *</label>
                                <div class="input-group">
                                    <input type="password" class="form-control" id="currPass" name="currentPassword" required>
                                    <button class="btn btn-outline-secondary toggle-password" type="button" data-target="currPass">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label small fw-semibold text-secondary">New Password *</label>
                                <div class="input-group">
                                    <input type="password" class="form-control" id="newPass" name="newPassword" placeholder="Min. 6 characters" required>
                                    <button class="btn btn-outline-secondary toggle-password" type="button" data-target="newPass">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label small fw-semibold text-secondary">Confirm New Password *</label>
                                <div class="input-group">
                                    <input type="password" class="form-control" id="confPass" name="confirmPassword" required>
                                    <button class="btn btn-outline-secondary toggle-password" type="button" data-target="confPass">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                            </div>

                            <div class="pt-3 border-top">
                                <button type="submit" class="btn btn-outline-danger w-100">Update Password</button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Account Security Card -->
                <div class="cc-card p-3 bg-light border">
                    <h6 class="fw-bold mb-2 small text-secondary"><i class="bi bi-info-circle me-1"></i> Role Governance</h6>
                    <p class="text-muted small mb-0">Role privileges (<code><c:out value="${user.role}"/></code>) are administered by system governance policy. Self-promotion of roles is restricted for audit accountability.</p>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
