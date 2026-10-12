package com.campuscare.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Automatically initializes database schema and seeds on fresh cloud deployments.
 */
public class DatabaseInitializer {

    private static volatile boolean initialized = false;

    public static synchronized void initializeIfNeeded(Connection conn) {
        if (initialized) {
            return;
        }

        try {
            boolean tablesExist = checkTableExists(conn, "departments");
            if (!tablesExist) {
                System.out.println("[DatabaseInitializer] Fresh database detected. Creating tables from schema.sql...");
                executeSqlScript(conn, "db/schema.sql");

                System.out.println("[DatabaseInitializer] Seeding initial data from seed.sql...");
                executeSqlScript(conn, "db/seed.sql");
                System.out.println("[DatabaseInitializer] Database initialization and seed completed successfully!");
            } else {
                // Ensure recent schema migrations are applied
                migrateSchemaIfNeeded(conn);
            }
            initialized = true;
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Warning during database check/initialization: " + e.getMessage());
        }
    }

    private static boolean checkTableExists(Connection conn, String tableName) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1 FROM " + tableName + " LIMIT 1")) {
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static void migrateSchemaIfNeeded(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Check if academic_branch column exists in users table
            boolean hasAcademicBranch = false;
            try (ResultSet rs = stmt.executeQuery("SELECT academic_branch FROM users LIMIT 1")) {
                hasAcademicBranch = true;
            } catch (SQLException ignored) {}

            if (!hasAcademicBranch) {
                System.out.println("[DatabaseInitializer] Adding academic_branch column to users table...");
                try {
                    stmt.executeUpdate("ALTER TABLE users ADD COLUMN academic_branch VARCHAR(100) NULL AFTER student_id");
                } catch (SQLException e) {
                    System.err.println("[DatabaseInitializer] Could not add academic_branch column: " + e.getMessage());
                }
            }

            // Ensure role ENUM contains FACULTY
            try {
                stmt.executeUpdate("ALTER TABLE users MODIFY COLUMN role ENUM('STUDENT', 'FACULTY', 'ADMIN', 'STAFF') NOT NULL DEFAULT 'STUDENT'");
            } catch (SQLException ignored) {}
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Migration check warning: " + e.getMessage());
        }
    }

    private static void executeSqlScript(Connection conn, String resourcePath) {
        try (InputStream in = DatabaseInitializer.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                System.err.println("[DatabaseInitializer] Resource not found: " + resourcePath);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                // Skip empty lines and single-line SQL comments
                if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("//")) {
                    continue;
                }
                sb.append(line).append("\n");
            }

            String[] statements = sb.toString().split(";");
            try (Statement stmt = conn.createStatement()) {
                for (String rawSql : statements) {
                    String sql = rawSql.trim();
                    if (sql.isEmpty()) {
                        continue;
                    }
                    // Skip database creation / switching in managed cloud databases
                    String upper = sql.toUpperCase();
                    if (upper.startsWith("CREATE DATABASE") || upper.startsWith("USE ")) {
                        continue;
                    }

                    try {
                        stmt.execute(sql);
                    } catch (SQLException e) {
                        // Tolerate existing tables or duplicate entries during re-runs
                        if (!e.getMessage().toLowerCase().contains("already exists") &&
                            !e.getMessage().toLowerCase().contains("duplicate")) {
                            System.err.println("[DatabaseInitializer] SQL Notice on '" + resourcePath + "': " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Failed executing " + resourcePath + ": " + e.getMessage());
        }
    }
}
