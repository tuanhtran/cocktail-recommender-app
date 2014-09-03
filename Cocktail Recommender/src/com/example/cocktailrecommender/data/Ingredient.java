package com.example.cocktailrecommender.data;

public class Ingredient {
	private int ingID;
	private String quantity;
	
	
	public Ingredient (Integer ingID, String quantity) {
		this.ingID = ingID;
		this.quantity = quantity;
	}
	
	public String getIngName() {
		String ingName = "";
		//get Name by using ingID;
		return ingName;
	}
	
	public String getQuantity() {
		return quantity;
	}
	
}
