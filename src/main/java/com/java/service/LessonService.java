package com.java.service;

import java.util.List;
import java.util.Map;

import com.java.DTO.LessonDTO;

public interface LessonService {
	List<LessonDTO> findAll(Map<String,Object> params);
}
