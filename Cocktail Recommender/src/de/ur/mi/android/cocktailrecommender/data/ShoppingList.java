package de.ur.mi.android.cocktailrecommender.data;

public class ShoppingList {
	private int id;
	private String name;
	private RecipeIngredient[] ingredients;
	
	public ShoppingList (int id, String name, RecipeIngredient[] ingredients){
		this.id = id;
		this.name = name;
		this.ingredients = ingredients;
	}
	
	public String getListName(){
		return name;
	}
	
	public RecipeIngredient[] getIngredients(){
		return ingredients;
	}
	
	public void setIngredients(RecipeIngredient[] ingredients){
		this.ingredients = ingredients;
	}
	
	public int getId(){
		return id;
	}
}
