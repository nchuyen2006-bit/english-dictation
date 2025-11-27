package com.java.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.Model.CheckAnswerRequest;
import com.java.Model.CheckAnswerResponse;
import com.java.repository.UserRepository;
import com.java.repository.entity.UserEntity;
import com.java.service.AnswerService;

@RestController
@RequestMapping("/api/answers")
public class AnswerAPI {

    @Autowired
    private AnswerService answerService;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Check đáp án của user
     * POST /api/answers/check
     */
    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAnswer(
            @RequestBody CheckAnswerRequest request,
            HttpServletRequest httpRequest) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Lấy email từ token (đã được AuthInterceptor set vào request)
            String userEmail = (String) httpRequest.getAttribute("userEmail");
            
            if (userEmail == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin user");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Lấy user_id từ email
            UserEntity user = userRepository.findByEmail(userEmail);
            if (user == null) {
                response.put("success", false);
                response.put("message", "User không tồn tại");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // Validate input
            if (request.getLessonId() == null) {
                response.put("success", false);
                response.put("message", "lessonId không được để trống");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (request.getAnswerText() == null || request.getAnswerText().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "answerText không được để trống");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check answer
            CheckAnswerResponse result = answerService.checkAnswer(user.getId(), request);
            
            response.put("success", true);
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}