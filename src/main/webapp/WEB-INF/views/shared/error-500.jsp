<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Server Error (500) - CampusCare" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-6 text-center">
            <div class="empty-state">
                <i class="bi bi-exclamation-triangle text-warning" style="font-size:4rem;"></i>
                <h2 class="mt-3">500 - Application Error</h2>
                <p class="text-muted">A server-side error occurred while processing your request. The technical details have been logged for administrative investigation.</p>
                <div class="mt-4">
                    <a href="${pageContext.request.contextPath}/" class="btn btn-cc-primary">Return to Portal</a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
