import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by gacela on 4/3/17.
 */
public class RajoutStageUI extends JPanel implements ActionListener {
    ConnectionBD connectionBD;
    JComboBox<String> sportList;
    JLabel sportLabel;
    HashMap<String, String> map;
    JComboBox<String> communeList;
    JLabel communeLabel;

    public RajoutStageUI() {
        this.connectionBD = new ConnectionBD();
        map = new HashMap<String, String>();

        try {
            String[] sports = getSportList();
            this.sportList = new JComboBox<String>(sports);
            sportList.addActionListener(this);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sportList){
            JComboBox cb = (JComboBox)e.getSource();
            String petName = (String)cb.getSelectedItem();
            System.out.println("Hello "+petName);
        }
        if (e.getSource() == communeList){
            JComboBox cb = (JComboBox)e.getSource();
            String petName = (String)cb.getSelectedItem();
            System.out.println("Hello "+petName);
        }
    }

    private String [] getTerrainCommune() throws SQLException {
        Connection conn = connectionBD.getConnection();
        String[] terrain_commune = new String[100];
        int i = 0;
        //String PRE_STMT1 = "select nomTerrain, commune from stage St where St.sport = " + map.get("sport");
        String PRE_STMT1 = "select nomTerrain, commune from (select typeTerrain from PeutSeJouerSur where sport =" + map.get("sport") + ") typeT, Terrain T where typeT.typeTerrain = T.typeTerrain";
        PreparedStatement stmt = conn.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        while (rset.next()) {
            terrain_commune [i] = rset.getString(1) + " - " + rset.getString(2);
            i++;
        }
        return terrain_commune;
    }
}