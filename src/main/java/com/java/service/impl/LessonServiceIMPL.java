package com.java.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.repository.LessonRepository;
import com.java.repository.entity.LessonEntity;
import com.java.service.LessonService;

@Service
public class LessonServiceIMPL implements LessonService{
	@Autowired
	private  LessonRepository lessonRepository;
	
	public List<LessonEntity> addLesson(List<LessonEntity> lesson){
		return lessonRepository.addLesson(lesson);
	}
	public boolean deletLesson(int id) {
		
		return lessonRepository.deletLesson(id);
	}
	@Override
	public List<LessonEntity> getLessonsByCategoryId(int categoryId) {
	    return lessonRepository.getLessonsByCategoryId(categoryId);
	}
}
