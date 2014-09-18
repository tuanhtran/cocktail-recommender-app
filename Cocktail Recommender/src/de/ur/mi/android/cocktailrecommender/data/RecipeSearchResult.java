package de.ur.mi.android.cocktailrecommender.data;

public class RecipeSearchResult implements Comparable<RecipeSearchResult> {
	public static final int DEFAULT_MATCH_RATE = 0;

	private Recipe recipe;
	private int matchRate;

	public RecipeSearchResult(Recipe recipe, int matchRate) {
		this.recipe = recipe;
		this.matchRate = matchRate;
	}

	public RecipeSearchResult(Recipe recipe) {
		this.recipe = recipe;
		matchRate = DEFAULT_MATCH_RATE;
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
