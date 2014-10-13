package de.ur.mi.android.cocktailrecommender;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import de.ur.mi.android.cocktailrecommender.data.CRDatabase;
import de.ur.mi.android.cocktailrecommender.data.CocktailRecommenderValues;
import de.ur.mi.android.cocktailrecommender.data.Recipe;
import de.ur.mi.android.cocktailrecommender.data.RecipeIngredient;
import de.ur.mi.android.cocktailrecommender.data.RecipeListEntry;
import de.ur.mi.android.cocktailrecommender.data.ShoppingList;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeFragment;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeFragment.OnFlingListener;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeFragment.OnShoppingListAddListener;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeListFragment;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeListFragment.OnRecipeSelectedListener;

/*
 * Activity displays various lists (with RecipeListFragments) of recipes:
 * All recipes, the search results, favorites and a history of seen recipes.
 * Central navigation is the action bar.
 * In portrait mode the activity replaces the RecipeListFragments with RecipeFragments when a list item is selected.
 * In landscape mode the layout displays both fragments side by side.
 */

public class RecipeBookActivity extends ActionBarActivity implements
		OnRecipeSelectedListener, OnShoppingListAddListener, OnFlingListener {

	private RecipeFragment recipeFragment;
	private RecipeListFragment searchResultListFragment;
	private RecipeListFragment historyListFragment;
	private RecipeListFragment favsListFragment;
	private RecipeListFragment allRecipesFragment;
	private ArrayList<RecipeListEntry> searchResultList;
	private ArrayList<RecipeListEntry> allRecipes;
	private ArrayList<RecipeListEntry> favList;
	private ArrayList<RecipeListEntry> historyList;
	private ActionBar actionBar;
	private RecipeIngredient[] selectedIngredients;
	private AlertDialog.Builder alertDialogBuilder;
	private Toast toast;
	private int recipePageIdx = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_book);

		initData();
		initUIFragments();
		setActionBarTabs();
		initDialog();

		if (savedInstanceState != null) {
			int index = savedInstanceState.getInt("index");
			getActionBar().setSelectedNavigationItem(index);
		}

	}

	/*
	 * Put current tab into bundle to select correct tab after orientation
	 * change
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		int currentTabIdx = getActionBar().getSelectedNavigationIndex();
		outState.putInt("index", currentTabIdx);
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
		if (id == R.id.action_about) {
			AlertDialog.Builder aboutAlert = new AlertDialog.Builder(this);
			aboutAlert.setTitle(R.string.about_dialog_title);
			aboutAlert.setMessage(R.string.about_dialog_message);
			aboutAlert.create();
			aboutAlert.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initData() {
		searchResultList = CRDatabase.getInstance(this).getSearchResults();
		allRecipes = CRDatabase.getInstance(this).getFullRecipeList();
		favList = CRDatabase.getInstance(this).getFavorites();
		historyList = CRDatabase.getInstance(this).getHistory();
	}

	private void initUIFragments() {
		searchResultListFragment = new RecipeListFragment(searchResultList);
		searchResultListFragment.setOnRecipeSelectedListener(this);
		historyListFragment = new RecipeListFragment(historyList);
		historyListFragment.setOnRecipeSelectedListener(this);
		favsListFragment = new RecipeListFragment(favList, true);
		favsListFragment.setOnRecipeSelectedListener(this);
		allRecipesFragment = new RecipeListFragment(allRecipes);
		allRecipesFragment.setOnRecipeSelectedListener(this);
		recipeFragment = new RecipeFragment();
		recipeFragment.setOnFlingListener(this);
		recipeFragment.setOnShoppingListAddListener(this);
		recipeFragment.setOnFavStatusChangedListener(favsListFragment);

	}

	// Correct tab to display is chosen through the calling intent of this
	// activity
	private void setActionBarTabs() {
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.Tab searchResultTab = actionBar.newTab().setText(
				R.string.search_result_tab_name);
		ActionBar.Tab historyTab = actionBar.newTab().setText(
				R.string.history_tab_name);
		ActionBar.Tab favsTab = actionBar.newTab().setText(
				R.string.favs_tab_name);
		ActionBar.Tab allRecipesTab = actionBar.newTab().setText(
				R.string.all_recipes_tab_name);
		searchResultTab.setTabListener(new RecipeBookTabListener(
				searchResultListFragment));
		historyTab
				.setTabListener(new RecipeBookTabListener(historyListFragment));
		favsTab.setTabListener(new RecipeBookTabListener(favsListFragment));
		allRecipesTab.setTabListener(new RecipeBookTabListener(
				allRecipesFragment));
		actionBar.addTab(allRecipesTab);
		actionBar.addTab(searchResultTab);
		actionBar.addTab(favsTab);
		actionBar.addTab(historyTab);

		switch (getIntent().getExtras().getInt(
				CocktailRecommenderValues.FRAGMENT_TO_DISPLAY)) {
		case CocktailRecommenderValues.ALL_RECIPES:
			actionBar.selectTab(allRecipesTab);
			break;
		case CocktailRecommenderValues.SEARCH_RESULTS:
			actionBar.selectTab(searchResultTab);
			break;
		case CocktailRecommenderValues.FAV_LIST:
			actionBar.selectTab(favsTab);
			break;
		case CocktailRecommenderValues.HISTORY_LIST:
			actionBar.selectTab(historyTab);
			break;
		default:
			actionBar.selectTab(allRecipesTab);
			break;
		}
	}

	/*
	 * Standard dialog that appears after the shopping list creation button is
	 * pressed. Two options: Create new list, or add to existing list. If no
	 * ingredient was selected before pressing the button the user will get an
	 * alert. Is called at setup and every canceled dialog to return to this
	 * standard dialog.
	 */
	private void initDialog() {
		final int OPTION_ONE = 0;
		final int OPTION_TWO = 1;
		if (toast == null)
			toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		CharSequence[] options = {
				getResources().getString(
						R.string.shopping_list_creation_dialog_option_one),
				getResources().getString(
						R.string.shopping_list_creation_dialog_option_two) };
		alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setNegativeButton(R.string.generic_cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				});
		alertDialogBuilder
				.setTitle(R.string.shopping_list_creation_dialog_title);
		alertDialogBuilder.setItems(options,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int item) {
						switch (item) {
						case OPTION_ONE:
							if (selectedIngredients == null) {
								break;
							}
							createNewShoppingList();
							dialog.dismiss();
							break;
						case OPTION_TWO:
							if (selectedIngredients == null) {
								break;
							}
							addToExistingShoppingList();
							dialog.dismiss();
							break;
						default:
							// wrong position index of OPTION_ONE and/or
							// OPTION_TWO
						}
					}
				});
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.create();
	}

	// Dialog: shows a list of existing shopping lists to add ingredients to.
	protected void addToExistingShoppingList() {
		final boolean isNewList = false;

		final ArrayList<ShoppingList> shoppingLists = CRDatabase.getInstance(
				this).getAllShoppingLists();

		CharSequence[] shoppingListNames = new CharSequence[shoppingLists
				.size()];
		for (int listIdx = 0; listIdx < shoppingLists.size(); listIdx++) {
			shoppingListNames[listIdx] = shoppingLists.get(listIdx)
					.getListName();
		}
		alertDialogBuilder
				.setTitle(R.string.shopping_list_creation_dialog_selection_list);
		if (shoppingLists.size() < 1) {
			alertDialogBuilder
					.setMessage(R.string.shopping_list_creation_dialog_no_lists);
		} else {
			alertDialogBuilder.setItems(shoppingListNames,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							ShoppingList modifiedList = modifyList(shoppingLists
									.get(which));

							CRDatabase.getInstance(RecipeBookActivity.this)
									.addShoppingList(modifiedList, isNewList);
							toast.setText(getResources().getString(
									R.string.shopping_list_added_to_existing));
							toast.show();
							initDialog();
						}

						// Prevents duplicate entries in the shopping list
						private ShoppingList modifyList(
								ShoppingList shoppingList) {
							boolean isNotDuplicate = false;
							RecipeIngredient[] existingIngList = shoppingList
									.getIngredients();
							ArrayList<RecipeIngredient> tempIngList = new ArrayList<RecipeIngredient>(
									Arrays.asList(existingIngList));
							for (RecipeIngredient selIngredient : selectedIngredients) {
								isNotDuplicate = true;
								for (RecipeIngredient exIngredient : existingIngList) {
									if (exIngredient.compareTo(selIngredient) == 0) {
										isNotDuplicate = false;
										break;
									}
								}
								if (isNotDuplicate) {
									tempIngList.add(selIngredient);
								}
							}
							shoppingList.setIngredients(tempIngList
									.toArray(new RecipeIngredient[tempIngList
											.size()]));

							return shoppingList;
						}
					});
		}
		alertDialogBuilder.setNegativeButton(R.string.generic_cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						initDialog();
					}
				});
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.show();
	}

	/*
	 * Dialog: User is prompted for a shopping list name; creates new shopping
	 * list
	 */
	protected void createNewShoppingList() {
		final EditText listName = new EditText(this);
		final boolean isNewList = true;
		alertDialogBuilder = new AlertDialog.Builder(this);
		listName.setHint(R.string.shopping_list_creation_dialog_entry_hint);
		alertDialogBuilder
				.setTitle(R.string.shopping_list_creation_dialog_name_entry);
		alertDialogBuilder.setView(listName);
		alertDialogBuilder.setNegativeButton(R.string.generic_cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						initDialog();
					}
				});
		alertDialogBuilder.setPositiveButton(R.string.generic_positive,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (!listName.getText().toString().equals("")) {
							int genericId = -1;
							String name = listName.getText().toString();
							ShoppingList shoppingList = new ShoppingList(
									genericId, name, selectedIngredients);
							CRDatabase.getInstance(RecipeBookActivity.this)
									.addShoppingList(shoppingList, isNewList);
							toast.setText(getResources().getString(
									R.string.shopping_list_created));
							toast.show();
							initDialog();
						} else {
							createNewShoppingList();
						}
					}

				});
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.show();
	}

	/*
	 * OnRecipeSelectedListener method implementation: If in portrait mode, the
	 * RecipeListFragment is replaced with the correct RecipeFragment. If in
	 * Landscape mode, the RecipeFragment is added to the correct layout
	 * container
	 */
	@Override
	public void onRecipeSelected(Recipe recipe) {
		getFragmentManager().executePendingTransactions();
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		recipePageIdx = findPage(recipe);
		recipeFragment.setRecipe(recipe);

		if (isInLandscapeMode()) {
			if (!(recipeFragment.isAdded())) {

				findViewById(R.id.recipe_page_temp).setVisibility(
						TextView.INVISIBLE);
				transaction.replace(R.id.recipe_book_container_side,
						recipeFragment);
			} else {
				recipeFragment.updateData();

			}
		} else {
			transaction
					.replace(R.id.recipe_book_container_main, recipeFragment);
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	private int findPage(Recipe recipe) {
		for (int idx = 0; idx < getListOfCurrentTab().size(); idx++) {
			if (getListOfCurrentTab().get(idx).getRecipe().equals(recipe)) {
				return idx;
			}
		}
		return -1;
	}

	public boolean isInLandscapeMode() {
		return (getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90 || getWindowManager()
				.getDefaultDisplay().getRotation() == Surface.ROTATION_270);
	}

	/*
	 * Fling methods allow the user to swipe left/right to navigate through
	 * recipes when in portrait mode and on the RecipeFragment. The recipes that
	 * can be gone through are the list of recipes in the current
	 * RecipeBookActivity tab.
	 */
	@Override
	public void onRightToLeftFling() {

		if (!isInLandscapeMode()) {
			ArrayList<RecipeListEntry> currentRecipeList = getListOfCurrentTab();
			if (currentRecipeList.size() == 0)
				currentRecipeList = allRecipes;
			recipePageIdx = ((recipePageIdx - 1) + currentRecipeList.size())
					% currentRecipeList.size();
			RecipeListEntry currentRecipe = currentRecipeList
					.get(recipePageIdx);
			CRDatabase.getInstance(this).addToHistory(currentRecipe);
			recipeFragment.setRecipe(currentRecipe.getRecipe());
			recipeFragment.updateData();

		}
	}

	@Override
	public void onLeftToRightFling() {

		if (!isInLandscapeMode()) {
			ArrayList<RecipeListEntry> currentRecipeList = getListOfCurrentTab();
			if (currentRecipeList.size() == 0)
				currentRecipeList = allRecipes;
			recipePageIdx = (recipePageIdx + 1) % currentRecipeList.size();
			RecipeListEntry currentRecipe = currentRecipeList
					.get(recipePageIdx);
			CRDatabase.getInstance(this).addToHistory(currentRecipe);
			recipeFragment.setRecipe(currentRecipe.getRecipe());
			recipeFragment.updateData();

		}
	}

	/*
	 * ActionBar Tabs are arranged in ascending order by their respective
	 * StartRecipeBookValues constants; Changing constants breaks the switch
	 * statement!
	 */
	private ArrayList<RecipeListEntry> getListOfCurrentTab() {

		switch (actionBar.getSelectedNavigationIndex()) {
		case CocktailRecommenderValues.ALL_RECIPES:
			return allRecipes;
		case CocktailRecommenderValues.SEARCH_RESULTS:
			return searchResultList;
		case CocktailRecommenderValues.FAV_LIST:
			return favList;
		case CocktailRecommenderValues.HISTORY_LIST:
			return historyList;
		default:
			return allRecipes;
		}

	}

	@Override
	public void onAddToShoppingList(RecipeIngredient[] selectedIngredients) {
		this.selectedIngredients = selectedIngredients;
		alertDialogBuilder.show();
	}

	@Override
	public void onNoIngredientSelected() {
		alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setTitle(R.string.shopping_list_creation_no_ing_selected);
		alertDialogBuilder.setPositiveButton(R.string.generic_positive,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						initDialog();
					}

				});
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.show();

	}

	// Implements ActionBar.Tab functionality
	private class RecipeBookTabListener implements ActionBar.TabListener {

		private RecipeListFragment fragment;

		public RecipeBookTabListener(RecipeListFragment fragment) {
			this.fragment = fragment;

		}

		@Override
		public void onTabReselected(Tab tab,
				android.support.v4.app.FragmentTransaction transaction) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabSelected(Tab tab,
				android.support.v4.app.FragmentTransaction transaction) {
			if (fragment == null) {
				fragment = new RecipeListFragment();
			}

			transaction.replace(R.id.recipe_book_container_main, fragment);
			// transaction.commit();

		}

		@Override
		public void onTabUnselected(Tab tab,
				android.support.v4.app.FragmentTransaction transaction) {

			transaction.remove(fragment);
			// transaction.commit();

			transaction.remove(fragment);
		}

	}

}
