package com.java.service;

import com.java.Model.CheckAnswerRequest;
import com.java.Model.CheckAnswerResponse;

public interface AnswerService {
    CheckAnswerResponse checkAnswer(Integer userId, CheckAnswerRequest request);
}