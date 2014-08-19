import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

/*Modified example from http://www.tutorialspoint.com/sqlite/sqlite_java.htm
 * DB_PATH points to directory where /cocktail-recommender-app/ is located, edit to point to the real path.
 * Simple Java console application, prompts for required Information (input by line only, no paragraphs!).
 */
public class SimpleDatabaseInsert
{
	//DB-PATH ist der Pfad zu der Datenbank der App. Sollte unter dem Projektverzeichniss im */assets/databases/recipes.db sein.
	//* muss ersetzt werden.
	private static final String DB_PATH = "C:/Users/faqxt/Documents/GitHub/cocktail-recommender-app/Cocktail Recommender/assets/databases/recipes.db";
	//private static final String DB_TEST_PATH = "C:/Users/faqxt/Documents/GitHub/DatabaseTest/assets/databases/test.db";
	private static final String TABLE_NAME = "Cocktails";
	private static final String INGREDIENTS_JSON_HEAD = "{\"ingredients\":[";
	private static final String TAGS_JSON_HEAD = "{\"tags\":[";
	
	public static void main(String args[]){
		
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
		      c = DriverManager.getConnection("jdbc:sqlite:"+DB_PATH);
		      c.setAutoCommit(false);
		      System.out.println("Opened database successfully");

		      stmt = c.createStatement();
		      while(true){
		    	  System.out.println("Cocktail Name eingeben: ");
		    	  name = input.nextLine();
		    	  
		    	  System.out.println("Zutatenanzahl eingeben: ");
		    	  ingredientsJSON = createJSON(Integer.parseInt(input.nextLine()), INGREDIENTS_JSON_HEAD, input);
		    	  
		    	  System.out.println("Taganzahl eingeben: ");
		    	  tagsJSON = createJSON(Integer.parseInt(input.nextLine()), TAGS_JSON_HEAD, input);
		    	  
		    	  System.out.println("Zubereitung eingeben: ");
		    	  preparation = input.nextLine();
		    	  
			      sql = "INSERT INTO "+TABLE_NAME+" (Name,Ingredients,Tags,Preparation) " +
			            "VALUES ('"+name+"','"+ingredientsJSON+"','"+tagsJSON+"','"+preparation+"');";
			      System.out.println(sql);
			      stmt.executeUpdate(sql);
			      
			      System.out.println("Neuer Eintrag? n für nein, beliebige Taste für ja");
			      String terminate = input.nextLine();
			      if (terminate.toLowerCase().equals("n")){
			    	  break;
			      }
		      }
		      input.close();
		      stmt.close();
		      c.commit();
		      c.close();
		    } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		    }
		    System.out.println("Records created successfully");
	}

	private static String createJSON(int parseInt, String jsonHead, Scanner mainInput) {
		String json = jsonHead;
		Scanner input = mainInput;
		for (int i = 0; i < parseInt; i++){
			if (jsonHead.contains("ingredients")){
				System.out.println("Enter ingredient no."+(i+1)+": ");
				json += input.nextLine()+":";
				System.out.println("Enter ingredient quantity: ");
				json += "\""+input.nextLine()+"\",";
			}
			if (jsonHead.contains("tags")){
				System.out.println("Enter tag no."+(i+1)+": ");
				json += "\""+input.nextLine()+"\",";
			}		
		}
		json += "]}";
		return json;
	}
	
}