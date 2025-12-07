package com.java.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.repository.UserProgressRepository;
import com.java.repository.entity.UserProgressEntity;
import com.java.service.UserProgressService;

@Service
public class UserProgressServiceIMPL implements UserProgressService {

    @Autowired
    private UserProgressRepository progressRepository;

    @Override
    public Map<String, Object> getCategoryProgress(Integer userId, Integer categoryId) {
        return progressRepository.getCategoryProgress(userId, categoryId);
    }

    @Override
    public boolean updateLessonProgress(Integer userId, Integer lessonId, BigDecimal score) {
        try {
            // Kiểm tra xem đã có progress chưa
            UserProgressEntity existingProgress = progressRepository.getUserProgress(userId, lessonId);
            
            if (existingProgress == null) {
                // Tạo mới progress
                UserProgressEntity newProgress = new UserProgressEntity();
                newProgress.setUser_id(userId);
                newProgress.setLesson_id(lessonId);
                newProgress.setAttempts(1);
                newProgress.setScore(score);
                
                // Xác định status dựa trên điểm
                if (score.compareTo(new BigDecimal("100")) >= 0) {
                    newProgress.setStatus("completed");
                    newProgress.setCompleted_at(new Timestamp(System.currentTimeMillis()));
                } else {
                    newProgress.setStatus("in_progress");
                    newProgress.setCompleted_at(null);
                }
                
                int result = progressRepository.createUserProgress(newProgress);
                return result > 0;
                
            } else {
                // Cập nhật progress hiện có
                existingProgress.setAttempts(existingProgress.getAttempts() + 1);
                existingProgress.setScore(score);
                
                // Cập nhật status
                if (score.compareTo(new BigDecimal("100")) >= 0) {
                    existingProgress.setStatus("completed");
                    // Chỉ set completed_at lần đầu tiên hoàn thành
                    if (existingProgress.getCompleted_at() == null) {
                        existingProgress.setCompleted_at(new Timestamp(System.currentTimeMillis()));
                    }
                } else {
                    existingProgress.setStatus("in_progress");
                }
                
                int result = progressRepository.updateUserProgress(existingProgress);
                return result > 0;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error updating lesson progress:");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean resetCategoryProgress(Integer userId, Integer categoryId) {
        return progressRepository.resetCategoryProgress(userId, categoryId);
    }

    @Override
    public Map<Integer, Map<String, Object>> getAllCategoryProgress(Integer userId) {
        return progressRepository.getAllCategoryProgress(userId);
    }
}