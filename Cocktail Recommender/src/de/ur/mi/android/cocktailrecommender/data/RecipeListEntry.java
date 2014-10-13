package de.ur.mi.android.cocktailrecommender.data;

public class RecipeListEntry implements Comparable<RecipeListEntry> {
	public static final int DEFAULT_MATCH_RATE = 0;

	private Recipe recipe;
	private int matchRate;

	public RecipeListEntry(Recipe recipe, int matchRate) {
		this.recipe = recipe;
		this.matchRate = matchRate;
	}

	public RecipeListEntry(Recipe recipe) {
		this.recipe = recipe;
		matchRate = DEFAULT_MATCH_RATE;
	}

	public Recipe getRecipe() {
		return recipe;
	}

	public int getMatchRate() {
		return matchRate;
	}

	/*
	 * Overrides the standard compareTo(...) method so RecipeListEntry objects
	 * are sorted by their matchRate (descending) first and name second.
	 */
	@Override
	public int compareTo(RecipeListEntry another) {
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
