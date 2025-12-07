package com.java.repository;

import java.util.Map;

import com.java.repository.entity.UserProgressEntity;

public interface UserProgressRepository {
    
    /**
     * Lấy progress của user cho 1 lesson
     */
    UserProgressEntity getUserProgress(Integer userId, Integer lessonId);
    
    /**
     * Tạo mới progress
     */
    int createUserProgress(UserProgressEntity progress);
    
    /**
     * Cập nhật progress
     */
    int updateUserProgress(UserProgressEntity progress);
    
    /**
     * Lấy tổng tiến độ của 1 category
     */
    Map<String, Object> getCategoryProgress(Integer userId, Integer categoryId);
    
    /**
     * Lấy tiến độ của tất cả categories
     */
    Map<Integer, Map<String, Object>> getAllCategoryProgress(Integer userId);
    
    /**
     * Xóa tất cả progress của 1 category (reset)
     */
    boolean resetCategoryProgress(Integer userId, Integer categoryId);
}