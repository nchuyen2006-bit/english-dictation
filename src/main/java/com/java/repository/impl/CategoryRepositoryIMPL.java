package com.java.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale.Category;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.java.repository.CategoryRepository;
import com.java.repository.entity.CategoryEntity;
import com.java.utils.ConnectionJDBCUtil;
import com.java.utils.StringUtil;
@Repository
public class CategoryRepositoryIMPL implements CategoryRepository {

	public static void innerjoin(Map<String,Object> params,StringBuilder sql) {
		String status =(String)params.get("status");
		if(StringUtil.checkString(status)) {
			sql.append(" inner join  user_progress on  user_progress.lesson_id = lessons.id ");
		}
		String slug =(String)params.get("slug");
		if(StringUtil.checkString(slug)) {
			sql.append(" inner join  programs on  categories.program_id = programs.id ");
		}
		String type=(String)params.get("type");
		if(StringUtil.checkString(type) && !StringUtil.checkString(slug)) {
			sql.append(" inner join   programs on  categories.program_id = programs.id ");
		}
		if(!StringUtil.checkString(type) && !StringUtil.checkString(slug)) {
			sql.append(" inner join   programs on categories.program_id = programs.id ");
		}
		String section_name=(String)params.get("name");
		if(StringUtil.checkString(section_name)) {
			sql.append(" inner join  sections on  sections.program_id = programs.id ");
		}
		
		
	}
	public static void querySpecial(Map<String,Object> params,StringBuilder where) {
		String status =(String)params.get("status");
		if(StringUtil.checkString(status)) {
			where.append(" and user_progress.status like "+ "'"+status+"' " );
		}
		String type =(String)params.get("type");
		String slug =(String)params.get("slug");
		String name =(String)params.get("name");
		if(StringUtil.checkString(type)) {
			where.append(" and programs.type like "+ "'"+type+"' " );
		}
		if(StringUtil.checkString(slug)) {
			where.append(" and programs.slug like "+ "'"+slug+"' " );
		}
		if(StringUtil.checkString(name)) {
			where.append(" and sections.name like "+ "'"+name+"' " );
		}
	}
	
	@Override
	public List<CategoryEntity> findAll(Map<String, Object> params)  {
		StringBuilder sql=new StringBuilder(" select categories.name ,categories.code,categories.total_lessons,programs.type,user_progress.status from categories inner join lessons on lessons.category_id=lessons.id ");
		innerjoin(params,sql);
		StringBuilder where=new StringBuilder(" where 1=1 ");
		querySpecial(params,where);
		sql.append(where);
		List<CategoryEntity> result=new ArrayList<>();
		try(Connection conn = ConnectionJDBCUtil.getConnection();
				Statement stmt=conn.createStatement();
				ResultSet rs=stmt.executeQuery(sql.toString());){
			while(rs.next()) {
				CategoryEntity lesson=new CategoryEntity();
				lesson.setCode(rs.getString("categories.code"));
				lesson.setStatus(rs.getString("user_progress.status"));
				lesson.setName(rs.getString("categories.name"));
				lesson.setType(rs.getString("programs.type"));
				result.add(lesson);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	@Override
	
	public int addCategory(CategoryEntity c) {
		String sql=" insert into categories(program_id,name,code, slug, total_lessons) "+" values(?,?,?,?,?) ";
		try(Connection conn = ConnectionJDBCUtil.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
			//ps.setInt(1,c.getId());
			ps.setInt(1,c.getProgram_id());
			ps.setString(2,c.getName());
			ps.setString(3,c.getCode());
			ps.setString(4,c.getSlug());
			ps.setInt(5,c.getTotal_lessons());
			ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()) return rs.getInt(1);
            
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	
}
