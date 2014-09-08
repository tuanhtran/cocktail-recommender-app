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
import com.example.cocktailrecommender.data.RecipeIngredient;
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
//				String cocktailName = cocktailCursor.getString(0);
//				SparseArray<RecipeIngredient> ingredients = new SparseArray<RecipeIngredient>();
//				try {
//					ingredients = JSONDataParser.getIngredientsFromJson(cocktailCursor
//							.getString(1));
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				int[] tags = null;
//				try {
//					tags = JSONDataParser.getTagsFromJson(cocktailCursor.getString(2));
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				String preparation = cocktailCursor.getString(3);
//				Cocktail cocktail = new Cocktail(cocktailName, ingredients,
//						tags, preparation);
//				cocktailList.add(cocktail);
			} while (cocktailCursor.moveToNext());
		}
		return cocktailList;
	}
}
