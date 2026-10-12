<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Access Denied (403) - CampusCare" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-6 text-center">
            <div class="empty-state">
                <i class="bi bi-shield-lock-fill text-danger" style="font-size:4rem;"></i>
                <h2 class="mt-3">403 - Access Denied</h2>
                <p class="text-muted">You do not have permission to access this resource or view another student's complaint.</p>
                <div class="mt-4">
                    <a href="${pageContext.request.contextPath}/" class="btn btn-cc-primary">Return to Safety</a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
