import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

//Modified example from http://www.tutorialspoint.com/sqlite/sqlite_java.htm
public class DatabaseCreator {
	private static final String COCKTAIL_RECOMMENDER = "C:/Users/faqxt/Documents/GitHub/cocktail-recommender-app/Cocktail Recommender/assets/databases/recipes.db";
	private static final String NEW_DB_DIRECTION = "C:/Users/Severin/workspace/cocktail-recommender-app/Cocktail Recommender/assets/databases/crDatabase.db";
	private static final String TEST_DIRECTION = "C:/Users/Severin/workspace/test.db";
	// private static final String DATABASE_TEST_TUAN =
	// "C:/Users/faqxt/Documents/GitHub/DatabaseTest/assets/databases/test.db";

	private static final String COCKTAIL_TABLE_NAME = "Cocktails";
	private static final String INGREDIENTS_TABLE_NAME = "Ingredients";
	private static final String FAVORITES_TABLE_NAME = "Favorites";
	private static final String HISTORY_TABLE_NAME = "History";
	private static final String SHOPPING_LISTS_TABLE_NAME = "ShoppingLists";
	private static final String TAGS_TABLE_NAME = "Tags";
	
	public static void main(String args[]) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + NEW_DB_DIRECTION); // hier
																				// Namen
																				// austauschen
			System.out.println("Opened database successfully");
			stmt = c.createStatement();
			String sqlCocktails = "CREATE TABLE " + COCKTAIL_TABLE_NAME + " "
					+ "(ID INTEGER PRIMARY KEY  AUTOINCREMENT, "
					+ " Name         	TEXT    NOT NULL, "
					+ " Ingredients    TEXT    NOT NULL, "
					+ " Tags        	TEXT 	NOT NULL, "
					+ " Preparation    TEXT	NOT NULL, " + " Similar		TEXT)";
			stmt.executeUpdate(sqlCocktails);

			String sqlIngredients = "CREATE TABLE " + INGREDIENTS_TABLE_NAME
					+ " " + "(ID INTEGER PRIMARY KEY,"
					+ " Name         	TEXT    NOT NULL)";
			stmt.executeUpdate(sqlIngredients);
			
			String sqlTags = "CREATE TABLE " + TAGS_TABLE_NAME
					+ " " + "(ID INTEGER PRIMARY KEY,"
					+ " Name         	TEXT    NOT NULL)";
			stmt.executeUpdate(sqlTags);

			String sqlFav = "CREATE TABLE " + FAVORITES_TABLE_NAME + " "
					+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ " DrinkID       	INTEGER    NOT NULL)";
			stmt.executeUpdate(sqlFav);

			String sqlHis = "CREATE TABLE " + HISTORY_TABLE_NAME + " "
					+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ " DrinkID       	INTEGER    NOT NULL)";
			stmt.executeUpdate(sqlHis);

			
			String sqlLists = "CREATE TABLE " + SHOPPING_LISTS_TABLE_NAME + " "
					+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ " List_Name      TEXT    NOT NULL, "
					+ " IngredientIDs       	TEXT    NOT NULL)";
			stmt.executeUpdate(sqlLists);

			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Table created successfully");
	}
}