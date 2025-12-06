package com.java.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.Model.TranscriptDTO;
import com.java.repository.TranscriptRepository;
import com.java.repository.entity.TranscriptEntity;
import com.java.service.TranscriptService;

@Service
public class TranscriptServiceIMPL implements TranscriptService {

    @Autowired
    private TranscriptRepository transcriptRepository;

    @Override
    public TranscriptDTO addOrUpdateTranscript(TranscriptDTO dto) {
        // Convert DTO to Entity
        TranscriptEntity entity = new TranscriptEntity();
        entity.setAudio_id(dto.getAudio_id());
        entity.setContent_en(dto.getContent_en());
        entity.setContent_clean(dto.getContent_clean());
        entity.setContent_vi(dto.getContent_vi());
        entity.setPronunciation_ipa(dto.getPronunciation_ipa());
        
        // Check if transcript exists
        boolean exists = transcriptRepository.existsByAudioId(dto.getAudio_id());
        
        int result;
        if (exists) {
            // Update existing
            result = transcriptRepository.updateTranscript(entity);
            if (result > 0) {
                // Get existing ID
                TranscriptEntity existing = transcriptRepository.getTranscriptByAudioId(dto.getAudio_id());
                dto.setId(existing.getId());
            }
        } else {
            // Add new
            result = transcriptRepository.addTranscript(entity);
            if (result > 0) {
                dto.setId(result);
            }
        }
        
        return result > 0 ? dto : null;
    }

    @Override
    public TranscriptDTO getTranscriptByAudioId(Integer audioId) {
        TranscriptEntity entity = transcriptRepository.getTranscriptByAudioId(audioId);
        
        if (entity == null) {
            return null;
        }
        
        // Convert Entity to DTO
        TranscriptDTO dto = new TranscriptDTO();
        dto.setId(entity.getId());
        dto.setAudio_id(entity.getAudio_id());
        dto.setContent_en(entity.getContent_en());
        dto.setContent_clean(entity.getContent_clean());
        dto.setContent_vi(entity.getContent_vi());
        dto.setPronunciation_ipa(entity.getPronunciation_ipa());
        
        return dto;
    }
}