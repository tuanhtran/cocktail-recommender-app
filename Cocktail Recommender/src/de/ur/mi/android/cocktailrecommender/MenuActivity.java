package de.ur.mi.android.cocktailrecommender;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import de.ur.mi.android.cocktailrecommender.data.CocktailRecommenderValues;

/*
 * Allows navigation to the other App functions. Serves as entry point and central hub of the application. 
 */

public class MenuActivity extends ActionBarActivity {

	private TextView menuTitle;
	private Button searchButton;
	private Button recipeBookButton;
	private Button shoppingListButton;
	private Button favListButton;
	private Button historyButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		initUI();
	}

	/*
	 * Initializes the UI (mostly buttons that lead to the different components
	 * of the application).
	 */
	private void initUI() {
		menuTitle = (TextView) findViewById(R.id.main_menu_title_text);
		menuTitle.setText(R.string.main_menu_title_text);

		searchButton = (Button) findViewById(R.id.main_menu_search_button);
		searchButton.setText(R.string.main_menu_search_button_text);
		searchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				startSearchActivity();
			}
		});

		recipeBookButton = (Button) findViewById(R.id.main_menu_recipe_book_button);
		recipeBookButton.setText(R.string.main_menu_recipe_book_button_text);
		recipeBookButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				openRecipeBook(CocktailRecommenderValues.ALL_RECIPES);
			}
		});

		shoppingListButton = (Button) findViewById(R.id.main_menu_shopping_list_button);
		shoppingListButton
				.setText(R.string.main_menu_shopping_list_button_text);
		shoppingListButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				startShoppingListActivity();
			}
		});

		favListButton = (Button) findViewById(R.id.main_menu_fav_button);
		favListButton.setText(R.string.main_menu_fav_list_button_text);
		favListButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				openRecipeBook(CocktailRecommenderValues.FAV_LIST);
			}
		});

		historyButton = (Button) findViewById(R.id.main_menu_history_button);
		historyButton.setText(R.string.main_menu_history_button_text);
		historyButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				openRecipeBook(CocktailRecommenderValues.HISTORY_LIST);
			}
		});
	}

	/*
	 * Opens the Search Activity
	 */
	private void startSearchActivity() {
		Intent intent = new Intent(MenuActivity.this, SearchActivity.class);
		startActivity(intent);
	}

	/*
	 * Opens the ShoppingList Activity
	 */
	private void startShoppingListActivity() {
		Intent intent = new Intent(MenuActivity.this,
				ShoppingListActivity.class);
		startActivity(intent);
	}

	/*
	 * Opens the RecipeBook Activity and passes a value that determines which
	 * section of the RecipeBook will be shown.
	 */
	private void openRecipeBook(int recipeBookSection) {
		Intent intent = new Intent(MenuActivity.this, RecipeBookActivity.class);

		switch (recipeBookSection) {
		case CocktailRecommenderValues.ALL_RECIPES:
			intent.putExtra(CocktailRecommenderValues.FRAGMENT_TO_DISPLAY,
					CocktailRecommenderValues.ALL_RECIPES);
			startActivity(intent);
			break;
		case CocktailRecommenderValues.FAV_LIST:
			intent.putExtra(CocktailRecommenderValues.FRAGMENT_TO_DISPLAY,
					CocktailRecommenderValues.FAV_LIST);
			startActivity(intent);
			break;
		case CocktailRecommenderValues.HISTORY_LIST:
			intent.putExtra(CocktailRecommenderValues.FRAGMENT_TO_DISPLAY,
					CocktailRecommenderValues.HISTORY_LIST);
			startActivity(intent);
			break;
		default:
			intent.putExtra(CocktailRecommenderValues.FRAGMENT_TO_DISPLAY,
					CocktailRecommenderValues.ALL_RECIPES);
			startActivity(intent);
			break;

		}
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
		if (id == R.id.action_nav_menu) {
			Intent openMenu = new Intent(this, MenuActivity.class);
			openMenu.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(openMenu);
			return true;
		}
		if (id == R.id.action_about) {
			AlertDialog.Builder aboutAlert = new AlertDialog.Builder(this);
			aboutAlert.setTitle(R.string.about_dialog_title);
			aboutAlert.setMessage(R.string.about_dialog_message);
			aboutAlert.create();
			aboutAlert.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
