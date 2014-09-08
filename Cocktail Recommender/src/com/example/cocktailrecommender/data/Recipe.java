package com.example.cocktailrecommender.data;

public class Recipe {
	private int recipeID;
	private String name;
	private RecipeIngredient[] ingredients;
	private Tag[] tags;
	private String preparation;

	public Recipe(int recipeID, String name, RecipeIngredient[] ingredients,
			Tag[] tags, String preparation) {
		this.recipeID = recipeID;
		this.name = name;
		this.ingredients = ingredients;
		this.tags = tags;
		this.preparation = preparation;
	}

	public int getRecipeID() {
		return recipeID;
	}

	public String getName() {
		return name;
	}

	public RecipeIngredient[] getIngredients() {
		return ingredients;
	}

	public Tag[] getTags() {
		return tags;
	}

	public String getPreparation() {
		return preparation;
	}
}
