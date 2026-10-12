<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Create Account — CampusCare" scope="request"/>
<jsp:include page="/WEB-INF/views/shared/header.jsp"/>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-7">
            
            <div class="text-center mb-4">
                <div class="cc-brand justify-content-center mb-2">
                    <span class="cc-brand-badge">CC</span>
                    <span class="fs-3">CampusCare</span>
                </div>
                <h3 class="fw-bold text-dark">Join CampusCare</h3>
                <p class="text-muted small">College Complaint Management & Campus Service Portal</p>
            </div>

            <!-- Error Alerts -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show small shadow-sm" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>
                    <c:out value="${errorMessage}"/>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <!-- Nav Tabs for Account Type -->
            <ul class="nav nav-pills nav-fill mb-3 p-1 bg-white border rounded-pill shadow-sm" id="regTabs" role="tablist">
                <li class="nav-item" role="presentation">
                    <button class="nav-link rounded-pill py-2 fw-semibold ${accountType != 'admin' ? 'active' : ''}" 
                            id="user-tab" data-bs-toggle="pill" data-bs-target="#user-pane" type="button" role="tab">
                        <i class="bi bi-people-fill me-1"></i> Student & Faculty
                    </button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link rounded-pill py-2 fw-semibold ${accountType == 'admin' ? 'active' : ''}" 
                            id="admin-tab" data-bs-toggle="pill" data-bs-target="#admin-pane" type="button" role="tab">
                        <i class="bi bi-shield-lock-fill me-1"></i> Verified Administrator
                    </button>
                </li>
            </ul>

            <div class="tab-content" id="regTabsContent">
                
                <!-- TAB 1: STUDENT & FACULTY REGISTRATION -->
                <div class="tab-pane fade ${accountType != 'admin' ? 'show active' : ''}" id="user-pane" role="tabpanel">
                    <div class="cc-card p-4 shadow-sm border">
                        
                        <div class="d-flex align-items-center justify-content-between pb-3 mb-3 border-bottom flex-wrap gap-2">
                            <div>
                                <h5 class="fw-bold mb-0 text-dark">Student & Teacher Registration</h5>
                                <span class="text-muted small">Register to lodge complaints and track campus maintenance requests</span>
                            </div>
                            <!-- Role Toggle -->
                            <div class="btn-group btn-group-sm" role="group" aria-label="Role selector">
                                <input type="radio" class="btn-check" name="roleSelector" id="roleStudent" autocomplete="off" 
                                       ${role != 'FACULTY' ? 'checked' : ''} onchange="onRoleChange('STUDENT')">
                                <label class="btn btn-outline-primary" for="roleStudent">
                                    <i class="bi bi-mortarboard me-1"></i> Student
                                </label>

                                <input type="radio" class="btn-check" name="roleSelector" id="roleFaculty" autocomplete="off"
                                       ${role == 'FACULTY' ? 'checked' : ''} onchange="onRoleChange('FACULTY')">
                                <label class="btn btn-outline-primary" for="roleFaculty">
                                    <i class="bi bi-person-video3 me-1"></i> Faculty / Teacher
                                </label>
                            </div>
                        </div>

                        <form action="${pageContext.request.contextPath}/register" method="post" id="userForm">
                            <input type="hidden" name="accountType" value="user">
                            <input type="hidden" name="role" id="hiddenRole" value="${role == 'FACULTY' ? 'FACULTY' : 'STUDENT'}">

                            <div class="row g-3">
                                <div class="col-md-12">
                                    <label for="fullName" class="form-label small fw-semibold text-secondary">Full Name *</label>
                                    <input type="text" class="form-control" id="fullName" name="fullName"
                                           placeholder="e.g. Dr. Sarah Jenkins or Alex Morgan" required
                                           value="<c:out value='${fullName != null && accountType != \"admin\" ? fullName : \"\"}'/>">
                                </div>

                                <div class="col-md-6">
                                    <label for="email" class="form-label small fw-semibold text-secondary">College Email *</label>
                                    <input type="email" class="form-control" id="email" name="email"
                                           placeholder="name@campuscare.edu" required
                                           value="<c:out value='${email != null && accountType != \"admin\" ? email : \"\"}'/>">
                                </div>

                                <div class="col-md-6">
                                    <label for="idNumber" id="idNumberLabel" class="form-label small fw-semibold text-secondary">
                                        <c:out value="${role == 'FACULTY' ? 'Faculty / Employee ID *' : 'Student ID / Roll No. *'}"/>
                                    </label>
                                    <input type="text" class="form-control" id="idNumber" name="idNumber"
                                           placeholder="${role == 'FACULTY' ? 'FAC-2022-042' : 'STU-2024-0042'}" required
                                           value="<c:out value='${idNumber != null ? idNumber : \"\"}'/>">
                                </div>

                                <div class="col-md-6">
                                    <label for="academicBranch" class="form-label small fw-semibold text-secondary">
                                        Academic Department / Branch *
                                    </label>
                                    <select class="form-select" id="academicBranch" name="academicBranch" required>
                                        <option value="">Select academic branch...</option>
                                        <option value="Computer Science & Engineering" ${academicBranch == 'Computer Science & Engineering' ? 'selected' : ''}>Computer Science & Engineering (CSE)</option>
                                        <option value="Information Technology" ${academicBranch == 'Information Technology' ? 'selected' : ''}>Information Technology (IT)</option>
                                        <option value="Electronics & Communication" ${academicBranch == 'Electronics & Communication' ? 'selected' : ''}>Electronics & Communication (ECE)</option>
                                        <option value="Electrical & Electronics" ${academicBranch == 'Electrical & Electronics' ? 'selected' : ''}>Electrical & Electronics (EEE)</option>
                                        <option value="Mechanical Engineering" ${academicBranch == 'Mechanical Engineering' ? 'selected' : ''}>Mechanical Engineering (ME)</option>
                                        <option value="Civil Engineering" ${academicBranch == 'Civil Engineering' ? 'selected' : ''}>Civil Engineering (CE)</option>
                                        <option value="Management Studies & Business" ${academicBranch == 'Management Studies & Business' ? 'selected' : ''}>Management Studies (MBA / BBA)</option>
                                        <option value="Applied Sciences & Humanities" ${academicBranch == 'Applied Sciences & Humanities' ? 'selected' : ''}>Applied Sciences & Humanities</option>
                                        <option value="Biotechnology" ${academicBranch == 'Biotechnology' ? 'selected' : ''}>Biotechnology</option>
                                        <option value="General Academic" ${academicBranch == 'General Academic' ? 'selected' : ''}>Other Academic Department</option>
                                    </select>
                                    <div class="form-text" style="font-size:0.75rem;">
                                        Your college academic faculty. (Service units like Electrical/Plumbing are handled by campus technicians).
                                    </div>
                                </div>

                                <div class="col-md-6">
                                    <label for="courseYear" id="courseYearLabel" class="form-label small fw-semibold text-secondary">
                                        <c:out value="${role == 'FACULTY' ? 'Designation / Title' : 'Course & Year'}"/>
                                    </label>
                                    <input type="text" class="form-control" id="courseYear" name="courseYear"
                                           placeholder="${role == 'FACULTY' ? 'e.g. Associate Professor, HOD' : 'e.g. B.Tech CSE - 3rd Year'}"
                                           value="<c:out value='${courseYear != null ? courseYear : \"\"}'/>">
                                </div>

                                <div class="col-md-12">
                                    <label for="phone" class="form-label small fw-semibold text-secondary">Contact Phone</label>
                                    <input type="tel" class="form-control" id="phone" name="phone"
                                           placeholder="+1-555-0188"
                                           value="<c:out value='${phone != null && accountType != \"admin\" ? phone : \"\"}'/>">
                                </div>

                                <div class="col-md-6">
                                    <label for="password" class="form-label small fw-semibold text-secondary">Password *</label>
                                    <div class="input-group">
                                        <input type="password" class="form-control" id="password" name="password"
                                               placeholder="Minimum 6 characters" minlength="6" required>
                                        <button class="btn btn-outline-secondary toggle-password" type="button" data-target="password">
                                            <i class="bi bi-eye"></i>
                                        </button>
                                    </div>
                                </div>

                                <div class="col-md-6">
                                    <label for="confirmPassword" class="form-label small fw-semibold text-secondary">Confirm Password *</label>
                                    <div class="input-group">
                                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                                               placeholder="Re-enter password" minlength="6" required>
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

                                <div class="col-12 mt-3">
                                    <button type="submit" class="btn btn-cc-primary w-100 py-2 fw-semibold">
                                        Complete Registration <i class="bi bi-arrow-right ms-1"></i>
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>

                    <!-- Staff Provisioning Info Callout -->
                    <div class="mt-3 p-3 bg-light rounded-3 border text-muted small d-flex align-items-center gap-2">
                        <i class="bi bi-info-circle text-primary fs-5"></i>
                        <div>
                            <strong>Maintenance / Staff accounts:</strong> Service technicians (electricians, plumbers, IT staff) do not self-register. Staff accounts are provisioned exclusively by Campus Administrators through their management console.
                        </div>
                    </div>
                </div>

                <!-- TAB 2: VERIFIED ADMINISTRATOR REGISTRATION -->
                <div class="tab-pane fade ${accountType == 'admin' ? 'show active' : ''}" id="admin-pane" role="tabpanel">
                    <div class="cc-card p-4 shadow-sm border">
                        
                        <div class="d-flex align-items-center gap-3 pb-3 mb-3 border-bottom">
                            <div class="rounded-circle bg-warning-subtle text-warning-emphasis p-2 d-flex align-items-center justify-content-center" style="width:40px;height:40px;">
                                <i class="bi bi-shield-check fs-4"></i>
                            </div>
                            <div>
                                <h5 class="fw-bold mb-0 text-dark">Institutional Administrator Setup</h5>
                                <span class="text-muted small">Restricted to authorized college administrative officials</span>
                            </div>
                        </div>

                        <div class="alert alert-warning border small py-2 px-3 mb-3" role="alert">
                            <i class="bi bi-lock-fill me-1 text-warning-emphasis"></i>
                            <strong>Identity Verification Required:</strong> You must enter your college's authorized verification secret key to authenticate administrator provisioning.
                        </div>

                        <form action="${pageContext.request.contextPath}/register" method="post">
                            <input type="hidden" name="accountType" value="admin">

                            <div class="row g-3">
                                <div class="col-md-12">
                                    <label for="adminFullName" class="form-label small fw-semibold text-secondary">Administrator Full Name *</label>
                                    <input type="text" class="form-control" id="adminFullName" name="fullName"
                                           placeholder="e.g. Dean Arthur Wallace" required
                                           value="<c:out value='${fullName != null && accountType == \"admin\" ? fullName : \"\"}'/>">
                                </div>

                                <div class="col-md-6">
                                    <label for="adminEmail" class="form-label small fw-semibold text-secondary">Official Institutional Email *</label>
                                    <input type="email" class="form-control" id="adminEmail" name="email"
                                           placeholder="admin.facilities@campuscare.edu" required
                                           value="<c:out value='${email != null && accountType == \"admin\" ? email : \"\"}'/>">
                                </div>

                                <div class="col-md-6">
                                    <label for="adminPhone" class="form-label small fw-semibold text-secondary">Office Phone</label>
                                    <input type="tel" class="form-control" id="adminPhone" name="phone"
                                           placeholder="+1-555-0100"
                                           value="<c:out value='${phone != null && accountType == \"admin\" ? phone : \"\"}'/>">
                                </div>

                                <div class="col-md-12">
                                    <label for="secretKey" class="form-label small fw-semibold text-danger">
                                        <i class="bi bi-key-fill me-1"></i> Institutional Verification Key *
                                    </label>
                                    <input type="password" class="form-control border-danger-subtle" id="secretKey" name="secretKey"
                                           placeholder="Enter institutional administration verification key" required
                                           value="<c:out value='${secretKey != null ? secretKey : \"\"}'/>">
                                    <div class="form-text" style="font-size:0.75rem;">
                                        Configured in system settings (Default master key: <code>CAMPUS-ADMIN-2026</code>).
                                    </div>
                                </div>

                                <div class="col-md-6">
                                    <label for="adminPassword" class="form-label small fw-semibold text-secondary">Admin Password *</label>
                                    <div class="input-group">
                                        <input type="password" class="form-control" id="adminPassword" name="password"
                                               placeholder="Minimum 6 characters" minlength="6" required>
                                        <button class="btn btn-outline-secondary toggle-password" type="button" data-target="adminPassword">
                                            <i class="bi bi-eye"></i>
                                        </button>
                                    </div>
                                </div>

                                <div class="col-md-6">
                                    <label for="adminConfirmPassword" class="form-label small fw-semibold text-secondary">Confirm Admin Password *</label>
                                    <div class="input-group">
                                        <input type="password" class="form-control" id="adminConfirmPassword" name="confirmPassword"
                                               placeholder="Re-enter password" minlength="6" required>
                                        <button class="btn btn-outline-secondary toggle-password" type="button" data-target="adminConfirmPassword">
                                            <i class="bi bi-eye"></i>
                                        </button>
                                    </div>
                                </div>

                                <div class="col-12 mt-4">
                                    <button type="submit" class="btn btn-dark w-100 py-2 fw-semibold">
                                        <i class="bi bi-shield-check me-1"></i> Verify & Register Administrator
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

            </div>

            <div class="text-center mt-3 text-muted small">
                Already have an account? <a href="${pageContext.request.contextPath}/login" class="fw-semibold">Sign in here</a>
            </div>

        </div>
    </div>
