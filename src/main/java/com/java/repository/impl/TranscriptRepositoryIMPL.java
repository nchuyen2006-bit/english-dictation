package com.java.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.stereotype.Repository;

import com.java.repository.TranscriptRepository;
import com.java.repository.entity.TranscriptEntity;
import com.java.utils.ConnectionJDBCUtil;

@Repository
public class TranscriptRepositoryIMPL implements TranscriptRepository {

    @Override
    public int addTranscript(TranscriptEntity transcript) {
        String sql = "INSERT INTO transcripts(audio_id, content_en, content_clean, content_vi, pronunciation_ipa) " +
                     "VALUES(?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, transcript.getAudio_id());
            ps.setString(2, transcript.getContent_en());
            ps.setString(3, transcript.getContent_clean());
            ps.setString(4, transcript.getContent_vi());
            ps.setString(5, transcript.getPronunciation_ipa());
            
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                System.out.println("✅ Created transcript ID: " + generatedId);
                return generatedId;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error adding transcript:");
            e.printStackTrace();
        }
        
        return -1;
    }

    @Override
    public int updateTranscript(TranscriptEntity transcript) {
        String sql = "UPDATE transcripts SET content_en = ?, content_clean = ?, content_vi = ?, pronunciation_ipa = ? " +
                     "WHERE audio_id = ?";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, transcript.getContent_en());
            ps.setString(2, transcript.getContent_clean());
            ps.setString(3, transcript.getContent_vi());
            ps.setString(4, transcript.getPronunciation_ipa());
            ps.setInt(5, transcript.getAudio_id());
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("✅ Updated transcript for audio_id: " + transcript.getAudio_id());
            return rowsAffected;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating transcript:");
            e.printStackTrace();
        }
        
        return -1;
    }

    @Override
    public TranscriptEntity getTranscriptByAudioId(Integer audioId) {
        String sql = "SELECT * FROM transcripts WHERE audio_id = ?";
        TranscriptEntity transcript = null;
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, audioId);
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
    public boolean existsByAudioId(Integer audioId) {
        String sql = "SELECT COUNT(*) FROM transcripts WHERE audio_id = ?";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, audioId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
}