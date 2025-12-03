package com.java.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.java.Model.AudioUploadDTO;
import com.java.repository.AudioRepository;
import com.java.repository.entity.AudioEntity;

@Service
public class AudioServiceIMPL implements com.java.service.AudioService {

    @Autowired
    private AudioRepository audioRepository;
    
    // Thư mục lưu audio files (có thể config trong application.properties)
    private static final String UPLOAD_DIR = "uploads/audios/";
    
    // Base URL để truy cập file (config theo domain của bạn)
    private static final String BASE_URL = "http://localhost:8081/uploads/audios/";

    @Override
    public AudioUploadDTO uploadAudio(Integer lessonId, MultipartFile file, Integer duration) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new RuntimeException("File rỗng");
            }
            
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new RuntimeException("Tên file không hợp lệ");
            }
            
            // Get file extension
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            if (!extension.matches("\\.(mp3|wav|ogg)")) {
                throw new RuntimeException("Chỉ hỗ trợ file mp3, wav, ogg");
            }
            
            // Create upload directory if not exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String newFilename = "lesson_" + lessonId + "_" + UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(newFilename);
            
            // Save file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Calculate file size in KB
            long sizeBytes = file.getSize();
            int sizeKb = (int) (sizeBytes / 1024);
            
            // Save to database
            AudioEntity audioEntity = new AudioEntity();
            audioEntity.setLesson_id(lessonId);
            audioEntity.setUrl(BASE_URL + newFilename);
            audioEntity.setDuration(duration != null ? duration : 0);
            audioEntity.setSize_kb(sizeKb);
            audioEntity.setBitrate(128); // Default bitrate
            audioEntity.setFormat(extension.substring(1)); // Remove dot
            
            int audioId = audioRepository.saveAudio(audioEntity);
            
            if (audioId == -1) {
                throw new RuntimeException("Lưu audio vào database thất bại");
            }
            
            // Return DTO
            AudioUploadDTO dto = new AudioUploadDTO();
            dto.setLessonId(lessonId);
            dto.setFileName(newFilename);
            dto.setFileUrl(BASE_URL + newFilename);
            dto.setDuration(duration);
            dto.setSizeKb((long) sizeKb);
            dto.setFormat(extension.substring(1));
            
            return dto;
            
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage());
        }
    }

    @Override
    public String getAudioUrl(Integer lessonId) {
        AudioEntity audio = audioRepository.getAudioByLessonId(lessonId);
        return audio != null ? audio.getUrl() : null;
    }
}