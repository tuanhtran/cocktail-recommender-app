package com.example.cocktailrecommender;

import java.util.ArrayList;

import com.example.cocktailrecommender.data.CRDatabase;
import com.example.cocktailrecommender.data.IngredientType;
import com.example.cocktailrecommender.data.adapter.IngredientSelectionListAdapter;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;

public class SearchActivity extends ActionBarActivity {

	private CRDatabase db;
	private IngredientSelectionListAdapter selectionListAdapter;
	private ArrayList<IngredientType> ings = new ArrayList<IngredientType>();

	private static final int BUTTON_STATE_IDX_NEUTRAL = 0;

	private int categoryButtonAlcStateIdx = BUTTON_STATE_IDX_NEUTRAL;
	private int categoryButtonNonAlcStateIdx = BUTTON_STATE_IDX_NEUTRAL;
	private int categoryButtonMiscStateIdx = BUTTON_STATE_IDX_NEUTRAL;
	private int categoryButtonSelectedStateIdx = BUTTON_STATE_IDX_NEUTRAL;

	private Button categoryButtonAlc;
	private Button categoryButtonNonAlc;
	private Button categoryButtonMisc;
	private Button categoryButtonSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_by_ing);
		initDB();
		initData();
		initUI();
	}

	private void initData() {
		ings.clear();
		ings.addAll(db.getIngList());
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
		SearchView filterBar = (SearchView) findViewById(R.id.ingredient_selection_filter_bar);
		filterBar.setOnQueryTextListener(new OnQueryTextListener() {
			public boolean onQueryTextSubmit(String queryString) {
				// searchViewInsert = queryString;
				// startFilter();
				return true;
			}

			public boolean onQueryTextChange(String queryString) {
				selectionListAdapter.setSearchViewInsert(queryString);
				startFilter();

				return true;
			}
		});

		ListView ingredientListView = (ListView) findViewById(R.id.ingredient_selection_listview);
		selectionListAdapter = new IngredientSelectionListAdapter(this, ings);
		ingredientListView.setAdapter(selectionListAdapter);
		selectionListAdapter.notifyDataSetChanged();

		categoryButtonAlc = (Button) findViewById(R.id.category_button_alcoholic);
		categoryButtonAlc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				buttonPressed(v.getId());
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});

		categoryButtonNonAlc = (Button) findViewById(R.id.category_button_non_alcoholic);
		categoryButtonNonAlc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				buttonPressed(v.getId());
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

			}
		});

		categoryButtonMisc = (Button) findViewById(R.id.category_button_misc);
		categoryButtonMisc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				buttonPressed(v.getId());
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});

		categoryButtonSelected = (Button) findViewById(R.id.category_button_selected);
		categoryButtonSelected.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				buttonPressed(v.getId());
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});
	}

	private void buttonPressed(int buttonId) {
		if (getButtonStateIdxAndCycle(buttonId) == BUTTON_STATE_IDX_NEUTRAL) {
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
			categoryButtonAlcStateIdx = (categoryButtonAlcStateIdx + 1) % 2;
			break;
		case R.id.category_button_non_alcoholic:
			buttonStateIdx = categoryButtonNonAlcStateIdx;
			categoryButtonNonAlcStateIdx = (categoryButtonNonAlcStateIdx + 1) % 2;
			break;
		case R.id.category_button_misc:
			buttonStateIdx = categoryButtonMiscStateIdx;
			categoryButtonMiscStateIdx = (categoryButtonMiscStateIdx + 1) % 2;
			break;
		case R.id.category_button_selected:
			buttonStateIdx = categoryButtonSelectedStateIdx;
			categoryButtonSelectedStateIdx = (categoryButtonSelectedStateIdx + 1) % 2;
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
		categoryButtonAlcStateIdx = BUTTON_STATE_IDX_NEUTRAL;
		categoryButtonNonAlcStateIdx = BUTTON_STATE_IDX_NEUTRAL;
		categoryButtonMiscStateIdx = BUTTON_STATE_IDX_NEUTRAL;
		categoryButtonSelectedStateIdx = BUTTON_STATE_IDX_NEUTRAL;
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

	private int getColorFromState(int state) {
		switch (state) {
		case 0:
			return getResources().getColor(R.color.test_button_gray);
		case 1:
			return getResources().getColor(R.color.test_button_gray_green);
		case 2:
			return getResources().getColor(R.color.test_button_gray_red);
		default:
			return -1;
		}
	}

	private void startFilter() {
		selectionListAdapter.getFilter().filter("");
	}

	private void initDB() {
		db = new CRDatabase(this);
		db.open();
	}

	@Override
	protected void onDestroy() {
		// db.close();
		super.onDestroy();
	}

}
