package de.ur.mi.android.cocktailrecommender.data;

public class SearchRecipeResult implements Comparable<SearchRecipeResult> {
	private Recipe recipe;
	private int matchRate;
	
	public SearchRecipeResult(Recipe recipe, int matchRate) {
		this.recipe = recipe;
		this.matchRate=matchRate;
	}
	
	public Recipe getRecipe() {
		return recipe;
	}
	
	public int getMatchRate() {
		return matchRate;
	}

	@Override
	public int compareTo(SearchRecipeResult another) {
		int comparisonValue = ((Integer)matchRate).compareTo((Integer)another.getMatchRate());
		if (comparisonValue == 0) {
			return recipe.getName().compareTo(another.getRecipe().getName());
		}
		return comparisonValue;
	}	
}
