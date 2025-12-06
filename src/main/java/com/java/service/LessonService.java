package com.java.service;

import java.util.List;

import com.java.Model.LessonDTO;
import com.java.repository.entity.LessonEntity;

public interface LessonService {
    public List<LessonEntity> addLesson(List<LessonEntity> lesson);
    public boolean deletLesson(int id);
    public List<LessonEntity> getLessonsByCategoryId(int categoryId);
}