package de.ur.mi.android.cocktailrecommender.data;

public class Tag implements Comparable<Tag>{
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

	@Override
	public int compareTo(Tag another) {
		return ((Integer)tagID).compareTo((Integer)another.getTagID());
	}
}
