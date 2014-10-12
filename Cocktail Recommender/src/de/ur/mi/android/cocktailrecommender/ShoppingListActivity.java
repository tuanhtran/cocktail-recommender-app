package de.ur.mi.android.cocktailrecommender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import de.ur.mi.android.cocktailrecommender.data.Recipe;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeFragment;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeListFragment.OnRecipeSelectedListener;
import de.ur.mi.android.cocktailrecommender.fragments.ShoppingListFragment;

public class ShoppingListActivity extends ActionBarActivity implements
		OnRecipeSelectedListener {

	private ShoppingListFragment shoppingListFragment;
	private RecipeFragment recipeFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_list);

		setShoppingListFragment();
		createRecipeFragment();
	}

	private void createRecipeFragment() {
		recipeFragment = new RecipeFragment();

	}

	private void setShoppingListFragment() {
		shoppingListFragment = new ShoppingListFragment();
		shoppingListFragment.setOnRecipeSelectedListener(this);
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.shopping_list_container, shoppingListFragment);
		transaction.commit();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_nav_menu) {
			Intent openMenu = new Intent(this, MenuActivity.class);
			openMenu.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(openMenu);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRecipeSelected(Recipe recipe) {
		getFragmentManager().executePendingTransactions();
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		recipeFragment.setRecipe(recipe);
		transaction.replace(R.id.shopping_list_container, recipeFragment);
		transaction.addToBackStack(null);
		transaction.commit();

	}

}
