package com.java.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.DTO.LessonDTO;
import com.java.repository.LessonRepository;
import com.java.repository.entity.LessonEntity;
import com.java.service.LessonService;

@Service
public class LessonServiceIMPL implements LessonService{
	@Autowired
	private LessonRepository lessonRepository;
	public List<LessonDTO> findAll(Map<String,Object> params){
		List<LessonEntity> lessonEntity=lessonRepository.findAll(params);
		List<LessonDTO> result =new ArrayList<>();
		for(LessonEntity i: lessonEntity) {
			LessonDTO lesson=new LessonDTO();
			lesson.setName(i.getName());
			lesson.setStatus(i.getStatus());
			lesson.setCode(i.getCode());
			lesson.setType(i.getType());
			result.add(lesson);
		}
		return result;
	}
}
