package com.java.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.java.repository.UserProgressRepository;
import com.java.repository.entity.UserProgressEntity;
import com.java.utils.ConnectionJDBCUtil;

@Repository
public class UserProgressRepositoryIMPL implements UserProgressRepository {

    @Override
    public UserProgressEntity getUserProgress(Integer userId, Integer lessonId) {
        String sql = "SELECT * FROM user_progress WHERE user_id = ? AND lesson_id = ?";
        UserProgressEntity progress = null;
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, lessonId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                progress = new UserProgressEntity();
                progress.setId(rs.getInt("id"));
                progress.setUser_id(rs.getInt("user_id"));
                progress.setLesson_id(rs.getInt("lesson_id"));
                progress.setStatus(rs.getString("status"));
                progress.setAttempts(rs.getInt("attempts"));
                progress.setScore(rs.getBigDecimal("score"));
                progress.setCompleted_at(rs.getTimestamp("completed_at"));
                progress.setCreated_at(rs.getTimestamp("created_at"));
                progress.setUpdated_at(rs.getTimestamp("updated_at"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return progress;
    }

    @Override
    public int createUserProgress(UserProgressEntity progress) {
        String sql = "INSERT INTO user_progress(user_id, lesson_id, status, attempts, score, completed_at) " +
                     "VALUES(?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, progress.getUser_id());
            ps.setInt(2, progress.getLesson_id());
            ps.setString(3, progress.getStatus());
            ps.setInt(4, progress.getAttempts());
            ps.setBigDecimal(5, progress.getScore());
            ps.setTimestamp(6, progress.getCompleted_at());
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }

    @Override
    public int updateUserProgress(UserProgressEntity progress) {
        String sql = "UPDATE user_progress SET status = ?, attempts = ?, score = ?, " +
                     "completed_at = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE user_id = ? AND lesson_id = ?";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, progress.getStatus());
            ps.setInt(2, progress.getAttempts());
            ps.setBigDecimal(3, progress.getScore());
            ps.setTimestamp(4, progress.getCompleted_at());
            ps.setInt(5, progress.getUser_id());
            ps.setInt(6, progress.getLesson_id());
            
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }

    @Override
    public Map<String, Object> getCategoryProgress(Integer userId, Integer categoryId) {
        Map<String, Object> result = new HashMap<>();
        
        String sql = "SELECT " +
                     "    COUNT(DISTINCT l.id) as total_lessons, " +
                     "    COUNT(DISTINCT CASE WHEN up.status = 'completed' AND up.score >= 100 THEN l.id END) as completed_lessons, " +
                     "    COUNT(DISTINCT CASE WHEN up.status IN ('in_progress', 'completed') THEN l.id END) as started_lessons " +
                     "FROM lessons l " +
                     "LEFT JOIN user_progress up ON l.id = up.lesson_id AND up.user_id = ? " +
                     "WHERE l.category_id = ?";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, categoryId);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int total = rs.getInt("total_lessons");
                int completed = rs.getInt("completed_lessons");
                int started = rs.getInt("started_lessons");
                
                result.put("total_lessons", total);
                result.put("completed_lessons", completed);
                result.put("started_lessons", started);
                
                // Xác định trạng thái
                String status;
                if (completed == total && total > 0) {
                    status = "completed";
                } else if (started > 0) {
                    status = "in_progress";
                } else {
                    status = "not_started";
                }
                result.put("status", status);
                
                // Tính % hoàn thành
                double percentage = total > 0 ? (completed * 100.0 / total) : 0;
                result.put("completion_percentage", Math.round(percentage * 10) / 10.0);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }

    @Override
    public Map<Integer, Map<String, Object>> getAllCategoryProgress(Integer userId) {
        Map<Integer, Map<String, Object>> result = new HashMap<>();
        
        String sql = "SELECT " +
                     "    c.id as category_id, " +
                     "    COUNT(DISTINCT l.id) as total_lessons, " +
                     "    COUNT(DISTINCT CASE WHEN up.status = 'completed' AND up.score >= 100 THEN l.id END) as completed_lessons, " +
                     "    COUNT(DISTINCT CASE WHEN up.status IN ('in_progress', 'completed') THEN l.id END) as started_lessons " +
                     "FROM categories c " +
                     "LEFT JOIN lessons l ON c.id = l.category_id " +
                     "LEFT JOIN user_progress up ON l.id = up.lesson_id AND up.user_id = ? " +
                     "GROUP BY c.id";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                int categoryId = rs.getInt("category_id");
                int total = rs.getInt("total_lessons");
                int completed = rs.getInt("completed_lessons");
                int started = rs.getInt("started_lessons");
                
                Map<String, Object> progress = new HashMap<>();
                progress.put("total_lessons", total);
                progress.put("completed_lessons", completed);
                progress.put("started_lessons", started);
                
                // Xác định trạng thái
                String status;
                if (completed == total && total > 0) {
                    status = "completed";
                } else if (started > 0) {
                    status = "in_progress";
                } else {
                    status = "not_started";
                }
                progress.put("status", status);
                
                // Tính %
                double percentage = total > 0 ? (completed * 100.0 / total) : 0;
                progress.put("completion_percentage", Math.round(percentage * 10) / 10.0);
                
                result.put(categoryId, progress);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }

    @Override
    public boolean resetCategoryProgress(Integer userId, Integer categoryId) {
        String sql = "DELETE up FROM user_progress up " +
                     "INNER JOIN lessons l ON up.lesson_id = l.id " +
                     "WHERE up.user_id = ? AND l.category_id = ?";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, categoryId);
            
            int rows = ps.executeUpdate();
            System.out.println("✅ Reset progress: deleted " + rows + " records");
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}