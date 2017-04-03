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

        try {
            String[] sports = getSportList();
            this.sportList = new JComboBox<String>(sports);
            this.sportLabel = new JLabel("Sport");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        add(this.sportLabel);
        add(this.sportList);
    }

    private String[] getSportList() throws SQLException {
        Connection connection = connectionBD.getConnection();
        String[] sports = new String[100];
        int i = 0;
        String PRE_STMT1 = "select nomSport from sport";
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        while (rset.next()) {
            sports[i]=rset.getString(1);
            i++;
        }
        return sports;

    }
}