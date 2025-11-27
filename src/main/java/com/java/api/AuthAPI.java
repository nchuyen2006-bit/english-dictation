package com.java.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.Model.LoginRequest;
import com.java.Model.RegisterRequest;
import com.java.Model.UserDTO;
import com.java.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthAPI {

    @Autowired
    private AuthService authService;

    /**
     * Đăng nhập
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            UserDTO user = authService.login(request);
            
            response.put("success", true);
            response.put("message", "Đăng nhập thành công");
            response.put("data", user);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Đăng ký
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            UserDTO user = authService.register(request);
            
            response.put("success", true);
            response.put("message", "Đăng ký thành công");
            response.put("data", user);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Lấy thông tin user hiện tại
     * GET /api/auth/me
     * Header: Authorization: Bearer {token}
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("success", false);
                response.put("message", "Token không hợp lệ");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            String token = authHeader.substring(7);
            UserDTO user = authService.getUserFromToken(token);
            
            if (user == null) {
                response.put("success", false);
                response.put("message", "Token không hợp lệ hoặc đã hết hạn");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            response.put("success", true);
            response.put("data", user);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Xác thực token
     * POST /api/auth/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("valid", false);
            return ResponseEntity.ok(response);
        }
        
        String token = authHeader.substring(7);
        boolean isValid = authService.validateToken(token);
        
        response.put("valid", isValid);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Generate password hash (CHỈ DÙNG ĐỂ TEST - XÓA SAU KHI SETUP XONG)
     * POST /api/auth/hash-password
     */
    @PostMapping("/hash-password")
    public ResponseEntity<Map<String, Object>> hashPassword(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String password = request.get("password");
            if (password == null || password.isEmpty()) {
                response.put("success", false);
                response.put("message", "Password không được để trống");
                return ResponseEntity.badRequest().body(response);
            }
            
            String hashedPassword = com.java.utils.PasswordUtil.hashPassword(password);
            
            response.put("success", true);
            response.put("password", password);
            response.put("hash", hashedPassword);
            response.put("warning", "XÓA API NÀY SAU KHI SETUP XONG!");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}