package com.example.cocktailrecommender.data;

public class IngredientType implements Comparable<IngredientType> {
	private int ingID;
	private String ingName;
	private boolean isSelected = false;

	public IngredientType(Integer ingID, String ingName) {
		this.ingID = ingID;
		this.ingName = ingName;
	}

	public int getID() {
		return ingID;
	}

	public String getIngName() {
		return ingName;
	}

	public char getCategoryPrefixChar() {
		String ID = "" + ingID + "";
		return ID.charAt(0);
	}

	public void toggleSelection() {
		isSelected = !isSelected;
	}

	public boolean isSelected() {
		return isSelected;
	}

	@Override
	public int compareTo(IngredientType another) {
		return ingName.compareTo(another.getIngName());
	}
}
