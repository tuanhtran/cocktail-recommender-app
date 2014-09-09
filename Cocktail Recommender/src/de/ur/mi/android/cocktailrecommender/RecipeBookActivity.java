package de.ur.mi.android.cocktailrecommender;

import java.util.ArrayList;

import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.CRDatabase;
import de.ur.mi.android.cocktailrecommender.data.Recipe;
import de.ur.mi.android.cocktailrecommender.data.SearchRecipeResult;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeFragment;
import de.ur.mi.android.cocktailrecommender.fragments.ResultListFragment;
import de.ur.mi.android.cocktailrecommender.fragments.ResultListFragment.OnRecipeSelectedListener;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Surface;
import android.widget.TextView;

public class RecipeBookActivity extends ActionBarActivity implements
		OnRecipeSelectedListener {
	private RecipeFragment recipeFragment;
	private ResultListFragment resultListFragment;
	private ArrayList<SearchRecipeResult> resultList;
	private boolean onRecipePage = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_book);
		initTestData();
		// initData();
		initUIFragments();
	}

	private void initUIFragments() {
		resultListFragment = new ResultListFragment(resultList);
		resultListFragment.setOnRecipeSelectedListener(this);
		recipeFragment = new RecipeFragment();
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.add(R.id.recipe_book_container_main, resultListFragment);
		transaction.commit();
	}

	@Override
	public void onRecipeSelected(Recipe recipe) {
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

	private boolean isInLandscapeMode() {
		return (getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90 || getWindowManager()
				.getDefaultDisplay().getRotation() == Surface.ROTATION_270);
	}

	private void initTestData() {
		CRDatabase db = new CRDatabase(this);
		db.open();
		resultList = new ArrayList<SearchRecipeResult>();
		ArrayList<Recipe> recipes = db.getFullRecipeList();
		for (Recipe r : recipes) {
			resultList.add(new SearchRecipeResult(r, 0));
		}
		db.close();
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

}
