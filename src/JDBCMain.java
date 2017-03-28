import javax.swing.*;
import java.sql.*;

public class JDBCMain {
	
	static final String CONN_URL = "jdbc:oracle:thin:@ensioracle1.imag.fr:1521:ensioracle1";
    static final String USER = "scott";
    static final String PASSWD = "tiger";
    static final String PRE_STMT = "select * from emp";
    
    public static void main(String[] args) {
    	
        SportQuery sportQuery = new SportQuery();
        Connexion connec = new Connexion();

    }
}
