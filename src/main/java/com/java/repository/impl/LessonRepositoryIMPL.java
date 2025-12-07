package com.java.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.java.repository.LessonRepository;
import com.java.repository.entity.LessonEntity;
import com.java.utils.ConnectionJDBCUtil;
@Repository
public class LessonRepositoryIMPL implements LessonRepository {
	public List<LessonEntity> addLesson(List<LessonEntity> lesson) {
	    Connection conn = null;
	    List<LessonEntity> insert = new ArrayList<>();  // list trả về cuối cùng

	    try {
	        conn = ConnectionJDBCUtil.getConnection();
	        conn.setAutoCommit(false);

	        String lessonsql = "INSERT INTO lessons(category_id, section_id, duration, order_num, is_premium) VALUES (?,?,?,?,?)";
	        PreparedStatement ps = conn.prepareStatement(lessonsql, Statement.RETURN_GENERATED_KEYS);

	        for (LessonEntity i : lesson) {
	            ps.setInt(1, i.getCategory_id());
	            ps.setInt(2, i.getSection_id());        
	            ps.setInt(3, i.getDuration());
	            ps.setInt(4, i.getOrder_num());
	            ps.setBoolean(5, i.isIs_premum());

	            ps.addBatch(); 
	        }

	        int[] results = ps.executeBatch(); // thực thi tất cả

	        ResultSet rs = ps.getGeneratedKeys();
	        int index = 0;
	        while (rs.next()) {
	            if (index < lesson.size()) {
	                lesson.get(index).setId(rs.getInt(1)); // gán ID tự sinh cho entity gốc
	                insert.add(lesson.get(index));        // thêm vào danh sách đã insert thành công
	                index++;
	            }
	        }

	        conn.commit();
	        return insert; // trả về danh sách các lesson đã insert + có ID

	    } catch (Exception e) {
	        if (conn != null) 
	        	try { 
	        		conn.rollback(); 
	        		} catch(Exception ex) {}
	        e.printStackTrace();
	        return insert; 
	    }
	}
	@Override
	public boolean deletLesson(int id) {
	    Connection conn = null;
	    try {
	        conn = ConnectionJDBCUtil.getConnection();
	        conn.setAutoCommit(false);  // Bắt đầu transaction
	        
	        // 1️⃣ Lấy audio_id từ bảng audios
	        String getAudioIdSql = "SELECT id FROM audios WHERE lesson_id = ?";
	        Integer audioId = null;
	        
	        try (PreparedStatement ps = conn.prepareStatement(getAudioIdSql)) {
	            ps.setInt(1, id);
	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) {
	                audioId = rs.getInt("id");
	                System.out.println("Found audio_id: " + audioId + " for lesson_id: " + id);
	            }
	        }
	        
	        // 2️⃣ Xóa transcript (nếu có audio_id)
	        if (audioId != null) {
	            String deleteTranscriptSql = "DELETE FROM transcripts WHERE audio_id = ?";
	            try (PreparedStatement ps = conn.prepareStatement(deleteTranscriptSql)) {
	                ps.setInt(1, audioId);
	                int transcriptRows = ps.executeUpdate();
	                System.out.println("Deleted " + transcriptRows + " transcript(s)");
	            }
	        }
	        
	        // 3️⃣ Xóa audio
	        String deleteAudioSql = "DELETE FROM audios WHERE lesson_id = ?";
	        try (PreparedStatement ps = conn.prepareStatement(deleteAudioSql)) {
	            ps.setInt(1, id);
	            int audioRows = ps.executeUpdate();
	            System.out.println("Deleted " + audioRows + " audio(s)");
	        }
	        
	        // 4️⃣ Xóa lesson
	        String deleteLessonSql = "DELETE FROM lessons WHERE id = ?";
	        boolean result = false;
	        try (PreparedStatement ps = conn.prepareStatement(deleteLessonSql)) {
	            ps.setInt(1, id);
	            int lessonRows = ps.executeUpdate();
	            result = lessonRows > 0;
	            System.out.println("Deleted " + lessonRows + " lesson(s)");
	        }
	        
	        conn.commit();  
	        System.out.println("Successfully deleted lesson " + id + " and related data");
	        return result;
	        
	    } catch (Exception e) {
	        if (conn != null) {
	            try {
	                conn.rollback();  
	                System.err.println("Rolled back transaction");
	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }
	        }
	        e.printStackTrace();
	        return false;
	    } finally {
	        if (conn != null) {
	            try {
	                conn.setAutoCommit(true);  
	                conn.close();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}
	@Override
	public List<LessonEntity> getLessonsByCategoryId(int categoryId) {
	    String sql = "SELECT l.id, l.category_id, l.section_id, l.order_num, l.duration, l.is_premium, " +
	                 "a.url as audioUrl, a.duration as audioDuration, " +
	                 "t.content_en as script, t.content_clean , t.content_vi as translation " +
	                 "FROM lessons l " +
	                 "LEFT JOIN audios a ON a.lesson_id = l.id " +
	                 "LEFT JOIN transcripts t ON t.audio_id = a.id " +
	                 "WHERE l.category_id = ? " +
	                 "ORDER BY l.order_num ASC";
	    
	    List<LessonEntity> result = new ArrayList<>();
	    
	    try (Connection conn = ConnectionJDBCUtil.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, categoryId);
	        ResultSet rs = ps.executeQuery();
	        
	        while (rs.next()) {
	            LessonEntity lesson = new LessonEntity();
	            lesson.setId(rs.getInt("id"));
	            lesson.setCategory_id(rs.getInt("category_id"));
	            
	            // Handle NULL values
	            Integer sectionId = (Integer) rs.getObject("section_id");
	            lesson.setSection_id(sectionId);
	            
	            lesson.setOrder_num(rs.getInt("order_num"));
	            lesson.setDuration(rs.getInt("duration"));
	            lesson.setIs_premum(rs.getBoolean("is_premium"));
	            
	            // Audio info
	            lesson.setAudioUrl(rs.getString("audioUrl"));
	            Integer audioDuration = (Integer) rs.getObject("audioDuration");
	            lesson.setAudioDuration(audioDuration != null ? audioDuration : 0);
	            
	            // Transcript
	            String scriptContent = rs.getString("script");
	            lesson.setScript(scriptContent); 
	            lesson.setTranscriptText(scriptContent); 
	            lesson.setContent_clean(rs.getString("content_clean"));
	            lesson.setTranslation(rs.getString("translation"));
	            
	            result.add(lesson);
	        }
	        
	        System.out.println("Found " + result.size() + " lessons for category " + categoryId);
	        
	    } catch (SQLException e) {
	        System.err.println("SQL Error in getLessonsByCategoryId:");
	        System.err.println("SQL: " + sql);
	        e.printStackTrace();
	    }
	    
	    return result;
	}
}
