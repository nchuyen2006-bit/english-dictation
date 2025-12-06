package com.java.Model;

public class TranscriptDTO {
    private Integer id;
    private Integer audio_id;
    private String content_en;
    private String content_clean;
    private String content_vi;
    private String pronunciation_ipa;

    public TranscriptDTO() {
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAudio_id() {
        return audio_id;
    }

    public void setAudio_id(Integer audio_id) {
        this.audio_id = audio_id;
    }

    public String getContent_en() {
        return content_en;
    }

    public void setContent_en(String content_en) {
        this.content_en = content_en;
    }

    public String getContent_clean() {
        return content_clean;
    }

    public void setContent_clean(String content_clean) {
        this.content_clean = content_clean;
    }

    public String getContent_vi() {
        return content_vi;
    }

    public void setContent_vi(String content_vi) {
        this.content_vi = content_vi;
    }

    public String getPronunciation_ipa() {
        return pronunciation_ipa;
    }

    public void setPronunciation_ipa(String pronunciation_ipa) {
        this.pronunciation_ipa = pronunciation_ipa;
    }
}