package com.gracie.douban.domain;

public class Book {

	private String name;
	private String description;
	private String bookUrl;
	private float rating;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getBookUrl() {
		return bookUrl;
	}
	
	public void setBookUrl(String bookUrl) {
		this.bookUrl = bookUrl;
	}
	
	public float getRating() {
		return rating;
	}
	
	public void setRating(float rating) {
		this.rating = rating;
	}
	
}
