package com.java.service;

import com.java.Model.TranscriptDTO;

public interface TranscriptService {
    TranscriptDTO addOrUpdateTranscript(TranscriptDTO transcript);
    TranscriptDTO getTranscriptByAudioId(Integer audioId);
}