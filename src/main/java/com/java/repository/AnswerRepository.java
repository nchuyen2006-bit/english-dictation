package com.java.repository;

import com.java.repository.entity.TranscriptEntity;
import com.java.repository.entity.UserAnswerEntity;
import com.java.repository.entity.UserProgressEntity;

public interface AnswerRepository {
    TranscriptEntity getTranscriptByLessonId(Integer lessonId);
    int saveUserAnswer(UserAnswerEntity answer);
    UserProgressEntity getUserProgress(Integer userId, Integer lessonId);
    int createUserProgress(UserProgressEntity progress);
    int updateUserProgress(UserProgressEntity progress);
}
