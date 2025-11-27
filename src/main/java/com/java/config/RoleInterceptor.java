package com.java.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor để kiểm tra quyền truy cập theo role
 */
@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        String userRole = (String) request.getAttribute("userRole");
        String requestURI = request.getRequestURI();
        
        // Kiểm tra quyền admin
        if (requestURI.startsWith("/api/admin")) {
            if (!"admin".equals(userRole)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"success\":false,\"message\":\"Bạn không có quyền truy cập\"}");
                return false;
            }
        }
        
        // Kiểm tra quyền teacher
        if (requestURI.startsWith("/api/teacher")) {
            if (!"teacher".equals(userRole) && !"admin".equals(userRole)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"success\":false,\"message\":\"Bạn không có quyền truy cập\"}");
                return false;
            }
        }
        
        // Premium content
        if (requestURI.contains("/premium")) {
            // Có thể kiểm tra subscription ở đây
            // Cần thêm logic để lấy thông tin subscription từ database
        }
        
        return true;
    }
}