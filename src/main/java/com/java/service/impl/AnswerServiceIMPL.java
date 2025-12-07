package com.java.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.Model.CheckAnswerRequest;
import com.java.Model.CheckAnswerResponse;
import com.java.repository.AnswerRepository;
import com.java.repository.entity.TranscriptEntity;
import com.java.repository.entity.UserAnswerEntity;
import com.java.service.AnswerService;
import com.java.service.UserProgressService;

@Service
public class AnswerServiceIMPL implements AnswerService {

    @Autowired
    private AnswerRepository answerRepository;
    
    @Autowired
    private UserProgressService progressService;

    @Override
    public CheckAnswerResponse checkAnswer(Integer userId, CheckAnswerRequest request) {
        CheckAnswerResponse response = new CheckAnswerResponse();
        
        try {
            System.out.println("===========================================");
            System.out.println("📝 CHECK ANSWER REQUEST");
            System.out.println("User ID: " + userId);
            System.out.println("Lesson ID: " + request.getLessonId());
            System.out.println("Answer Text: " + request.getAnswerText());
            System.out.println("===========================================");
            
            // Lấy transcript từ database
            TranscriptEntity transcript = answerRepository.getTranscriptByLessonId(request.getLessonId());
            
            if (transcript == null) {
                System.err.println("❌ Không tìm thấy transcript cho lesson " + request.getLessonId());
                throw new RuntimeException("Không tìm thấy transcript cho lesson này");
            }
            
            String correctAnswer = transcript.getContent_clean();
            String userAnswer = request.getAnswerText();
            
            System.out.println("📋 Correct Answer: " + correctAnswer);
            System.out.println("✍️ User Answer: " + userAnswer);
            
            // Tính điểm
            BigDecimal score = calculateDetailedScore(userAnswer, correctAnswer);
            System.out.println("📊 Score: " + score);
            
            // Set response data
            response.setScore(score);
            response.setCorrectAnswer(transcript.getContent_en());
            response.setUserAnswer(userAnswer);
            response.setCorrect(score.compareTo(new BigDecimal("100")) == 0);
            
            // Generate feedback
            String feedback = generateFeedback(score);
            response.setFeedback(feedback);
            
            // So sánh từng từ
            List<CheckAnswerResponse.WordComparison> differences = compareWords(userAnswer, correctAnswer);
            response.setDifferences(differences);
            
            // ✅ Lưu user answer vào database
            try {
                UserAnswerEntity userAnswerEntity = new UserAnswerEntity();
                userAnswerEntity.setUser_id(userId);
                userAnswerEntity.setLesson_id(request.getLessonId());
                userAnswerEntity.setAnswer_text(userAnswer);
                userAnswerEntity.setIs_correct(response.isCorrect());
                answerRepository.saveUserAnswer(userAnswerEntity);
                System.out.println("✅ Saved user answer to database");
            } catch (Exception e) {
                System.err.println("⚠️ Error saving user answer: " + e.getMessage());
            }
            
            // ✅ CẬP NHẬT PROGRESS - QUAN TRỌNG!
            try {
                System.out.println("🔄 Updating progress...");
                System.out.println("   User ID: " + userId);
                System.out.println("   Lesson ID: " + request.getLessonId());
                System.out.println("   Score: " + score);
                
                boolean progressUpdated = progressService.updateLessonProgress(
                    userId, 
                    request.getLessonId(), 
                    score
                );
                
                if (progressUpdated) {
                    System.out.println("✅ Progress updated successfully!");
                } else {
                    System.err.println("⚠️ Progress update returned false");
                }
            } catch (Exception e) {
                System.err.println("❌ ERROR updating progress:");
                e.printStackTrace();
            }
            
            System.out.println("===========================================");
            
            return response;
            
        } catch (Exception e) {
            System.err.println("❌ ERROR in checkAnswer:");
            e.printStackTrace();
            throw new RuntimeException("Error checking answer: " + e.getMessage());
        }
    }
    
    private BigDecimal calculateDetailedScore(String userAnswer, String correctAnswer) {
        if (userAnswer == null || correctAnswer == null) {
            return BigDecimal.ZERO;
        }
        
        String user = normalizeText(userAnswer);
        String correct = normalizeText(correctAnswer);
        
        if (user.equals(correct)) {
            return new BigDecimal("100.00");
        }
        
        String[] userWords = user.split("\\s+");
        String[] correctWords = correct.split("\\s+");
        
        int correctCount = 0;
        int maxLength = Math.max(userWords.length, correctWords.length);
        
        for (int i = 0; i < Math.min(userWords.length, correctWords.length); i++) {
            if (userWords[i].equals(correctWords[i])) {
                correctCount++;
            }
        }
        
        if (maxLength == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal score = new BigDecimal(correctCount)
                .multiply(new BigDecimal("100"))
                .divide(new BigDecimal(maxLength), 2, RoundingMode.HALF_UP);
        
        return score;
    }
    
    private List<CheckAnswerResponse.WordComparison> compareWords(String userAnswer, String correctAnswer) {
        List<CheckAnswerResponse.WordComparison> result = new ArrayList<>();
        
        String user = normalizeText(userAnswer);
        String correct = normalizeText(correctAnswer);
        
        String[] userWords = user.split("\\s+");
        String[] correctWords = correct.split("\\s+");
        
        int maxLength = Math.max(userWords.length, correctWords.length);
        
        for (int i = 0; i < maxLength; i++) {
            if (i < userWords.length && i < correctWords.length) {
                String status = userWords[i].equals(correctWords[i]) ? "correct" : "wrong";
                result.add(new CheckAnswerResponse.WordComparison(userWords[i], status));
            } else if (i < correctWords.length) {
                result.add(new CheckAnswerResponse.WordComparison(correctWords[i], "missing"));
            } else {
                result.add(new CheckAnswerResponse.WordComparison(userWords[i], "extra"));
            }
        }
        
        return result;
    }
    
    private String normalizeText(String text) {
        if (text == null) return "";
        
        return text.toLowerCase()
                   .trim()
                   .replaceAll("[^a-z0-9\\s]", "")
                   .replaceAll("\\s+", " ");
    }
    
    private String generateFeedback(BigDecimal score) {
        int scoreInt = score.intValue();
        
        if (scoreInt == 100) {
            return "Perfect! Hoàn hảo 100%! 🎉";
        } else if (scoreInt >= 90) {
            return "Tuyệt vời! Chỉ còn một chút nữa thôi! 💪";
        } else if (scoreInt >= 80) {
            return "Tốt lắm! Bạn đã làm rất tốt. 👍";
        } else if (scoreInt >= 70) {
            return "Khá tốt! Hãy nghe kỹ hơn một chút. 📝";
        } else if (scoreInt >= 60) {
            return "Cố gắng thêm! Hãy nghe lại và thử lần nữa. 🎧";
        } else {
            return "Hãy nghe thật kỹ và thử lại nhé! 💡";
        }
    }
}