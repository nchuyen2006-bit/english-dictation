package com.java.repository;

import java.util.List;
import java.util.Map;

import com.java.repository.entity.LessonEntity;

public interface LessonRepository {
	public List<LessonEntity> findAll(Map<String, Object> params);
}
