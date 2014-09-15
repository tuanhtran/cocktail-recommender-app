package de.ur.mi.android.cocktailrecommender;

import java.util.ArrayList;

import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.CRDatabase;
import de.ur.mi.android.cocktailrecommender.data.IngredientType;
import de.ur.mi.android.cocktailrecommender.data.Recipe;
import de.ur.mi.android.cocktailrecommender.data.RecipeSearchResult;
import de.ur.mi.android.cocktailrecommender.data.adapter.IngredientSelectionListAdapter;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Switch;

public class SearchActivity extends ActionBarActivity {

	private IngredientSelectionListAdapter selectionListAdapter;
	private ArrayList<IngredientType> ings = new ArrayList<IngredientType>();

	private Dialog searchSettings;
	private boolean mustContainAllSelectedIngs = false;
	private boolean canContainNonSelectedIngs = true;

	private static final int CATEGORY_BUTTON_STATE_IDX_NEUTRAL = 0;
	private static final int CATEGORY_BUTTON_STATE_NUM = 2;

	private int categoryButtonAlcStateIdx = CATEGORY_BUTTON_STATE_IDX_NEUTRAL;
	private int categoryButtonNonAlcStateIdx = CATEGORY_BUTTON_STATE_IDX_NEUTRAL;
	private int categoryButtonMiscStateIdx = CATEGORY_BUTTON_STATE_IDX_NEUTRAL;
	private int categoryButtonSelectedStateIdx = CATEGORY_BUTTON_STATE_IDX_NEUTRAL;

