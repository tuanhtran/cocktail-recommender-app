package de.ur.mi.android.cocktailrecommender;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Switch;
import de.ur.mi.android.cocktailrecommender.data.CRDatabase;
import de.ur.mi.android.cocktailrecommender.data.CRDatabase.OnSearchResultListener;
import de.ur.mi.android.cocktailrecommender.data.IngredientType;
import de.ur.mi.android.cocktailrecommender.data.SearchParameter;
import de.ur.mi.android.cocktailrecommender.data.StartRecipeBookValues;
import de.ur.mi.android.cocktailrecommender.data.Tag;
import de.ur.mi.android.cocktailrecommender.data.adapter.IngredientSelectionListAdapter;
import de.ur.mi.android.cocktailrecommender.data.adapter.TagSelectionListAdapter;

public class SearchActivity extends ActionBarActivity implements
		OnSearchResultListener {

	private IngredientSelectionListAdapter ingListAdapter;
	private TagSelectionListAdapter tagListAdapter;
	private ArrayList<IngredientType> ings = new ArrayList<IngredientType>();
	private ArrayList<Tag> tags = new ArrayList<Tag>();
	private boolean mustContainAllSelectedIngs = false;
	private boolean canContainNonSelectedIngs = true;
	private boolean mustContainAllSelectedTags = false;
	private boolean canContainNonSelectedTags = true;
	private boolean isSearchInProgress;

	private Dialog searchSettings;
	private Dialog searchProgress;
	private SearchView filterBar;

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
		isSearchInProgress = false;
		ings.clear();
		tags.clear();
		ings.addAll(CRDatabase.getInstance(this).getFullIngList());
		tags.addAll(CRDatabase.getInstance(this).getFullTagList());
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

	private void initUI() {
		initDialogs();
		initSearchView();
		initListViews();
		initButtonViews();
	}

	private void initDialogs() {
		initSearchSettingsDialog();
		initSearchProgressDialog();
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

		Switch switchButtonThree = (Switch) searchSettings
				.findViewById(R.id.search_settings_option_three_switch);
		switchButtonThree.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mustContainAllSelectedTags = !mustContainAllSelectedTags;
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});

		Switch switchButtonFour = (Switch) searchSettings
				.findViewById(R.id.search_settings_option_four_switch);
		switchButtonFour.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				canContainNonSelectedTags = !canContainNonSelectedTags;
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});
	}

	private void initSearchProgressDialog() {
		searchProgress = new Dialog(this);
		searchProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
		searchProgress.setContentView(R.layout.dialog_search_progress_layout);
	}

	private void initSearchView() {
		filterBar = (SearchView) findViewById(R.id.ingredient_selection_filter_bar);
		filterBar.setOnQueryTextListener(new OnQueryTextListener() {
			public boolean onQueryTextSubmit(String queryString) {
				return true;
			}

			public boolean onQueryTextChange(String queryString) {
				ingListAdapter.setSearchViewInsert(queryString);
				startFilter();
				return true;
			}
		});
		filterBar.setIconifiedByDefault(false);
	}

	private void initListViews() {
		ListView ingredientListView = (ListView) findViewById(R.id.ingredient_selection_listview);
		ingListAdapter = new IngredientSelectionListAdapter(this, ings);
		ingredientListView.setAdapter(ingListAdapter);
		ingListAdapter.notifyDataSetChanged();

		GridView tagListView = (GridView) findViewById(R.id.tag_selection);
		tagListAdapter = new TagSelectionListAdapter(this, tags);
		tagListView.setAdapter(tagListAdapter);
		tagListAdapter.notifyDataSetChanged();
	}

	private void initButtonViews() {
		categoryButtonAlc = (Button) findViewById(R.id.category_button_alcoholic);
		categoryButtonAlc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				categoryButtonPressed(v.getId());
			}
		});

		categoryButtonNonAlc = (Button) findViewById(R.id.category_button_non_alcoholic);
		categoryButtonNonAlc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				categoryButtonPressed(v.getId());

			}
		});

		categoryButtonMisc = (Button) findViewById(R.id.category_button_misc);
		categoryButtonMisc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				categoryButtonPressed(v.getId());
			}
		});

		categoryButtonSelected = (Button) findViewById(R.id.category_button_selected);
		categoryButtonSelected.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				filterBar.setQuery("", false);
				categoryButtonPressed(v.getId());
			}
		});

		ImageButton startSearchButton = (ImageButton) findViewById(R.id.start_search_button);
		startSearchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				searchProgress.show();
				if (!isSearchInProgress) {
					searchForDrinks();
				}
			}
		});

		ImageButton openSearchSettingsButton = (ImageButton) findViewById(R.id.search_settings_button);
		openSearchSettingsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				searchSettings.show();
			}
		});
	}

	private void searchForDrinks() {
		ArrayList<Integer> selectedIngIDs = getSelectedIngredientIDs();
		ArrayList<Integer> selectedTagIDs = getSelectedTagIDs();
		if ((selectedIngIDs.isEmpty()) && (selectedTagIDs.isEmpty())) {
			createFailedSearchMsg(R.string.search_error_no_selection_title,
					R.string.search_error_no_selection_msg);
			setSearchNotInProgress();
			return;
		}
		CRDatabase.getInstance(this).searchByIngredient(
				new SearchParameter(mustContainAllSelectedIngs,
						canContainNonSelectedIngs, mustContainAllSelectedTags,
						canContainNonSelectedTags, selectedIngIDs,
						selectedTagIDs), this);
	}

	private void setSearchNotInProgress() {
		searchProgress.dismiss();
		isSearchInProgress = false;
	}

	private void openRecipeBook(int listIdx) {
		Intent intent = new Intent(SearchActivity.this,
				RecipeBookActivity.class);
		intent.putExtra(StartRecipeBookValues.FRAGMENT_TO_DISPLAY, listIdx);
		startActivity(intent);
	}

	private void createFailedSearchMsg(int title, int msg) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(msg);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setNegativeButton(R.string.search_error_option_back,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialogBuilder.setPositiveButton(
				R.string.search_error_option_recipebook,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						openRecipeBook(StartRecipeBookValues.ALL_RECIPES);
						dialog.dismiss();
					}
				});
		alertDialogBuilder.create().show();
	}

	private void categoryButtonPressed(int buttonId) {
		if (getButtonStateIdxAndCycle(buttonId) == CATEGORY_BUTTON_STATE_IDX_NEUTRAL) {
			setAllButtonsToNotSelected();
			setButtonToSelected((Button) findViewById(buttonId));
			ingListAdapter
					.setSelectedCategoryButton(getFilterQueryValue(buttonId));
		} else {
			setAllButtonsToNeutral();
			ingListAdapter
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
				R.color.background_not_selected_blue));

		categoryButtonNonAlc.setBackgroundColor(getResources().getColor(
				R.color.background_not_selected_blue));
		categoryButtonMisc.setBackgroundColor(getResources().getColor(
				R.color.background_not_selected_blue));
		categoryButtonSelected.setBackgroundColor(getResources().getColor(
				R.color.background_not_selected_blue));
		categoryButtonAlcStateIdx = CATEGORY_BUTTON_STATE_IDX_NEUTRAL;
		categoryButtonNonAlcStateIdx = CATEGORY_BUTTON_STATE_IDX_NEUTRAL;
		categoryButtonMiscStateIdx = CATEGORY_BUTTON_STATE_IDX_NEUTRAL;
		categoryButtonSelectedStateIdx = CATEGORY_BUTTON_STATE_IDX_NEUTRAL;
	}

	private void setAllButtonsToNotSelected() {
		categoryButtonAlc.setBackgroundColor(getResources().getColor(
				R.color.background_not_selected_blue));
		categoryButtonNonAlc.setBackgroundColor(getResources().getColor(
				R.color.background_not_selected_blue));
		categoryButtonMisc.setBackgroundColor(getResources().getColor(
				R.color.background_not_selected_blue));
		categoryButtonSelected.setBackgroundColor(getResources().getColor(
				R.color.background_not_selected_blue));
	}

	private void setButtonToSelected(Button button) {
		button.setBackgroundColor(getResources().getColor(
				R.color.background_selected_dark_blue));
	}

	private void startFilter() {
		ingListAdapter.getFilter().filter("");
	}

	private ArrayList<Integer> getSelectedIngredientIDs() {
		setAllButtonsToNeutral();
		ingListAdapter
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

	private ArrayList<Integer> getSelectedTagIDs() {
		ArrayList<Integer> selectedTagIDs = new ArrayList<Integer>();
		for (int idx = 0; idx < tags.size(); idx++) {
			if (tags.get(idx).isSelected()) {
				selectedTagIDs.add(tags.get(idx).getTagID());
			}
		}
		return selectedTagIDs;
	}

	@Override
	public void onSearchInitiated() {
		isSearchInProgress = true;
		searchProgress.show();
	}

	@Override
	public void onSearchFailed() {
		setSearchNotInProgress();
		createFailedSearchMsg(R.string.search_error_no_results_title,
				R.string.search_error_no_results_msg);
	}

	@Override
	public void onSearchCompleted() {
		setSearchNotInProgress();
		openRecipeBook(StartRecipeBookValues.SEARCH_RESULTS);
	}
}
