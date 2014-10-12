package de.ur.mi.android.cocktailrecommender.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

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
	private ArrayList<RecipeListEntry> history;
	private ArrayList<RecipeListEntry> favorites;
	private OnSearchResultListener listener;

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

	/*
	 * Returns the instance of CRDatabase. If that instance has not been
	 * initialized yet, it is created first using the application's context.
	 */
	public static CRDatabase getInstance(Context context) {
		if (instance == null) {
			instance = new CRDatabase(context.getApplicationContext());
		}
		return instance;
	}

	public void searchByIngredient(SearchParameter params,
			OnSearchResultListener listener) {
		this.listener = listener;
		new BackgroundTask().execute(params);
		listener.onSearchInitiated();
	}

	public void reactToSearchActivityRebuild(OnSearchResultListener listener) {
		this.listener = listener;
	}

	/*
	 * Creates and returns an ArrayList that contains the RecipeListEntry
	 * objects created from the recipeIDs in the database table Searchresults.
	 */
	public ArrayList<RecipeListEntry> getSearchResults() {
		ArrayList<RecipeListEntry> searchResults = new ArrayList<RecipeListEntry>();

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

				RecipeListEntry result = new RecipeListEntry(recipe, matchRate);
				searchResults.add(result);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return searchResults;
	}

	/*
	 * Creates and returns an ArrayList that contains all the RecipeListEntry
	 * objects created from the data in the database table Cocktails.
	 */
	public ArrayList<RecipeListEntry> getFullRecipeList() {
		ArrayList<RecipeListEntry> recipeList = new ArrayList<RecipeListEntry>();

		Cursor cursor = db.query(DATABASE_TABLE_COCKTAILS, new String[] {
				COCKTAILS_KEY_ID, COCKTAILS_KEY_NAME,
				COCKTAILS_KEY_INGREDIENTS, COCKTAILS_KEY_TAGS,
				COCKTAILS_KEY_PREPARATION }, null, null, null, null,
				COCKTAILS_KEY_NAME);

		if (cursor.moveToFirst()) {
			do {
				Recipe recipeToAdd = getRecipeFromCursor(cursor);
				recipeList.add(new RecipeListEntry(recipeToAdd));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return recipeList;
	}

	/*
	 * Creates and returns an ArrayList that contains all the IngredientType
	 * objects created from the data in the database table Ingredients.
	 */
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
		cursor.close();
		return ingredientList;
	}

	/*
	 * Creates and returns an ArrayList that contains all the Tag objects
	 * created from the data in the database table Tags.
	 */
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
		cursor.close();
		return tagList;
	}

	/*
	 * Creates and returns an ArrayList that contains all the ShoppingList
	 * objects created from the data in the database table ShoppingLists.
	 */
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
					e.printStackTrace();
				}
				ShoppingList list = new ShoppingList(id, name, ingredients);
				shoppingLists.add(list);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return shoppingLists;
	}

	/*
	 * Adds a new shopping list to the database or updates the ingredient data
	 * of an already existing shopping list.
	 */
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
		db.execSQL("vacuum");
	}

	/*
	 * Creates and returns an ArrayList that contains all...
	 */
	public void deleteShoppingList(ShoppingList list) {
		db.delete(DATABASE_TABLE_SHOPPINGLISTS, SHOPPINGLISTS_KEY_ID + "=?",
				new String[] { String.valueOf(list.getId()) });
		db.execSQL("vacuum");
	}

	/*
	 * Creates and returns an ArrayList that contains all...
	 */
	public ArrayList<RecipeListEntry> getFavorites() {
		return favorites;
	}

	/*
	 * Adds a RecipeListEntry to the ArrayList<RecipeListEntry> favorites and
	 * saves the new list to the database, unless the list already contains that
	 * RecipeListEntry
	 */
	public void addToFavorites(RecipeListEntry recipeSR) {
		if (!listAlreadyContainsRecipeListEntry(recipeSR, favorites)) {
			favorites.add(recipeSR);
			saveFavoritesToDB();
		}
	}

	public void removeFromFavorites(RecipeListEntry recipeSR) {
		ArrayList<RecipeListEntry> toRemove = new ArrayList<RecipeListEntry>();
		for (RecipeListEntry rsr : favorites) {
			if (rsr.getRecipe().getRecipeID() == recipeSR.getRecipe()
					.getRecipeID()) {
				toRemove.add(rsr);
			}
		}
		favorites.removeAll(toRemove);
	}

	public ArrayList<RecipeListEntry> getHistory() {
		return history;
	}

	public void addToHistory(RecipeListEntry recipeLE) {
		if (!listAlreadyContainsRecipeListEntry(recipeLE, history)) {
			history.add(0, recipeLE);
			while (history.size() > HISTORY_MAX_SIZE) {
				history.remove(history.size() - 1);
			}
			saveHistoryToDB();
		}
	}

	/*
	 * Creates a new entry in the database table SearchResults, containing the
	 * recipe ID and the match rate of a newly found search result. No opening
	 * or closing of the database necessary, because this method is called from
	 * within a method that opens and closes it.
	 */
	private void setSearchResult(Recipe recipe, int matchRate) {
		String sqlInsert = "INSERT INTO " + DATABASE_TABLE_SEARCHRESULTS + " ("
				+ SEARCHRESULTS_KEY_RECIPEID + ","
				+ SEARCHRESULTS_KEY_MATCH_RATE + ") VALUES ("
				+ recipe.getRecipeID() + "," + matchRate + ");";
		db.execSQL(sqlInsert);
		db.execSQL("vacuum");
	}

	/*
	 * Clears the database table SearchResults.
	 */
	private void clearSearchResults() {
		db.execSQL("delete from " + DATABASE_TABLE_SEARCHRESULTS);
		db.execSQL("vacuum");
	}

	/*
	 * Creates and returns a Recipe object from the data from an entry of the
	 * database table Cocktails which matches the given recipe ID.
	 */
	private Recipe getRecipeFromID(int recipeID) {
		Cursor cursor = db.query(DATABASE_TABLE_COCKTAILS, new String[] {
				COCKTAILS_KEY_ID, COCKTAILS_KEY_NAME,
				COCKTAILS_KEY_INGREDIENTS, COCKTAILS_KEY_TAGS,
				COCKTAILS_KEY_PREPARATION }, COCKTAILS_KEY_ID + "=" + recipeID,
				null, null, null, null);

		cursor.moveToFirst();
		Recipe recipe = getRecipeFromCursor(cursor);
		cursor.close();
		return recipe;
	}

	/*
	 * Returns the value of the column Name of an entry from the database table
	 * Ingredients which matches the given ingredient ID.
	 */
	private String getIngNameFromID(int ingID) {
		Cursor cursor = db.query(DATABASE_TABLE_INGREDIENTS,
				new String[] { INGREDIENTS_KEY_NAME }, INGREDIENTS_KEY_ID + "="
						+ ingID, null, null, null, null);

		cursor.moveToFirst();
		String name = cursor.getString(INGREDIENTS_COLUMN_IDX_NAME - 1);
		cursor.close();
		return name;
	}

	/*
	 * Returns the value of the column Name of an entry from the database table
	 * Tags which matches the given tag ID.
	 */
	private String getTagNameFromID(int tagID) {
		Cursor cursor = db.query(DATABASE_TABLE_TAGS,
				new String[] { TAGS_KEY_NAME }, TAGS_KEY_ID + "=" + tagID,
				null, null, null, null);

		cursor.moveToFirst();
		String name = cursor.getString(TAGS_COLUMN_IDX_NAME - 1);
		cursor.close();
		return name;
	}

	/*
	 * Creates and returns a Recipe object based on the data from the given
	 * cursor.
	 */
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

	/*
	 * Creates and returns an ArrayList that contains all the RecipeListEntry
	 * objects created from the data in the database table Favorites.
	 */
	private ArrayList<RecipeListEntry> getFavoritesFromDB() {
		ArrayList<RecipeListEntry> favos = new ArrayList<RecipeListEntry>();

		Cursor cursor = db.query(DATABASE_TABLE_FAVORITES, new String[] {
				FAVORITES_KEY_ID, FAVORITES_KEY_RECIPEID }, null, null, null,
				null, null);

		if (cursor.moveToFirst()) {
			do {
				Recipe recipe = getRecipeFromID(cursor
						.getInt(FAVORITES_COLUMN_IDX_RECIPEID));
				RecipeListEntry faveToAdd = new RecipeListEntry(recipe);
				favos.add(faveToAdd);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return favos;
	}

	/*
	 * Clears the database table Favorites and adds the recipe IDs of all the
	 * RecipeListEntry objects from the Arraylist favorites.
	 */
	private void saveFavoritesToDB() {
		db.execSQL("delete from " + DATABASE_TABLE_FAVORITES);
		db.execSQL("vacuum");
		for (RecipeListEntry favoriteRecipe : favorites) {
			String sqlInsert = "INSERT INTO " + DATABASE_TABLE_FAVORITES + " ("
					+ FAVORITES_KEY_RECIPEID + ") VALUES ("
					+ favoriteRecipe.getRecipe().getRecipeID() + ");";
			db.execSQL(sqlInsert);
		}
	}

	/*
	 * Creates and returns an ArrayList that contains all the RecipeListEntry
	 * objects created from the data in the database table History.
	 */
	private ArrayList<RecipeListEntry> getHistoryFromDB() {
		ArrayList<RecipeListEntry> hist = new ArrayList<RecipeListEntry>();

		Cursor cursor = db.query(DATABASE_TABLE_HISTORY, new String[] {
				HISTORY_KEY_ID, HISTORY_KEY_RECIPEID }, null, null, null, null,
				null);

		if (cursor.moveToFirst()) {
			do {
				Recipe recipe = getRecipeFromID(cursor
						.getInt(HISTORY_COLUMN_IDX_RECIPEID));
				RecipeListEntry faveToAdd = new RecipeListEntry(recipe);
				hist.add(faveToAdd);
			} while (cursor.moveToNext());
		}
		return hist;
	}

	/*
	 * Clears the database table History and adds the recipe IDs of all the
	 * RecipeListEntry objects from the Arraylist history.
	 */
	private void saveHistoryToDB() {
		db.execSQL("delete from " + DATABASE_TABLE_HISTORY);
		db.execSQL("vacuum");
		for (RecipeListEntry historyRecipe : history) {
			String sqlInsert = "INSERT INTO " + DATABASE_TABLE_HISTORY + " ("
					+ HISTORY_KEY_RECIPEID + ") VALUES ("
					+ historyRecipe.getRecipe().getRecipeID() + ");";
			db.execSQL(sqlInsert);
		}
	}

	/*
	 * Checks if an arraylist already contains an RecipeListEntry object with
	 * the same recipe ID as the given RLE object. Return true if does and false
	 * if it does not.
	 */
	private boolean listAlreadyContainsRecipeListEntry(
			RecipeListEntry recipeLE, ArrayList<RecipeListEntry> list) {
		for (RecipeListEntry rsr : list) {
			if (rsr.getRecipe().getRecipeID() == recipeLE.getRecipe()
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

	private class BackgroundTask extends
			AsyncTask<SearchParameter, Integer, Integer> {

		/*
		 * Starts a search using the SearchEngine and the given SearchParameters
		 * and returns the number of search results.
		 */
		@Override
		protected Integer doInBackground(SearchParameter... params) {
			searchEngine.setSearchParameter(params[0]);
			clearSearchResults();
			return searchEngine.searchByIngredients();
		}

		/*
		 * Informs the listener about the success of the search using the
		 * callback methods defined in the OnSearchResultListener interface-
		 */
		@Override
		protected void onPostExecute(Integer numOfResults) {
			if ((int) numOfResults == 0) {
				listener.onSearchFailed();
			} else {
				listener.onSearchCompleted();
			}
		}
	}

	private class SearchEngine {
		private SearchParameter params;

		public void setSearchParameter(SearchParameter params) {
			this.params = params;
		}

		/*
		 * User defined search parameters are used to search for recipes in form
		 * of a Sqlite query. Where clause of query is generated from parameters
		 */
		public int searchByIngredients() {
			int numOfResults = 0;
			String whereClause = getWhereClause();
			String selectionArgs[] = getSelectionArgs();

			Cursor cursor = db.query(DATABASE_TABLE_COCKTAILS, new String[] {
					COCKTAILS_KEY_ID, COCKTAILS_KEY_INGREDIENTS,
					COCKTAILS_KEY_TAGS }, whereClause, selectionArgs, null,
					null, null);

			if (cursor.moveToFirst()) {
				do {
					Recipe recipe = getRecipeFromID(cursor
							.getInt(COCKTAILS_COLUMN_IDX_ID));

					if ((containsNonSelectedIngs(recipe.getIngredients()) && !params
							.canContainNonSelectedIngs())
							|| (containsNonSelectedTags(recipe.getTags()) && !params
									.canContainNonSelectedTags())) {
						continue;
					}
					int matchRate = determineMatchRate(recipe.getIngredients(),
							recipe.getTags());
					setSearchResult(recipe, matchRate);
					numOfResults++;
				} while (cursor.moveToNext());
			}
			cursor.close();
			return numOfResults;
		}

		private boolean containsNonSelectedIngs(RecipeIngredient[] ingredients) {
			for (RecipeIngredient rIng : ingredients) {
				if (!params.getSelectedIngIDs().contains(rIng.getID())) {
					return true;
				}
			}
			return false;
		}

		private boolean containsNonSelectedTags(Tag[] tags) {
			for (Tag tag : tags) {
				if (!params.getSelectedTagIDs().contains(tag.getTagID())) {
					return true;
				}
			}
			return false;
		}

		private int determineMatchRate(RecipeIngredient[] recipeIngs, Tag[] tags) {
			int rate = 0;
			if (!params.getSelectedIngIDs().isEmpty()) {
				for (int ingIdx = 0; ingIdx < recipeIngs.length; ingIdx++) {
					if (params.getSelectedIngIDs().contains(
							(Integer) recipeIngs[ingIdx].getID())) {
						rate++;
					}
				}
			}
			if (!params.getSelectedTagIDs().isEmpty()) {
				for (int tagIdx = 0; tagIdx < tags.length; tagIdx++) {
					if (params.getSelectedTagIDs().contains(
							(Integer) tags[tagIdx].getTagID())) {
						rate++;
					}
				}
			}
			rate *= 100;
			rate /= (params.getSelectedIngIDs().size() + params
					.getSelectedTagIDs().size());

			return rate;
		}

		private String getWhereClause() {
			String whereClause = "";

			if (!params.getSelectedIngIDs().isEmpty()) {
				whereClause = whereClause + "(";
				for (int ingIdx = 0; ingIdx < params.getSelectedIngIDs().size(); ingIdx++) {
					whereClause = whereClause + COCKTAILS_KEY_INGREDIENTS
							+ " LIKE ?";
					if (ingIdx + 1 != params.getSelectedIngIDs().size()) {
						whereClause = whereClause
								+ getConjunction(params
										.mustContainAllSelectedIngs());
					}
				}
				whereClause = whereClause + ")";
			}
			if ((!params.getSelectedIngIDs().isEmpty())
					&& (!params.getSelectedTagIDs().isEmpty())) {
				whereClause = whereClause + " AND ";
			}

			if (!params.getSelectedTagIDs().isEmpty()) {
				whereClause = whereClause + "(";
				for (int ingIdx = 0; ingIdx < params.getSelectedTagIDs().size(); ingIdx++) {
					whereClause = whereClause + COCKTAILS_KEY_TAGS + " LIKE ?";
					if (ingIdx + 1 != params.getSelectedTagIDs().size()) {
						whereClause = whereClause
								+ getConjunction(params
										.mustContainAllSelectedTags());
					}
				}
				whereClause = whereClause + ")";
			}
			return whereClause;
		}

		private String[] getSelectionArgs() {
			String[] args = new String[params.getSelectedIngIDs().size()
					+ params.getSelectedTagIDs().size()];
			for (int idx = 0; idx < params.getSelectedIngIDs().size(); idx++) {
				args[idx] = "%"
						+ params.getSelectedIngIDs().get(idx).toString() + "%";
			}
			for (int idx = 0; idx < params.getSelectedTagIDs().size(); idx++) {
				args[params.getSelectedIngIDs().size() + idx] = "%"
						+ params.getSelectedTagIDs().get(idx).toString() + "%";
			}
			return args;
		}

		private String getConjunction(boolean boole) {
			if (boole) {
				return " AND ";
			} else {
				return " OR ";
			}
		}

	}

	// Gets appropriate data objects from JSON
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

	public interface OnSearchResultListener {
		public void onSearchInitiated();

		public void onSearchFailed();

		public void onSearchCompleted();
	}
}