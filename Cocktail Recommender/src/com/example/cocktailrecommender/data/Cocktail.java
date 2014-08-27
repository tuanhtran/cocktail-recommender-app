package com.example.cocktailrecommender.data;

import android.util.SparseArray;

public class Cocktail {
	private String name;
	private SparseArray<Ingredient> ingredients;
	private int[] tags;
	private String preparation;
	
	public Cocktail(String name, SparseArray<Ingredient> ingredients, int[] tags, String preparation){
		this.name = name;
		this.ingredients = ingredients;
		this.tags = tags;
		this.preparation = preparation;
	}
	
	public String getName(){
		return name;
	}
	
	public SparseArray<Ingredient> getIngredients(){
		return ingredients;
	}
	
	public int[] getTags(){
		return tags;
	}
	
	public String getPreparation(){
		return preparation;
	}
	
	public String toString(){
		String cocktailString="";
		String ingredientsString="Ingredients: ";
		for (int i = 0; i < ingredients.size(); i++){
			if (ingredients.keyAt(i) <= IngredientsData.ALCOHOL_END){
				ingredientsString += IngredientsData.getAlcoholString(ingredients.keyAt(i))+"="+ingredients.valueAt(i)+", ";
			} else if (ingredients.keyAt(i) <= IngredientsData.JUICES_END){
				ingredientsString += IngredientsData.getJuiceString(ingredients.keyAt(i))+"="+ingredients.valueAt(i)+", ";
			} else {
				ingredientsString += IngredientsData.getOtherString(ingredients.keyAt(i))+"="+ingredients.valueAt(i)+", ";
			}
		}
		String tagsString="Tags: ";
		for (int i = 0; i < tags.length; i++){
			tagsString += tags[i]+", ";
		}
		cocktailString=name+" "+ingredientsString+" "+tagsString+" "+preparation;
		return cocktailString;
	}
}
