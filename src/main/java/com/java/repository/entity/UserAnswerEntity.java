package com.java.repository.entity;

import java.sql.Timestamp;

public class UserAnswerEntity {
    private Integer id;
    private Integer user_id;
    private Integer lesson_id;
    private String answer_text;
    private Boolean is_correct;
    private Timestamp checked_at;
    private Timestamp created_at;

    public UserAnswerEntity() {
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
    public String getAnswer_text() {
        return answer_text;
    }
    public void setAnswer_text(String answer_text) {
        this.answer_text = answer_text;
    }
    public Boolean getIs_correct() {
        return is_correct;
    }
    public void setIs_correct(Boolean is_correct) {
        this.is_correct = is_correct;
    }
    public Timestamp getChecked_at() {
        return checked_at;
    }
    public void setChecked_at(Timestamp checked_at) {
        this.checked_at = checked_at;
    }
    public Timestamp getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}