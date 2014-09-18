package de.ur.mi.android.cocktailrecommender.data.adapter;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.CRDatabase;
import de.ur.mi.android.cocktailrecommender.data.RecipeIngredient;
import de.ur.mi.android.cocktailrecommender.data.ShoppingList;

public class ShoppingListAdapter extends ArrayAdapter<ShoppingList> {

	private Context context;
	private ArrayList<ShoppingList> shoppingLists;

	public ShoppingListAdapter(Context context, ArrayList<ShoppingList> shoppingLists) {
		super(context, R.layout.listitem_shopping_list, shoppingLists);
		this.context = context;
		this.shoppingLists = shoppingLists;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.listitem_shopping_list,
					null);

		}

		final ShoppingList list = shoppingLists.get(position);
		if (list != null) {
			TextView ShoppingListTextView = (TextView) view
					.findViewById(R.id.shopping_list_entry);

			ShoppingListTextView.setText(list.getListName());
			
		}

		view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				alertDialogBuilder.setTitle(R.string.ingredients_to_buy);
				alertDialogBuilder.setPositiveButton(R.string.generic_positive, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
					}
				});
				RecipeIngredient[] ingredients = list.getIngredients();
				CharSequence[] ingredientNames = new CharSequence[ingredients.length];
				for (int ingIdx = 0; ingIdx < ingredients.length; ingIdx++){
					ingredientNames[ingIdx] = ingredients[ingIdx].getIngName();
				}
				alertDialogBuilder.setItems(ingredientNames, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				alertDialogBuilder.create();
				alertDialogBuilder.show();
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});
		
		view.setOnLongClickListener(new OnLongClickListener() {
			
			public boolean onLongClick(View v) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				alertDialogBuilder.setTitle(R.string.shopping_list_delete);
				alertDialogBuilder.setPositiveButton(R.string.generic_positive, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						CRDatabase.getInstance(context).deleteShoppingList(list);
						shoppingLists.remove(position);
						dialog.dismiss();
						notifyDataSetChanged();
					}
				});
				
				alertDialogBuilder.setNegativeButton(R.string.generic_cancel, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
					}
				});
				alertDialogBuilder.create();
				alertDialogBuilder.show();
				
				return false;
			}
		});
		return view;
	}
	
}
