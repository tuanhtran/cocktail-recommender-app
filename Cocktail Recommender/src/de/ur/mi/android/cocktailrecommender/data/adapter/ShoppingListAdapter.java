package de.ur.mi.android.cocktailrecommender.data.adapter;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.CRDatabase;
import de.ur.mi.android.cocktailrecommender.data.Recipe;
import de.ur.mi.android.cocktailrecommender.data.RecipeIngredient;
import de.ur.mi.android.cocktailrecommender.data.ShoppingList;
import de.ur.mi.android.cocktailrecommender.fragments.RecipeListFragment.OnRecipeSelectedListener;

/*
 * The adapter creates a nested ExpandableListView with the following hierarchy per parent group item (a single shoppinglist entry):
 * shoppinglistentry -> recipes, ingredients
 * recipes -> recipe children
 * ingredients -> ingredient children
 * 
 */

public class ShoppingListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<ShoppingList> shoppingLists;
	private OnRecipeSelectedListener listener;

	public ShoppingListAdapter(Context context,
			ArrayList<ShoppingList> shoppingLists,
			OnRecipeSelectedListener listener) {

		this.context = context;
		this.shoppingLists = shoppingLists;
		this.listener = listener;
	}

	@Override
	public int getGroupCount() {
		return shoppingLists.size();
	}

	// Has one child consisting of two ExpandableListViews
	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return shoppingLists.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return shoppingLists.get(groupPosition).getIngredients()[childPosition];
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.listitem_shopping_list,
					null);

		}

		TextView shoppingListName = (TextView) convertView
				.findViewById(R.id.shopping_list_name);
		shoppingListName
				.setText(shoppingLists.get(groupPosition).getListName());

		ImageView shoppingListDelete = (ImageView) convertView
				.findViewById(R.id.shopping_list_delete_button);
		shoppingListDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
				alertDialogBuilder.setCancelable(false);
				alertDialogBuilder.setTitle(context.getResources().getString(
						R.string.shopping_list_delete)
						+ " " + shoppingLists.get(groupPosition).getListName());
				alertDialogBuilder.setNegativeButton(R.string.generic_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}
						});
				alertDialogBuilder.setPositiveButton(R.string.generic_positive,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								CRDatabase.getInstance(context)
										.deleteShoppingList(
												shoppingLists
														.get(groupPosition));
								shoppingLists.remove(groupPosition);
								notifyDataSetChanged();

							}
						});
				alertDialogBuilder.create();
				alertDialogBuilder.show();
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});

		return convertView;
	}

	/*
	 * ExpandableListViews recipeList and ingList are children that register
	 * their own adapters
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(
					R.layout.listitem_shopping_list_child, null);
		}

		ExpandableListView recipeList = (ExpandableListView) convertView
				.findViewById(R.id.shopping_list_recipe_view);
		recipeList.setAdapter(new ShoppingListRecipeAdapter(context,
				shoppingLists.get(groupPosition).getRecipes()));
		ExpandableListView ingList = (ExpandableListView) convertView
				.findViewById(R.id.shopping_list__ing_view);
		ingList.setAdapter(new ShoppingListIngAdapter(context, shoppingLists
				.get(groupPosition).getIngredients()));

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * Adapter for the recipe ExpandableListView; click on a child fires a
	 * callback to an onRecipeSelectedListener
	 */
	private class ShoppingListRecipeAdapter extends BaseExpandableListAdapter {

		private int[] recipeIds;
		private Context context;

		public ShoppingListRecipeAdapter(Context context, int[] recipeIds) {
			this.recipeIds = recipeIds;
			this.context = context;
		}

		@Override
		public int getGroupCount() {
			return 1;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return recipeIds.length;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return recipeIds;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return recipeIds[childPosition];
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {

			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(
						R.layout.listitem_shopping_sub_list_child, null);

			}
			TextView recipeParent = (TextView) convertView
					.findViewById(R.id.shopping_list_child_entry);
			recipeParent.setText(R.string.recipes);
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this.context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(
						R.layout.listitem_shopping_sub_list_child, null);
			}
			final Recipe recipe = CRDatabase.getInstance(context)
					.getRecipeFromID(recipeIds[childPosition]);
			TextView entry = (TextView) convertView
					.findViewById(R.id.shopping_list_child_entry);
			entry.setText(recipe.getName());
			entry.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onRecipeSelected(recipe);

				}
			});
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}

	}
	
	
	/*
	 * Adapter for ingredients ExpandableListView
	 */
	private class ShoppingListIngAdapter extends BaseExpandableListAdapter {
		private RecipeIngredient[] ingredients;
		private Context context;

		public ShoppingListIngAdapter(Context context,
				RecipeIngredient[] ingredients) {
			this.ingredients = ingredients;
			this.context = context;
		}

		@Override
		public int getGroupCount() {
			return 1;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return ingredients.length;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return ingredients;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return ingredients[childPosition];
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(
						R.layout.listitem_shopping_sub_list_child, null);

			}
			TextView ingParent = (TextView) convertView
					.findViewById(R.id.shopping_list_child_entry);
			ingParent.setText(R.string.ingredients);
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this.context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(
						R.layout.listitem_shopping_sub_list_child, null);
			}
			RecipeIngredient ingredient = shoppingLists.get(groupPosition)
					.getIngredients()[childPosition];

			TextView ingredientItem = (TextView) convertView
					.findViewById(R.id.shopping_list_child_entry);
			ingredientItem.setText(ingredient.getIngName());
			//set onClickListener to delete entry
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}

	}

}
