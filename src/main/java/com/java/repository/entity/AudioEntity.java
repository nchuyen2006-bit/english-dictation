package com.java.repository.entity;

import java.sql.Timestamp;

public class AudioEntity {
    private Integer id;
    
    private Integer lesson_id;
    private String url;
    private Integer duration;
    private Integer size_kb;
    private Integer bitrate;
    private String format;
    private Timestamp created_at;

    public AudioEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLesson_id() {
        return lesson_id;
    }

    public void setLesson_id(Integer lesson_id) {
        this.lesson_id = lesson_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getSize_kb() {
        return size_kb;
    }

    public void setSize_kb(Integer size_kb) {
        this.size_kb = size_kb;
    }

    public Integer getBitrate() {
        return bitrate;
    }

    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}