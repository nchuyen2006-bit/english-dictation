package com.java.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.java.repository.CategoryRepository;
import com.java.repository.entity.CategoryEntity;
import com.java.utils.ConnectionJDBCUtil;
import com.java.utils.StringUtil;

@Repository
public class CategoryRepositoryIMPL implements CategoryRepository {

	@Override
	public List<CategoryEntity> findAll(Map<String, Object> params) {
	    String status = (String) params.get("status");
	    String type = (String) params.get("type");
	    String slug = (String) params.get("slug");
	    String sectionName = (String) params.get("name");
	    String keyword = (String) params.get("keyword"); 
	    
	    StringBuilder sql = new StringBuilder();
	    
	    
	    sql.append("SELECT categories.id, categories.program_id, categories.code, ");
	    sql.append("categories.name, categories.slug, categories.total_lessons, programs.type");
	    
	    boolean hasStatus = StringUtil.checkString(status);
	    if (hasStatus) {
	        sql.append(", user_progress.status");
	    }
	    
	    sql.append(" FROM categories");
	    sql.append(" LEFT JOIN lessons ON lessons.category_id = categories.id");
	    sql.append(" INNER JOIN programs ON categories.program_id = programs.id");
	    
	    if (hasStatus) {
	        sql.append(" LEFT JOIN user_progress ON user_progress.lesson_id = lessons.id");
	    }
	    
	    if (StringUtil.checkString(sectionName)) {
	        sql.append(" INNER JOIN sections ON sections.program_id = programs.id");
	    }
	    
	    sql.append(" WHERE 1=1");
	    
	    if (hasStatus) {
	        sql.append(" AND user_progress.status = '").append(status).append("'");
	    }
	    
	    if (StringUtil.checkString(type)) {
	        sql.append(" AND programs.type = '").append(type).append("'");
	    }
	    
	    if (StringUtil.checkString(slug)) {
	        sql.append(" AND programs.slug = '").append(slug).append("'");
	    }
	    
	    if (StringUtil.checkString(sectionName)) {
	        sql.append(" AND sections.name = '").append(sectionName).append("'");
	    }
	    
	    if (StringUtil.checkString(keyword)) {
	        sql.append(" AND (categories.name LIKE '%").append(keyword).append("%'");
	        sql.append(" OR categories.code LIKE '%").append(keyword).append("%'");
	        sql.append(" OR categories.description LIKE '%").append(keyword).append("%')");
	    }
	    
	    
	    sql.append(" GROUP BY categories.id, categories.program_id, categories.code, ");
	    sql.append("categories.name, categories.slug, categories.total_lessons, programs.type");
	    
	    if (hasStatus) {
	        sql.append(", user_progress.status");
	    }
	    
	    // Sắp xếp theo ID giảm dần (mới nhất lên đầu)
	    sql.append(" ORDER BY categories.id DESC");
	    
	    List<CategoryEntity> result = new ArrayList<>();
	    
	    try (Connection conn = ConnectionJDBCUtil.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql.toString())) {
	        
	        while (rs.next()) {
	            CategoryEntity category = new CategoryEntity();
	            category.setId(rs.getInt("id"));
	            category.setProgram_id(rs.getInt("program_id"));
	            category.setCode(rs.getString("code"));
	            category.setName(rs.getString("name"));
	            category.setSlug(rs.getString("slug"));
	            category.setTotal_lessons(rs.getInt("total_lessons"));
	            category.setType(rs.getString("type"));

	            if (hasStatus) {
	                category.setStatus(rs.getString("status"));
	            }
	            
	            result.add(category);
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("SQL Error: " + sql.toString());
	        e.printStackTrace();
	    }
	    
	    return result;
	}
    
    @Override
    public int addCategory(CategoryEntity c) {
        String sql = "INSERT INTO categories(program_id, name, code, slug, total_lessons) VALUES(?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, c.getProgram_id());
            ps.setString(2, c.getName());
            ps.setString(3, c.getCode());
            ps.setString(4, c.getSlug());
            ps.setInt(5, c.getTotal_lessons());
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("✅ Rows inserted: " + rowsAffected);
            
            ResultSet rs = ps.getGeneratedKeys();
            
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                System.out.println("✅ Category created with ID: " + generatedId);
                return generatedId;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error creating category:");
            e.printStackTrace();
        }
        return -1;
        
   }
    
    @Override
    public boolean deleteCategory(int id) {
        String deleteCategorySQL = "DELETE FROM categories WHERE id = ?";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteCategorySQL)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}