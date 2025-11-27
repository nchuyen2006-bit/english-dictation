package com.java.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.Model.CheckAnswerRequest;
import com.java.Model.CheckAnswerResponse;
import com.java.Model.CheckAnswerResponse.WordComparison;
import com.java.repository.AnswerRepository;
import com.java.repository.entity.TranscriptEntity;
import com.java.repository.entity.UserAnswerEntity;
import com.java.repository.entity.UserProgressEntity;
import com.java.utils.TextComparisonUtil;

@Service
public class AnswerServiceIMPL implements com.java.service.AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Override
    public CheckAnswerResponse checkAnswer(Integer userId, CheckAnswerRequest request) {
        // 1. Lấy transcript đúng từ DB
        TranscriptEntity transcript = answerRepository.getTranscriptByLessonId(request.getLessonId());
        
        if (transcript == null) {
            throw new RuntimeException("Không tìm thấy transcript cho lesson này");
        }

        String correctAnswer = transcript.getContent_clean();
        String userAnswer = request.getAnswerText();

        // 2. Tính điểm và so sánh
        BigDecimal score = TextComparisonUtil.calculateScore(correctAnswer, userAnswer);
        boolean isCorrect = score.compareTo(new BigDecimal("100.00")) == 0;
        
        // 3. So sánh chi tiết từng từ
        List<WordComparison> differences = TextComparisonUtil.compareWords(correctAnswer, userAnswer);
        
        // 4. Tạo feedback
        String feedback = TextComparisonUtil.generateFeedback(score);

        // 5. Lưu câu trả lời vào DB
        UserAnswerEntity answerEntity = new UserAnswerEntity();
        answerEntity.setUser_id(userId);
        answerEntity.setLesson_id(request.getLessonId());
        answerEntity.setAnswer_text(userAnswer);
        answerEntity.setIs_correct(isCorrect);
        
        answerRepository.saveUserAnswer(answerEntity);

        // 6. Cập nhật user progress
        updateUserProgress(userId, request.getLessonId(), score, isCorrect);

        // 7. Tạo response
        CheckAnswerResponse response = new CheckAnswerResponse();
        response.setCorrect(isCorrect);
        response.setScore(score);
        response.setCorrectAnswer(transcript.getContent_en()); // Hiển thị bản gốc có dấu câu
        response.setUserAnswer(userAnswer);
        response.setDifferences(differences);
        response.setFeedback(feedback);

        return response;
    }

    private void updateUserProgress(Integer userId, Integer lessonId, BigDecimal score, boolean isCorrect) {
        UserProgressEntity progress = answerRepository.getUserProgress(userId, lessonId);

        if (progress == null) {
            // Tạo mới progress
            progress = new UserProgressEntity();
            progress.setUser_id(userId);
            progress.setLesson_id(lessonId);
            progress.setStatus("in_progress");
            progress.setAttempts(1);
            progress.setScore(score);
            
            if (isCorrect) {
                progress.setStatus("completed");
                progress.setCompleted_at(new Timestamp(System.currentTimeMillis()));
            }
            
            answerRepository.createUserProgress(progress);
        } else {
            // Update progress
            progress.setAttempts(progress.getAttempts() + 1);
            
            // Chỉ update score nếu điểm mới cao hơn
            if (score.compareTo(progress.getScore()) > 0) {
                progress.setScore(score);
            }
            
            if (isCorrect && !"completed".equals(progress.getStatus())) {
                progress.setStatus("completed");
                progress.setCompleted_at(new Timestamp(System.currentTimeMillis()));
            } else if (!isCorrect && "not_started".equals(progress.getStatus())) {
                progress.setStatus("in_progress");
            }
            
            answerRepository.updateUserProgress(progress);
        }
    }
}