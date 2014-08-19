import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

//Modified example from http://www.tutorialspoint.com/sqlite/sqlite_java.htm
public class DatabaseCreator {
	private static final String COCKTAIL_RECOMMENDER = "C:/Users/faqxt/Documents/GitHub/cocktail-recommender-app/Cocktail Recommender/assets/databases/recipes.db";
	private static final String DATABASE_TEST = "C:/Users/faqxt/Documents/GitHub/DatabaseTest/assets/databases/test.db";
	
	public static void main(String args[]) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:"
					+ COCKTAIL_RECOMMENDER);
			System.out.println("Opened database successfully");
			stmt = c.createStatement();
			String sql = "CREATE TABLE Cocktails "
					+ "(ID INTEGER PRIMARY KEY     AUTOINCREMENT,"
					+ " Name         	Text    NOT NULL, "
					+ " Ingredients    Text    NOT NULL, "
					+ " Tags        	Text 	NOT NULL, "
					+ " Preparation    Text	NOT NULL, " + " Similar		Text)";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Table created successfully");
	}
}