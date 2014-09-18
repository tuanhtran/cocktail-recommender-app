package de.ur.mi.android.cocktailrecommender;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Surface;
import android.widget.EditText;
import android.widget.TextView;
import de.ur.mi.android.cocktailrecommender.data.CRDatabase;
import de.ur.mi.android.cocktailrecommender.data.Recipe;
import de.ur.mi.android.cocktailrecommender.data.RecipeIngredient;
import de.ur.mi.android.cocktailrecommender.data.RecipeSearchResult;
import de.ur.mi.android.cocktailrecommender.data.ShoppingList;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeFragment;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeFragment.OnFlingListener;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeFragment.OnShoppingListAddListener;
import de.ur.mi.android.cocktailrecommender.fragments.ResultListFragment;
import de.ur.mi.android.cocktailrecommender.fragments.ResultListFragment.OnRecipeSelectedListener;

public class RecipeBookActivity extends ActionBarActivity implements
		OnRecipeSelectedListener, OnShoppingListAddListener, OnFlingListener {

	public static final String RESULT_LIST_KEY = "ResultList";
	public static final String RESULT_LIST_BUNDLE_KEY = "ResultListBundle";

	private RecipeFragment recipeFragment;
	private ResultListFragment resultListFragment;
	private ArrayList<RecipeSearchResult> resultList;
	private RecipeIngredient[] selectedIngredients;
	private AlertDialog.Builder alertDialogBuilder;
	private int recipePageIdx = 0;
	private boolean onRecipePage = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_book);
		initData();
		initUIFragments();
		initDialog();
	}

	private void initData() {
		resultList = CRDatabase.getInstance(this).getSearchResults();
	}

	private void initUIFragments() {
		resultListFragment = new ResultListFragment(resultList);
		resultListFragment.setOnRecipeSelectedListener(this);
		recipeFragment = new RecipeFragment();
		recipeFragment.setOnFlingListener(this);
		recipeFragment.setOnShoppingListAddListener(this);
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.add(R.id.recipe_book_container_main, resultListFragment);
		transaction.commit();
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
						startShoppingListActivity();

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
						int genericId = -1;
						String name = listName.getText().toString();
						ShoppingList shoppingList = new ShoppingList(genericId,
								name, selectedIngredients);
						CRDatabase.getInstance(RecipeBookActivity.this)
								.addShoppingList(shoppingList, isNewList);
						initDialog();
						startShoppingListActivity();
					}

				});
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
			recipePageIdx = ((recipePageIdx - 1) + resultList.size())
					% resultList.size();
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

	@Override
	public void onAddToShoppingList(RecipeIngredient[] selectedIngredients) {
		this.selectedIngredients = selectedIngredients;
		alertDialogBuilder.show();
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

	private void startShoppingListActivity() {
		Intent intent = new Intent(RecipeBookActivity.this,
				ShoppingListActivity.class);
		startActivity(intent);
	}

}
