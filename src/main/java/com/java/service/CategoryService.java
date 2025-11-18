package com.java.service;

import java.util.List;
import java.util.Locale.Category;
import java.util.Map;

import com.java.Model.CategoryDTO;
import com.java.repository.entity.CategoryEntity;

public interface CategoryService {
	public List<CategoryDTO> findAll(Map<String,Object> params);
	public int addCategory(CategoryEntity c);
}
