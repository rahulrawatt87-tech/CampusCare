/**
 * CampusCare - Client-side interactions and helper scripts
 */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Password Visibility Toggle
    const toggleBtns = document.querySelectorAll('.toggle-password');
    toggleBtns.forEach(btn => {
        btn.addEventListener('click', function () {
            const targetId = this.getAttribute('data-target');
            const input = document.getElementById(targetId);
            if (input) {
                if (input.type === 'password') {
                    input.type = 'text';
                    this.innerHTML = '<i class="bi bi-eye-slash"></i>';
                } else {
                    input.type = 'password';
                    this.innerHTML = '<i class="bi bi-eye"></i>';
                }
            }
        });
    });

    // 2. Department to Staff Filtering in Assignment Modals
    const deptSelect = document.getElementById('assignDeptSelect');
    const staffSelect = document.getElementById('assignStaffSelect');
    if (deptSelect && staffSelect) {
        deptSelect.addEventListener('change', function () {
            const selectedDeptId = this.value;
            const options = staffSelect.querySelectorAll('option');
            let hasMatch = false;

            options.forEach(opt => {
                if (opt.value === '') {
                    opt.style.display = 'block';
                    return;
                }
                const optDept = opt.getAttribute('data-dept');
                if (!selectedDeptId || optDept === selectedDeptId || optDept === 'null' || optDept === '') {
                    opt.style.display = 'block';
                    hasMatch = true;
                } else {
                    opt.style.display = 'none';
                }
            });
            staffSelect.value = '';
        });
    }

    // 3. Status Change Confirmation
    const statusForms = document.querySelectorAll('.confirm-status-form');
    statusForms.forEach(form => {
        form.addEventListener('submit', function (e) {
            const statusVal = form.querySelector('[name="status"]')?.value;
            if (statusVal === 'REJECTED') {
                const reason = form.querySelector('[name="rejectionReason"]')?.value;
                if (!reason || reason.trim().length === 0) {
                    alert('Please provide a rejection reason.');
                    e.preventDefault();
                    return false;
                }
            }
            if (statusVal === 'RESOLVED') {
                const notes = form.querySelector('[name="resolutionNotes"]')?.value;
                if (!notes || notes.trim().length === 0) {
                    alert('Please provide resolution notes before marking as resolved.');
                    e.preventDefault();
                    return false;
                }
            }
        });
    });

    // 4. Client-side File Upload Size & Extension Validation
    const fileInput = document.querySelector('input[type="file"][name="attachment"]');
    if (fileInput) {
        fileInput.addEventListener('change', function () {
            if (this.files && this.files[0]) {
                const file = this.files[0];
                const maxBytes = 10 * 1024 * 1024; // 10MB
                if (file.size > maxBytes) {
                    alert('File size exceeds the 10MB limit. Please choose a smaller file.');
                    this.value = '';
                    return;
                }
                const ext = file.name.split('.').pop().toLowerCase();
                const allowed = ['jpg', 'jpeg', 'png', 'gif', 'pdf'];
                if (!allowed.includes(ext)) {
                    alert('Invalid file format. Allowed formats: JPG, PNG, GIF, PDF.');
                    this.value = '';
                }
            }
        });
    }
});
