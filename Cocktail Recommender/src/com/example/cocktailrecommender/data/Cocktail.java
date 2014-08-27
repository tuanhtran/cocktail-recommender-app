package com.example.cocktailrecommender.data;

import android.util.SparseArray;

public class Cocktail {
	private String name;
	private SparseArray<String> ingredients;
	private String[] tags;
	private String preparation;
	
	public Cocktail(String name, SparseArray<String> ingredients, String[] tags, String preparation){
		this.name = name;
		this.ingredients = ingredients;
		this.tags = tags;
		this.preparation = preparation;
	}
	
	public String getName(){
		return name;
	}
	
	public SparseArray<String> getIngredients(){
		return ingredients;
	}
	
	public String[] getTags(){
		return tags;
	}
	
	public String getPreparation(){
		return preparation;
	}
	
	public String toString(){
		String cocktailString="";
		String ingredientsString="Ingredients: ";
		for (int i = 0; i < ingredients.size(); i++){
			if (ingredients.keyAt(i) <= Ingredients.ALCOHOL_END){
				ingredientsString += Ingredients.getAlcoholString(ingredients.keyAt(i))+"="+ingredients.valueAt(i)+", ";
			} else if (ingredients.keyAt(i) <= Ingredients.JUICES_END){
				ingredientsString += Ingredients.getJuiceString(ingredients.keyAt(i))+"="+ingredients.valueAt(i)+", ";
			} else {
				ingredientsString += Ingredients.getOtherString(ingredients.keyAt(i))+"="+ingredients.valueAt(i)+", ";
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
