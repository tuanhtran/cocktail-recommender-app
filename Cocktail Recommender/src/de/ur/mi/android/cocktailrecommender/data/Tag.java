package de.ur.mi.android.cocktailrecommender.data;
/*
 * Class bundles all necessary information for cocktail tags
 */
public class Tag implements Comparable<Tag>{
	private int tagID;
	private String tagName;
	private boolean isSelected = false;
	
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
	
	public void toggleSelection() {
		isSelected = !isSelected;
	}

	public boolean isSelected() {
		return isSelected;
	}

	@Override
	public int compareTo(Tag another) {
		return (tagName.compareTo(another.getTagName()));
	}
}
