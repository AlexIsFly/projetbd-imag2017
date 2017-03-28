import javax.swing.*;
import java.sql.*;

public class JDBCMain {
    
    public static void main(String[] args) {
        ConnectionBD connect = new ConnectionBD();
        SportQuery sportQuery = new SportQuery(connect);
    }
}
