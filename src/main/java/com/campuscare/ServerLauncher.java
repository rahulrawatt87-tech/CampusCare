package com.campuscare;

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
 * Allows running CampusCare with a single command without standalone Tomcat installation.
 */
public class ServerLauncher {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String portProp = System.getProperty("server.port");
        if (portProp != null && !portProp.trim().isEmpty()) {
            try {
                port = Integer.parseInt(portProp.trim());
            } catch (NumberFormatException ignored) {}
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector(); // Trigger connector initialization

        File baseDir = new File("target/tomcat");
        baseDir.mkdirs();
        tomcat.setBaseDir(baseDir.getAbsolutePath());

        File webappDir = new File("src/main/webapp");
        String contextPath = "/CampusCare";

        Context ctx = tomcat.addWebapp(contextPath, webappDir.getAbsolutePath());
        // Crucial: assign classloader so Tomcat digester and webapp find all Maven classes
        ctx.setParentClassLoader(ServerLauncher.class.getClassLoader());

        // Enable loading compiled classes from target/classes
        File classesDir = new File("target/classes");
        if (classesDir.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", classesDir.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        // Mount ROOT context to gracefully redirect http://localhost:8080/ -> http://localhost:8080/CampusCare/
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

        System.out.println("==================================================================");
        System.out.println("  CampusCare College Complaint Management System v2.0");
        System.out.println("  Embedded Tomcat 9 Server running on port: " + port);
        System.out.println("  Local URL: http://localhost:" + port + contextPath + "/");
        System.out.println("==================================================================");

        tomcat.start();
        System.out.println("Server is running. Press Ctrl+C to stop.");
        tomcat.getServer().await();
    }
}
