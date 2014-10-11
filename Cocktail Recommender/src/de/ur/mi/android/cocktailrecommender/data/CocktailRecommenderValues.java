package de.ur.mi.android.cocktailrecommender.data;

import de.ur.mi.android.cocktailrecommender.R;
import android.widget.ImageView;

public final class CocktailRecommenderValues {

	// Constants to open the RecipeBook on the right tab

	public final static int ALL_RECIPES = 0;
	public final static int SEARCH_RESULTS = 1;
	public final static int FAV_LIST = 2;
	public final static int HISTORY_LIST = 3;
	public final static boolean NO_MATCH_RATE = true;

	public final static String FRAGMENT_TO_DISPLAY = "Select correct Tab please!";

	// Constants for TagIds
	public final static int TAG_KLASSISCH = 1;
	public final static int TAG_SOUR = 2;
	public final static int TAG_APERITIF = 3;
	public final static int TAG_HIGHBALL = 4;
	public final static int TAG_SHOT = 5;
	public final static int TAG_DIGESTIF = 6;
	public final static int TAG_FIZZES = 7;
	public final static int TAG_SHOOTER = 8;
	public final static int TAG_STRONG = 9;

	public static int getCorrectTagImageResource(ImageView tagIcon,
			Tag tag) {
		switch (tag.getTagID()) {
		case CocktailRecommenderValues.TAG_APERITIF:
			return R.drawable.ic_tag_aperitif;
			
		case CocktailRecommenderValues.TAG_HIGHBALL:
			return R.drawable.ic_tag_highball;
			
		case CocktailRecommenderValues.TAG_SHOT:
			return R.drawable.ic_tag_shooter;
			
		case CocktailRecommenderValues.TAG_DIGESTIF:
			return R.drawable.ic_tag_digestif;
			
		case CocktailRecommenderValues.TAG_KLASSISCH:
			return R.drawable.ic_tag_klassisch;
			
		case CocktailRecommenderValues.TAG_SOUR:
			return R.drawable.ic_tag_sour;
			
		case CocktailRecommenderValues.TAG_FIZZES:
			return R.drawable.ic_tag_fizz;
			
		case CocktailRecommenderValues.TAG_SHOOTER:
			return R.drawable.ic_tag_shooter;
			
		case CocktailRecommenderValues.TAG_STRONG:
			return R.drawable.ic_tag_strong;
			
		default:
			return R.drawable.tag_icon_placeholder;
			
			
		}
		
	}
}
