package com.java.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.java.utils.JwtUtil;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        // Bỏ qua OPTIONS request (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        // Lấy token từ header
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"Token không hợp lệ\"}");
            return false;
        }
        
        String token = authHeader.substring(7);
        
        // Xác thực token
        if (!JwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"Token không hợp lệ hoặc đã hết hạn\"}");
            return false;
        }
        
        // Lưu thông tin user vào request
        String email = JwtUtil.getEmailFromToken(token);
        String role = JwtUtil.getRoleFromToken(token);
        
        request.setAttribute("userEmail", email);
        request.setAttribute("userRole", role);
        
        return true;
    }
}
