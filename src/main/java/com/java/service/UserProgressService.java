package com.java.service;

import java.math.BigDecimal;
import java.util.Map;

public interface UserProgressService {
    
    /**
     * Lấy tiến độ của user trong 1 category
     */
    Map<String, Object> getCategoryProgress(Integer userId, Integer categoryId);
    
    /**
     * Cập nhật tiến độ khi user hoàn thành lesson
     */
    boolean updateLessonProgress(Integer userId, Integer lessonId, BigDecimal score);
    
    /**
     * Reset tiến độ của 1 category (làm lại)
     */
    boolean resetCategoryProgress(Integer userId, Integer categoryId);
    
    /**
     * Lấy danh sách progress của tất cả categories cho user
     */
    Map<Integer, Map<String, Object>> getAllCategoryProgress(Integer userId);
}