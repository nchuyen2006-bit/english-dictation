package com.java.Model;

public class AudioUploadDTO {
    private Integer lessonId;
    private String fileName;
    private String fileUrl;
    private Integer duration;
    private Long sizeKb;
    private String format;

    public AudioUploadDTO() {
    }

    public Integer getLessonId() {
        return lessonId;
    }

    public void setLessonId(Integer lessonId) {
        this.lessonId = lessonId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Long getSizeKb() {
        return sizeKb;
    }

    public void setSizeKb(Long sizeKb) {
        this.sizeKb = sizeKb;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
