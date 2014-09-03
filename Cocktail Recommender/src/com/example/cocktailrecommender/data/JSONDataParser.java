package com.example.cocktailrecommender.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.SparseArray;

public class JSONDataParser {

	private static final String ING_ARRAY_KEY = "Ingredients";
	private static final String ING_ID_KEY = "ID";
	private static final String ING_QUANTITY_KEY = "Qty";

	private static final String TAG_ARRAY_KEY = "Tags";
	private static final String TAG_ID_KEY = "ID";

	
	
	public static SparseArray<Ingredient> getIngredientsFromJson(
			String jsonString) throws JSONException {

		if (jsonString.equals("")) {
			return null;
		}

		SparseArray<Ingredient> ingredients = new SparseArray<Ingredient>();
		JSONObject json = new JSONObject(jsonString);
		JSONArray ingredientArray = json.getJSONArray(ING_ARRAY_KEY);

		for (int ingIdx = 0; ingIdx < ingredientArray.length(); ingIdx++) {
			JSONObject JSONingredient = ingredientArray.getJSONObject(ingIdx);

			Integer ingredientID = JSONingredient.getInt(ING_ID_KEY);
			String ingredientQuantity = JSONingredient
					.getString(ING_QUANTITY_KEY);

			Ingredient ingredientToAdd = new Ingredient(ingredientID,
					ingredientQuantity);
			ingredients.append(ingIdx, ingredientToAdd);
		}

		return ingredients;
	}

	public static int[] getTagsFromJson(String jsonString) throws JSONException {
		if (jsonString.equals("")) {
			return null;
		}

		JSONObject json = new JSONObject(jsonString);
		JSONArray tagArray = json.getJSONArray(TAG_ARRAY_KEY);
		int[] tagIDs = new int[tagArray.length()];

		for (int tagIdx = 0; tagIdx < tagArray.length(); tagIdx++) {
			JSONObject JSONtag = tagArray.getJSONObject(tagIdx);

			Integer tagID = JSONtag.getInt(TAG_ID_KEY);
			tagIDs[tagIdx] = tagID;
		}

		return tagIDs;

	}

}
