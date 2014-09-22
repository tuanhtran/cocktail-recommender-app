package de.ur.mi.android.cocktailrecommender.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class CRDatabase {

	private static CRDatabase instance = null;

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
	private static final String FAVORITES_KEY_RECIPEID = "DrinkID"; // Integer
	private static final int FAVORITES_COLUMN_IDX_ID = 0;
	private static final int FAVORITES_COLUMN_IDX_RECIPEID = 1;

	private static final String DATABASE_TABLE_HISTORY = "History";
	private static final String HISTORY_KEY_ID = "ID"; // Integer
	private static final String HISTORY_KEY_RECIPEID = "DrinkID"; // Integer
	private static final int HISTORY_COLUMN_IDX_ID = 0;
	private static final int HISTORY_COLUMN_IDX_RECIPEID = 1;
	public static final int HISTORY_MAX_SIZE = 10;

	private static final String DATABASE_TABLE_SHOPPINGLISTS = "ShoppingLists";
	private static final String SHOPPINGLISTS_KEY_ID = "ID"; // Integer
	private static final String SHOPPINGLISTS_KEY_NAME = "List_Name"; // String
	private static final String SHOPPINGLISTS_KEY_INGREDIENTIDS = "IngredientIDs"; // (JSON)String
	private static final int SHOPPINGLISTS_COLUMN_IDX_ID = 0;
	private static final int SHOPPINGLISTS_COLUMN_IDX_NAME = 1;
	private static final int SHOPPINGLISTS_COLUMN_IDX_INGRREDIENTIDS = 2;

	private static final String DATABASE_TABLE_SEARCHRESULTS = "SearchResults";
	private static final String SEARCHRESULTS_KEY_ID = "ID"; // Integer
	private static final String SEARCHRESULTS_KEY_RECIPEID = "RecipeID"; // Integer
	private static final String SEARCHRESULTS_KEY_MATCH_RATE = "MatchRate"; // Integer
	private static final int SEARCHRESULTS_COLUMN_IDX_ID = 0;
	private static final int SEARCHRESULTS_COLUMN_IDX_RECIPEID = 1;
	private static final int SEARCHRESULTS_COLUMN_IDX_MATCHRATE = 2;

	public static final char ALCOHOLIC_ING_PREFIX = '1';
	public static final char NON_ALCOHOLIC_ING_PREFIX = '2';
	public static final char MISC_ING_PREFIX = '3';

	public static final int SEARCH_TYPE_FAVORITES = 0;
	public static final int SEARCH_TYPE_HISTORY = 1;

	private static CRDatabaseHelper helper;
	private static SQLiteDatabase db;
	private JSONDataParser jsonDataParser;
	private SearchEngine searchEngine;
	private ArrayList<RecipeSearchResult> history;
	private ArrayList<RecipeSearchResult> favorites;

	public CRDatabase(Context context) {
		helper = new CRDatabaseHelper(context);
		jsonDataParser = new JSONDataParser();
		searchEngine = new SearchEngine();
	}

	public void open() {
		db = helper.getWritableDatabase();
		history = getHistoryFromDB();
		favorites = getFavoritesFromDB();
	}

	public void close() {
		saveHistoryToDB();
		saveFavoritesToDB();
		db.close();
		helper.close();
	}

	public static CRDatabase getInstance(Context context) {
		if (instance == null) {
			instance = new CRDatabase(context);
		}
		return instance;
	}

	public boolean searchByIngredient(ArrayList<Integer> selectedIngIDs,
			ArrayList<Integer> selectedTagIDs,
			boolean mustContainAllSelectedIngs,
			boolean canContainNonSelectedIngs) {
		searchEngine.setSearchParameters(selectedIngIDs, selectedTagIDs,
				mustContainAllSelectedIngs, canContainNonSelectedIngs);
		ArrayList<RecipeSearchResult> results = searchEngine
				.searchByIngredients();
		if (results.size() > 0) {
			setSearchResults(results);
			return true;
		}
		return false;
	}

	public ArrayList<RecipeSearchResult> getSearchResults() {
		ArrayList<RecipeSearchResult> searchResults = new ArrayList<RecipeSearchResult>();

		Cursor cursor = db.query(DATABASE_TABLE_SEARCHRESULTS, new String[] {
				SEARCHRESULTS_KEY_RECIPEID, SEARCHRESULTS_KEY_MATCH_RATE },
				null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				int recipeID = cursor
						.getInt(SEARCHRESULTS_COLUMN_IDX_RECIPEID - 1);
				Recipe recipe = getRecipeFromID(recipeID);
				int matchRate = cursor
						.getInt(SEARCHRESULTS_COLUMN_IDX_MATCHRATE - 1);

				RecipeSearchResult result = new RecipeSearchResult(recipe,
						matchRate);
				searchResults.add(result);
			} while (cursor.moveToNext());
		}
		return searchResults;
	}

	public ArrayList<Recipe> getFullRecipeList() {
		ArrayList<Recipe> recipeList = new ArrayList<Recipe>();

		Cursor cursor = db.query(DATABASE_TABLE_COCKTAILS, new String[] {
				COCKTAILS_KEY_ID, COCKTAILS_KEY_NAME,
				COCKTAILS_KEY_INGREDIENTS, COCKTAILS_KEY_TAGS,
				COCKTAILS_KEY_PREPARATION }, null, null, null, null,
				COCKTAILS_KEY_NAME);

		if (cursor.moveToFirst()) {
			do {
				Recipe recipeToAdd = getRecipeFromCursor(cursor);
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

	public ArrayList<ShoppingList> getAllShoppingLists() {
		ArrayList<ShoppingList> shoppingLists = new ArrayList<ShoppingList>();

		Cursor cursor = db
				.query(DATABASE_TABLE_SHOPPINGLISTS, new String[] {
						SHOPPINGLISTS_KEY_ID, SHOPPINGLISTS_KEY_NAME,
						SHOPPINGLISTS_KEY_INGREDIENTIDS }, null, null, null,
						null, null);
		if (cursor.moveToFirst()) {
			do {
				int id = cursor.getInt(SHOPPINGLISTS_COLUMN_IDX_ID);
				String name = cursor.getString(SHOPPINGLISTS_COLUMN_IDX_NAME);
				RecipeIngredient[] ingredients = null;
				try {
					ingredients = jsonDataParser
							.getIngredientsFromJson(cursor
									.getString(SHOPPINGLISTS_COLUMN_IDX_INGRREDIENTIDS));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ShoppingList list = new ShoppingList(id, name, ingredients);
				shoppingLists.add(list);
			} while (cursor.moveToNext());
		}
		return shoppingLists;
	}

	public void addShoppingList(ShoppingList shoppingList, boolean isNewList) {
		ContentValues values = new ContentValues();

		if (isNewList) {
			String ingredientJSONString = jsonDataParser
					.getIngredientJSONString(shoppingList.getIngredients());
			values.put(SHOPPINGLISTS_KEY_NAME, shoppingList.getListName());
			values.put(SHOPPINGLISTS_KEY_INGREDIENTIDS, ingredientJSONString);
			db.insert(DATABASE_TABLE_SHOPPINGLISTS, null, values);
		} else {
			values.put(SHOPPINGLISTS_KEY_INGREDIENTIDS, jsonDataParser
					.getIngredientJSONString(shoppingList.getIngredients()));
			db.update(DATABASE_TABLE_SHOPPINGLISTS, values,
					SHOPPINGLISTS_KEY_ID + "=?",
					new String[] { String.valueOf(shoppingList.getId()) });
		}
	}

	public void deleteShoppingList(ShoppingList list) {
		db.delete(DATABASE_TABLE_SHOPPINGLISTS, SHOPPINGLISTS_KEY_ID + "=?",
				new String[] { String.valueOf(list.getId()) });

	}

	public ArrayList<RecipeSearchResult> getFavorites() {
		return favorites;
	}

	public void addToFavorites(RecipeSearchResult recipeSR) {
		if (!listAlreadyContainsRecipeSR(recipeSR, favorites)) {
			favorites.add(recipeSR);
		}
	}

	public void removeFromFavorites(RecipeSearchResult recipeSR) {
		for (RecipeSearchResult rsr : favorites) {
			if (rsr.getRecipe().getRecipeID() == recipeSR.getRecipe()
					.getRecipeID()) {
				favorites.remove(rsr);
			}
		}
	}

	public ArrayList<RecipeSearchResult> getHistory() {
		return history;
	}

	public void addToHistory(RecipeSearchResult recipeSR) {
		if (!listAlreadyContainsRecipeSR(recipeSR, history)) {
			history.add(0, recipeSR);
			while (history.size() > HISTORY_MAX_SIZE) {
				history.remove(history.size() - 1);
			}
		}
	}

	private void setSearchResults(ArrayList<RecipeSearchResult> results) {
		if ((results.size() == 0) || (results == null)) {
			return;
		}
		db.execSQL("delete from " + DATABASE_TABLE_SEARCHRESULTS);
		db.execSQL("vacuum");
		for (RecipeSearchResult searchResult : results) {
			String sqlInsert = "INSERT INTO " + DATABASE_TABLE_SEARCHRESULTS
					+ " (" + SEARCHRESULTS_KEY_RECIPEID + ","
					+ SEARCHRESULTS_KEY_MATCH_RATE + ") VALUES ("
					+ searchResult.getRecipe().getRecipeID() + ","
					+ searchResult.getMatchRate() + ");";
			db.execSQL(sqlInsert);
		}
	}

	private Recipe getRecipeFromID(int recipeID) {
		Cursor cursor = db.query(DATABASE_TABLE_COCKTAILS, new String[] {
				COCKTAILS_KEY_ID, COCKTAILS_KEY_NAME,
				COCKTAILS_KEY_INGREDIENTS, COCKTAILS_KEY_TAGS,
				COCKTAILS_KEY_PREPARATION }, COCKTAILS_KEY_ID + "=" + recipeID,
				null, null, null, null);
		cursor.moveToFirst();
		return getRecipeFromCursor(cursor);
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

	private Recipe getRecipeFromCursor(Cursor cursor) {
		int recipeID = cursor.getInt(COCKTAILS_COLUMN_IDX_ID);
		String name = cursor.getString(COCKTAILS_COLUMN_IDX_NAME);

		String ingredientsJsonString = cursor
				.getString(COCKTAILS_COLUMN_IDX_INGREDIENTS);
		String tagsJsonString = cursor.getString(COCKTAILS_COLUMN_IDX_TAGS);

		RecipeIngredient[] ingredients = null;
		Tag[] tags = null;
		try {
			ingredients = jsonDataParser
					.getIngredientsFromJson(ingredientsJsonString);
			tags = jsonDataParser.getTagsFromJson(tagsJsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String preparation = cursor.getString(COCKTAILS_COLUMN_IDX_PREPARATION);

		Recipe recipe = new Recipe(recipeID, name, ingredients, tags,
				preparation);
		return recipe;
	}

	private ArrayList<RecipeSearchResult> getFavoritesFromDB() {
		ArrayList<RecipeSearchResult> favos = new ArrayList<RecipeSearchResult>();

		Cursor cursor = db.query(DATABASE_TABLE_FAVORITES, new String[] {
				FAVORITES_KEY_ID, FAVORITES_KEY_RECIPEID }, null, null, null,
				null, null);
		if (cursor.moveToFirst()) {
			do {
				Recipe recipe = getRecipeFromID(cursor
						.getInt(FAVORITES_COLUMN_IDX_RECIPEID));
				RecipeSearchResult faveToAdd = new RecipeSearchResult(recipe);
				favos.add(faveToAdd);
			} while (cursor.moveToNext());
		}
		return favos;
	}

	private void saveFavoritesToDB() {
		db.execSQL("delete from " + DATABASE_TABLE_FAVORITES);
		db.execSQL("vacuum");
		for (RecipeSearchResult favoriteRecipe : favorites) {
			String sqlInsert = "INSERT INTO " + DATABASE_TABLE_FAVORITES + " ("
					+ FAVORITES_KEY_RECIPEID + ") VALUES ("
					+ favoriteRecipe.getRecipe().getRecipeID() + ");";
			db.execSQL(sqlInsert);
		}
	}

	private ArrayList<RecipeSearchResult> getHistoryFromDB() {
		ArrayList<RecipeSearchResult> hist = new ArrayList<RecipeSearchResult>();

		Cursor cursor = db.query(DATABASE_TABLE_HISTORY, new String[] {
				HISTORY_KEY_ID, HISTORY_KEY_RECIPEID }, null, null, null, null,
				null);
		if (cursor.moveToFirst()) {
			do {
				Recipe recipe = getRecipeFromID(cursor
						.getInt(HISTORY_COLUMN_IDX_RECIPEID));
				RecipeSearchResult faveToAdd = new RecipeSearchResult(recipe);
				hist.add(faveToAdd);
			} while (cursor.moveToNext());
		}
		return hist;
	}

	private void saveHistoryToDB() {
		db.execSQL("delete from " + DATABASE_TABLE_HISTORY);
		db.execSQL("vacuum");
		for (RecipeSearchResult historyRecipe : history) {
			String sqlInsert = "INSERT INTO " + DATABASE_TABLE_HISTORY + " ("
					+ HISTORY_KEY_RECIPEID + ") VALUES ("
					+ historyRecipe.getRecipe().getRecipeID() + ");";
			db.execSQL(sqlInsert);
		}
	}

	private boolean listAlreadyContainsRecipeSR(RecipeSearchResult recipeSR,
			ArrayList<RecipeSearchResult> list) {
		for (RecipeSearchResult rsr : list) {
			if (rsr.getRecipe().getRecipeID() == recipeSR.getRecipe()
					.getRecipeID()) {
				return true;
			}
		}
		return false;
	}

	public static class CRDatabaseHelper extends SQLiteAssetHelper {
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

		public String getIngredientJSONString(RecipeIngredient[] ingredients) {
			String jsonString = "{\"" + ING_ARRAY_KEY + "\":[";
			for (RecipeIngredient ingredient : ingredients) {
				jsonString = jsonString + "{\"" + ING_ID_KEY + "\":"
						+ ingredient.getID() + ", \"" + ING_QUANTITY_KEY
						+ "\":\"" + ingredient.getQuantity() + "\"},";
			}
			jsonString = jsonString.substring(0, jsonString.length() - 1)
					+ "]}";
			return jsonString;
		}
	}

	private class SearchEngine {
		private boolean mustContainAllSelectedIngs = false;
		private boolean canContainNonSelectedIngs = false;
		private ArrayList<Integer> selectedIngIDs = new ArrayList<Integer>();
		private ArrayList<Integer> selectedTagIDs = new ArrayList<Integer>();

		public void setSearchParameters(ArrayList<Integer> selectedIngIDs,
				ArrayList<Integer> selectedTagIDs,
				boolean mustContainAllSelectedIngs,
				boolean canContainNonSelectedIngs) {
			this.selectedIngIDs.addAll(selectedIngIDs);
			this.selectedTagIDs.addAll(selectedTagIDs);
			this.mustContainAllSelectedIngs = mustContainAllSelectedIngs;
			this.canContainNonSelectedIngs = canContainNonSelectedIngs;
		}

		public ArrayList<RecipeSearchResult> searchByIngredients() {
			ArrayList<RecipeSearchResult> searchResults = new ArrayList<RecipeSearchResult>();

			String searchTerm = getSearchTerm();

			Cursor cursor = db.query(DATABASE_TABLE_COCKTAILS, new String[] {
					COCKTAILS_KEY_ID, COCKTAILS_KEY_INGREDIENTS,
					COCKTAILS_KEY_TAGS }, searchTerm, null, null, null, null);

			if (cursor.moveToFirst()) {
				do {
					Recipe recipe = getRecipeFromID(cursor
							.getInt(COCKTAILS_COLUMN_IDX_ID));

					int matchRate = determineMatchRate(recipe.getIngredients(),
							selectedIngIDs);
					RecipeSearchResult result = new RecipeSearchResult(recipe,
							matchRate);
					searchResults.add(result);

				} while (cursor.moveToNext());
			}
			this.selectedIngIDs.clear();
			this.selectedTagIDs.clear();
			return searchResults;
		}

		private int determineMatchRate(RecipeIngredient[] recipeIngs,
				ArrayList<Integer> selectedIngIDs) {
			int rate = 0;
			for (int ingIdx = 0; ingIdx < recipeIngs.length; ingIdx++) {
				if (selectedIngIDs.contains((Integer) recipeIngs[ingIdx]
						.getID())) {
					rate++;
				}
			}
			rate *= 100;
			rate /= selectedIngIDs.size();
			return rate;
		}

		private String getSearchTerm() {
			String term = "";
			for (int ingIdx = 0; ingIdx < selectedIngIDs.size(); ingIdx++) {
				term = term + COCKTAILS_KEY_INGREDIENTS + " LIKE " + "'%"
						+ selectedIngIDs.get(ingIdx) + "%'";
				if (ingIdx + 1 != selectedIngIDs.size()) {
					term = term + getConjunction();
				}
			}
			if ((!selectedIngIDs.isEmpty()) && (!selectedTagIDs.isEmpty())) {
				// term = term + " AND ";
			}

			return term;
		}

		private String getConjunction() {
			if (mustContainAllSelectedIngs) {
				return " AND ";
			} else {
				return " OR ";
			}
		}

	}

}
