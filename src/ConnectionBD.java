import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by gacela on 3/28/17.
 */
public class ConnectionBD {
    static final String CONN_URL = "jdbc:oracle:thin:[gacela/gacela]@ensioracle1.imag.fr:1521:ensioracle1";
    static final String USER = "gacela";
    static final String PASSWD = "gacela";

    public ConnectionBD (){
        System.out.print("Loading Oracle driver... ");
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        } catch (SQLException e) {
            System.out.println("Error loading driver");
            e.printStackTrace();
        }
        System.out.println("loaded");

    }

    public Connection getConnection() throws SQLException {
        // Etablissement de la connection

        System.out.print("Connecting to the database... ");
        Connection conn = DriverManager.getConnection(CONN_URL, USER, PASSWD);
        System.out.println("connected");
        return conn;
    }
}
