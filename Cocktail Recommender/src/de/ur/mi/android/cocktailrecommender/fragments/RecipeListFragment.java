package de.ur.mi.android.cocktailrecommender.fragments;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.RecipeBookActivity;
import de.ur.mi.android.cocktailrecommender.data.CRDatabase;
import de.ur.mi.android.cocktailrecommender.data.Recipe;
import de.ur.mi.android.cocktailrecommender.data.RecipeListEntry;
import de.ur.mi.android.cocktailrecommender.data.adapter.RecipeListAdapter;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeFragment.OnFavStatusChangedListener;

/*
 *Fragment displays lists of recipes.
 *RecipeBookActivity uses this fragment for every tab.
 *The ability to delete list items with a longClick is set by the corresponding constructor,
 *is only used for the favorites list in the recipe book.
 */

public class RecipeListFragment extends Fragment implements
		OnItemClickListener, OnItemLongClickListener,
		OnFavStatusChangedListener {
	View fragmentView;
	ArrayList<RecipeListEntry> recipeList;
	RecipeListAdapter adapter;
	private OnRecipeSelectedListener listener;
	private boolean enableLongClick = false;

	public RecipeListFragment() {
		recipeList = new ArrayList<RecipeListEntry>();
	}

	public RecipeListFragment(ArrayList<RecipeListEntry> recipeList) {
		this.recipeList = recipeList;
	}

	public RecipeListFragment(ArrayList<RecipeListEntry> recipeList,
			boolean enableLongClick) {
		this.recipeList = recipeList;
		this.enableLongClick = enableLongClick;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.recipe_list_fragment_layout,
				container, false);
		initUI();
		return fragmentView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CRDatabase.getInstance(getActivity()).addToHistory(
				recipeList.get(position));
		Recipe selectedRecipe = recipeList.get(position).getRecipe();
		if (!((RecipeBookActivity) getActivity()).isInLandscapeMode()) {
			adapter.resetFilter();
			adapter.notifyDataSetChanged();
		}
		listener.onRecipeSelected(selectedRecipe);
	}

	/*
	 * LongClick to delete list items, only used for favorites in this app
	 * context!
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int position, long id) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
		String dialogTitle = recipeList.get(position).getRecipe().getName()
				+ " "
				+ getResources().getString(
						R.string.favorite_list_deletion_dialog_title);
		alertDialogBuilder.setTitle(dialogTitle);
		alertDialogBuilder.setNegativeButton(R.string.generic_cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				});
		alertDialogBuilder.setPositiveButton(R.string.generic_positive,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						CRDatabase.getInstance(getActivity())
								.removeFromFavorites(recipeList.get(position));
						adapter.notifyDataSetChanged();

					}
				});
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.create();
		alertDialogBuilder.show();
		return true;
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

	}

	private void initListView() {
		ListView recipeListView = (ListView) fragmentView
				.findViewById(R.id.recipe_listview);
		recipeListView.setOnItemClickListener(this);
		if (enableLongClick)
			recipeListView.setOnItemLongClickListener(this);
		adapter = new RecipeListAdapter(getActivity(), recipeList);
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
		if(adapter != null)
			adapter.notifyDataSetChanged();

	}

	@Override
	public void onFavAdded(RecipeListEntry recipeToFavorite) {
		if(adapter != null)
			adapter.notifyDataSetChanged();

	}

}
