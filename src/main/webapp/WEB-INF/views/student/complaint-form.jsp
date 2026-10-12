<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Submit a Complaint — CampusCare" scope="request"/>
<c:set var="activeNav" value="new-complaint" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<div class="app-wrapper">
    <jsp:include page="/WEB-INF/views/shared/sidebar.jsp"/>

    <main class="main-content">
        <div class="mb-4">
            <h2 class="fw-bold mb-1">Submit a complaint</h2>
            <p class="text-muted small mb-0">Share enough detail so the right campus team can investigate and fix it.</p>
        </div>

        <!-- Alert messages -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show small" role="alert">
                <i class="bi bi-exclamation-circle-fill me-2"></i>
                <c:out value="${errorMessage}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="cc-card p-4">
            <form action="${pageContext.request.contextPath}/complaints" method="post" enctype="multipart/form-data">
                <input type="hidden" name="csrfToken" value="${csrfToken != null ? csrfToken : sessionScope.CSRF_TOKEN}">

                <div class="row g-4">
                    <div class="col-md-7">
                        <label for="title" class="form-label fw-semibold text-secondary small">Title *</label>
                        <input type="text" class="form-control" id="title" name="title"
                               placeholder="e.g. Wi-Fi not working in the library" required
                               value="<c:out value='${title != null ? title : \"\"}'/>">
                        <div class="form-text">Summarize the issue in a few concise words.</div>
                    </div>

                    <div class="col-md-5">
                        <label for="categoryId" class="form-label fw-semibold text-secondary small">Category *</label>
                        <select class="form-select" id="categoryId" name="categoryId" required>
                            <option value="">Select a category...</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.categoryId}" ${categoryId == cat.categoryId ? 'selected' : ''}>
                                    <c:out value="${cat.categoryName}"/>
                                </option>
                            </c:forEach>
                        </select>
                        <div class="form-text">Determines default department routing.</div>
                    </div>

                    <div class="col-md-7">
                        <label for="location" class="form-label fw-semibold text-secondary small">Location *</label>
                        <input type="text" class="form-control" id="location" name="location"
                               placeholder="e.g. Central Library, second floor near study hall" required
                               value="<c:out value='${location != null ? location : \"\"}'/>">
                        <div class="form-text">Specify block, floor, room number or landmark.</div>
                    </div>

                    <div class="col-md-5">
                        <label for="priority" class="form-label fw-semibold text-secondary small">Priority</label>
                        <select class="form-select" id="priority" name="priority">
                            <option value="LOW" ${'LOW'.equalsIgnoreCase(priority) ? 'selected' : ''}>Low (Non-urgent issue)</option>
                            <option value="MEDIUM" ${empty priority || 'MEDIUM'.equalsIgnoreCase(priority) ? 'selected' : ''}>Medium (Standard service request)</option>
                            <option value="HIGH" ${'HIGH'.equalsIgnoreCase(priority) ? 'selected' : ''}>High (Disrupts classes or daily routine)</option>
                            <option value="URGENT" ${'URGENT'.equalsIgnoreCase(priority) ? 'selected' : ''}>Urgent (Hazard, water leak, safety risk)</option>
                        </select>
                        <div class="form-text">Administration may calibrate priority upon review.</div>
                    </div>

                    <div class="col-12">
                        <label for="description" class="form-label fw-semibold text-secondary small">Describe the issue *</label>
                        <textarea class="form-control" id="description" name="description" rows="5" required
                                  placeholder="What happened? When did it start? Who or what is affected? Include any error codes or technician attempts."><c:out value="${description != null ? description : ''}"/></textarea>
                    </div>

                    <div class="col-12">
                        <label for="attachment" class="form-label fw-semibold text-secondary small">Photo or document (optional)</label>
                        <input class="form-control" type="file" id="attachment" name="attachment" accept=".jpg,.jpeg,.png,.gif,.pdf">
                        <div class="form-text">JPG, PNG or PDF • maximum size 10MB • files are stored securely with restricted access.</div>
                    </div>

                    <div class="col-12 pt-3 border-top d-flex align-items-center gap-3 flex-wrap">
                        <button type="submit" class="btn btn-cc-primary px-4 py-2">
                            <i class="bi bi-send-fill me-2"></i>Submit complaint
                        </button>
                        <span class="text-muted small">Your unique ticket identifier will be generated upon submission.</span>
                    </div>
                </div>
            </form>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
