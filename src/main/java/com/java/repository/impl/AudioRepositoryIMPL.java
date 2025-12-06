package com.java.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.stereotype.Repository;

import com.java.repository.AudioRepository;
import com.java.repository.entity.AudioEntity;
import com.java.utils.ConnectionJDBCUtil;

@Repository
public class AudioRepositoryIMPL implements AudioRepository {

    @Override
    public int saveAudio(AudioEntity audio) {
        String checkSql = "SELECT id FROM audios WHERE lesson_id = ?";
        String insertSql = "INSERT INTO audios(lesson_id, url, duration, size_kb, bitrate, format) VALUES(?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE audios SET url = ?, duration = ?, size_kb = ?, bitrate = ?, format = ? WHERE lesson_id = ?";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection()) {
            // Check if audio exists
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, audio.getLesson_id());
                ResultSet rs = checkPs.executeQuery();
                
                if (rs.next()) {
                    // Update existing
                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setString(1, audio.getUrl());
                        updatePs.setInt(2, audio.getDuration());
                        updatePs.setInt(3, audio.getSize_kb());
                        updatePs.setInt(4, audio.getBitrate());
                        updatePs.setString(5, audio.getFormat());
                        updatePs.setInt(6, audio.getLesson_id());
                        updatePs.executeUpdate();
                        return rs.getInt("id");
                    }
                } else {
                    // Insert new
                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                        insertPs.setInt(1, audio.getLesson_id());
                        insertPs.setString(2, audio.getUrl());
                        insertPs.setInt(3, audio.getDuration());
                        insertPs.setInt(4, audio.getSize_kb());
                        insertPs.setInt(5, audio.getBitrate());
                        insertPs.setString(6, audio.getFormat());
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
    public AudioEntity getAudioByLessonId(Integer lessonId) {
        String sql = "SELECT * FROM audios WHERE lesson_id = ?";
        AudioEntity audio = null;
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, lessonId);
            ResultSet rs = ps.executeQuery();
            
            
            if (rs.next()) {
                audio = new AudioEntity();
                audio.setId(rs.getInt("id"));
                audio.setLesson_id(rs.getInt("lesson_id"));
                audio.setUrl(rs.getString("url"));
                audio.setDuration(rs.getInt("duration"));
                audio.setSize_kb(rs.getInt("size_kb"));
                audio.setBitrate(rs.getInt("bitrate"));
                audio.setFormat(rs.getString("format"));
                audio.setCreated_at(rs.getTimestamp("created_at"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return audio;
    }

    @Override
    public int updateAudio(AudioEntity audio) {
        String sql = "UPDATE audios SET url = ?, duration = ?, size_kb = ?, bitrate = ?, format = ? WHERE id = ?";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, audio.getUrl());
            ps.setInt(2, audio.getDuration());
            ps.setInt(3, audio.getSize_kb());
            ps.setInt(4, audio.getBitrate());
            ps.setString(5, audio.getFormat());
            ps.setInt(6, audio.getId());
            
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }

    @Override
    public boolean deleteAudio(Integer audioId) {
        String sql = "DELETE FROM audios WHERE id = ?";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, audioId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}