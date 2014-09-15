package de.ur.mi.android.cocktailrecommender.fragments;

import java.util.ArrayList;

import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.Recipe;
import de.ur.mi.android.cocktailrecommender.data.RecipeSearchResult;
import de.ur.mi.android.cocktailrecommender.data.adapter.ResultListAdapter;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnQueryTextListener;

public class ResultListFragment extends Fragment implements OnItemClickListener {
	View fragmentView;
	ArrayList<RecipeSearchResult> resultList;
	ResultListAdapter adapter;
	private OnRecipeSelectedListener listener;

	public ResultListFragment(){
		resultList = new ArrayList<RecipeSearchResult>();
	}
	
	public ResultListFragment(ArrayList<RecipeSearchResult> resultList) {
		this.resultList = resultList;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.result_list_fragment_layout,
				container, false);
		initData();
		initUI();
		return fragmentView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		listener.onRecipeSelected(resultList.get(position).getRecipe());

	}

	private void initData() {

	}

	private void initUI() {
		initSearchView();
		initListView();
	}

	private void initSearchView() {
		SearchView filterBar = (SearchView) fragmentView.findViewById(
				R.id.search_result_filter_bar);
		filterBar.setOnQueryTextListener(new OnQueryTextListener() {
			public boolean onQueryTextSubmit(String queryString) {
				return true;
			}

			public boolean onQueryTextChange(String queryString) {
				adapter.getFilter().filter(queryString);
				return true;
			}
		});
	}

	private void initListView() {
		ListView resultListView = (ListView) fragmentView.findViewById(
				R.id.search_result_listview);
		resultListView.setOnItemClickListener(this);
		adapter = new ResultListAdapter(getActivity(), resultList);
		resultListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	public void setOnRecipeSelectedListener(OnRecipeSelectedListener listener) {
		this.listener = listener;
	}

	public interface OnRecipeSelectedListener {
		public void onRecipeSelected(Recipe recipe);
	}

}
