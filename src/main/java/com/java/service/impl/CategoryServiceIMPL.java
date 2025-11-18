package com.java.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.Model.CategoryDTO;
import com.java.repository.CategoryRepository;
import com.java.repository.entity.CategoryEntity;
import com.java.service.CategoryService;

@Service
public class CategoryServiceIMPL implements CategoryService{
	@Autowired
	private CategoryRepository categoryRepository;
	
	public List<CategoryDTO> findAll(Map<String,Object> params){
		List<CategoryEntity> categoryEntity=categoryRepository.findAll(params);
		List<CategoryDTO> result =new ArrayList<>();
		for(CategoryEntity i: categoryEntity) {
			CategoryDTO lesson=new CategoryDTO();
			lesson.setName(i.getName());
			lesson.setStatus(i.getStatus());
			lesson.setCode(i.getCode());
			lesson.setType(i.getType());
			result.add(lesson);
		}
		return result;
	}

	@Override
	public int addCategory(CategoryEntity c) {
		 return categoryRepository.addCategory(c);
	}
	
}
