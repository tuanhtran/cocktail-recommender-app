package de.ur.mi.android.cocktailrecommender.data;

public class RecipeSearchResult implements Comparable<RecipeSearchResult> {
	private Recipe recipe;
	private int matchRate;

	public RecipeSearchResult(Recipe recipe, int matchRate) {
		this.recipe = recipe;
		this.matchRate = matchRate;
	}

	public Recipe getRecipe() {
		return recipe;
	}

	public int getMatchRate() {
		return matchRate;
	}

	@Override
	public int compareTo(RecipeSearchResult another) {
		int comparisonValue = ((Integer) matchRate).compareTo((Integer) another
				.getMatchRate());
		switch (comparisonValue) {
		case -1:
			return 1;
		case 0:
			return recipe.getName().compareTo(another.getRecipe().getName());
		default:
			return -1;
		}
	}
}
