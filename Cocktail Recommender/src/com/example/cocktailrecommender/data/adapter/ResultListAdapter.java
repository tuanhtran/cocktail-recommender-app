package com.example.cocktailrecommender.data.adapter;

import java.util.ArrayList;
import java.util.Collections;

import com.example.cocktailrecommender.R;
import com.example.cocktailrecommender.data.SearchRecipeResult;

import android.content.Context;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class ResultListAdapter extends ArrayAdapter<SearchRecipeResult>
		implements Filterable {

	private Context context;
	private ArrayList<SearchRecipeResult> resultList;
	private ArrayList<SearchRecipeResult> notShownResults = new ArrayList<SearchRecipeResult>();

	public ResultListAdapter(Context context,
			ArrayList<SearchRecipeResult> results) {
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

		final SearchRecipeResult searchResult = resultList.get(position);

		if (searchResult != null) {
			TextView searchResultName = (TextView) view
					.findViewById(R.id.recipe_result_name);

			searchResultName.setText(searchResult.getRecipe().getName());
		}
		return view;
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
						.addAll((ArrayList<SearchRecipeResult>) results.values);
				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				ArrayList<SearchRecipeResult> filteredResultList = new ArrayList<SearchRecipeResult>();

				if (constraint.equals("")) {
					filteredResultList.addAll(resultList);
				} else {
					for (int idx = 0; idx < resultList.size(); idx++) {
						if (resultList.get(idx).getRecipe().getName()
								.toLowerCase().contains(constraint)) {
							filteredResultList
									.add(resultList.get(idx));
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
}
