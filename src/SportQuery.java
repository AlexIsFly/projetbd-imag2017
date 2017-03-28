import java.sql.*;

/**
 * Created by gacela on 3/28/17.
 */
public class SportQuery {
    static final String CONN_URL = "jdbc:oracle:thin:@ensioracle1.imag.fr:1521:ensioracle1";

    static final String USER = "scott";
    static final String PASSWD = "tiger";

    static final String PRE_STMT1 =
            "select min(hiredate),max(hiredate) from emp";

    static final String PRE_STMT2 =
            "select ename,hiredate from emp where to_char(hiredate,'DD-MM-YY') = ?";

    static final String PRE_STMT3 =
            "select ename,hiredate from emp where hiredate > ?";

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

            // Fermeture

            rset.close();
            stmt.close();

            conn.close();

        } catch (SQLException e) {
            System.err.println("failed");
            e.printStackTrace(System.err);
        }
    }
}
