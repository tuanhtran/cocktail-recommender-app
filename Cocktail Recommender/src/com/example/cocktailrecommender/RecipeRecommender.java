package com.example.cocktailrecommender;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseArray;

import com.example.cocktailrecommender.data.Cocktail;
import com.example.cocktailrecommender.data.RecipeData;

public class RecipeRecommender {
	private RecipeData recipeData;
	private Context context;

	public RecipeRecommender(Context context) {
		this.context = context;
		recipeData = new RecipeData(this.context);
	}

	public ArrayList<Cocktail> getRecipesByName(String name) {
		Cursor cocktailCursor = recipeData.selectByName(name);
		return getCocktailList(cocktailCursor);
	}

	public ArrayList<Cocktail> getRecipesByIngredients(String[] ingredients,
			boolean strict) {
		Cursor cocktailCursor = recipeData.selectByIngredients(ingredients,
				strict);
		return getCocktailList(cocktailCursor);
	}

	public ArrayList<Cocktail> getRecipesByTags(String[] tags, boolean strict) {
		Cursor cocktailCursor = recipeData.selectByTags(tags, strict);
		return getCocktailList(cocktailCursor);
	}

	// Creates ArrayList from table rows held in cocktailCursor
	private ArrayList<Cocktail> getCocktailList(Cursor cocktailCursor) {
		ArrayList<Cocktail> cocktailList = new ArrayList<Cocktail>();
		if (cocktailCursor.moveToFirst()) {
			do {
				String cocktailName = cocktailCursor.getString(0);
				SparseArray<String> ingredients = new SparseArray<String>();
				try {
					ingredients = getIngredientsFromJSON(cocktailCursor
							.getString(1));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String[] tags = null;
				try {
					tags = getTagsFromJSON(cocktailCursor.getString(2));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String preparation = cocktailCursor.getString(3);
				Cocktail cocktail = new Cocktail(cocktailName, ingredients,
						tags, preparation);
				cocktailList.add(cocktail);
			} while (cocktailCursor.moveToNext());
		}
		return cocktailList;
	}

	// JSONArray should only contain Strings
	private String[] getTagsFromJSON(String tagsJSON) throws JSONException {
		JSONObject tags = new JSONObject(tagsJSON);
		JSONArray tagsJSONArray = tags.getJSONArray("tags");
		String[] tagsArray = new String[tagsJSONArray.length()];
		for (int i = 0; i < tagsJSONArray.length(); i++) {
			tagsArray[i] = tagsJSONArray.getString(i);
		}
		return tagsArray;
	}

	// Expected JSON format: {name:[name1:value1, name2:value2,...]}, where
	// namex is a String representing an Integer (unknown).
	// namex is needed to create SparseArray, so an iterator is created for each
	// JSONArray element (even though the elements have only one key).
	// ToDo: find better way to access namex.
	private SparseArray<String> getIngredientsFromJSON(String ingredientsJSON)
			throws JSONException {
		SparseArray<String> ingredientsArray = new SparseArray<String>();
		JSONObject ingredients = new JSONObject(ingredientsJSON);
		JSONArray ingredientsJSONArray = ingredients
				.getJSONArray("ingredients");

		for (int i = 0; i < ingredientsJSONArray.length(); i++) {
			JSONObject ingredientObject = ingredientsJSONArray.getJSONObject(i);
			Iterator<?> iterator = ingredientObject.keys();
			String name = (String) iterator.next();
			int id = Integer.parseInt(name);
			String amount = ingredientObject.getString(name);
			ingredientsArray.append(id, amount);
		}

		return ingredientsArray;
	}

}
