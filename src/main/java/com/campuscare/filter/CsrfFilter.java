package com.campuscare.filter;

import com.campuscare.util.CsrfUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class CsrfFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Ensure CSRF token exists in session
        HttpSession session = request.getSession(true);
        String token = CsrfUtil.getToken(session);
        request.setAttribute("csrfToken", token);

        String method = request.getMethod();
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        String relativePath = path.substring(contextPath.length());

        // Validate state-changing POST requests for authenticated sections
        if ("POST".equalsIgnoreCase(method)) {
            // Exclude public login / register form submissions if initial handshake
            boolean isPublicForm = "/login".equals(relativePath) || "/register".equals(relativePath);

            if (!isPublicForm) {
                if (!CsrfUtil.isValid(request)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
