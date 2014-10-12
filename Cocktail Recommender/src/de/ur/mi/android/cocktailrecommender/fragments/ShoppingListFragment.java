package de.ur.mi.android.cocktailrecommender.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.CRDatabase;
import de.ur.mi.android.cocktailrecommender.data.ShoppingList;
import de.ur.mi.android.cocktailrecommender.data.adapter.ShoppingListAdapter;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeListFragment.OnRecipeSelectedListener;

public class ShoppingListFragment extends Fragment{
	private View fragmentView;
	private ArrayList<ShoppingList> shoppingLists;
	private ShoppingListAdapter adapter;
	private OnRecipeSelectedListener listener;
	
	public ShoppingListFragment(OnRecipeSelectedListener listener){
		shoppingLists = CRDatabase.getInstance(getActivity()).getAllShoppingLists();
		this.listener = listener;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.shopping_list_fragment_layout,
				container, false);
		
		initShoppingList();
		return fragmentView;
	}

	private void initShoppingList() {
		adapter = new ShoppingListAdapter(getActivity(), shoppingLists, listener);
		ExpandableListView listView = (ExpandableListView) fragmentView.findViewById(R.id.shopping_list_view);
		listView.setAdapter(adapter);
		
	}


}
