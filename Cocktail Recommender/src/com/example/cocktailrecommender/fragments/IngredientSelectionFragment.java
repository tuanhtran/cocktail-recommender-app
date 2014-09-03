package com.example.cocktailrecommender.fragments;

import com.example.cocktailrecommender.R;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class IngredientSelectionFragment extends Fragment {
	private View fragmentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.ing_selection_fragment_layout,
				container, false);

		initUI();

		return fragmentView;
	}

	private void initUI() {
		SearchView filterBar = (SearchView) fragmentView
				.findViewById(R.id.ingredient_selection_filter_bar);

		ListView ingredientList = (ListView) fragmentView
				.findViewById(R.id.ingredient_selection_listview);

		Button categoryButtonAlc = (Button) fragmentView
				.findViewById(R.id.category_button_alcoholic);
		Button categoryButtonNonAlc = (Button) fragmentView
				.findViewById(R.id.category_button_non_alcoholic);
		Button categoryButtonMisc = (Button) fragmentView
				.findViewById(R.id.category_button_misc);
		Button categoryButtonTags = (Button) fragmentView
				.findViewById(R.id.category_button_tags);

	}

}
