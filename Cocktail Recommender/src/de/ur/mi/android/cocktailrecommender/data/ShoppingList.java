package de.ur.mi.android.cocktailrecommender.data;

public class ShoppingList {
	private int id;
	private String name;
	private RecipeIngredient[] ingredients;
	private int[] recipeIds;
	
	public ShoppingList (int id, String name, RecipeIngredient[] ingredients, int[] recipeIds){
		this.id = id;
		this.name = name;
		this.ingredients = ingredients;
		this.recipeIds = recipeIds;
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
	
	public int[] getRecipes(){
		return recipeIds;
	}
	
	public void setRecipes(int[] recipeIds){
		this.recipeIds = recipeIds;
	}
	
	public int getId(){
		return id;
	}
}
