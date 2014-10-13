package de.ur.mi.android.cocktailrecommender.data;

import java.util.ArrayList;

/*
 * SearchParameter bundles all information needed for SearchEngine class in CRDatabase:
 * Parameters, selected tags and ingredients
 */
public class SearchParameter {
	private boolean mustContainAllSelectedIngs = false;
	private boolean canContainNonSelectedIngs = false;
	private boolean mustContainAllSelectedTags = false;
	private boolean canContainNonSelectedTags = false;
	private ArrayList<Integer> selectedIngIDs = new ArrayList<Integer>();
	private ArrayList<Integer> selectedTagIDs = new ArrayList<Integer>();

	public SearchParameter(boolean mustContainAllSelectedIngs,
			boolean canContainNonSelectedIngs,
			boolean mustContainAllSelectedTags,
			boolean canContainNonSelectedTags,
			ArrayList<Integer> selectedIngIDs, ArrayList<Integer> selectedTagIDs) {
		this.selectedIngIDs.addAll(selectedIngIDs);
		this.selectedTagIDs.addAll(selectedTagIDs);
		this.mustContainAllSelectedIngs = mustContainAllSelectedIngs;
		this.canContainNonSelectedIngs = canContainNonSelectedIngs;
		this.mustContainAllSelectedTags = mustContainAllSelectedTags;
		this.canContainNonSelectedTags = canContainNonSelectedTags;
	}

	/**
	 * @return the mustContainAllSelectedIngs
	 */
	public boolean mustContainAllSelectedIngs() {
		return mustContainAllSelectedIngs;
	}

	/**
	 * @return the canContainNonSelectedIngs
	 */
	public boolean canContainNonSelectedIngs() {
		return canContainNonSelectedIngs;
	}

	/**
	 * @return the mustContainAllSelectedTags
	 */
	public boolean mustContainAllSelectedTags() {
		return mustContainAllSelectedTags;
	}

	/**
	 * @return the canContainNonSelectedTags
	 */
	public boolean canContainNonSelectedTags() {
		return canContainNonSelectedTags;
	}

	/**
	 * @return the selectedIngIDs
	 */
	public ArrayList<Integer> getSelectedIngIDs() {
		return selectedIngIDs;
	}

	/**
	 * @return the selectedTagIDs
	 */
	public ArrayList<Integer> getSelectedTagIDs() {
		return selectedTagIDs;
	}
}
