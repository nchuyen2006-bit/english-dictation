package com.java.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.Model.CategoryDTO;
import com.java.repository.entity.CategoryEntity;
import com.java.service.CategoryService;

@RestController
public class CategoryAPI {
	@Autowired
	private CategoryService categoryService;

	@GetMapping(value = "/api/lesson")
	public List<CategoryDTO> getLesson(@RequestParam Map<String, Object> params) {
		List<CategoryDTO> result = categoryService.findAll(params);
		return result;
	}

	@RequestMapping("/api/categories")
		@PostMapping
		public Map<String, Object> createCategory(@RequestBody CategoryEntity category) {
			int categoryId = categoryService.addCategory(category);
			Map<String, Object> result = new HashMap<>();
			result.put("id", categoryId);
			return result;
		}
}
