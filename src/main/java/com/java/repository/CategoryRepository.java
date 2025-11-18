package com.java.repository;

import java.util.List;
import java.util.Locale.Category;
import java.util.Map;

import com.java.repository.entity.CategoryEntity;

public interface CategoryRepository {
	public List<CategoryEntity> findAll(Map<String, Object> params);
	public int addCategory(CategoryEntity c); 
}
