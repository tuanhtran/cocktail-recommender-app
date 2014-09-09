package com.example.cocktailrecommender.fragments;

import java.util.zip.Inflater;

import com.example.cocktailrecommender.R;
import com.example.cocktailrecommender.data.Recipe;
import com.example.cocktailrecommender.data.RecipeIngredient;
import com.example.cocktailrecommender.data.adapter.RecipePageIngredientListAdapter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RecipeFragment extends Fragment {
	View fragmentView;
	Recipe recipe;
	TextView recipeName;
	ListView recipeIngredients;
	TextView recipePreparation;
	RecipePageIngredientListAdapter adapter;

	public RecipeFragment(Recipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.recipe_fragment_layout,
				container, false);
		initUI();
		return fragmentView;
	}

	private void initUI() {
		recipeName = (TextView) fragmentView
				.findViewById(R.id.recipe_page_name);
		recipeName.setText(recipe.getName());

		recipeIngredients = (ListView) fragmentView
				.findViewById(R.id.recipe_page_ingredient_list);
		
		adapter = new RecipePageIngredientListAdapter(getActivity(),
		recipe.getIngredients());
		recipeIngredients.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adjustListViewHeight (recipeIngredients);

		recipePreparation = (TextView) fragmentView
				.findViewById(R.id.recipe_page_preparation);
		recipePreparation.setText(recipe.getPreparation());
	}

	
	 private void adjustListViewHeight(ListView listView) {
		    RecipePageIngredientListAdapter adapter = (RecipePageIngredientListAdapter) listView.getAdapter();
		    if (adapter == null) {
		        return;
		    }

		    int totalHeight = 0;
		    for (int i = 0; i < adapter.getCount(); i++) {
		        View listItem = adapter.getView(i, null, listView);
		        listItem.measure(0, 0);
		        totalHeight += listItem.getMeasuredHeight();
		    }

		    ViewGroup.LayoutParams params = listView.getLayoutParams();
		    params.height = totalHeight
		            + (listView.getDividerHeight() * (adapter.getCount() - 1));
		    listView.setLayoutParams(params);
		      }
	
	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
		updateData();
	}

	private void updateData() {

	}
}
