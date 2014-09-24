package de.ur.mi.android.cocktailrecommender.data.adapter;

import java.util.ArrayList;
import java.util.Collections;

import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.RecipeIngredient;
import de.ur.mi.android.cocktailrecommender.data.RecipeSearchResult;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class ResultListAdapter extends ArrayAdapter<RecipeSearchResult>
		implements Filterable {

	private static final int ING_PREVIEW_SIZE_THRESHOLD = 30;

	private Context context;
	private ArrayList<RecipeSearchResult> resultList;
	private ArrayList<RecipeSearchResult> notShownResults = new ArrayList<RecipeSearchResult>();
	private boolean noMatchRate = false;
	
	public ResultListAdapter(Context context,
			ArrayList<RecipeSearchResult> results) {
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

		final RecipeSearchResult searchResult = resultList.get(position);

		if (searchResult != null) {
			String resultNameText = searchResult.getRecipe().getName();
			if(!noMatchRate){
				resultNameText = resultNameText + " - "
						+ searchResult.getMatchRate() + "%";
			}
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
						.addAll((ArrayList<RecipeSearchResult>) results.values);
				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				ArrayList<RecipeSearchResult> filteredResultList = new ArrayList<RecipeSearchResult>();

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

	private void resetFilter() {
		resultList.addAll(notShownResults);
		notShownResults.clear();
	}

	public void dontdisplayNoMatchRate() {
		noMatchRate = true;
		
	}
}
