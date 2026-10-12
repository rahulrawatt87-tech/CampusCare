<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="CampusCare — College Complaint Management System" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<!-- Hero Section -->
<section class="landing-hero">
    <div class="container">
        <div class="row align-items-center g-5">
            <div class="col-lg-6">
                <span class="hero-tag"><i class="bi bi-shield-check me-1"></i>Official College Service Portal</span>
                <h1 class="hero-headline">A better way to make campus better.</h1>
                <p class="hero-subtitle">
                    Report an issue. Follow the progress. Know what happens next. A centralized system connecting students with college administration.
                </p>
                <div class="d-flex flex-wrap gap-3 mb-4">
                    <c:choose>
                        <c:when test="${not empty sessionScope.currentUser}">
                            <a href="${pageContext.request.contextPath}/complaints/new" class="btn btn-cc-primary btn-lg px-4">
                                <i class="bi bi-plus-circle me-2"></i>Submit a complaint
                            </a>
                            <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-outline-secondary btn-lg px-4">
                                Open Workspace
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/login" class="btn btn-cc-primary btn-lg px-4">
                                <i class="bi bi-send me-2"></i>Submit a complaint
                            </a>
                            <a href="${pageContext.request.contextPath}/register" class="btn btn-outline-secondary btn-lg px-4">
                                Register as Student
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="text-muted small">
                    <i class="bi bi-info-circle me-1"></i>For campus facilities, electrical, Wi-Fi, plumbing, hostels, and academic support.
                </div>
            </div>

            <!-- Campus Graphic Card -->
            <div class="col-lg-6">
                <div class="cc-card shadow-sm border-0 bg-white p-4">
                    <div class="d-flex align-items-center justify-content-between mb-3 border-bottom pb-3">
                        <div class="d-flex align-items-center gap-2">
                            <span class="badge bg-primary-subtle text-primary fw-semibold p-2 rounded-3">
                                <i class="bi bi-buildings fs-5"></i>
                            </span>
                            <div>
                                <h6 class="mb-0 fw-bold">Campus Services Hub</h6>
                                <span class="text-muted small">Direct routing to 10+ campus departments</span>
                            </div>
                        </div>
                        <span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1">Operational</span>
                    </div>

                    <div class="row g-3 py-2">
                        <div class="col-6">
                            <div class="p-3 rounded-3 bg-light border">
                                <i class="bi bi-wifi text-primary fs-4 mb-2 d-block"></i>
                                <span class="fw-semibold d-block small">IT Support</span>
                                <span class="text-muted" style="font-size:0.75rem;">Wi-Fi & Labs</span>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="p-3 rounded-3 bg-light border">
                                <i class="bi bi-lightning-charge text-warning fs-4 mb-2 d-block"></i>
                                <span class="fw-semibold d-block small">Electrical</span>
                                <span class="text-muted" style="font-size:0.75rem;">Lighting & Power</span>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="p-3 rounded-3 bg-light border">
                                <i class="bi bi-droplet text-info fs-4 mb-2 d-block"></i>
                                <span class="fw-semibold d-block small">Plumbing</span>
                                <span class="text-muted" style="font-size:0.75rem;">Water & Washrooms</span>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="p-3 rounded-3 bg-light border">
                                <i class="bi bi-house-door text-success fs-4 mb-2 d-block"></i>
                                <span class="fw-semibold d-block small">Hostels</span>
                                <span class="text-muted" style="font-size:0.75rem;">Room Maintenance</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- How CampusCare Works -->
<section class="py-5 bg-white border-bottom">
    <div class="container">
        <div class="text-center mb-5">
            <span class="text-uppercase fw-bold text-muted small letter-spacing">Streamlined Workflow</span>
            <h2 class="fw-bold mt-1">How CampusCare works</h2>
            <p class="text-muted">A practical service portal for students, staff, and campus administrators.</p>
        </div>

        <div class="row g-4">
            <div class="col-md-4">
                <div class="metric-card h-100 p-4">
                    <div class="d-flex align-items-center justify-content-between mb-3">
                        <span class="badge bg-secondary-subtle text-secondary fw-bold fs-6 px-3 py-1">01</span>
                        <i class="bi bi-pencil-square fs-3 text-secondary"></i>
                    </div>
                    <h5 class="fw-bold mb-2">Submit</h5>
                    <p class="text-muted mb-0">Tell us what needs attention. Select category, priority, building location, and attach an optional photo.</p>
                </div>
            </div>

            <div class="col-md-4">
                <div class="metric-card h-100 p-4">
                    <div class="d-flex align-items-center justify-content-between mb-3">
                        <span class="badge bg-primary-subtle text-primary fw-bold fs-6 px-3 py-1">02</span>
                        <i class="bi bi-clock-history fs-3 text-primary"></i>
                    </div>
                    <h5 class="fw-bold mb-2">Track</h5>
                    <p class="text-muted mb-0">Follow real-time status updates on your ticket timeline. Receive in-app notifications as staff investigates.</p>
                </div>
            </div>

            <div class="col-md-4">
                <div class="metric-card h-100 p-4">
                    <div class="d-flex align-items-center justify-content-between mb-3">
                        <span class="badge bg-success-subtle text-success fw-bold fs-6 px-3 py-1">03</span>
                        <i class="bi bi-check2-circle fs-3 text-success"></i>
                    </div>
                    <h5 class="fw-bold mb-2">Resolve</h5>
                    <p class="text-muted mb-0">Review the official resolution note from technicians and submit feedback with a 1 to 5 star rating.</p>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Demo Accounts Helper for Viva & Testing -->
<section class="py-5" style="background-color: var(--cc-canvas);">
    <div class="container">
        <div class="cc-card border shadow-sm p-4">
            <div class="d-flex align-items-center justify-content-between flex-wrap gap-2 mb-3">
                <div>
                    <h5 class="fw-bold mb-1"><i class="bi bi-mortarboard-fill text-primary me-2"></i>College Evaluation & Demo Accounts</h5>
                    <p class="text-muted small mb-0">Pre-configured test accounts ready for end-to-end workflow demonstration.</p>
                </div>
                <span class="badge bg-primary px-3 py-2">Quick Sign-in Available</span>
            </div>

            <div class="row g-3">
                <div class="col-md-4">
                    <div class="p-3 bg-white rounded border">
                        <span class="badge bg-info-subtle text-info fw-bold mb-2">Student Account</span>
                        <div class="fw-bold">alex@campuscare.edu</div>
                        <div class="text-muted small">Password: <code>student123</code></div>
                        <div class="small text-secondary mt-1">Alex Morgan (CSE 3rd Year)</div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="p-3 bg-white rounded border">
                        <span class="badge bg-danger-subtle text-danger fw-bold mb-2">Administrator Account</span>
                        <div class="fw-bold">admin@campuscare.edu</div>
                        <div class="text-muted small">Password: <code>admin123</code></div>
                        <div class="small text-secondary mt-1">Prof. Robert Vance (Chief Admin)</div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="p-3 bg-white rounded border">
                        <span class="badge bg-warning-subtle text-warning-emphasis fw-bold mb-2">Department Staff</span>
                        <div class="fw-bold">it.staff@campuscare.edu</div>
                        <div class="text-muted small">Password: <code>staff123</code></div>
                        <div class="small text-secondary mt-1">Priya Sharma (IT Support Lead)</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
