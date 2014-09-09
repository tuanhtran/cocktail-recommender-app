package com.example.cocktailrecommender;

import java.util.ArrayList;

import com.example.cocktailrecommender.data.CRDatabase;
import com.example.cocktailrecommender.data.Recipe;
import com.example.cocktailrecommender.data.SearchRecipeResult;
import com.example.cocktailrecommender.fragments.RecipeFragment;
import com.example.cocktailrecommender.fragments.ResultListFragment;
import com.example.cocktailrecommender.fragments.ResultListFragment.OnRecipeSelectedListener;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Surface;
import android.widget.TextView;

public class RecipeBookActivity extends ActionBarActivity implements
		OnRecipeSelectedListener {
	private FragmentManager manager;
	private RecipeFragment recipeFragment;
	private ResultListFragment resultListFragment;
	private ArrayList<SearchRecipeResult> resultList;
	private TextView tempText;
	private boolean onRecipePage = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_book);
		initData();
		initUI();
		resultListFragment.setOnRecipeSelectedListener(this);
	}

	private void initUI() {
		tempText = (TextView) findViewById(R.id.recipe_page_temp);

		resultListFragment = new ResultListFragment(resultList);
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.recipe_book_container_main, resultListFragment);
		transaction.commit();
	}

	@Override
	public void onRecipeSelected(Recipe recipe) {
		recipeFragment = null;
		recipeFragment = new RecipeFragment(recipe);
		manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		if (getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90
				|| getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_270) {
			tempText.setVisibility(TextView.INVISIBLE);
			transaction
					.replace(R.id.recipe_book_container_side, recipeFragment);
		} else {
			transaction
					.replace(R.id.recipe_book_container_main, recipeFragment);
			transaction.addToBackStack(null);
			onRecipePage = true;
		}
		transaction.commit();
	}

	private void initData() {
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
			manager.popBackStackImmediate();
			onRecipePage = false;
		} else {
			super.onBackPressed();
		}
	}

}
