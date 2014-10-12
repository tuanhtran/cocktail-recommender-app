package de.ur.mi.android.cocktailrecommender.data.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.CRDatabase;
import de.ur.mi.android.cocktailrecommender.data.RecipeIngredient;
import de.ur.mi.android.cocktailrecommender.data.ShoppingList;

public class ShoppingListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<ShoppingList> shoppingLists;

	public ShoppingListAdapter(Context context,
			ArrayList<ShoppingList> shoppingLists) {

		this.context = context;
		this.shoppingLists = shoppingLists;

	}

	@Override
	public int getGroupCount() {
		return shoppingLists.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return shoppingLists.get(groupPosition).getIngredients().length;
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
			
			//AlertDialogs for the deletion of shopping list items
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

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(
					R.layout.listitem_shopping_list_child, null);
		}

		final RecipeIngredient[] ingredients = shoppingLists.get(groupPosition)
				.getIngredients();

		TextView ingredientItem = (TextView) convertView
				.findViewById(R.id.shopping_list_ing_entry);
		ingredientItem.setText(ingredients[childPosition].getIngName());
		ImageView ingDeleteButton = (ImageView) convertView
				.findViewById(R.id.shopping_list_ing_entry_delete_button);
		ingDeleteButton.setOnClickListener(new View.OnClickListener() {

			//AlertDialogs for the deletion of ingredient items
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
				alertDialogBuilder.setNegativeButton(R.string.generic_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}
						});
				if (ingredients.length > 1) {
					alertDialogBuilder.setTitle(ingredients[childPosition]
							.getIngName()
							+ " "
							+ context
									.getResources()
									.getString(
											R.string.shopping_list_ing_entry_deletion_title));
					alertDialogBuilder.setPositiveButton(
							R.string.generic_positive,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									ArrayList<RecipeIngredient> tempIngList = new ArrayList<RecipeIngredient>(
											Arrays.asList(ingredients));
									tempIngList.remove(childPosition);
									RecipeIngredient[] modifiedIngList = tempIngList
											.toArray(new RecipeIngredient[tempIngList
													.size()]);
									shoppingLists.get(groupPosition)
											.setIngredients(modifiedIngList);
									CRDatabase
											.getInstance(context)
											.addShoppingList(
													shoppingLists
															.get(groupPosition),
													false);
									notifyDataSetChanged();
								}

							});
				} else {
					alertDialogBuilder.setTitle(ingredients[childPosition]
							.getIngName()
							+ " "
							+ context
									.getResources()
									.getString(
											R.string.shopping_list_ing_entry_deletion_title));
					alertDialogBuilder
							.setMessage(R.string.shopping_list_ing_entry_last_item_deletion_message);
					alertDialogBuilder.setPositiveButton(
							R.string.generic_positive,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									CRDatabase
											.getInstance(context)
											.deleteShoppingList(
													shoppingLists
															.get(groupPosition));
									shoppingLists.remove(groupPosition);
									notifyDataSetChanged();
								}
							});
				}
				alertDialogBuilder.create();
				alertDialogBuilder.show();
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
