package com.campuscare;

import com.campuscare.util.DBConnection;
import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Embedded Tomcat 9 Server Launcher.
 * Supports cloud hosting (Render, Railway, Docker) and local execution.
 */
public class ServerLauncher {

    public static void main(String[] args) throws Exception {
        // 1. Resolve Port (Render provides dynamic $PORT environment variable)
        int port = 8080;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.trim().isEmpty()) {
            try {
                port = Integer.parseInt(envPort.trim());
            } catch (NumberFormatException ignored) {}
        } else {
            String portProp = System.getProperty("server.port");
            if (portProp != null && !portProp.trim().isEmpty()) {
                try {
                    port = Integer.parseInt(portProp.trim());
                } catch (NumberFormatException ignored) {}
            }
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector(); // Trigger connector initialization

        File baseDir = new File("target/tomcat");
        baseDir.mkdirs();
        tomcat.setBaseDir(baseDir.getAbsolutePath());

        // 2. Resolve WebApp Directory
        File webappDir = new File("src/main/webapp");
        if (!webappDir.exists()) {
            webappDir = new File("target/CampusCare");
        }

        // 3. Resolve Context Path (Render defaults to root "")
        String contextPath = System.getenv("SERVER_CONTEXT_PATH");
        if (contextPath != null) {
            contextPath = contextPath.replace("\"", "").replace("'", "").trim();
        }
        if (contextPath == null || contextPath.isEmpty()) {
            contextPath = System.getProperty("server.contextPath");
            if (contextPath != null) {
                contextPath = contextPath.replace("\"", "").replace("'", "").trim();
            }
        }
        if (contextPath == null || contextPath.isEmpty()) {
            // Default to root in cloud if PORT is provided by Render, else /CampusCare
            contextPath = (envPort != null) ? "" : "/CampusCare";
        }
        if ("/".equals(contextPath) || "root".equalsIgnoreCase(contextPath)) {
            contextPath = "";
        }

        Context ctx = tomcat.addWebapp(contextPath, webappDir.getAbsolutePath());
        ctx.setParentClassLoader(ServerLauncher.class.getClassLoader());

        // 4. Attach classes directory
        File classesDir = new File("target/classes");
        if (!classesDir.exists()) {
            classesDir = new File("target/CampusCare/WEB-INF/classes");
        }
        if (classesDir.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", classesDir.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        // 5. Handle Context Fallback / Redirects
        if ("/CampusCare".equals(contextPath)) {
            // Root context redirects to /CampusCare/
            File rootTemp = new File("target/rootTemp");
            rootTemp.mkdirs();
            Context rootCtx = tomcat.addContext("", rootTemp.getAbsolutePath());
            Tomcat.addServlet(rootCtx, "rootRedirect", new HttpServlet() {
                @Override
                protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                    resp.sendRedirect("/CampusCare/");
                }
            });
            rootCtx.addServletMappingDecoded("/*", "rootRedirect");
        } else if ("".equals(contextPath)) {
            // App is mounted at root. Redirect any legacy /CampusCare/* requests cleanly to /*
            File ccTemp = new File("target/ccTemp");
            ccTemp.mkdirs();
            Context ccCtx = tomcat.addContext("/CampusCare", ccTemp.getAbsolutePath());
            Tomcat.addServlet(ccCtx, "ccRedirect", new HttpServlet() {
                @Override
                protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                    String uri = req.getRequestURI();
                    String pathAfter = uri.startsWith("/CampusCare") ? uri.substring("/CampusCare".length()) : "";
                    if (pathAfter.isEmpty()) {
                        pathAfter = "/";
                    }
                    if (req.getQueryString() != null) {
                        pathAfter += "?" + req.getQueryString();
                    }
                    resp.sendRedirect(pathAfter);
                }
            });
            ccCtx.addServletMappingDecoded("/*", "ccRedirect");
        }

        System.out.println("==================================================================");
        System.out.println("  CampusCare College Complaint Management System v2.0");
        System.out.println("  Server running on port: " + port);
        System.out.println("  Context Path: '" + contextPath + "'");
        System.out.println("==================================================================");

        // Pre-test DB connection on startup
        try {
            boolean dbOk = DBConnection.testConnection();
            System.out.println("  Database Connectivity: " + (dbOk ? "CONNECTED (OK)" : "PENDING INITIALIZATION"));
        } catch (Exception e) {
            System.out.println("  Database Connectivity Notice: " + e.getMessage());
        }
        System.out.println("==================================================================");

        tomcat.start();
        System.out.println("Server is running. Listening on port " + port + "...");
        tomcat.getServer().await();
    }
}
