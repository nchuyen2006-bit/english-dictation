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
	            ps.setInt(2, i.getSection_id());        // ← DÒNG NÀY BỊ THIẾU DẤU NGOẶC ĐÓNG TRONG ẢNH CỦA BẠN!!!
	            ps.setInt(3, i.getDuration());
	            ps.setInt(4, i.getOrder_num());
	            ps.setBoolean(5, i.isIs_premum());

	            ps.addBatch(); // quan trọng: dùng batch để nhanh hơn
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
	        return insert; // vẫn trả về những cái đã insert được (hoặc rỗng nếu lỗi ngay từ đầu)
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
}
