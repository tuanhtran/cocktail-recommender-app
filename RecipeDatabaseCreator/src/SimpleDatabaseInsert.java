import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

/*Modified example from http://www.tutorialspoint.com/sqlite/sqlite_java.htm
 * DB_PATH points to directory where /cocktail-recommender-app/ is located, edit to point to the real path.
 * Simple Java console application, prompts for required Information (input by line only, no paragraphs!).
 */
public class SimpleDatabaseInsert {
	// DB-PATH ist der Pfad zu der Datenbank der App. Sollte unter dem
	// Projektverzeichniss im */assets/databases/recipes.db sein.
	// * muss ersetzt werden.
	private static final String DB_PATH_TUAN = "C:/Users/faqxt/Documents/GitHub/cocktail-recommender-app/Cocktail Recommender/assets/databases/recipes.db";
	private static final String DB_PATH_SEVERIN = "C:/Users/Severin/workspace/cocktail-recommender-app/Cocktail Recommender/assets/databases/recipes.db";
	private static final String DB_PATH_ISABELL = "";

	// private static final String DB_TEST_PATH_TUAN =
	// "C:/Users/faqxt/Documents/GitHub/DatabaseTest/assets/databases/test.db";
	// private static final String DB_TEST_PATH_SEVERIN =
	// "C:/Severin/eclipse/workspace/cocktail-recommender-app/RecipeDataBaseCreator/test2.db";

	private static final String ING_ARRAY_KEY = "Ingredients";
	private static final String ING_ID_KEY = "ID";
	private static final String ING_QUANTITY_KEY = "Qty";
	private static final String TAG_ARRAY_KEY = "Tags";
	private static final String TAG_ID_KEY = "ID";

	private static final String TABLE_NAME = "Cocktails";
	private static final String INGREDIENTS_JSON_HEAD = "{\"" + ING_ARRAY_KEY
			+ "\":[";
	private static final String TAGS_JSON_HEAD = "{\"" + TAG_ARRAY_KEY + "\":[";

	public static void main(String args[]) {

		Connection c = null;
		Statement stmt = null;
		String sql;
		String name;
		String ingredientsJSON;
		String tagsJSON;
		String preparation;

		Scanner input = new Scanner(System.in);
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH_SEVERIN); // Hier
																				// Namen
																				// austauschen
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			while (true) {
				System.out.println("Cocktail Name eingeben: ");
				name = input.nextLine();

				System.out.println("Zutatenanzahl eingeben: ");
				ingredientsJSON = createJSON(
						Integer.parseInt(input.nextLine()),
						INGREDIENTS_JSON_HEAD, input);

				System.out.println("Taganzahl eingeben: ");
				tagsJSON = createJSON(Integer.parseInt(input.nextLine()),
						TAGS_JSON_HEAD, input);

				System.out.println("Zubereitung eingeben: ");
				preparation = input.nextLine();

				sql = "INSERT INTO " + TABLE_NAME
						+ " (Name,Ingredients,Tags,Preparation) " + "VALUES ('"
						+ name + "','" + ingredientsJSON + "','" + tagsJSON
						+ "','" + preparation + "');";
				System.out.println(sql);
				stmt.executeUpdate(sql);

				System.out
						.println("Neuer Eintrag? n für nein, beliebige Taste für ja");
				String terminate = input.nextLine();
				if (terminate.toLowerCase().equals("n")) {
					break;
				}
			}
			input.close();
			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Records created successfully");
	}

	private static String createJSON(int parseInt, String jsonHead,
			Scanner mainInput) {
		String json = jsonHead;
		Scanner input = mainInput;

		if (jsonHead.contains(ING_ARRAY_KEY)) {
			for (int i = 0; i < parseInt; i++) {
				System.out.println("Enter ingredient no." + (i + 1)
						+ " (as int ID): ");
				String id = getLegitInput(input);

				System.out.println("Enter ingredient quantity: ");
				String qty = input.nextLine();

				json += "{\"" + ING_ID_KEY + "\"" + ":" + id + ", " + "\""
						+ ING_QUANTITY_KEY + "\"" + ":" + "\"" + qty + "\""
						+ "},";
			}
		}

		if (jsonHead.contains(TAG_ARRAY_KEY)) {
			for (int i = 0; i < parseInt; i++) {
				System.out
						.println("Enter tag no." + (i + 1) + " (as int ID): ");
				String id = getLegitInput(input);
				json += "{\"" + TAG_ID_KEY + ":" + id + "},";
			}
		}
		json = json.substring(0, json.length() - 1) + "]}";

		System.out.println(json);
		return json;
	}

	private static String getLegitInput(Scanner mainInput) {
		Scanner input = mainInput;
		String id = "";
		id = input.nextLine();
		while (true) {
			if (hasOnlyNumbers(id)) {
				return id;
			} else {
				System.out
						.println("String may only contain numbers (0-9), please try again: ");
				id = input.nextLine();
			}
		}

	}

	private static boolean hasOnlyNumbers(String id) {
		for (int letterIdx = 0; letterIdx < id.length(); letterIdx++) {
			if ((id.charAt(letterIdx) < 48) || (id.charAt(letterIdx) > 57)) {
				return false;
			}
		}
		return true;
	}

}