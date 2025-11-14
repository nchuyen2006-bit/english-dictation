package com.java.repository.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.java.repository.LessonRepository;
import com.java.repository.entity.LessonEntity;
import com.java.utils.ConnectionJDBCUtil;
import com.java.utils.StringUtil;
@Repository
public class LessonRepositoryIMPL implements LessonRepository {

	public static void innerjoin(Map<String,Object> params,StringBuilder sql) {
		String status =(String)params.get("status");
		if(StringUtil.checkString(status)) {
			sql.append(" inner join  user_progress on  user_progress.lesson_id = lessons.id ");
		}
		String slug =(String)params.get("slug");
		if(StringUtil.checkString(slug)) {
			sql.append(" inner join  programs on  c.program_id = programs.id ");
		}
		String type=(String)params.get("type");
		if(StringUtil.checkString(type) && !StringUtil.checkString(slug)) {
			sql.append(" inner join   programs on  c.program_id = programs.id ");
		}
		String section_name=(String)params.get("name");
		if(!StringUtil.checkString(type) && !StringUtil.checkString(slug)) {
			sql.append(" inner join   programs on  c.program_id = programs.id ");
		}
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
	public List<LessonEntity> findAll(Map<String, Object> params)  {
		StringBuilder sql=new StringBuilder(" select DISTINCT c.code, c.name, programs.type, user_progress.status from categories c inner join lessons on lessons.category_id=c.id  ");
		innerjoin(params,sql);
		StringBuilder where=new StringBuilder(" where 1=1 ");
		querySpecial(params,where);
		sql.append(where);
		List<LessonEntity> result=new ArrayList<>();
		try(Connection conn = ConnectionJDBCUtil.getConnection();
				Statement stmt=conn.createStatement();
				ResultSet rs=stmt.executeQuery(sql.toString());){
			while(rs.next()) {
				LessonEntity lesson=new LessonEntity();
				lesson.setCode(rs.getString("c.code"));
				lesson.setStatus(rs.getString("user_progress.status"));
				lesson.setName(rs.getString("c.name"));
				lesson.setType(rs.getString("programs.type"));
				result.add(lesson);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
