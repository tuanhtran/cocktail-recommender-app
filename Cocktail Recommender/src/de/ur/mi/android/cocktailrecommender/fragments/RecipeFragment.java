package de.ur.mi.android.cocktailrecommender.fragments;

import java.util.ArrayList;

import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.Recipe;
import de.ur.mi.android.cocktailrecommender.data.RecipeIngredient;
import de.ur.mi.android.cocktailrecommender.data.adapter.RecipePageIngredientListAdapter;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class RecipeFragment extends Fragment {
	private View fragmentView;
	private Recipe recipe;
	private TextView recipeName;
	private ListView recipeIngredients;
	private TextView recipePreparation;
	private RecipePageIngredientListAdapter adapter;
	private ArrayList<RecipeIngredient> ingredients;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.recipe_fragment_layout,
				container, false);
		initData();
		initUI();
		return fragmentView;
	}

	private void initData() {
		ingredients = new ArrayList<RecipeIngredient>();
	}

	private void initUI() {
		recipeName = (TextView) fragmentView
				.findViewById(R.id.recipe_page_name);
		recipeIngredients = (ListView) fragmentView
				.findViewById(R.id.recipe_page_ingredient_list);
		recipePreparation = (TextView) fragmentView
				.findViewById(R.id.recipe_page_preparation);
		initAdapter();
		updateData();
	}
	
	private void initAdapter() {
		adapter = new RecipePageIngredientListAdapter(getActivity(),
				ingredients);
		recipeIngredients.setAdapter(adapter);		
	}

	public void updateData() {
		ingredients.clear();
		ingredients.addAll(recipe.getIngredientsAsList());
		adapter.notifyDataSetChanged();
		recipeName.setText(recipe.getName());
		adjustListViewHeight(recipeIngredients);
		recipePreparation.setText(recipe.getPreparation());
	}

	private void adjustListViewHeight(ListView listView) {
		RecipePageIngredientListAdapter adapter = (RecipePageIngredientListAdapter) listView
				.getAdapter();
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
	}

	
}
