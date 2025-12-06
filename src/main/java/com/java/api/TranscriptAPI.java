package com.java.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.Model.TranscriptDTO;
import com.java.service.TranscriptService;

@RestController
@RequestMapping("/api/transcripts")
@CrossOrigin(origins = "*")
public class TranscriptAPI {

    @Autowired
    private TranscriptService transcriptService;

    /**
     * Add or update transcript
     * POST /api/transcripts
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addTranscript(
            @RequestBody TranscriptDTO transcript,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("========================================");
            System.out.println("📝 ADD/UPDATE TRANSCRIPT REQUEST");
            System.out.println("Audio ID: " + transcript.getAudio_id());
            System.out.println("Content EN length: " + (transcript.getContent_en() != null ? transcript.getContent_en().length() : 0));
            System.out.println("========================================");
            
            // Check role (optional - có thể bỏ nếu đã exclude trong WebConfig)
            String userRole = (String) request.getAttribute("userRole");
            if (userRole != null && !"admin".equals(userRole) && !"teacher".equals(userRole)) {
                response.put("success", false);
                response.put("message", "Chỉ admin hoặc teacher mới có quyền thêm transcript");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // Validate
            if (transcript.getAudio_id() == null) {
                response.put("success", false);
                response.put("message", "audio_id không được để trống");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (transcript.getContent_en() == null || transcript.getContent_en().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "content_en không được để trống");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (transcript.getContent_clean() == null || transcript.getContent_clean().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "content_clean không được để trống");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Add or update
            TranscriptDTO result = transcriptService.addOrUpdateTranscript(transcript);
            
            if (result != null) {
                System.out.println("✅ Transcript saved successfully! ID: " + result.getId());
                
                response.put("success", true);
                response.put("message", "Lưu transcript thành công");
                response.put("data", result);
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Lỗi khi lưu transcript");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error adding transcript:");
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get transcript by audio ID
     * GET /api/transcripts/audio/{audioId}
     */
    @GetMapping("/audio/{audioId}")
    public ResponseEntity<Map<String, Object>> getTranscriptByAudioId(@PathVariable Integer audioId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            TranscriptDTO transcript = transcriptService.getTranscriptByAudioId(audioId);
            
            if (transcript == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy transcript cho audio này");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", true);
            response.put("data", transcript);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}