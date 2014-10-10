package de.ur.mi.android.cocktailrecommender.data.adapter;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;
import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.IngredientType;

public class IngredientSelectionListAdapter extends
		ArrayAdapter<IngredientType> implements Filterable {

	private Context context;
	private ArrayList<IngredientType> ingredientList;
	private ArrayList<IngredientType> notShownIngList = new ArrayList<IngredientType>();
	private String searchViewInsert = "";
	private int selectedCategoryButton = 0;

	public final static int DONT_FILTER_FOR_CATEGORY = 0;
	public final static int FILTER_FOR_CATEGORY_ALCOHOLIC = 1;
	public final static int FILTER_FOR_CATEGORY_NON_ALCOHOLIC = 2;
	public final static int FILTER_FOR_CATEGORY_MISC = 3;
	public final static int FILTER_FOR_CATEGORY_SELECTED = 4;

	public IngredientSelectionListAdapter(Context context,
			ArrayList<IngredientType> ingredientList) {
		super(context, R.id.listitem_selection_ingredient, ingredientList);

		this.context = context;
		this.ingredientList = ingredientList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.listitem_ing_selection, null);

		}
		
		final IngredientType ingType = ingredientList.get(position);

		if (ingType != null) {
			TextView ingName = (TextView) view
					.findViewById(R.id.selection_ingredient_name);

			ingName.setText(ingType.getIngName());
		}
		view.setBackgroundColor(view.getResources().getColor(
				getBGColor(ingType.isSelected())));

		view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				ingType.toggleSelection();
				v.setBackgroundColor(v.getResources().getColor(
						getBGColor(ingType.isSelected())));
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});
		return view;
	}

	private int getBGColor(boolean isSelected) {
		if (isSelected) {
			Toast.makeText(context, context.getResources().getString(R.string.toast_ing_select), Toast.LENGTH_SHORT).show();
			return R.color.background_selected_dark_blue;
		} else {
			Toast.makeText(context, context.getResources().getString(R.string.toast_ing_remove), Toast.LENGTH_SHORT).show();
			return R.color.background_black;
		}
	}

	@Override
	public void notifyDataSetChanged() {
		Collections.sort(ingredientList);
		super.notifyDataSetChanged();
	}

	@Override
	public Filter getFilter() {
		resetFilter();
		Filter filter = new Filter() {
			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				ingredientList.clear();
				ingredientList
						.addAll((ArrayList<IngredientType>) results.values);
				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				ArrayList<IngredientType> filteredIngredientList = new ArrayList<IngredientType>();

				if (selectedCategoryButton == DONT_FILTER_FOR_CATEGORY) {
					filteredIngredientList.addAll(ingredientList);
				} else if ((selectedCategoryButton == FILTER_FOR_CATEGORY_ALCOHOLIC)
						|| (selectedCategoryButton == FILTER_FOR_CATEGORY_NON_ALCOHOLIC)
						|| (selectedCategoryButton == FILTER_FOR_CATEGORY_MISC)) {

					char prefix = ("" + selectedCategoryButton + "").charAt(0);
					for (int idx = 0; idx < ingredientList.size(); idx++) {
						if (prefix == ingredientList.get(idx)
								.getCategoryPrefixChar()) {
							filteredIngredientList.add(ingredientList.get(idx));
						} else {
							notShownIngList.add(ingredientList.get(idx));
						}
					}
				} else if (selectedCategoryButton == FILTER_FOR_CATEGORY_SELECTED) {
					for (int idx = 0; idx < ingredientList.size(); idx++) {
						if (ingredientList.get(idx).isSelected()) {
							filteredIngredientList.add(ingredientList.get(idx));
						} else {
							notShownIngList.add(ingredientList.get(idx));
						}
					}
				}

				if (!(searchViewInsert.equals("") || searchViewInsert == null)) {
					searchViewInsert = searchViewInsert.toLowerCase();
					for (int idx = 0; idx < filteredIngredientList.size(); idx++) {
						if (!(filteredIngredientList.get(idx).getIngName()
								.toLowerCase().contains(searchViewInsert))) {
							notShownIngList
									.add(filteredIngredientList.get(idx));
							filteredIngredientList.remove(idx);
							idx--;
						}
					}
				}

				results.count = filteredIngredientList.size();
				results.values = filteredIngredientList;
				return results;
			}
		};
		return filter;
	}

	public void setSearchViewInsert(String s) {
		searchViewInsert = s;
	}

	public void setSelectedCategoryButton(int i) {
		selectedCategoryButton = i;
	}

	private void resetFilter() {
		ingredientList.addAll(notShownIngList);
		notShownIngList.clear();
	}

}