</div>

<script>
function onRoleChange(role) {
    var hiddenRole = document.getElementById('hiddenRole');
    var idLabel = document.getElementById('idNumberLabel');
    var idInput = document.getElementById('idNumber');
    var courseLabel = document.getElementById('courseYearLabel');
    var courseInput = document.getElementById('courseYear');

    hiddenRole.value = role;

    if (role === 'FACULTY') {
        idLabel.textContent = 'Faculty / Employee ID *';
        idInput.placeholder = 'FAC-2022-042';
        courseLabel.textContent = 'Designation / Title';
        courseInput.placeholder = 'e.g. Associate Professor, HOD';
    } else {
        idLabel.textContent = 'Student ID / Roll No. *';
        idInput.placeholder = 'STU-2024-0042';
        courseLabel.textContent = 'Course & Year';
        courseInput.placeholder = 'e.g. B.Tech CSE - 3rd Year';
    }
}

// Password toggle helper
document.querySelectorAll('.toggle-password').forEach(function(btn) {
    btn.addEventListener('click', function() {
        var targetId = this.getAttribute('data-target');
        var input = document.getElementById(targetId);
        var icon = this.querySelector('i');
        if (input && icon) {
            if (input.type === 'password') {
                input.type = 'text';
                icon.className = 'bi bi-eye-slash';
            } else {
                input.type = 'password';
                icon.className = 'bi bi-eye';
            }
        }
    });
});
</script>

<jsp:include page="/WEB-INF/views/shared/footer.jsp"/>
