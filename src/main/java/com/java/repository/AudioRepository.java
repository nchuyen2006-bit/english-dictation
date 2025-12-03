package com.java.repository;

import com.java.repository.entity.AudioEntity;

public interface AudioRepository {
    int saveAudio(AudioEntity audio);
    AudioEntity getAudioByLessonId(Integer lessonId);
    int updateAudio(AudioEntity audio);
    boolean deleteAudio(Integer audioId);
}