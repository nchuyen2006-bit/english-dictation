package com.java.repository;

import java.util.List;

import com.java.repository.entity.LessonEntity;

public interface LessonRepository {
	public List<LessonEntity> addLesson(List<LessonEntity> lesson);
	public boolean deletLesson(int id) ;
}
