package com.campuscare.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * Utility to access application.properties configuration.
 */
public class ConfigUtil {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = ConfigUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load application.properties: " + e.getMessage());
        }
    }

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static String getAdminRegistrationKey() {
        return get("admin.registration.secretKey", "CAMPUS-ADMIN-2026").trim();
    }
}
