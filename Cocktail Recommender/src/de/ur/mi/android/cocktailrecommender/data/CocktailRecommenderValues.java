package de.ur.mi.android.cocktailrecommender.data;

import android.widget.ImageView;
import de.ur.mi.android.cocktailrecommender.R;

/*
 * Various values that are being used in this app
 */

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
	public final static int TAG_NO_ALCOHOL = 7;
	public final static int TAG_SHOOTER = 8;
	public final static int TAG_STRONG = 9;

	// Returns the correct image resource for tags
	public static int getCorrectTagImageResource(ImageView tagIcon,
			Tag tag) {
		switch (tag.getTagID()) {
		case TAG_APERITIF:
			return R.drawable.ic_tag_aperitif;
			
		case TAG_HIGHBALL:
			return R.drawable.ic_tag_highball;
			
		case TAG_SHOT:
			return R.drawable.ic_tag_shooter;
			
		case TAG_DIGESTIF:
			return R.drawable.ic_tag_digestif;
			
		case TAG_KLASSISCH:
			return R.drawable.ic_tag_klassisch;
			
		case TAG_SOUR:
			return R.drawable.ic_tag_sour;			
			
		case TAG_SHOOTER:
			return R.drawable.ic_tag_shooter;
			
		case TAG_STRONG:
			return R.drawable.ic_tag_strong;
			
		case TAG_NO_ALCOHOL:
			return R.drawable.ic_tag_no_alcohol;
			
		default:
			return R.drawable.tag_icon_placeholder;
			
			
		}
		
	}
}
