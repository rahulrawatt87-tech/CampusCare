<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Student Registration — CampusCare" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-7 col-lg-6">
            
            <div class="text-center mb-4">
                <div class="cc-brand justify-content-center mb-2">
                    <span class="cc-brand-badge">CC</span>
                    <span class="fs-3">CampusCare</span>
                </div>
                <h4 class="fw-bold">Create Student Account</h4>
                <p class="text-muted small">Register to lodge complaints and track campus maintenance requests</p>
            </div>

            <!-- Error Alerts -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show small" role="alert">
                    <i class="bi bi-exclamation-circle-fill me-2"></i>
                    <c:out value="${errorMessage}"/>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <div class="cc-card p-4 shadow-sm border">
                <form action="${pageContext.request.contextPath}/register" method="post">
                    <div class="row g-3">
                        <div class="col-md-12">
                            <label for="fullName" class="form-label small fw-semibold text-secondary">Full Name *</label>
                            <input type="text" class="form-control" id="fullName" name="fullName"
                                   placeholder="e.g. Alex Morgan" required
                                   value="<c:out value='${fullName != null ? fullName : \"\"}'/>">
                        </div>

                        <div class="col-md-6">
                            <label for="email" class="form-label small fw-semibold text-secondary">College Email *</label>
                            <input type="email" class="form-control" id="email" name="email"
                                   placeholder="alex@campuscare.edu" required
                                   value="<c:out value='${email != null ? email : \"\"}'/>">
                        </div>

                        <div class="col-md-6">
                            <label for="studentId" class="form-label small fw-semibold text-secondary">Student ID / Roll No. *</label>
                            <input type="text" class="form-control" id="studentId" name="studentId"
                                   placeholder="STU-2024-0042" required
                                   value="<c:out value='${studentId != null ? studentId : \"\"}'/>">
                        </div>

                        <div class="col-md-6">
                            <label for="departmentId" class="form-label small fw-semibold text-secondary">Department</label>
                            <select class="form-select" id="departmentId" name="departmentId">
                                <option value="">Select your department...</option>
                                <c:forEach var="dept" items="${departments}">
                                    <option value="${dept.departmentId}" ${departmentId == dept.departmentId ? 'selected' : ''}>
                                        <c:out value="${dept.departmentName}"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="col-md-6">
                            <label for="courseYear" class="form-label small fw-semibold text-secondary">Course & Year</label>
                            <input type="text" class="form-control" id="courseYear" name="courseYear"
                                   placeholder="e.g. B.Tech CSE - 3rd Year"
                                   value="<c:out value='${courseYear != null ? courseYear : \"\"}'/>">
                        </div>

                        <div class="col-md-12">
                            <label for="phone" class="form-label small fw-semibold text-secondary">Contact Phone</label>
                            <input type="tel" class="form-control" id="phone" name="phone"
                                   placeholder="+1-555-0188"
                                   value="<c:out value='${phone != null ? phone : \"\"}'/>">
                        </div>

                        <div class="col-md-6">
                            <label for="password" class="form-label small fw-semibold text-secondary">Password *</label>
                            <div class="input-group">
                                <input type="password" class="form-control" id="password" name="password"
                                       placeholder="Minimum 6 characters" required>
                                <button class="btn btn-outline-secondary toggle-password" type="button" data-target="password">
                                    <i class="bi bi-eye"></i>
                                </button>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <label for="confirmPassword" class="form-label small fw-semibold text-secondary">Confirm Password *</label>
                            <div class="input-group">
                                <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                                       placeholder="Re-enter password" required>
                                <button class="btn btn-outline-secondary toggle-password" type="button" data-target="confirmPassword">
                                    <i class="bi bi-eye"></i>
                                </button>
                            </div>
                        </div>

                        <div class="col-12 mt-3">
                            <div class="form-check small text-muted">
                                <input class="form-check-input" type="checkbox" id="termsCheck" required checked>
                                <label class="form-check-label" for="termsCheck">
                                    I agree to the college campus service policy and code of conduct for lodging tickets.
                                </label>
                            </div>
                        </div>

                        <div class="col-12 mt-4">
                            <button type="submit" class="btn btn-cc-primary w-100 py-2 fw-semibold">
                                Complete Registration <i class="bi bi-check-lg ms-1"></i>
                            </button>
                        </div>
                    </div>
                </form>
            </div>

            <div class="text-center mt-3 text-muted small">
                Already registered? <a href="${pageContext.request.contextPath}/login" class="fw-semibold">Sign in here</a>
            </div>

        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
