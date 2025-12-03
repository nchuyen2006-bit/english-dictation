package com.java.service;

import org.springframework.web.multipart.MultipartFile;

import com.java.Model.AudioUploadDTO;

public interface AudioService {
    AudioUploadDTO uploadAudio(Integer lessonId, MultipartFile file, Integer duration);
    String getAudioUrl(Integer lessonId);
}