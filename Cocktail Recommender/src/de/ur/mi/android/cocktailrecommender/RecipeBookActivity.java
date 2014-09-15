package de.ur.mi.android.cocktailrecommender;

import java.util.ArrayList;

import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.CRDatabase;
import de.ur.mi.android.cocktailrecommender.data.Recipe;
import de.ur.mi.android.cocktailrecommender.data.RecipeSearchResult;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeFragment;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeFragment.OnFlingListener;
import de.ur.mi.android.cocktailrecommender.fragments.ResultListFragment;
import de.ur.mi.android.cocktailrecommender.fragments.ResultListFragment.OnRecipeSelectedListener;
import android.app.FragmentTransaction;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Surface;
import android.widget.TextView;

public class RecipeBookActivity extends ActionBarActivity implements
		OnRecipeSelectedListener, OnFlingListener {
	
	public static final String RESULT_LIST_KEY = "ResultList";
	public static final String RESULT_LIST_BUNDLE_KEY = "ResultListBundle";

	private RecipeFragment recipeFragment;
	private ResultListFragment resultListFragment;
	private ArrayList<RecipeSearchResult> resultList;
	private int recipePageIdx = 0;
	private boolean onRecipePage = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_book);
		initData();
		initUIFragments();
	}

	private void initData() {
		resultList = CRDatabase.getInstance(this).getSearchResults();
	}

	private void initUIFragments() {
		resultListFragment = new ResultListFragment(resultList);
		resultListFragment.setOnRecipeSelectedListener(this);
		recipeFragment = new RecipeFragment();
		recipeFragment.setOnFlingListener(this);
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.add(R.id.recipe_book_container_main, resultListFragment);
		transaction.commit();
	}

	@Override
	public void onRecipeSelected(Recipe recipe) {
		recipePageIdx = findPage(recipe);
		recipeFragment.setRecipe(recipe);
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		if (isInLandscapeMode()) {
			if (!(recipeFragment.isAdded())) {
				findViewById(R.id.recipe_page_temp).setVisibility(
						TextView.INVISIBLE);
				transaction
						.add(R.id.recipe_book_container_side, recipeFragment);
			} else {
				recipeFragment.updateData();
			}
		} else {
			transaction
					.replace(R.id.recipe_book_container_main, recipeFragment);
			transaction.addToBackStack(null);
			onRecipePage = true;
		}
		transaction.commit();
	}

	private int findPage(Recipe recipe) {
		for (int idx = 0; idx < resultList.size(); idx++) {
			if (resultList.get(idx).getRecipe().equals(recipe)) {
				return idx;
			}
		}
		return -1;
	}

	private boolean isInLandscapeMode() {
		return (getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90 || getWindowManager()
				.getDefaultDisplay().getRotation() == Surface.ROTATION_270);
	}


	@Override
	public void onBackPressed() {
		if (onRecipePage) {
			getFragmentManager().popBackStackImmediate();
			onRecipePage = false;
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onRightToLeftFling() {
		if (!isInLandscapeMode()) {
			recipePageIdx = ((recipePageIdx - 1) + resultList.size())  % resultList.size();
			recipeFragment.setRecipe(resultList.get(recipePageIdx).getRecipe());
			recipeFragment.updateData();
		}
	}

	@Override
	public void onLeftToRightFling() {
		if (!isInLandscapeMode()) {
			recipePageIdx = (recipePageIdx + 1) % resultList.size();
			recipeFragment.setRecipe(resultList.get(recipePageIdx).getRecipe());
			recipeFragment.updateData();
		}
	}
	
	private void initTestData() {
		CRDatabase db = new CRDatabase(this);
		db.open();
		resultList = new ArrayList<RecipeSearchResult>();
		ArrayList<Recipe> recipes = db.getFullRecipeList();
		for (Recipe r : recipes) {
			resultList.add(new RecipeSearchResult(r, 0));
		}
		db.close();
	}

}
