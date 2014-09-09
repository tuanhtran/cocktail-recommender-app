package de.ur.mi.android.cocktailrecommender.data;

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
	
	//If all of the tags have to be present set strict to true
	public Cursor selectByTags(String[] tags, boolean strict){
		String[] columns = new String[]{NAME_KEY,ING_KEY,TAG_KEY,PREP_KEY};
		String selection = TAG_KEY+" LIKE '";
		if(strict){
			for (int i = 0; i < tags.length; i++){
				selection += "%"+tags[i];
			}		
			selection += "%'";
		} else{
			for (int i = 0; i < tags.length-1; i++){
				selection += "%"+tags[i]+"%' OR "+TAG_KEY+" LIKE '";
			}
			selection += "%"+tags[tags.length-1]+"%'";
		}
		Cursor cursor = db.query(TABLE_NAME,columns,selection,null,null,null,null);
		return cursor;
	}
	
	//If all of the ingredients have to be present set strict to true
	public Cursor selectByIngredients(String[] ingredients, boolean strict){
		String[] columns = new String[]{NAME_KEY,ING_KEY,TAG_KEY,PREP_KEY};
		String selection = ING_KEY+" LIKE '";
		if(strict){
			for (int i = 0; i < ingredients.length; i++){
				selection += "%"+ingredients[i];
			}		
			selection += "%'";
		} else{
			for (int i = 0; i < ingredients.length-1; i++){
				selection += "%"+ingredients[i]+"%' OR "+TAG_KEY+" LIKE '";
			}
			selection += "%"+ingredients[ingredients.length-1]+"%'";
		}
		Cursor cursor = db.query(TABLE_NAME,columns,selection,null,null,null,null);
		return cursor;
	}
}
