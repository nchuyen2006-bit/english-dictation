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
	public boolean deletLesson(int id )  {
		String deletsql= " delete from lessons where id = ? ";
		try(Connection conn=ConnectionJDBCUtil.getConnection();
			PreparedStatement ps=conn.prepareStatement(deletsql)){
			ps.setInt(1, id);
			int rowsAffected = ps.executeUpdate();
			return rowsAffected > 0;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
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
