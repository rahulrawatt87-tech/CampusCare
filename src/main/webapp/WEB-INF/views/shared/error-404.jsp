<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Page Not Found (404) - CampusCare" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-6 text-center">
            <div class="empty-state">
                <i class="bi bi-question-circle text-secondary" style="font-size:4rem;"></i>
                <h2 class="mt-3">404 - Ticket or Page Not Found</h2>
                <p class="text-muted">The requested complaint ticket or page does not exist or has been relocated.</p>
                <div class="mt-4">
                    <a href="${pageContext.request.contextPath}/" class="btn btn-cc-primary">Back to Home</a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
