package com.java.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.stereotype.Repository;

import com.java.repository.AnswerRepository;
import com.java.repository.entity.TranscriptEntity;
import com.java.repository.entity.UserAnswerEntity;
import com.java.repository.entity.UserProgressEntity;
import com.java.utils.ConnectionJDBCUtil;

@Repository
public class AnswerRepositoryIMPL implements AnswerRepository {

    @Override
    public TranscriptEntity getTranscriptByLessonId(Integer lessonId) {
        String sql = "SELECT t.* FROM transcripts t " +
                     "INNER JOIN audios a ON t.audio_id = a.id " +
                     "INNER JOIN lessons l ON a.lesson_id = l.id " +
                     "WHERE l.id = ?";
        
        TranscriptEntity transcript = null;
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, lessonId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                transcript = new TranscriptEntity();
                transcript.setId(rs.getInt("id"));
                transcript.setAudio_id(rs.getInt("audio_id"));
                transcript.setContent_en(rs.getString("content_en"));
                transcript.setContent_clean(rs.getString("content_clean"));
                transcript.setContent_vi(rs.getString("content_vi"));
                transcript.setPronunciation_ipa(rs.getString("pronunciation_ipa"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transcript;
    }

    @Override
    public int saveUserAnswer(UserAnswerEntity answer) {
        // Kiểm tra đã có câu trả lời chưa
        String checkSql = "SELECT id FROM user_answers WHERE user_id = ? AND lesson_id = ?";
        String insertSql = "INSERT INTO user_answers(user_id, lesson_id, answer_text, is_correct, checked_at) " +
                          "VALUES(?, ?, ?, ?, NOW())";
        String updateSql = "UPDATE user_answers SET answer_text = ?, is_correct = ?, checked_at = NOW() " +
                          "WHERE user_id = ? AND lesson_id = ?";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection()) {
            // Check existing
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, answer.getUser_id());
                checkPs.setInt(2, answer.getLesson_id());
                ResultSet rs = checkPs.executeQuery();
                
                if (rs.next()) {
                    // Update existing
                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setString(1, answer.getAnswer_text());
                        updatePs.setBoolean(2, answer.getIs_correct());
                        updatePs.setInt(3, answer.getUser_id());
                        updatePs.setInt(4, answer.getLesson_id());
                        return updatePs.executeUpdate();
                    }
                } else {
                    // Insert new
                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                        insertPs.setInt(1, answer.getUser_id());
                        insertPs.setInt(2, answer.getLesson_id());
                        insertPs.setString(3, answer.getAnswer_text());
                        insertPs.setBoolean(4, answer.getIs_correct());
                        insertPs.executeUpdate();
                        
                        ResultSet generatedKeys = insertPs.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }

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
        String sql = "INSERT INTO user_progress(user_id, lesson_id, status, attempts, score) " +
                     "VALUES(?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, progress.getUser_id());
            ps.setInt(2, progress.getLesson_id());
            ps.setString(3, progress.getStatus());
            ps.setInt(4, progress.getAttempts());
            ps.setBigDecimal(5, progress.getScore());
            
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }

    @Override
    public int updateUserProgress(UserProgressEntity progress) {
        String sql = "UPDATE user_progress SET status = ?, attempts = ?, score = ?, " +
                     "completed_at = ?, updated_at = NOW() " +
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
}