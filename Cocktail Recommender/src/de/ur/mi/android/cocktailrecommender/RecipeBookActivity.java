package de.ur.mi.android.cocktailrecommender;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.widget.EditText;
import android.widget.TextView;
import de.ur.mi.android.cocktailrecommender.data.CRDatabase;
import de.ur.mi.android.cocktailrecommender.data.Recipe;
import de.ur.mi.android.cocktailrecommender.data.RecipeIngredient;
import de.ur.mi.android.cocktailrecommender.data.RecipeListEntry;
import de.ur.mi.android.cocktailrecommender.data.ShoppingList;
import de.ur.mi.android.cocktailrecommender.data.StartRecipeBookValues;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeFragment;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeFragment.OnFlingListener;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeFragment.OnShoppingListAddListener;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeListFragment;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeListFragment.OnRecipeSelectedListener;

public class RecipeBookActivity extends ActionBarActivity implements
		OnRecipeSelectedListener, OnShoppingListAddListener, OnFlingListener {

	private RecipeFragment recipeFragment;
	private RecipeListFragment recipeListFragment;
	private ArrayList<RecipeListEntry> recipeList;
	private ArrayList<RecipeListEntry> allRecipes;
	private ArrayList<RecipeListEntry> favList;
	private ArrayList<RecipeListEntry> historyList;
	private ActionBar actionBar;
	private RecipeIngredient[] selectedIngredients;
	private AlertDialog.Builder alertDialogBuilder;
	private int recipePageIdx = 0;
	private boolean onRecipePage = false;

	// Fragment type not final, using RecipeListFragment to test. Are custom
	// types necessary?
	private RecipeListFragment historyListFragment;
	private RecipeListFragment favsListFragment;
	private RecipeListFragment allRecipesFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_book);
		
		initData();
		initUIFragments();
		setActionBarTabs();
		initDialog();
		
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
		if(id == R.id.action_nav_menu){
			Intent openMenu = new Intent(this, MenuActivity.class);
			openMenu.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(openMenu);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initData() {
		recipeList = CRDatabase.getInstance(this).getSearchResults();
		allRecipes = CRDatabase.getInstance(this).getFullRecipeList();
		favList = CRDatabase.getInstance(this).getFavorites();
		historyList = CRDatabase.getInstance(this).getHistory();
	}

	private void initUIFragments() {
		recipeListFragment = new RecipeListFragment(recipeList);
		recipeListFragment.setOnRecipeSelectedListener(this);
		historyListFragment = new RecipeListFragment(historyList, StartRecipeBookValues.NO_MATCH_RATE);
		historyListFragment.setOnRecipeSelectedListener(this);
		favsListFragment = new RecipeListFragment(favList, StartRecipeBookValues.NO_MATCH_RATE);
		favsListFragment.setOnRecipeSelectedListener(this);
		allRecipesFragment = new RecipeListFragment(allRecipes, StartRecipeBookValues.NO_MATCH_RATE);
		allRecipesFragment.setOnRecipeSelectedListener(this);
		recipeFragment = new RecipeFragment();
		recipeFragment.setOnFlingListener(this);
		recipeFragment.setOnShoppingListAddListener(this);

	}

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
				recipeListFragment));
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
				StartRecipeBookValues.FRAGMENT_TO_DISPLAY)) {
		case StartRecipeBookValues.ALL_RECIPES:
			actionBar.selectTab(allRecipesTab);
			break;
		case StartRecipeBookValues.SEARCH_RESULTS:
			actionBar.selectTab(searchResultTab);
			break;
		case StartRecipeBookValues.FAV_LIST:
			actionBar.selectTab(favsTab);
			break;
		case StartRecipeBookValues.HISTORY_LIST:
			actionBar.selectTab(historyTab);
			break;
		default:
			actionBar.selectTab(allRecipesTab);
			break;
		}
	}

	// Dialog erscheint wenn im RecipeFragment auf den
	// "Zutaten zu Einkaufsliste hinzufügen Button gedrückt wird" und bietet 2
	// Optionen an:
	// Neue Liste erstellen oder zu einer vorhandenen hinzufügen
	private void initDialog() {
		final int OPTION_ONE = 0;
		final int OPTION_TWO = 1;
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

	// Dialog der erscheint um Zutaten zu einer vorhandenen hinzuzufügen. Alle
	// vorhandenen Listen werden angezeigt.
	protected void addToExistingShoppingList() {
		final boolean isNewList = false;

		final ArrayList<ShoppingList> shoppingLists = CRDatabase.getInstance(
				this).getAllShoppingLists();

		if (shoppingLists == null) {
			alertDialogBuilder
					.setMessage(R.string.shopping_list_creation_dialog_no_lists);
		}
		CharSequence[] shoppingListNames = new CharSequence[shoppingLists
				.size()];
		for (int listIdx = 0; listIdx < shoppingLists.size(); listIdx++) {
			shoppingListNames[listIdx] = shoppingLists.get(listIdx)
					.getListName();
		}
		alertDialogBuilder
				.setTitle(R.string.shopping_list_creation_dialog_selection_list);
		alertDialogBuilder.setItems(shoppingListNames,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ShoppingList modifiedList = modifyList(shoppingLists
								.get(which));

						CRDatabase.getInstance(RecipeBookActivity.this)
								.addShoppingList(modifiedList, isNewList);
						initDialog();

					}

					// Soll doppelte Einträge in einer EInkaufsliste verhindern;
					private ShoppingList modifyList(ShoppingList shoppingList) {
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

	// Dialog der für das Erstellen einer neuen Einkaufsliste erscheint. Nach
	// Eingabe eines Namens für die Liste wird diese in die Datenbank
	// eingetragen
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
							initDialog();
						} else {
							createNewShoppingList();
						}
					}

				});
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.show();
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
		for (int idx = 0; idx < recipeList.size(); idx++) {
			if (recipeList.get(idx).getRecipe().equals(recipe)) {
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
			recipePageIdx = ((recipePageIdx - 1) + recipeList.size())
					% recipeList.size();
			recipeFragment.setRecipe(recipeList.get(recipePageIdx).getRecipe());
			recipeFragment.updateData();
		}
	}

	@Override
	public void onLeftToRightFling() {
		if (!isInLandscapeMode()) {
			recipePageIdx = (recipePageIdx + 1) % recipeList.size();
			recipeFragment.setRecipe(recipeList.get(recipePageIdx).getRecipe());
			recipeFragment.updateData();
		}
	}

	@Override
	public void onAddToShoppingList(RecipeIngredient[] selectedIngredients) {
		this.selectedIngredients = selectedIngredients;
		alertDialogBuilder.show();
	}
		
	
	private class RecipeBookTabListener implements ActionBar.TabListener {

		private RecipeListFragment fragment;
		
		//FragmentTransaction from implemented methods don't work with RecipeListFragment
		private FragmentTransaction customTransaction;

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
			customTransaction = getFragmentManager()
					.beginTransaction();
			customTransaction
					.replace(R.id.recipe_book_container_main, fragment);
			customTransaction.addToBackStack(null);
			customTransaction.commit();

		}

		@Override
		public void onTabUnselected(Tab tab,
				android.support.v4.app.FragmentTransaction transaction) {
			customTransaction = getFragmentManager()
					.beginTransaction();
			customTransaction.remove(fragment);
			customTransaction.commit();

		}

	}

}
