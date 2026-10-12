<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Notifications — CampusCare" scope="request"/>
<c:set var="activeNav" value="notifications" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<div class="app-wrapper">
    <jsp:include page="/WEB-INF/views/shared/sidebar.jsp"/>

    <main class="main-content">
        <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
            <div>
                <h2 class="fw-bold mb-1">Notifications</h2>
                <p class="text-muted small mb-0">Stay updated on ticket assignments, reviews, and resolutions.</p>
            </div>
            <c:if test="${unreadCount > 0}">
                <form action="${pageContext.request.contextPath}/notifications/read-all" method="post" class="m-0">
                    <input type="hidden" name="csrfToken" value="${csrfToken != null ? csrfToken : sessionScope.CSRF_TOKEN}">
                    <button type="submit" class="btn btn-outline-secondary btn-sm">
                        <i class="bi bi-check2-all me-1"></i> Mark All as Read
                    </button>
                </form>
            </c:if>
        </div>

        <div class="cc-card">
            <c:choose>
                <c:when test="${not empty notifications}">
                    <ul class="list-group list-group-flush">
                        <c:forEach var="n" items="${notifications}">
                            <li class="list-group-item p-3 d-flex align-items-start justify-content-between gap-3 ${n.read ? '' : 'bg-light'}">
                                <div class="d-flex align-items-start gap-3">
                                    <div class="mt-1">
                                        <c:choose>
                                            <c:when test="${!n.read}">
                                                <i class="bi bi-bell-fill text-primary fs-5"></i>
                                            </c:when>
                                            <c:otherwise>
                                                <i class="bi bi-bell text-muted fs-5"></i>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div>
                                        <div class="text-dark ${n.read ? '' : 'fw-bold'}">
                                            <c:out value="${n.message}"/>
                                        </div>
                                        <div class="d-flex align-items-center gap-3 mt-1">
                                            <span class="text-muted small">
                                                <fmt:formatDate value="${n.createdAt}" pattern="dd MMM yyyy, hh:mm a"/>
                                            </span>
                                            <c:if test="${not empty n.ticketNumber}">
                                                <a href="${pageContext.request.contextPath}/complaints/${n.ticketNumber}" class="small ticket-code">
                                                    View Ticket <c:out value="${n.ticketNumber}"/>
                                                </a>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>

                                <c:if test="${!n.read}">
                                    <form action="${pageContext.request.contextPath}/notifications/read" method="post" class="m-0 flex-shrink-0">
                                        <input type="hidden" name="csrfToken" value="${csrfToken != null ? csrfToken : sessionScope.CSRF_TOKEN}">
                                        <input type="hidden" name="notificationId" value="${n.notificationId}">
                                        <button type="submit" class="btn btn-sm btn-link text-muted p-0 text-decoration-none" title="Mark as read">
                                            <i class="bi bi-check2"></i>
                                        </button>
                                    </form>
                                </c:if>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise>
                    <div class="empty-state py-5">
                        <i class="bi bi-bell-slash empty-state-icon"></i>
                        <div class="empty-state-title">No notifications yet</div>
                        <div class="empty-state-desc">You'll receive alert notifications whenever there is an update on your complaint tickets.</div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
