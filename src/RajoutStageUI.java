import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by gacela on 4/3/17.
 */
public class RajoutStageUI extends JPanel {
    ConnectionBD connectionBD;
    JComboBox<String> sportList;
    JLabel sportLabel;

    public RajoutStageUI() {
        this.connectionBD = new ConnectionBD();

        this.sportList = new JComboBox<String>();
        this.sportLabel = new JLabel("Sport");
        add(this.sportLabel);
        add(this.sportList);
    }

    private String[] getSportList() throws SQLException {
        Connection connection = connectionBD.getConnection();

        String PRE_STMT1 = "select nomSport from sport";
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        return null;

    }
}