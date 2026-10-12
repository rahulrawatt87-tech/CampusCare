package com.campuscare.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Thread-safe Database Connection utility.
 * Supports cloud deployment environment variables (Render, Heroku, Docker)
 * with graceful fallback to application.properties.
 */
public class DBConnection {

    private static String url;
    private static String user;
    private static String password;
    private static String driver;
    private static volatile boolean initialized = false;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load application.properties, using defaults. " + e.getMessage());
        }

        // 1. Database Driver
        String envDriver = System.getenv("DB_DRIVER");
        driver = (envDriver != null && !envDriver.trim().isEmpty())
                ? envDriver.trim()
                : props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");

        // 2. Database URL (supports DB_URL, DATABASE_URL, or application.properties)
        String envUrl = System.getenv("DB_URL");
        if (envUrl == null || envUrl.trim().isEmpty()) {
            envUrl = System.getenv("DATABASE_URL");
        }
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            url = envUrl.trim();
        } else {
            url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/campuscare_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8");
        }

        // 3. Database Username
        String envUser = System.getenv("DB_USER");
        if (envUser == null || envUser.trim().isEmpty()) {
            envUser = System.getenv("DATABASE_USER");
        }
        user = (envUser != null && !envUser.trim().isEmpty())
                ? envUser.trim()
                : props.getProperty("db.user", "root");

        // 4. Database Password
        String envPass = System.getenv("DB_PASSWORD");
        if (envPass == null) {
            envPass = System.getenv("DATABASE_PASSWORD");
        }
        password = (envPass != null)
                ? envPass
                : props.getProperty("db.password", "root");

        // Auto-sanitize Aiven & Cloud connection string formats (e.g. mysql://user:pass@host:port/db)
        if (url.startsWith("mysql://")) {
            String stripped = url.substring("mysql://".length());
            if (stripped.contains("@")) {
                String[] parts = stripped.split("@", 2);
                String userPass = parts[0];
                String hostAndDb = parts[1];
                if (userPass.contains(":")) {
                    String[] creds = userPass.split(":", 2);
                    if (envUser == null || envUser.trim().isEmpty()) {
                        user = creds[0];
                    }
                    if (envPass == null || envPass.trim().isEmpty()) {
                        password = creds[1];
                    }
                }
                url = "jdbc:mysql://" + hostAndDb;
            } else {
                url = "jdbc:mysql://" + stripped;
            }
        }
        if (!url.startsWith("jdbc:")) {
            url = "jdbc:" + url;
        }

        // Convert ssl-mode parameter to standard MySQL Connector/J parameters
        if (url.contains("ssl-mode=REQUIRED") || url.contains("ssl-mode=required")) {
            url = url.replace("ssl-mode=REQUIRED", "useSSL=true&allowPublicKeyRetrieval=true")
                     .replace("ssl-mode=required", "useSSL=true&allowPublicKeyRetrieval=true");
        } else if (!url.contains("useSSL=") && !url.contains("sslMode=")) {
            String delimiter = url.contains("?") ? "&" : "?";
            url = url + delimiter + "useSSL=true&allowPublicKeyRetrieval=true";
        }

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database JDBC Driver not found: " + driver, e);
        }
    }

    /**
     * Obtains a database connection, automatically initializing tables if needed on fresh deployments.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, password);
        if (!initialized) {
            synchronized (DBConnection.class) {
                if (!initialized) {
                    DatabaseInitializer.initializeIfNeeded(conn);
                    initialized = true;
                }
            }
        }
        return conn;
    }

    /**
     * Tests whether the database connection is currently healthy.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public static String getUrl() {
        return url;
    }
}
