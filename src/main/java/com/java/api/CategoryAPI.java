package com.java.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.Model.CategoryDTO;
import com.java.repository.entity.CategoryEntity;
import com.java.repository.entity.LessonEntity;
import com.java.service.CategoryService;
import com.java.service.LessonService;

@RestController
public class CategoryAPI {
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private LessonService lessonService;
	
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
	@RequestMapping("/api/add-lesson")
	@PostMapping
	public Map<String, Object> addLesson(@RequestBody List<LessonEntity> lessons) {
	    Map<String, Object> result = new HashMap<>();
	    
	    try {
	        List<LessonEntity> addedLessons = lessonService.addLesson(lessons);
	        
	        if (addedLessons != null && !addedLessons.isEmpty()) {
	            result.put("success", true);
	            result.put("message", "Thêm " + addedLessons.size() + " bài học thành công");
	            result.put("data", addedLessons);
	            result.put("total", addedLessons.size());
	        } else {
	            result.put("success", false);
	            result.put("message", "Không thêm được bài học nào");
	            result.put("data", null);
	        }
	        
	    } catch (Exception e) {
	        result.put("success", false);
	        result.put("message", "Lỗi: " + e.getMessage());
	        result.put("data", null);
	        e.printStackTrace();
	    }
		return result;
	}
	@DeleteMapping("/api/lesson/{id}")
	public Map<String, Object> deletLesson(@PathVariable("id") int id) {
		Map<String, Object> result=new HashMap<>();
		try {
			boolean deleted=lessonService.deletLesson(id);
			if(deleted) {
				result.put("success", true);
	            result.put("message", "Xóa bài học có id = " + id + " thành công!");
			}else {
				result.put("success", false);
	            result.put("message", "Không tìm thấy bài học có id = " + id);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	@DeleteMapping("/api/category/{id}")
	public Map<String, Object> deleateCategory(@PathVariable("id") int id){
		Map<String, Object> result =new HashMap<>();
		try{
			boolean deleted =categoryService.deleteCategory(id);
			if(deleted) {
				result.put("success", true);
	            result.put("message", "Xóa chủ đề có id = " + id + " thành công!");
			}
			else {
				result.put("success", false);
	            result.put("message", "Không tìm thành chủ đề có id = "+id);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
}