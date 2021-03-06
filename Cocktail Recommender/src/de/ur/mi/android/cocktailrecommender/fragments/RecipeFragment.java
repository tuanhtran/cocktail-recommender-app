package de.ur.mi.android.cocktailrecommender.fragments;

import java.util.ArrayList;

/*
 * RecipeFragment displays the recipe with its name, tags, ingredient list and the preparation text.
 * Allows adding to Favorites and creating shopping lists.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter.LengthFilter;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.CRDatabase;
import de.ur.mi.android.cocktailrecommender.data.CocktailRecommenderValues;
import de.ur.mi.android.cocktailrecommender.data.Recipe;
import de.ur.mi.android.cocktailrecommender.data.RecipeIngredient;
import de.ur.mi.android.cocktailrecommender.data.RecipeListEntry;
import de.ur.mi.android.cocktailrecommender.data.Tag;
import de.ur.mi.android.cocktailrecommender.data.adapter.RecipePageIngredientListAdapter;

public class RecipeFragment extends Fragment {
	private View fragmentView;
	private Recipe recipe;
	private TextView recipeName;
	private ListView recipeIngredients;
	private TextView recipePreparation;
	private ImageButton recipeToFavoritesToggle;
	private ImageButton ingredientsToShoppingList;
	private LinearLayout tagDisplayParent;
	private RecipePageIngredientListAdapter adapter;
	private ArrayList<RecipeIngredient> ingredients;
	private OnFlingListener listener;
	private OnShoppingListAddListener shoppingListener;
	private OnFavStatusChangedListener favStatusListener;
	private Toast toast;

	/*
	 * This Fragment is used to display all the information about a recipe
	 * (name, ingredients, tags etc).
	 */

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

	/*
	 * Guarantees that recipe is never null (important for orientation change):
	 * Gets the last looked at recipe and sets the instance field if available;
	 * else the first recipe of the complete recipe list is chosen.
	 */
	private void initData() {
		if (recipe == null) {
			Recipe dummyRecipe = CRDatabase.getInstance(getActivity())
					.getHistory().get(0).getRecipe();
			if (dummyRecipe != null) {
				recipe = dummyRecipe;
			} else {
				recipe = CRDatabase.getInstance(getActivity())
						.getFullRecipeList().get(0).getRecipe();
			}
		}
		ingredients = new ArrayList<RecipeIngredient>();
	}

	private void initUI() {
		recipeName = (TextView) fragmentView
				.findViewById(R.id.recipe_page_name);
		recipeIngredients = (ListView) fragmentView
				.findViewById(R.id.recipe_page_ingredient_list);
		recipePreparation = (TextView) fragmentView
				.findViewById(R.id.recipe_page_preparation);
		ingredientsToShoppingList = (ImageButton) fragmentView
				.findViewById(R.id.recipe_page_create_shopping_list_button);
		recipeToFavoritesToggle = (ImageButton) fragmentView
				.findViewById(R.id.recipe_page_add_to_favs_toggle);

		tagDisplayParent = (LinearLayout) fragmentView
				.findViewById(R.id.recipe_page_tag_display_parent);
		for (Tag tag : recipe.getTags()) {
			addTagIconToLayout(tagDisplayParent, tag);
		}
		initAdapter();
		updateData();
	}

	private void addTagIconToLayout(LinearLayout tagDisplayParent, final Tag tag) {
		ImageView tagIcon = new ImageView(getActivity());
		tagIcon.setImageResource(CocktailRecommenderValues
				.getCorrectTagImageResource(tagIcon, tag));
		float scaleFactor = getResources().getDisplayMetrics().density;
		int pixelValue = (int) (getResources().getInteger(
				R.integer.recipe_page_tag_icon_padding_dp)
				* scaleFactor + 0.5f);
		tagIcon.setPadding(pixelValue, 0, pixelValue, 0);
		tagIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (toast == null) {
					toast = Toast.makeText(getActivity(), "",
							Toast.LENGTH_SHORT);
				}
				toast.setText(tag.getTagName());
				toast.show();
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});
		tagDisplayParent.addView(tagIcon);
	}

	private boolean checkIfFavorite(RecipeListEntry recipeToFavorite) {
		boolean isFavorite = false;
		ArrayList<RecipeListEntry> favorites = CRDatabase.getInstance(
				getActivity()).getFavorites();
		for (RecipeListEntry favRecipe : favorites) {
			if (favRecipe.compareTo(recipeToFavorite) == 0) {
				isFavorite = true;
				break;
			}
		}
		return isFavorite;
	}

	private void initAdapter() {
		adapter = new RecipePageIngredientListAdapter(getActivity(),
				ingredients);
		recipeIngredients.setAdapter(adapter);
	}

	public void updateData() {
		recipeName.setText(recipe.getName());
		updateTagIcons();
		updateIngredientListView();
		recipePreparation.setText(recipe.getPreparation());
		setFavToggle();
		setShopListButton();

	}

	private void updateTagIcons() {
		tagDisplayParent.removeAllViews();
		for (Tag tag : recipe.getTags()) {
			addTagIconToLayout(tagDisplayParent, tag);
		}
	}

	private void updateIngredientListView() {
		ingredients.clear();
		ingredients.addAll(recipe.getIngredientsAsList());
		adapter.notifyDataSetChanged();
		adjustListViewHeight(recipeIngredients);
	}

	private void setShopListButton() {
		ingredientsToShoppingList
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						RecipeIngredient[] selectedIngs = adapter
								.getSelectedIngredients();
						v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						if (selectedIngs.length > 0) {
							shoppingListener.onAddToShoppingList(adapter
									.getSelectedIngredients());
						} else {
							shoppingListener.onNoIngredientSelected();
						}
					}
				});

	}

	private void setFavToggle() {
		if (checkIfFavorite(new RecipeListEntry(recipe))) {
			recipeToFavoritesToggle
					.setImageResource(R.drawable.ic_action_star_favorite);
		} else {
			recipeToFavoritesToggle
					.setImageResource(R.drawable.ic_action_star_not_favorite);
		}

		recipeToFavoritesToggle.setOnClickListener(new View.OnClickListener() {

			RecipeListEntry recipeToFavorite = new RecipeListEntry(recipe);

			@Override
			public void onClick(View v) {
				boolean isFavorite = checkIfFavorite(recipeToFavorite);
				if (toast == null)
					toast = Toast.makeText(getActivity(), "",
							Toast.LENGTH_SHORT);
				if (isFavorite) {
					CRDatabase.getInstance(getActivity()).removeFromFavorites(
							recipeToFavorite);
					v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
					recipeToFavoritesToggle
							.setImageResource(R.drawable.ic_action_star_not_favorite);
					if (isInLandscapeMode() && favStatusListener != null)
						favStatusListener.onFavRemoved(recipeToFavorite);
					toast.setText(R.string.toast_unfavorite);
					toast.show();
				} else {
					CRDatabase.getInstance(getActivity()).addToFavorites(
							recipeToFavorite);
					v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
					recipeToFavoritesToggle
							.setImageResource(R.drawable.ic_action_star_favorite);
					if (isInLandscapeMode() && favStatusListener != null)
						favStatusListener.onFavAdded(recipeToFavorite);
					toast.setText(R.string.toast_favorite);
					toast.show();
				}

			}

		});
	}

	/*
	 * Due to an unexpected problem that occurred when the ingredient list was
	 * displayed as a (dynamically created) Linear Layout, a ListView is used
	 * instead. Although this is not optimal in terms of performance, there is
	 * no noticeable negative impact in this case. To prevent any problems
	 * created by a vertically scrolling ListView within a vertically scrolling
	 * ScrollView, the height of the ListView is changed to the combined height
	 * of its ChildViews (+separators). Since the ListView is now high enough to
	 * show all its ChildViews there is no need for actual scrolling. This
	 * workaround as well as the method to adjust the ListView's height is based
	 * on the suggestion and code of the User "DougW" on stackoveerflow.com.
	 * 
	 * URL: http://stackoverflow.com/questions/3495890/how-can
	 * -i-put-a-listview-into-a-scrollview-without-it-collapsing
	 */
	private void adjustListViewHeight(ListView listView) {
		RecipePageIngredientListAdapter adapter = (RecipePageIngredientListAdapter) listView
				.getAdapter();
		if (adapter == null) {
			return;
		}

		int totalHeight = listView.getPaddingTop()
				+ listView.getPaddingBottom();
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
								|| (Math.abs(e1.getY() - e2.getY()) > FLING_MAX_VERTICAL)
								|| listener == null) {
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

	private boolean isInLandscapeMode() {
		return (getActivity().getWindowManager().getDefaultDisplay()
				.getRotation() == Surface.ROTATION_90 || getActivity()
				.getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_270);
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

		public void onNoIngredientSelected();
	}

	public void setOnShoppingListAddListener(OnShoppingListAddListener listener) {
		shoppingListener = listener;
	}

	public interface OnFavStatusChangedListener {

		public void onFavRemoved(RecipeListEntry recipeToFavorite);

		public void onFavAdded(RecipeListEntry recipeToFavorite);
	}

	public void setOnFavStatusChangedListener(
			OnFavStatusChangedListener listener) {
		favStatusListener = listener;
	}

}