	private Button categoryButtonAlc;
	private Button categoryButtonNonAlc;
	private Button categoryButtonMisc;
	private Button categoryButtonSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initData();
		initUI();
	}

	private void initData() {
		ings.clear();
		ings.addAll(CRDatabase.getInstance(this).getFullIngList());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initUI() {
		initSearchSettingsDialog();
		initSearchView();
		initListView();
		initButtonViews();
	}

	private void initSearchView() {
		SearchView filterBar = (SearchView) findViewById(R.id.ingredient_selection_filter_bar);
		filterBar.setOnQueryTextListener(new OnQueryTextListener() {
			public boolean onQueryTextSubmit(String queryString) {
				return true;
			}

			public boolean onQueryTextChange(String queryString) {
				selectionListAdapter.setSearchViewInsert(queryString);
				startFilter();
				return true;
			}
		});
	}

	private void initListView() {
		ListView ingredientListView = (ListView) findViewById(R.id.ingredient_selection_listview);
		selectionListAdapter = new IngredientSelectionListAdapter(this, ings);
		ingredientListView.setAdapter(selectionListAdapter);
		selectionListAdapter.notifyDataSetChanged();
	}

	private void initButtonViews() {
		categoryButtonAlc = (Button) findViewById(R.id.category_button_alcoholic);
		categoryButtonAlc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				categoryButtonPressed(v.getId());
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});

		categoryButtonNonAlc = (Button) findViewById(R.id.category_button_non_alcoholic);
		categoryButtonNonAlc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				categoryButtonPressed(v.getId());
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

			}
		});

		categoryButtonMisc = (Button) findViewById(R.id.category_button_misc);
		categoryButtonMisc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				categoryButtonPressed(v.getId());
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});

		categoryButtonSelected = (Button) findViewById(R.id.category_button_selected);
		categoryButtonSelected.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				categoryButtonPressed(v.getId());
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});

		Button startSearchButton = (Button) findViewById(R.id.start_search_button);
		startSearchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				searchForDrinks();
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});

		Button openSearchSettingsButton = (Button) findViewById(R.id.search_settings_button);
		openSearchSettingsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				searchSettings.show();
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});
	}

	private void searchForDrinks() {
		ArrayList<Integer> selectedIngIDs = getSelectedIngredientIDs();
		int[] selectedTags = getSelectedTags();
		if ((selectedIngIDs.size() == 0) && (selectedTags.length == 0)) {
			// Throw no Items selected Error with option for whole book
			return;
		}
		ArrayList<RecipeSearchResult> results = CRDatabase.getInstance(this).searchByIngredient(
				selectedIngIDs, selectedTags, mustContainAllSelectedIngs,
				canContainNonSelectedIngs);
		if (results.size() > 0) {
			CRDatabase.getInstance(this).setSearchResults(results);
			openRecipeBook();
		} else {
			// No results message
		}
	}

	private void openRecipeBook() {
		Intent intent = new Intent(SearchActivity.this,
				RecipeBookActivity.class);
		startActivity(intent);
	}

	private void categoryButtonPressed(int buttonId) {
		if (getButtonStateIdxAndCycle(buttonId) == CATEGORY_BUTTON_STATE_IDX_NEUTRAL) {
			setAllButtonsToRed();
			setButtonToGreen((Button) findViewById(buttonId));
			selectionListAdapter
					.setSelectedCategoryButton(getFilterQueryValue(buttonId));
		} else {
			setAllButtonsToNeutral();
			selectionListAdapter
					.setSelectedCategoryButton(IngredientSelectionListAdapter.DONT_FILTER_FOR_CATEGORY);
		}
		startFilter();
	}

	private int getFilterQueryValue(int buttonId) {
		switch (buttonId) {
		case R.id.category_button_alcoholic:
			return IngredientSelectionListAdapter.FILTER_FOR_CATEGORY_ALCOHOLIC;
		case R.id.category_button_non_alcoholic:
			return IngredientSelectionListAdapter.FILTER_FOR_CATEGORY_NON_ALCOHOLIC;
		case R.id.category_button_misc:
			return IngredientSelectionListAdapter.FILTER_FOR_CATEGORY_MISC;
		case R.id.category_button_selected:
			return IngredientSelectionListAdapter.FILTER_FOR_CATEGORY_SELECTED;
		default:
			return -1;
		}
	}

	private int getButtonStateIdxAndCycle(int buttonId) {
		int buttonStateIdx = -1;
		switch (buttonId) {
		case R.id.category_button_alcoholic:
			buttonStateIdx = categoryButtonAlcStateIdx;
			categoryButtonAlcStateIdx = (categoryButtonAlcStateIdx + 1)
					% CATEGORY_BUTTON_STATE_NUM;
			break;
		case R.id.category_button_non_alcoholic:
			buttonStateIdx = categoryButtonNonAlcStateIdx;
			categoryButtonNonAlcStateIdx = (categoryButtonNonAlcStateIdx + 1)
					% CATEGORY_BUTTON_STATE_NUM;
			break;
		case R.id.category_button_misc:
			buttonStateIdx = categoryButtonMiscStateIdx;
			categoryButtonMiscStateIdx = (categoryButtonMiscStateIdx + 1)
					% CATEGORY_BUTTON_STATE_NUM;
			break;
		case R.id.category_button_selected:
			buttonStateIdx = categoryButtonSelectedStateIdx;
			categoryButtonSelectedStateIdx = (categoryButtonSelectedStateIdx + 1)
					% CATEGORY_BUTTON_STATE_NUM;
			break;
		}
		return buttonStateIdx;
	}

	private void setAllButtonsToNeutral() {
		categoryButtonAlc.setBackgroundColor(getResources().getColor(
				R.color.test_button_gray));

		categoryButtonNonAlc.setBackgroundColor(getResources().getColor(
				R.color.test_button_gray));
		categoryButtonMisc.setBackgroundColor(getResources().getColor(
				R.color.test_button_gray));
		categoryButtonSelected.setBackgroundColor(getResources().getColor(
				R.color.test_button_gray));
		categoryButtonAlcStateIdx = CATEGORY_BUTTON_STATE_IDX_NEUTRAL;
		categoryButtonNonAlcStateIdx = CATEGORY_BUTTON_STATE_IDX_NEUTRAL;
		categoryButtonMiscStateIdx = CATEGORY_BUTTON_STATE_IDX_NEUTRAL;
		categoryButtonSelectedStateIdx = CATEGORY_BUTTON_STATE_IDX_NEUTRAL;
	}

	private void setAllButtonsToRed() {
		categoryButtonAlc.setBackgroundColor(getResources().getColor(
				R.color.test_button_gray_red));
		categoryButtonNonAlc.setBackgroundColor(getResources().getColor(
				R.color.test_button_gray_red));
		categoryButtonMisc.setBackgroundColor(getResources().getColor(
				R.color.test_button_gray_red));
		categoryButtonSelected.setBackgroundColor(getResources().getColor(
				R.color.test_button_gray_red));
	}

	private void setButtonToGreen(Button button) {
		button.setBackgroundColor(getResources().getColor(
				R.color.test_button_gray_green));
	}

	private void startFilter() {
		selectionListAdapter.getFilter().filter("");
	}

	private ArrayList<Integer> getSelectedIngredientIDs() {
		setAllButtonsToNeutral();
		selectionListAdapter
				.setSelectedCategoryButton(IngredientSelectionListAdapter.DONT_FILTER_FOR_CATEGORY);
		startFilter();
		ArrayList<Integer> selectedIngIDs = new ArrayList<Integer>();
		for (int idx = 0; idx < ings.size(); idx++) {
			if (ings.get(idx).isSelected()) {
				selectedIngIDs.add(ings.get(idx).getID());
			}
		}
		return selectedIngIDs;
	}

	private int[] getSelectedTags() {
		int[] selectedTags = new int[0];
		return selectedTags;
	}

	private void initSearchSettingsDialog() {
		searchSettings = new Dialog(this);
		searchSettings.requestWindowFeature(Window.FEATURE_NO_TITLE);
		searchSettings.setContentView(R.layout.dialog_search_settings_layout);
		Switch switchButtonOne = (Switch) searchSettings
				.findViewById(R.id.search_settings_option_one_switch);
		switchButtonOne.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mustContainAllSelectedIngs = !mustContainAllSelectedIngs;
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});
		Switch switchButtonTwo = (Switch) searchSettings
				.findViewById(R.id.search_settings_option_two_switch);
		switchButtonTwo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				canContainNonSelectedIngs = !canContainNonSelectedIngs;
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});
	}
}
