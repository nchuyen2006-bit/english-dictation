package com.java.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    
    /**
     * Get categories with filters
     * GET /api/lesson?type=audio&slug=ielts-listening&status=completed&name=Section 1&keyword=nano
     */
    @GetMapping(value = "/api/lesson")
    public ResponseEntity<Map<String, Object>> getLesson(@RequestParam Map<String, Object> params) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CategoryDTO> result = categoryService.findAll(params);
            
            response.put("success", true);
            response.put("data", result);
            response.put("total", result.size());
            
            // Thêm thông tin về filters đã áp dụng
            if (params.containsKey("keyword")) {
                response.put("keyword", params.get("keyword"));
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Search categories by keyword only
     * GET /api/lesson/search?keyword=nano
     */
    @GetMapping("/api/lesson/search")
    public ResponseEntity<Map<String, Object>> searchCategories(@RequestParam String keyword) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Keyword không được để trống");
                return ResponseEntity.badRequest().body(response);
            }
            
            Map<String, Object> params = new HashMap<>();
            params.put("keyword", keyword);
            
            List<CategoryDTO> result = categoryService.findAll(params);
            
            response.put("success", true);
            response.put("data", result);
            response.put("total", result.size());
            response.put("keyword", keyword);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Create category
     * POST /api/categories
     */
    @RequestMapping("/api/categories")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCategory(@RequestBody CategoryEntity category) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int categoryId = categoryService.addCategory(category);
            
            if (categoryId > 0) {
                response.put("success", true);
                response.put("id", categoryId);
                response.put("message", "Tạo category thành công");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("success", false);
                response.put("message", "Tạo category thất bại");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Add lessons (batch)
     * POST /api/add-lesson
     */
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
    
    /**
     * Delete lesson
     * DELETE /api/lesson/{id}
     */
    @DeleteMapping("/api/lesson/{id}")
    public Map<String, Object> deleteLesson(@PathVariable("id") int id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean deleted = lessonService.deletLesson(id);
            
            if (deleted) {
                result.put("success", true);
                result.put("message", "Xóa bài học có id = " + id + " thành công!");
            } else {
                result.put("success", false);
                result.put("message", "Không tìm thấy bài học có id = " + id);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Delete category
     * DELETE /api/category/{id}
     */
    @DeleteMapping("/api/category/{id}")
    public Map<String, Object> deleteCategory(@PathVariable("id") int id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean deleted = categoryService.deleteCategory(id);
            
            if (deleted) {
                result.put("success", true);
                result.put("message", "Xóa chủ đề có id = " + id + " thành công!");
            } else {
                result.put("success", false);
                result.put("message", "Không tìm thấy chủ đề có id = " + id);
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
}