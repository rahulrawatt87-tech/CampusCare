<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Sign In — CampusCare" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-5 col-lg-4">
            
            <div class="text-center mb-4">
                <div class="cc-brand justify-content-center mb-2">
                    <span class="cc-brand-badge">CC</span>
                    <span class="fs-3">CampusCare</span>
                </div>
                <h4 class="fw-bold">Welcome back</h4>
                <p class="text-muted small">Sign in to your campus complaint tracking portal</p>
            </div>

            <!-- Error and Success Alerts -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show small" role="alert">
                    <i class="bi bi-exclamation-circle-fill me-2"></i>
                    <c:out value="${errorMessage}"/>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success alert-dismissible fade show small" role="alert">
                    <i class="bi bi-check-circle-fill me-2"></i>
                    <c:out value="${successMessage}"/>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <div class="cc-card p-4 shadow-sm border">
                <form action="${pageContext.request.contextPath}/login" method="post" id="loginForm">
                    <div class="mb-3">
                        <label for="email" class="form-label small fw-semibold text-secondary">College Email</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light text-muted"><i class="bi bi-envelope"></i></span>
                            <input type="email" class="form-control" id="email" name="email"
                                   placeholder="name@campuscare.edu" required
                                   value="<c:out value='${enteredEmail != null ? enteredEmail : \"\"}'/>">
                        </div>
                    </div>

                    <div class="mb-4">
                        <div class="d-flex justify-content-between align-items-center mb-1">
                            <label for="password" class="form-label small fw-semibold text-secondary mb-0">Password</label>
                        </div>
                        <div class="input-group">
                            <span class="input-group-text bg-light text-muted"><i class="bi bi-lock"></i></span>
                            <input type="password" class="form-control" id="password" name="password"
                                   placeholder="••••••••" required>
                            <button class="btn btn-outline-secondary toggle-password" type="button" data-target="password">
                                <i class="bi bi-eye"></i>
                            </button>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-cc-primary w-100 py-2 fw-semibold">
                        Sign In <i class="bi bi-arrow-right ms-1"></i>
                    </button>
                </form>

                <!-- Quick Fill Buttons for Project Evaluator / Viva Demo -->
                <div class="mt-4 pt-3 border-top">
                    <div class="text-muted small fw-semibold mb-2 text-center">Quick Demo Sign-in:</div>
                    <div class="d-flex flex-wrap gap-2 justify-content-center">
                        <button type="button" class="btn btn-sm btn-outline-primary"
                                onclick="fillCreds('alex@campuscare.edu', 'student123')">Student (Alex)</button>
                        <button type="button" class="btn btn-sm btn-outline-danger"
                                onclick="fillCreds('admin@campuscare.edu', 'admin123')">Admin (Vance)</button>
                        <button type="button" class="btn btn-sm btn-outline-warning text-dark"
                                onclick="fillCreds('it.staff@campuscare.edu', 'staff123')">Staff (Priya)</button>
                    </div>
                </div>
            </div>

            <div class="text-center mt-3 text-muted small">
                Don't have an account? <a href="${pageContext.request.contextPath}/register" class="fw-semibold">Register here</a>
            </div>

        </div>
    </div>
</div>

<script>
function fillCreds(e, p) {
    document.getElementById('email').value = e;
    document.getElementById('password').value = p;
    document.getElementById('loginForm').submit();
}
</script>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
