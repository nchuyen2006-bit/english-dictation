package com.java.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.java.Model.AudioUploadDTO;
import com.java.service.AudioService;

@RestController
@RequestMapping("/api/audios")
public class AudioAPI {

    @Autowired
    private AudioService audioService;

    /**
     * Upload audio file cho lesson
     * POST /api/audios/upload
     * Form-data: file, lessonId, duration
     * Requires: Admin or Teacher role
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadAudio(
            @RequestParam("file") MultipartFile file,
            @RequestParam("lessonId") Integer lessonId,
            @RequestParam(value = "duration", required = false) Integer duration,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check role (chỉ admin/teacher mới được upload)
            String userRole = (String) request.getAttribute("userRole");
            if (!"admin".equals(userRole) && !"teacher".equals(userRole)) {
                response.put("success", false);
                response.put("message", "Chỉ admin hoặc teacher mới có quyền upload audio");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // Validate input
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "File không được để trống");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (lessonId == null) {
                response.put("success", false);
                response.put("message", "lessonId không được để trống");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Upload file
            AudioUploadDTO result = audioService.uploadAudio(lessonId, file, duration);
            
            response.put("success", true);
            response.put("message", "Upload audio thành công");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get audio URL by lesson ID
     * GET /api/audios/lesson/{lessonId}
     */
    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<Map<String, Object>> getAudioUrl(@PathVariable Integer lessonId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String audioUrl = audioService.getAudioUrl(lessonId);
            
            if (audioUrl == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy audio cho lesson này");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", true);
            response.put("audioUrl", audioUrl);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}