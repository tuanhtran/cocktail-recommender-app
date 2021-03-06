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

/*
 * Adapter for the ingredient list in SearchActivity
 */
public class IngredientSelectionListAdapter extends
		ArrayAdapter<IngredientType> implements Filterable {

	private Context context;
	private ArrayList<IngredientType> ingredientList;
	private ArrayList<IngredientType> notShownIngList = new ArrayList<IngredientType>();
	private String searchViewQueryText = "";
	private int selectedCategoryButtonIdx = 0;
	private Toast toast;

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
				if (toast == null)
					toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
				if (ingType.isSelected())
					toast.setText(context.getResources().getString(
							R.string.toast_ing_select));
				else
					toast.setText(context.getResources().getString(
							R.string.toast_ing_remove));
				toast.show();
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

	@Override
	public void notifyDataSetChanged() {
		Collections.sort(ingredientList);
		super.notifyDataSetChanged();
	}

	/*
	 * This Filter does not use the CharSequence that is passed when the Filter
	 * is called. Instead the CharSequence/String and the ingredient category
	 * index that are both used to filter the list are passed on to the
	 * IngredientSelectionListAdapter beforehand. When the list is filtered, all
	 * entries that are removed are saved to a backup list, so the filtering is
	 * completly reversable.
	 */
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

				if (!(selectedCategoryButtonIdx == FILTER_FOR_CATEGORY_SELECTED)) {

					if (selectedCategoryButtonIdx == DONT_FILTER_FOR_CATEGORY) {
						filteredIngredientList.addAll(ingredientList);
					} else if ((selectedCategoryButtonIdx == FILTER_FOR_CATEGORY_ALCOHOLIC)
							|| (selectedCategoryButtonIdx == FILTER_FOR_CATEGORY_NON_ALCOHOLIC)
							|| (selectedCategoryButtonIdx == FILTER_FOR_CATEGORY_MISC)) {

						char prefix = ("" + selectedCategoryButtonIdx + "")
								.charAt(0);
						for (int idx = 0; idx < ingredientList.size(); idx++) {
							if (prefix == ingredientList.get(idx)
									.getCategoryPrefixChar()) {
								filteredIngredientList.add(ingredientList
										.get(idx));
							} else {
								notShownIngList.add(ingredientList.get(idx));
							}
						}

					}
					if (!(searchViewQueryText.length() == 0)
							|| (searchViewQueryText == null)) {
						searchViewQueryText = searchViewQueryText.toLowerCase();
						for (int idx = 0; idx < filteredIngredientList.size(); idx++) {
							if (!(filteredIngredientList.get(idx).getIngName()
									.toLowerCase()
									.contains(searchViewQueryText))) {
								notShownIngList.add(filteredIngredientList
										.get(idx));
								filteredIngredientList.remove(idx);
								idx--;
							}
						}
					}
				} else {
					for (int idx = 0; idx < ingredientList.size(); idx++) {
						if (ingredientList.get(idx).isSelected()) {
							filteredIngredientList.add(ingredientList.get(idx));
						} else {
							notShownIngList.add(ingredientList.get(idx));
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

	public void setSearchViewQueryText(String queryText) {
		searchViewQueryText = queryText;
	}

	public String getSearchViewQueryText() {
		return searchViewQueryText;
	}

	public void setSelectedCategoryButton(int buttonIdx) {
		selectedCategoryButtonIdx = buttonIdx;
	}

	public int getSelectedCategoryButton() {
		return selectedCategoryButtonIdx;
	}

	private void resetFilter() {
		ingredientList.addAll(notShownIngList);
		notShownIngList.clear();
	}

	/*
	 * Creates and returns and ArrayList<IngredientType> that contains all the
	 * selected IngredientTypes from both the currently visible list as well as
	 * from the list containing the IngredientTypes that are currently not shown. 
	 */
	public ArrayList<IngredientType> getSelectedIngredientTypes() {
		ArrayList<IngredientType> toRemove = new ArrayList<IngredientType>();
		ArrayList<IngredientType> ings = new ArrayList<IngredientType>();
		ings.addAll(ingredientList);
		ings.addAll(notShownIngList);
		for (IngredientType ingType : ings) {
			if (!ingType.isSelected()) {
				toRemove.add(ingType);
			}
		}
		ings.removeAll(toRemove);
		return ings;
	}
}
