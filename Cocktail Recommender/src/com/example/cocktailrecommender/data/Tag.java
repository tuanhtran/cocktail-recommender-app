package com.example.cocktailrecommender.data;

public class Tag {
	private int tagID;
	private String tagName;
	
	public Tag(int tagID, String tagName) {
		this.tagID = tagID;
		this.tagName= tagName;
	}
	
	public int getTagID() {
		return tagID;
	}
	
	public String getTagName() {
		return tagName;
	}
}
