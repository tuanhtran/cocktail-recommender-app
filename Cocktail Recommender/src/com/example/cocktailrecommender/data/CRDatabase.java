package com.example.cocktailrecommender.data;

import java.util.ArrayList;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CRDatabase {

	private static final String DATABASE_TABLE_COCKTAILS = "Cocktails";
	private static final String COCKTAILS_KEY_ID = "ID"; // Integer
	private static final String COCKTAILS_KEY_NAME = "Name"; // String
	private static final String COCKTAILS_KEY_INGREDIENTS = "Ingredients"; // (JSON)String
	private static final String COCKTAILS_KEY_TAGS = "Tags"; // (JSON)String
	private static final String COCKTAILS_KEY_PREPARATION = "Preparation"; // String
	private static final int COCKTAILS_COLUMN_ID_IDX = 0;
	private static final int COCKTAILS_COLUMN_NAME_IDX = 1;
	private static final int COCKTAILS_COLUMN_INGREDIENTS_IDX = 2;
	private static final int COCKTAILS_COLUMN_TAGS_IDX = 3;
	private static final int COCKTAILS_COLUMN_PREPARATION_IDX = 4;

	private static final String DATABASE_TABLE_INGREDIENTS = "Ingredients";
	private static final String INGREDIENTS_KEY_ID = "ID"; // Integer
	private static final String INGREDIENTS_KEY_NAME = "Name";// String
	private static final int INGREDIENTS_COLUMN_ID_IDX = 0;
	private static final int INGREDIENTS_COLUMN_NAME_IDX = 1;

	private static final String DATABASE_TABLE_TAGS = "Tags";
	private static final String TAGS_KEY_ID = "ID"; // Integer
	private static final String TAGS_KEY_NAME = "Name";// String
	private static final int TAGS_COLUMN_ID_IDX = 0;
	private static final int TAGS_COLUMN_NAME_IDX = 1;

	private static final String DATABASE_TABLE_FAVORITES = "Favorites";
	private static final String FAVORITES_KEY_ID = "ID"; // Integer
	private static final String FAVORITES_KEY_DRINK_ID = "DringID"; // Integer
	private static final int FAVORITES_COLUMN_ID_IDX = 0;
	private static final int FAVORITES_COLUMN_DRINK_ID_IDX = 1;

	private static final String DATABASE_TABLE_HISTORY = "History";
	private static final String HISTORY_KEY_ID = "ID"; // Integer
	private static final String HISTORY_KEY_DRINK_ID = "DringID"; // Integer
	private static final int HISTORY_COLUMN_ID_IDX = 0;
	private static final int HISTORY_COLUMN_DRINK_ID_IDX = 1;

	private static final String DATABASE_TABLE_SHOPPINGLISTS = "ShoppingLists";
	private static final String SHOPPINGLISTS_KEY_ID = "ID"; // Integer
	private static final String SHOPPINGLISTS_NAME_ID = "List_Name"; // String
	private static final String SHOPPINGLISTS_INGREDIENT_IDS = "IngredientIDs"; // (JSON)String
	private static final int SHOPPINGLISTS_COLUMN_ID_IDX = 0;
	private static final int SHOPPINGLISTS_COLUMN_NAME_IDX = 1;
	private static final int SHOPPINGLISTS_COLUMN_INGRREDIENT_IDS_IDX = 2;

	public static final char ALCOHOLIC_ING_PREFIX = '1';
	public static final char NON_ALCOHOLIC_ING_PREFIX = '2';
	public static final char MISC_ING_PREFIX = '3';

	private CRDatabaseHelper helper;
	private SQLiteDatabase db;

	public CRDatabase(Context context) {
		helper = new CRDatabaseHelper(context);
	}

	public void open() {
		db = helper.getWritableDatabase();
	}

	public void close() {
		db.close();
		helper.close();
	}

	public ArrayList<IngredientType> getIngList() {
		ArrayList<IngredientType> ingredientList = new ArrayList<IngredientType>();

		Cursor cursor = db.query(DATABASE_TABLE_INGREDIENTS, new String[] {
				INGREDIENTS_KEY_ID, INGREDIENTS_KEY_NAME }, null, null, null,
				null, null);

		if (cursor.moveToFirst()) {
			do {
				int iD = cursor.getInt(INGREDIENTS_COLUMN_ID_IDX);
				String name = cursor.getString(INGREDIENTS_COLUMN_NAME_IDX);
				IngredientType ingTypeToAdd = new IngredientType(iD, name);
				ingredientList.add(ingTypeToAdd);
			} while (cursor.moveToNext());
		}
		return ingredientList;
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

}
