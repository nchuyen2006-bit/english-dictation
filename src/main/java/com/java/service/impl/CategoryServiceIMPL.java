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
	
	@Override
	public List<CategoryDTO> findAll(Map<String,Object> params){
	    List<CategoryEntity> categoryEntity = categoryRepository.findAll(params);
	    List<CategoryDTO> result = new ArrayList<>();
	    
	    for(CategoryEntity i: categoryEntity) {
	        CategoryDTO dto = new CategoryDTO();
	        dto.setId(i.getId());        
	        dto.setName(i.getName());
	        dto.setStatus(i.getStatus());
	        dto.setCode(i.getCode());
	        dto.setType(i.getType());
	        result.add(dto);
	    }
	    
	    return result;
	}

	@Override
	public int addCategory(CategoryEntity c) {
		 return categoryRepository.addCategory(c);
	}
	public boolean deleteCategory(int id) {
		return categoryRepository.deleteCategory(id);
	}
	
}
