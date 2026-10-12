<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<aside class="cc-sidebar">
    <c:choose>
        <%-- STUDENT WORKSPACE SIDEBAR --%>
        <c:when test="${sessionScope.currentUser.student}">
            <div class="sidebar-heading">Student workspace</div>
            <ul class="sidebar-nav">
                <li>
                    <a href="${pageContext.request.contextPath}/student/dashboard" class="sidebar-link ${activeNav == 'dashboard' ? 'active' : ''}">
                        <i class="bi bi-speedometer2"></i>
                        <span>Dashboard</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/complaints/new" class="sidebar-link ${activeNav == 'new-complaint' ? 'active' : ''}">
                        <i class="bi bi-plus-circle"></i>
                        <span>New complaint</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/complaints" class="sidebar-link ${activeNav == 'my-complaints' ? 'active' : ''}">
                        <i class="bi bi-journal-text"></i>
                        <span>My complaints</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/notifications" class="sidebar-link ${activeNav == 'notifications' ? 'active' : ''}">
                        <i class="bi bi-bell"></i>
                        <span>Notifications</span>
                        <c:if test="${not empty unreadNotifs && unreadNotifs > 0}">
                            <span class="sidebar-badge">${unreadNotifs}</span>
                        </c:if>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/profile" class="sidebar-link ${activeNav == 'profile' ? 'active' : ''}">
                        <i class="bi bi-person"></i>
                        <span>My profile</span>
                    </a>
                </li>
            </ul>
        </c:when>

        <%-- ADMIN WORKSPACE SIDEBAR --%>
        <c:when test="${sessionScope.currentUser.admin}">
            <div class="sidebar-heading">Admin workspace</div>
            <ul class="sidebar-nav">
                <li>
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="sidebar-link ${activeNav == 'dashboard' ? 'active' : ''}">
                        <i class="bi bi-speedometer2"></i>
                        <span>Dashboard</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/complaints" class="sidebar-link ${activeNav == 'complaint-queue' ? 'active' : ''}">
                        <i class="bi bi-inbox"></i>
                        <span>Manage complaints</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/departments" class="sidebar-link ${activeNav == 'departments' ? 'active' : ''}">
                        <i class="bi bi-building"></i>
                        <span>Departments</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/reports" class="sidebar-link ${activeNav == 'reports' ? 'active' : ''}">
                        <i class="bi bi-bar-chart"></i>
                        <span>Reports & Stats</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/notifications" class="sidebar-link ${activeNav == 'notifications' ? 'active' : ''}">
                        <i class="bi bi-bell"></i>
                        <span>Notifications</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/profile" class="sidebar-link ${activeNav == 'profile' ? 'active' : ''}">
                        <i class="bi bi-person"></i>
                        <span>My profile</span>
                    </a>
                </li>
            </ul>
        </c:when>

        <%-- STAFF WORKSPACE SIDEBAR --%>
        <c:when test="${sessionScope.currentUser.staff}">
            <div class="sidebar-heading">Staff workspace</div>
            <ul class="sidebar-nav">
                <li>
                    <a href="${pageContext.request.contextPath}/staff/dashboard" class="sidebar-link ${activeNav == 'dashboard' ? 'active' : ''}">
                        <i class="bi bi-tools"></i>
                        <span>Assigned tickets</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/notifications" class="sidebar-link ${activeNav == 'notifications' ? 'active' : ''}">
                        <i class="bi bi-bell"></i>
                        <span>Notifications</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/profile" class="sidebar-link ${activeNav == 'profile' ? 'active' : ''}">
                        <i class="bi bi-person"></i>
                        <span>My profile</span>
                    </a>
                </li>
            </ul>
        </c:when>
    </c:choose>

    <div class="mt-auto pt-3 border-top text-muted small">
        <div class="d-flex align-items-center gap-2">
            <i class="bi bi-person-circle fs-5 text-secondary"></i>
            <div class="text-truncate">
                <div class="fw-semibold text-dark text-truncate"><c:out value="${sessionScope.currentUser.fullName}"/></div>
                <div class="text-muted" style="font-size:0.75rem;"><c:out value="${sessionScope.currentUser.role}"/></div>
            </div>
        </div>
    </div>
</aside>
