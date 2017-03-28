import java.sql.*;

public class Connexion {
	
	static final String CONN_URL = "jdbc:oracle:thin:@ensioracle1.imag.fr:1521:ensioracle1";
    static final String USER = "scott";
    static final String PASSWD = "tiger";
    static final String PRE_STMT = "select * from emp";
    
    
	
	public startConnexion() {
		try {
		    // Enregistrement du driver Oracle
		    System.out.print("Loading Oracle driver... "); 
		    DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
	            System.out.println("loaded");

		    // Etablissement de la connection
		    System.out.print("Connecting to the database... "); 
		    Connection conn = DriverManager.getConnection(CONN_URL, USER, PASSWD);
	            System.out.println("connected");
		} catch (SQLException e) {
            System.err.println("Connexion failed");
            e.printStackTrace(System.err);
        }
	}
	
	public endConnexion() {
		// Fermeture 
	    rset.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.err.println("Closing failed");
            e.printStackTrace(System.err);
        }
}
