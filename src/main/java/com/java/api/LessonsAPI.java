package com.java.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.DTO.LessonDTO;
import com.java.service.LessonService;

@RestController
public class LessonsAPI {
	@Autowired
	private LessonService lessonService;
	@GetMapping(value="/api/lesson")
	public List<LessonDTO> getLesson(@RequestParam Map<String,Object> params){
		List<LessonDTO> result=lessonService.findAll(params);
		return result;
		
	}
   
}
