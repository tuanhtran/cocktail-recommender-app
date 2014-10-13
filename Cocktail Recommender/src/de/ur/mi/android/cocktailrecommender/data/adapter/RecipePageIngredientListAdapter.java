package de.ur.mi.android.cocktailrecommender.data.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.RecipeIngredient;


/*
 * Adapter for the ingredient list in RecipeFragment
 */
public class RecipePageIngredientListAdapter extends
		ArrayAdapter<RecipeIngredient> {

	private Context context;
	private ArrayList<RecipeIngredient> ingredients;
	private Toast toast;

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
			view = inflater.inflate(R.layout.listitem_recipe_page_ingredient,
					null);

		}

		final RecipeIngredient ing = ingredients.get(position);

		if (ing != null) {
			TextView ingredientTextView = (TextView) view
					.findViewById(R.id.recipe_page_ingredient);

			ingredientTextView.setText(ing.getFullIngredientString());
		}
		view.setBackgroundColor(view.getResources().getColor(
				getBGColor(ing.isSelected())));

		view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (toast == null)
					toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
				ing.toggleSelection();
				if (ing.isSelected())
					toast.setText(context.getResources().getString(
							R.string.toast_ing_select));
				else
					toast.setText(context.getResources().getString(
							R.string.toast_ing_remove));
				toast.show();

				v.setBackgroundColor(v.getResources().getColor(
						getBGColor(ing.isSelected())));
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});
		return view;
	}

	private int getBGColor(boolean isSelected) {
		if (isSelected) {
			return R.color.background_selected_dark_blue;
		} else {
			return R.color.background_black;
		}

	}
	
	/*
	 * Method returns currently selected ingredients
	 */
	public RecipeIngredient[] getSelectedIngredients() {
		ArrayList<RecipeIngredient> selectedIngredientsTemp = new ArrayList<RecipeIngredient>();
		for (int ingIdx = 0; ingIdx < ingredients.size(); ingIdx++) {
			if (ingredients.get(ingIdx).isSelected())
				selectedIngredientsTemp.add(ingredients.get(ingIdx));
		}
		RecipeIngredient[] selectedIngredients = selectedIngredientsTemp
				.toArray(new RecipeIngredient[selectedIngredientsTemp.size()]);
		return selectedIngredients;

	}

}
