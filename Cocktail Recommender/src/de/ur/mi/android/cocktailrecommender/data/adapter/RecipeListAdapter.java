package de.ur.mi.android.cocktailrecommender.data.adapter;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.RecipeIngredient;
import de.ur.mi.android.cocktailrecommender.data.RecipeListEntry;

/*
 * Adapter for the recipe list in RecipeListFragment
 */
public class RecipeListAdapter extends ArrayAdapter<RecipeListEntry>
		implements Filterable {

	private static final int ING_PREVIEW_SIZE_THRESHOLD = 30;

	private Context context;
	private ArrayList<RecipeListEntry> resultList;
	private ArrayList<RecipeListEntry> notShownResults = new ArrayList<RecipeListEntry>();
	
	public RecipeListAdapter(Context context,
			ArrayList<RecipeListEntry> results) {
		super(context, R.layout.listitem_recipe_result_list, results);

		this.context = context;
		this.resultList = results;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.listitem_recipe_result_list, null);

		}

		final RecipeListEntry searchResult = resultList.get(position);

		if (searchResult != null) {
			String resultNameText = searchResult.getRecipe().getName();
			TextView searchResultName = (TextView) view
					.findViewById(R.id.recipe_result_name);
			TextView searchResultIngPreview = (TextView) view
					.findViewById(R.id.recipe_result_ing_preview);
			searchResultName.setText(resultNameText);
			searchResultIngPreview.setText(getIngPreview(searchResult
					.getRecipe().getIngredients()));
		}
		return view;
	}

	private CharSequence getIngPreview(RecipeIngredient[] ingredients) {
		String preview = "";

		for (RecipeIngredient rIng : ingredients) {
			preview += rIng.getIngName();
			if (preview.length() >= ING_PREVIEW_SIZE_THRESHOLD) {
				preview = preview.substring(0, ING_PREVIEW_SIZE_THRESHOLD - 3);
				preview += "...";
				return preview;
			}
			preview += ", ";
		}
		preview = preview.substring(0, preview.length() - 2);
		return preview;
	}

	@Override
	public void notifyDataSetChanged() {
		Collections.sort(resultList);
		super.notifyDataSetChanged();
	}

	/*
	 * Filters the recipe list to show only recipe that at least
	 * partially match the entered query in the SearchView
	 */
	@Override
	public Filter getFilter() {
		resetFilter();
		Filter filter = new Filter() {
			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				resultList.clear();
				resultList
						.addAll((ArrayList<RecipeListEntry>) results.values);
				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				ArrayList<RecipeListEntry> filteredResultList = new ArrayList<RecipeListEntry>();

				if (constraint.equals("")) {
					filteredResultList.addAll(resultList);
				} else {
					for (int idx = 0; idx < resultList.size(); idx++) {
						if (resultList.get(idx).getRecipe().getName()
								.toLowerCase().contains(constraint)) {
							filteredResultList.add(resultList.get(idx));
						} else {
							notShownResults.add(resultList.get(idx));
						}
					}
				}
				results.count = filteredResultList.size();
				results.values = filteredResultList;
				return results;
			}
		};
		return filter;
	}

	public void resetFilter() {
		resultList.addAll(notShownResults);
		notShownResults.clear();
	}
}
