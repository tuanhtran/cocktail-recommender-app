package de.ur.mi.android.cocktailrecommender.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.CRDatabase;
import de.ur.mi.android.cocktailrecommender.data.Recipe;
import de.ur.mi.android.cocktailrecommender.data.RecipeListEntry;
import de.ur.mi.android.cocktailrecommender.data.adapter.RecipeListAdapter;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeFragment.OnFavStatusChangedListener;

public class RecipeListFragment extends Fragment implements OnItemClickListener, OnFavStatusChangedListener {
	View fragmentView;
	ArrayList<RecipeListEntry> recipeList;
	RecipeListAdapter adapter;
	private OnRecipeSelectedListener listener;
	private boolean noMatchRate = false;
	

	public RecipeListFragment() {
		recipeList = new ArrayList<RecipeListEntry>();
	}

	public RecipeListFragment(ArrayList<RecipeListEntry> recipeList) {
		this.recipeList = recipeList;
	}
	
	public RecipeListFragment(ArrayList<RecipeListEntry> recipeList, boolean noMatchRate) {
		this.recipeList = recipeList;
		this.noMatchRate = noMatchRate;
	}

	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.recipe_list_fragment_layout,
				container, false);
		initData();
		initUI();
		return fragmentView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CRDatabase.getInstance(getActivity()).addToHistory(
				recipeList.get(position));
		listener.onRecipeSelected(recipeList.get(position).getRecipe());
		
	}
	
	


	private void initData() {

	}

	private void initUI() {
		initSearchView();
		initListView();
	}

	private void initSearchView() {
		SearchView filterBar = (SearchView) fragmentView
				.findViewById(R.id.recipe_list_filter_bar);
		filterBar.setOnQueryTextListener(new OnQueryTextListener() {
			public boolean onQueryTextSubmit(String queryString) {
				return true;
			}

			public boolean onQueryTextChange(String queryString) {
				adapter.getFilter().filter(queryString);
				return true;
			}
		});
		filterBar.setIconifiedByDefault(false);
	}

	private void initListView() {
		ListView recipeListView = (ListView) fragmentView
				.findViewById(R.id.recipe_listview);
		recipeListView.setOnItemClickListener(this);
		adapter = new RecipeListAdapter(getActivity(), recipeList);
		if(noMatchRate)
			adapter.dontdisplayNoMatchRate();
		recipeListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	public void setOnRecipeSelectedListener(OnRecipeSelectedListener listener) {
		this.listener = listener;
	}

	public interface OnRecipeSelectedListener {
		public void onRecipeSelected(Recipe recipe);
	}


	@Override
	public void onFavRemoved(RecipeListEntry recipeToFavorite) {
		adapter.notifyDataSetChanged();
		
	}

	@Override
	public void onFavAdded(RecipeListEntry recipeToFavorite) {
		adapter.notifyDataSetChanged();
		
	}

}
