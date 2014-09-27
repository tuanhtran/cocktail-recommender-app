package de.ur.mi.android.cocktailrecommender;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import de.ur.mi.android.cocktailrecommender.data.CRDatabase;
import de.ur.mi.android.cocktailrecommender.data.ShoppingList;
import de.ur.mi.android.cocktailrecommender.data.adapter.ShoppingListAdapter;

public class ShoppingListActivity extends ActionBarActivity {

	private ArrayList<ShoppingList> shoppingLists;
	private ShoppingListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_list);

		getShoppingLists();
		setShoppingListAdapter();
		setViews();
	}

	private void setViews() {
		ExpandableListView shoppingListView = (ExpandableListView) findViewById(R.id.shopping_list_view);
		shoppingListView.setAdapter(adapter);
	}

	private void getShoppingLists() {
		shoppingLists = CRDatabase.getInstance(this).getAllShoppingLists();

	}

	private void setShoppingListAdapter() {
		adapter = new ShoppingListAdapter(this, shoppingLists);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.action_nav_menu){
			Intent openMenu = new Intent(this, MenuActivity.class);
			openMenu.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(openMenu);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
