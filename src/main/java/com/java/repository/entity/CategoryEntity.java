package com.java.repository.entity;

public class CategoryEntity {
	private Integer id;
	private Integer program_id;
	private String code;
	private String name;
	private String slug;
	private String description ;
	private String image_url;
	private Integer total_lessons;
	private Integer total_duration;
	private String status;
	private String type;
	
	public CategoryEntity() {
		this.id = id;
		this.program_id = program_id;
		this.code = code;
		this.name = name;
		this.slug = slug;
		this.description = description;
		this.image_url = image_url;
		this.total_lessons = total_lessons;
		this.total_duration = total_duration;
		this.status = status;
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getProgram_id() {
		return program_id;
	}
	public void setProgram_id(Integer program_id) {
		this.program_id = program_id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public Integer getTotal_lessons() {
		return total_lessons;
	}
	public void setTotal_lessons(Integer total_lessons) {
		this.total_lessons = total_lessons;
	}
	public Integer getTotal_duration() {
		return total_duration;
	}
	public void setTotal_duration(Integer total_duration) {
		this.total_duration = total_duration;
	}
}
