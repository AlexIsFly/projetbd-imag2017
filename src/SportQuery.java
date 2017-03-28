import java.sql.*;

/**
 * Created by gacela on 3/28/17.
 */
public class SportQuery {
    static final String CONN_URL = "jdbc:oracle:thin:[gacela/gacela]@ensioracle1.imag.fr:1521:ensioracle1";

    static final String USER = "gacela";
    static final String PASSWD = "gacela";

    static final String PRE_STMT1 =
            "select * from sport";

    public SportQuery() {
        try {

            // Enregistrement du driver Oracle

            System.out.print("Loading Oracle driver... ");
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            System.out.println("loaded");

            // Etablissement de la connection

            System.out.print("Connecting to the database... ");
            Connection conn = DriverManager.getConnection(CONN_URL, USER, PASSWD);
            System.out.println("connected");

         /* Recherche de la date d'embauche la plus ancienne et
          * de la plus récente dans la table EMP
          */

            // Creation de la requete

            PreparedStatement stmt = conn.prepareStatement(PRE_STMT1);

            // Execution de la requete

            ResultSet rset = stmt.executeQuery();

            while (rset.next ()) {
                System.out.println (
                        " Nom Sport " + rset.getString ( "nomSport" )
                                + " -> Tarif Stage : " + rset.getString( "tarifStage" )
                );
            }
            // Fermeture

            rset.close();
            System.out.println("closing result set");
            stmt.close();
            System.out.println("closing statement");
            conn.close();
            System.out.println("closing connection");


        } catch (SQLException e) {
            System.err.println("failed");
            e.printStackTrace(System.err);
        }
    }
}
