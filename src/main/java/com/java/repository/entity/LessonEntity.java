package com.java.repository.entity;

import java.util.List;

public class LessonEntity  {
	private Integer id;
	private Integer category_id;
	private Integer section_id;
	private Integer order_num;
	private Integer duration;
	private boolean is_premum;
	private String audioUrl;
	private Integer audioDuration;
	
	public boolean isIs_premum() {
		return is_premum;
	}
	public void setIs_premum(boolean is_premum) {
		this.is_premum = is_premum;
	}
	
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public String getAudioUrl() {
		return audioUrl;
	}
	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}
	public Integer getAudioDuration() {
		return audioDuration;
	}
	public void setAudioDuration(Integer audioDuration) {
		this.audioDuration = audioDuration;
	}
	public String getTranscriptText() {
		return transcriptText;
	}
	public void setTranscriptText(String transcriptText) {
		this.transcriptText = transcriptText;
	}
	private String transcriptText;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getCategory_id() {
		return category_id;
	}
	public void setCategory_id(Integer category_id) {
		this.category_id = category_id;
	}
	public Integer getSection_id() {
		return section_id;
	}
	public void setSection_id(Integer section_id) {
		this.section_id = section_id;
	}
	public Integer getOrder_num() {
		return order_num;
	}
	public void setOrder_num(Integer order_num) {
		this.order_num = order_num;
	}
}
