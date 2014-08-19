package com.example.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


//Using https://github.com/jgilfelt/android-sqlite-asset-helper library for easier use of pre-created databases
public class RecipeData {
	private static final String DB_NAME = "recipe.db";
	private static final String TABLE_NAME = "Cocktails";
	private static final String NAME_KEY = "Name";
	private static final String ING_KEY = "Ingredients";
	private static final String TAG_KEY = "Tags";
	private static final String PREP_KEY = "Preparation";
	//private static final String SIMILAR_KEY = "Similar";
	
	private RecipeDatabaseHelper helper;
	private SQLiteDatabase db;

	public RecipeData(Context context) {
		helper = new RecipeDatabaseHelper(context);
	}

	public void openDB() {
		db = helper.getReadableDatabase();
	}

	public void closeDB() {
		db.close();
		helper.close();
	}

	

	private class RecipeDatabaseHelper extends SQLiteAssetHelper {

		
		private static final int DB_VERSION = 1;

		public RecipeDatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}

		

	}

	public Cursor selectByName(String name){
		String[] columns = new String[]{NAME_KEY,ING_KEY,TAG_KEY,PREP_KEY};
		String selection = NAME_KEY+" LIKE '%"+name+"%'";
		Cursor cursor = db.query(TABLE_NAME,columns,selection,null,null,null,null);
		return cursor;
		
	}
	
	public Cursor selectByTags(String[] tags){
		String[] columns = new String[]{NAME_KEY,ING_KEY,TAG_KEY,PREP_KEY};
		String selection = TAG_KEY+" IN ";
		for (int i = 0; i < tags.length; i++){
			//Does a "where TAG_KEY in ('tags[0]',...,'tags[n]')" statement return the desired rows?
		}
		Cursor cursor = db.query(TABLE_NAME,columns,selection,tags,null,null,null);
		return cursor;
	}
}
