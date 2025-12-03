package com.java.repository.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class UserProgressEntity {
    private Integer id;
    private Integer user_id;
    private Integer lesson_id;
    private String status;
    private Integer attempts;
    private BigDecimal score;
    private Timestamp completed_at;
    private Timestamp created_at;
    private Timestamp updated_at;

    public UserProgressEntity() {
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getUser_id() {
        return user_id;
    }
    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
    public Integer getLesson_id() {
        return lesson_id;
    }
    public void setLesson_id(Integer lesson_id) {
        this.lesson_id = lesson_id;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Integer getAttempts() {
        return attempts;
    }
    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }
    public BigDecimal getScore() {
        return score;
    }
    public void setScore(BigDecimal score) {
        this.score = score;
    }
    public Timestamp getCompleted_at() {
        return completed_at;
    }
    public void setCompleted_at(Timestamp completed_at) {
        this.completed_at = completed_at;
    }
    public Timestamp getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
    public Timestamp getUpdated_at() {
        return updated_at;
    }
    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }
}