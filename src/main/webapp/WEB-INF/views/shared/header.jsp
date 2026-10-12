<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'CampusCare - College Complaint Management System'}"/></title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- CampusCare Custom Design Tokens & Styles -->
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>

<header class="cc-navbar">
    <div class="container-fluid d-flex align-items-center justify-content-between">
        <a href="${pageContext.request.contextPath}/" class="cc-brand">
            <span class="cc-brand-badge">CC</span>
            <span>CampusCare</span>
        </a>

        <div class="d-flex align-items-center gap-3">
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <!-- Notifications Link -->
                    <a href="${pageContext.request.contextPath}/notifications" class="btn btn-light position-relative p-2 rounded-circle" title="Notifications">
                        <i class="bi bi-bell fs-5 text-secondary"></i>
                        <c:if test="${not empty unreadNotifs && unreadNotifs > 0}">
                            <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" style="font-size:0.65rem;">
                                ${unreadNotifs}
                            </span>
                        </c:if>
                    </a>

                    <!-- User Menu Dropdown -->
                    <div class="dropdown">
                        <button class="btn btn-outline-secondary d-flex align-items-center gap-2 py-1 px-3 rounded-pill" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <span class="badge bg-primary rounded-circle p-1" style="width:24px;height:24px;display:inline-flex;align-items:center;justify-content:center;">
                                <c:out value="${sessionScope.currentUser.fullName.substring(0, 1)}"/>
                            </span>
                            <span class="fw-semibold text-truncate" style="max-width:140px;"><c:out value="${sessionScope.currentUser.fullName}"/></span>
                            <i class="bi bi-chevron-down small"></i>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end shadow-sm">
                            <li class="px-3 py-2 border-bottom">
                                <div class="fw-bold"><c:out value="${sessionScope.currentUser.fullName}"/></div>
                                <div class="text-muted small"><c:out value="${sessionScope.currentUser.email}"/></div>
                                <div class="badge bg-light text-dark border mt-1"><c:out value="${sessionScope.currentUser.role}"/></div>
                            </li>
                            <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person me-2"></i>My Profile</a></li>
                            <c:if test="${sessionScope.currentUser.student}">
                                <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/student/dashboard"><i class="bi bi-speedometer2 me-2"></i>Student Workspace</a></li>
                                <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/complaints"><i class="bi bi-journal-text me-2"></i>My Complaints</a></li>
                            </c:if>
                            <c:if test="${sessionScope.currentUser.admin}">
                                <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/admin/dashboard"><i class="bi bi-grid me-2"></i>Admin Dashboard</a></li>
                                <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/admin/complaints"><i class="bi bi-inbox me-2"></i>Complaint Queue</a></li>
                                <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/admin/reports"><i class="bi bi-graph-up me-2"></i>Reports & Analytics</a></li>
                            </c:if>
                            <c:if test="${sessionScope.currentUser.staff}">
                                <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/staff/dashboard"><i class="bi bi-tools me-2"></i>Staff Dashboard</a></li>
                            </c:if>
                            <li><hr class="dropdown-divider"></li>
                            <li>
                                <form action="${pageContext.request.contextPath}/logout" method="post" class="m-0 p-0">
                                    <input type="hidden" name="csrfToken" value="${csrfToken != null ? csrfToken : sessionScope.CSRF_TOKEN}">
                                    <button type="submit" class="dropdown-item py-2 text-danger"><i class="bi bi-box-arrow-right me-2"></i>Sign Out</button>
                                </form>
                            </li>
                        </ul>
                    </div>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-secondary px-3">Sign in</a>
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-cc-primary px-3">Create account</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</header>
