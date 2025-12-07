package com.java.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.java.repository.UserRepository;
import com.java.repository.entity.UserEntity;
import com.java.service.UserProgressService;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "*")
public class UserProgressAPI {

    @Autowired
    private UserProgressService progressService;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * ✅ THÊM MỚI: Lấy tiến độ của user hiện tại (từ token)
     * GET /api/progress/my-progress
     */
    @GetMapping("/my-progress")
    public ResponseEntity<Map<String, Object>> getMyProgress(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Lấy email từ token (AuthInterceptor đã set vào request)
            String userEmail = (String) request.getAttribute("userEmail");
            
            if (userEmail == null) {
                response.put("success", false);
                response.put("message", "Chưa đăng nhập");
                return ResponseEntity.status(401).body(response);
            }
            
            // Lấy user từ email
            UserEntity user = userRepository.findByEmail(userEmail);
            if (user == null) {
                response.put("success", false);
                response.put("message", "User không tồn tại");
                return ResponseEntity.status(404).body(response);
            }
            
            // Lấy progress của user
            Map<Integer, Map<String, Object>> progress = progressService.getAllCategoryProgress(user.getId());
            
            response.put("success", true);
            response.put("data", progress);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error loading progress: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Lấy tiến độ của tất cả categories cho user
     * GET /api/progress/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getAllProgress(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<Integer, Map<String, Object>> progress = progressService.getAllCategoryProgress(userId);
            
            response.put("success", true);
            response.put("data", progress);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error loading progress: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * ✅ THÊM MỚI: Lấy tiến độ của 1 category (user hiện tại)
     * GET /api/progress/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getMyCategoryProgress(
            @PathVariable Integer categoryId,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String userEmail = (String) request.getAttribute("userEmail");
            if (userEmail == null) {
                response.put("success", false);
                response.put("message", "Chưa đăng nhập");
                return ResponseEntity.status(401).body(response);
            }
            
            UserEntity user = userRepository.findByEmail(userEmail);
            if (user == null) {
                response.put("success", false);
                response.put("message", "User không tồn tại");
                return ResponseEntity.status(404).body(response);
            }
            
            Map<String, Object> progress = progressService.getCategoryProgress(user.getId(), categoryId);
            
            response.put("success", true);
            response.put("data", progress);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error loading progress: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Lấy tiến độ của 1 category cụ thể
     * GET /api/progress/user/{userId}/category/{categoryId}
     */
    @GetMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getCategoryProgress(
            @PathVariable Integer userId, 
            @PathVariable Integer categoryId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> progress = progressService.getCategoryProgress(userId, categoryId);
            
            response.put("success", true);
            response.put("data", progress);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error loading progress: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * ✅ THÊM MỚI: Reset tiến độ của 1 category (user hiện tại)
     * POST /api/progress/category/{categoryId}/reset
     */
    @PostMapping("/category/{categoryId}/reset")
    public ResponseEntity<Map<String, Object>> resetMyProgress(
            @PathVariable Integer categoryId,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String userEmail = (String) request.getAttribute("userEmail");
            if (userEmail == null) {
                response.put("success", false);
                response.put("message", "Chưa đăng nhập");
                return ResponseEntity.status(401).body(response);
            }
            
            UserEntity user = userRepository.findByEmail(userEmail);
            if (user == null) {
                response.put("success", false);
                response.put("message", "User không tồn tại");
                return ResponseEntity.status(404).body(response);
            }
            
            boolean success = progressService.resetCategoryProgress(user.getId(), categoryId);
            
            response.put("success", success);
            response.put("message", success ? "Đã reset tiến độ thành công" : "Reset thất bại");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error resetting progress: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Reset tiến độ của 1 category (làm lại)
     * POST /api/progress/user/{userId}/category/{categoryId}/reset
     */
    @PostMapping("/user/{userId}/category/{categoryId}/reset")
    public ResponseEntity<Map<String, Object>> resetCategoryProgress(
            @PathVariable Integer userId, 
            @PathVariable Integer categoryId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = progressService.resetCategoryProgress(userId, categoryId);
            
            response.put("success", success);
            response.put("message", success ? "Progress reset successfully" : "Failed to reset progress");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error resetting progress: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}