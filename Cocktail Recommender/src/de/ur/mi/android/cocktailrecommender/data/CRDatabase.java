package de.ur.mi.android.cocktailrecommender.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CRDatabase {

	private static final String DATABASE_TABLE_COCKTAILS = "Cocktails";
	private static final String COCKTAILS_KEY_ID = "ID"; // Integer
	private static final String COCKTAILS_KEY_NAME = "Name"; // String
	private static final String COCKTAILS_KEY_INGREDIENTS = "Ingredients"; // (JSON)String
	private static final String COCKTAILS_KEY_TAGS = "Tags"; // (JSON)String
	private static final String COCKTAILS_KEY_PREPARATION = "Preparation"; // String
	private static final int COCKTAILS_COLUMN_IDX_ID = 0;
	private static final int COCKTAILS_COLUMN_IDX_NAME = 1;
	private static final int COCKTAILS_COLUMN_IDX_INGREDIENTS = 2;
	private static final int COCKTAILS_COLUMN_IDX_TAGS = 3;
	private static final int COCKTAILS_COLUMN_IDX_PREPARATION = 4;

	private static final String DATABASE_TABLE_INGREDIENTS = "Ingredients";
	private static final String INGREDIENTS_KEY_ID = "ID"; // Integer
	private static final String INGREDIENTS_KEY_NAME = "Name";// String
	private static final int INGREDIENTS_COLUMN_IDX_ID = 0;
	private static final int INGREDIENTS_COLUMN_IDX_NAME = 1;

	private static final String DATABASE_TABLE_TAGS = "Tags";
	private static final String TAGS_KEY_ID = "ID"; // Integer
	private static final String TAGS_KEY_NAME = "Name";// String
	private static final int TAGS_COLUMN_IDX_ID = 0;
	private static final int TAGS_COLUMN_IDX_NAME = 1;

	private static final String DATABASE_TABLE_FAVORITES = "Favorites";
	private static final String FAVORITES_KEY_ID = "ID"; // Integer
	private static final String FAVORITES_KEY_DRINK_ID = "DringID"; // Integer
	private static final int FAVORITES_COLUMN_IDX_ID = 0;
	private static final int FAVORITES_COLUMN_IDX_DRINK_ID = 1;

	private static final String DATABASE_TABLE_HISTORY = "History";
	private static final String HISTORY_KEY_ID = "ID"; // Integer
	private static final String HISTORY_KEY_DRINK_ID = "DringID"; // Integer
	private static final int HISTORY_COLUMN_IDX_ID = 0;
	private static final int HISTORY_COLUMN_IDX_DRINK_ID = 1;

	private static final String DATABASE_TABLE_SHOPPINGLISTS = "ShoppingLists";
	private static final String SHOPPINGLISTS_KEY_ID = "ID"; // Integer
	private static final String SHOPPINGLISTS_NAME_ID = "List_Name"; // String
	private static final String SHOPPINGLISTS_INGREDIENT_IDS = "IngredientIDs"; // (JSON)String
	private static final int SHOPPINGLISTS_COLUMN_IDX_ID = 0;
	private static final int SHOPPINGLISTS_COLUMN_IDX_NAME = 1;
	private static final int SHOPPINGLISTS_COLUMN_IDX_INGRREDIENT_IDS = 2;

	public static final char ALCOHOLIC_ING_PREFIX = '1';
	public static final char NON_ALCOHOLIC_ING_PREFIX = '2';
	public static final char MISC_ING_PREFIX = '3';

	private CRDatabaseHelper helper;
	private SQLiteDatabase db;
	private JSONDataParser jsonDataParser;

	public CRDatabase(Context context) {
		helper = new CRDatabaseHelper(context);
		jsonDataParser = new JSONDataParser();
	}

	public void open() {
		db = helper.getWritableDatabase();
	}

	public void close() {
		db.close();
		helper.close();
	}

	public ArrayList<Recipe> getFullRecipeList() {
		ArrayList<Recipe> recipeList = new ArrayList<Recipe>();

		Cursor cursor = db.query(DATABASE_TABLE_COCKTAILS, new String[] {
				COCKTAILS_KEY_ID, COCKTAILS_KEY_NAME,
				COCKTAILS_KEY_INGREDIENTS, COCKTAILS_KEY_TAGS,
				COCKTAILS_KEY_PREPARATION }, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				int recipeID = cursor.getInt(COCKTAILS_COLUMN_IDX_ID);
				String name = cursor.getString(COCKTAILS_COLUMN_IDX_NAME);

				String ingredientsJsonString = cursor
						.getString(COCKTAILS_COLUMN_IDX_INGREDIENTS);
				String tagsJsonString = cursor
						.getString(COCKTAILS_COLUMN_IDX_TAGS);

				RecipeIngredient[] ingredients = null;
				Tag[] tags = null;
				try {
					ingredients = jsonDataParser
							.getIngredientsFromJson(ingredientsJsonString);
					tags = jsonDataParser.getTagsFromJson(tagsJsonString);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String preparation = cursor
						.getString(COCKTAILS_COLUMN_IDX_PREPARATION);

				Recipe recipeToAdd = new Recipe(recipeID, name, ingredients,
						tags, preparation);
				recipeList.add(recipeToAdd);
			} while (cursor.moveToNext());
		}

		return recipeList;

	}

	public ArrayList<IngredientType> getFullIngList() {
		ArrayList<IngredientType> ingredientList = new ArrayList<IngredientType>();

		Cursor cursor = db.query(DATABASE_TABLE_INGREDIENTS, new String[] {
				INGREDIENTS_KEY_ID, INGREDIENTS_KEY_NAME }, null, null, null,
				null, null);

		if (cursor.moveToFirst()) {
			do {
				int iD = cursor.getInt(INGREDIENTS_COLUMN_IDX_ID);
				String name = cursor.getString(INGREDIENTS_COLUMN_IDX_NAME);
				IngredientType ingTypeToAdd = new IngredientType(iD, name);
				ingredientList.add(ingTypeToAdd);
			} while (cursor.moveToNext());
		}
		return ingredientList;
	}

	public ArrayList<Tag> getFullTagList() {
		ArrayList<Tag> tagList = new ArrayList<Tag>();

		Cursor cursor = db.query(DATABASE_TABLE_TAGS, new String[] {
				TAGS_KEY_ID, TAGS_KEY_NAME }, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				int iD = cursor.getInt(TAGS_COLUMN_IDX_ID);
				String name = cursor.getString(TAGS_COLUMN_IDX_NAME);
				Tag tagToAdd = new Tag(iD, name);
				tagList.add(tagToAdd);
			} while (cursor.moveToNext());
		}
		return tagList;
	}

	private String getIngNameFromID(int ingID) {
		Cursor cursor = db.query(DATABASE_TABLE_INGREDIENTS,
				new String[] { INGREDIENTS_KEY_NAME }, INGREDIENTS_KEY_ID + "="
						+ ingID, null, null, null, null);
		cursor.moveToFirst();
		String name = cursor.getString(INGREDIENTS_COLUMN_IDX_NAME - 1);
		return name;
	}

	private String getTagNameFromID(int tagID) {
		Cursor cursor = db.query(DATABASE_TABLE_TAGS,
				new String[] { TAGS_KEY_NAME }, TAGS_KEY_ID + "=" + tagID,
				null, null, null, null);
		cursor.moveToFirst();
		String name = cursor.getString(TAGS_COLUMN_IDX_NAME - 1);
		return name;
	}

	private class CRDatabaseHelper extends SQLiteAssetHelper {
		private static final int DB_VERSION = 1;
		private static final String DB_NAME = "crDatabase.db";

		public CRDatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}

	}

	private class JSONDataParser {
		private static final String ING_ARRAY_KEY = "Ingredients";
		private static final String ING_ID_KEY = "ID";
		private static final String ING_QUANTITY_KEY = "Qty";

		private static final String TAG_ARRAY_KEY = "Tags";
		private static final String TAG_ID_KEY = "ID";

		public RecipeIngredient[] getIngredientsFromJson(String jsonString)
				throws JSONException {

			if (jsonString.equals("")) {
				return null;
			}

			JSONObject json = new JSONObject(jsonString);
			JSONArray ingredientArray = json.getJSONArray(ING_ARRAY_KEY);
			RecipeIngredient[] ingredients = new RecipeIngredient[ingredientArray
					.length()];

			for (int ingIdx = 0; ingIdx < ingredientArray.length(); ingIdx++) {
				JSONObject JSONingredient = ingredientArray
						.getJSONObject(ingIdx);

				Integer ingredientID = JSONingredient.getInt(ING_ID_KEY);
				String ingredientName = getIngNameFromID(ingredientID);
				String ingredientQuantity = JSONingredient
						.getString(ING_QUANTITY_KEY);

				RecipeIngredient ingredientToAdd = new RecipeIngredient(
						ingredientID, ingredientName, ingredientQuantity);
				ingredients[ingIdx] = ingredientToAdd;
			}

			return ingredients;
		}

		public Tag[] getTagsFromJson(String jsonString) throws JSONException {
			if (jsonString.equals("")) {
				return null;
			}

			JSONObject json = new JSONObject(jsonString);
			JSONArray tagArray = json.getJSONArray(TAG_ARRAY_KEY);
			Tag[] tags = new Tag[tagArray.length()];

			for (int tagIdx = 0; tagIdx < tagArray.length(); tagIdx++) {
				JSONObject JSONtag = tagArray.getJSONObject(tagIdx);
				Integer tagID = JSONtag.getInt(TAG_ID_KEY);
				String tagName = getTagNameFromID(tagID);
				Tag tagToAdd = new Tag(tagID, tagName);
				tags[tagIdx] = tagToAdd;
			}

			return tags;

		}
	}

}
