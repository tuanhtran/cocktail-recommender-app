package de.ur.mi.android.cocktailrecommender.fragments;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.Recipe;
import de.ur.mi.android.cocktailrecommender.data.RecipeIngredient;
import de.ur.mi.android.cocktailrecommender.data.adapter.RecipePageIngredientListAdapter;

public class RecipeFragment extends Fragment {
	private View fragmentView;
	private Recipe recipe;
	private TextView recipeName;
	private ListView recipeIngredients;
	private TextView recipePreparation;
	private Button ingredientsToShoppingList;
	private RecipePageIngredientListAdapter adapter;
	private ArrayList<RecipeIngredient> ingredients;
	private OnFlingListener listener;
	private OnShoppingListAddListener shoppingListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.recipe_fragment_layout,
				container, false);
		initData();
		initUI();
		final GestureDetector detector = getNewDetector();
		fragmentView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});
		return fragmentView;
	}

	private void initData() {
		ingredients = new ArrayList<RecipeIngredient>();
	}

	private void initUI() {
		recipeName = (TextView) fragmentView
				.findViewById(R.id.recipe_page_name);
		recipeIngredients = (ListView) fragmentView
				.findViewById(R.id.recipe_page_ingredient_list);
		recipePreparation = (TextView) fragmentView
				.findViewById(R.id.recipe_page_preparation);
		ingredientsToShoppingList = (Button) fragmentView
				.findViewById(R.id.recipe_page_create_shopping_list_button);
		ingredientsToShoppingList
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						shoppingListener.onAddToShoppingList(adapter.getSelectedIngredients());
						v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
					}
				});
		initAdapter();
		updateData();
	}

	private void initAdapter() {
		adapter = new RecipePageIngredientListAdapter(getActivity(),
				ingredients);
		recipeIngredients.setAdapter(adapter);
	}

	public void updateData() {
		ingredients.clear();
		ingredients.addAll(recipe.getIngredientsAsList());
		adapter.notifyDataSetChanged();
		adjustListViewHeight(recipeIngredients);
		recipeName.setText(recipe.getName());
		recipePreparation.setText(recipe.getPreparation());
	}

	private void adjustListViewHeight(ListView listView) {
		RecipePageIngredientListAdapter adapter = (RecipePageIngredientListAdapter) listView
				.getAdapter();
		if (adapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < adapter.getCount(); i++) {
			View listItem = adapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (adapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}

	private GestureDetector getNewDetector() {
		return new GestureDetector(getActivity(),
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						final int FLING_MIN_HORIZONTAL = getResources()
								.getInteger(R.integer.fling_min_horizontal);
						final int FLING_MAX_VERTICAL = getResources()
								.getInteger(R.integer.fling_max_vertical);
						final int FLING_MIN_VELOCITY = getResources()
								.getInteger(R.integer.fling_min_velocity);

						if (e1 == null
								|| e2 == null
								|| (Math.abs(e1.getY() - e2.getY()) > FLING_MAX_VERTICAL)) {
							return false;
						}
						if (e1.getX() - e2.getX() > FLING_MIN_HORIZONTAL
								&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
							listener.onLeftToRightFling();
						} else if (e2.getX() - e1.getX() > FLING_MIN_HORIZONTAL
								&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
							listener.onRightToLeftFling();
						}
						return super.onFling(e1, e2, velocityX, velocityY);
					}
				});

	}

	public interface OnFlingListener {
		public void onRightToLeftFling();

		public void onLeftToRightFling();
	}

	public void setOnFlingListener(OnFlingListener listener) {
		this.listener = listener;
	}

	
	
	public interface OnShoppingListAddListener {
		public void onAddToShoppingList(RecipeIngredient[] ingredients);
	}
	
	public void setOnShoppingListAddListener(OnShoppingListAddListener listener) {
		shoppingListener = listener;
	}

}
