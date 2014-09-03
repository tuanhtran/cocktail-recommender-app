package com.example.cocktailrecommender.fragments;

import com.example.cocktailrecommender.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class SelectedIngredientsFragment extends Fragment {
	private View fragmentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.selected_ings_fragment_layout,
				container, false);

		initUI();

		return fragmentView;
	}

	private void initUI() {

		ListView selectedIngs = (ListView) fragmentView
				.findViewById(R.id.selected_ingredients_listview);

		Button startSearch = (Button) fragmentView
				.findViewById(R.id.start_search_button);

		Button searchProperties = (Button) fragmentView
				.findViewById(R.id.search_properties_button);

	}
}
