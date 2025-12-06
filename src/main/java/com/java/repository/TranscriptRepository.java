package com.java.repository;

import com.java.repository.entity.TranscriptEntity;

public interface TranscriptRepository {
    int addTranscript(TranscriptEntity transcript);
    int updateTranscript(TranscriptEntity transcript);
    TranscriptEntity getTranscriptByAudioId(Integer audioId);
    boolean existsByAudioId(Integer audioId);
}