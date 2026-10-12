package com.campuscare.filter;

import com.campuscare.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class RoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        String relativePath = path.substring(contextPath.length());

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user != null) {
            if (relativePath.startsWith("/admin")) {
                if (!user.isAdmin()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator role required.");
                    return;
                }
            } else if (relativePath.startsWith("/staff")) {
                if (!user.isStaff() && !user.isAdmin()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Department staff privileges required.");
                    return;
                }
            } else if (relativePath.startsWith("/student")) {
                if (!user.isComplainant() && !user.isAdmin()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Complainant privileges required.");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
