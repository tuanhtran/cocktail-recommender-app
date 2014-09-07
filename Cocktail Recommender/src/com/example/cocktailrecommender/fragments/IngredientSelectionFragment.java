package com.example.cocktailrecommender.fragments;

import java.util.ArrayList;

import com.example.cocktailrecommender.R;
import com.example.cocktailrecommender.data.CRDatabase;
import com.example.cocktailrecommender.data.IngredientType;
import com.example.cocktailrecommender.data.adapter.IngredientSelectionListAdapter;

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
	private CRDatabase db;
	private IngredientSelectionListAdapter listAdapter;
	private ArrayList<IngredientType> ings = new ArrayList<IngredientType>();
	
	public IngredientSelectionFragment (CRDatabase db) {
		this.db = db;
		db.open();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.ing_selection_fragment_layout,
				container, false);

		ings.addAll(db.getIngList());
		initUI();
		initIngList();

		return fragmentView;
	}

	private void initIngList() {
		updateIngList();
	}

	private void updateIngList() {
		ings.clear();
		ings.addAll(db.getIngList());
		listAdapter.notifyDataSetChanged();		
	}
	
	private void initListAdapter(){
		ListView ingredientList = (ListView) fragmentView
				.findViewById(R.id.ingredient_selection_listview);
		listAdapter = new IngredientSelectionListAdapter(getActivity(),ings);
		ingredientList.setAdapter(listAdapter);

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
