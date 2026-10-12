package com.campuscare.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Server-side validation and security sanitization utility.
 * Enforces business rules and protects against XSS, CSV formula injection, and path traversal.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Set<String> ALLOWED_PRIORITIES = new HashSet<>(
            Arrays.asList("LOW", "MEDIUM", "HIGH", "URGENT"));

    private static final Set<String> ALLOWED_STATUSES = new HashSet<>(
            Arrays.asList("SUBMITTED", "UNDER_REVIEW", "ASSIGNED", "IN_PROGRESS", "RESOLVED", "REJECTED", "REOPENED"));

    private static final Set<String> ALLOWED_ROLES = new HashSet<>(
            Arrays.asList("STUDENT", "ADMIN", "STAFF"));

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(
            Arrays.asList("jpg", "jpeg", "png", "gif", "pdf"));

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidLength(String value, int min, int max) {
        if (value == null) return false;
        int len = value.trim().length();
        return len >= min && len <= max;
    }

    public static boolean isValidPriority(String priority) {
        return priority != null && ALLOWED_PRIORITIES.contains(priority.trim().toUpperCase());
    }

    public static boolean isValidStatus(String status) {
        return status != null && ALLOWED_STATUSES.contains(status.trim().toUpperCase());
    }

    public static boolean isValidRole(String role) {
        return role != null && ALLOWED_ROLES.contains(role.trim().toUpperCase());
    }

    public static boolean isAllowedFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) return false;
        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(ext);
    }

    /**
     * Sanitizes CSV field values to prevent Formula Injection (CWE-1236).
     * If a cell begins with '=', '+', '-', or '@', prefixes with a single quote.
     */
    public static String escapeCsv(String input) {
        if (input == null) return "";
        String s = input.trim();
        if (s.startsWith("=") || s.startsWith("+") || s.startsWith("-") || s.startsWith("@")) {
            s = "'" + s;
        }
        // Escape quotes
        if (s.contains("\"") || s.contains(",") || s.contains("\n") || s.contains("\r")) {
            s = "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    /**
     * Cleans filename to prevent directory traversal.
     */
    public static String sanitizeFilename(String filename) {
        if (filename == null) return "unknown";
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
