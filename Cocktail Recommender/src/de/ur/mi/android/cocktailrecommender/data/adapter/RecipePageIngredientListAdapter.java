package de.ur.mi.android.cocktailrecommender.data.adapter;

import java.util.ArrayList;

import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.RecipeIngredient;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RecipePageIngredientListAdapter extends
		ArrayAdapter<RecipeIngredient> {

	private Context context;
	private ArrayList<RecipeIngredient> ingredients;

	public RecipePageIngredientListAdapter(Context context,
			ArrayList<RecipeIngredient> ingredients) {
		super(context, R.layout.listitem_recipe_page_ingredient, ingredients);
		this.context = context;
		this.ingredients = ingredients;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.listitem_recipe_page_ingredient, null);

		}

		RecipeIngredient ing = ingredients.get(position);

		if (ing != null) {
			TextView ingredientTextView = (TextView) view
					.findViewById(R.id.recipe_page_ingredient);

			ingredientTextView.setText(ing.getFullIngredientString());
		}
		return view;
	}

}
