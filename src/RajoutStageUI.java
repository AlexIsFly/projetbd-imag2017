import javax.swing.*;
import java.awt.*;
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
    HashMap<String, String> map;

    JComboBox<String> sportList;
    JLabel sportLabel;
    JComboBox<String> terrainList;
    JLabel terrainLabel;


    public RajoutStageUI(ConnectionBD connectionBD) {
        this.connectionBD = connectionBD;
        map = new HashMap<String, String>();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));


        try {
            String[] sports = getSportList();
            this.sportList = new JComboBox<String>(sports);
            sportList.addActionListener(this);
            this.sportLabel = new JLabel("Sport");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.terrainList = new JComboBox<String>();
        this.terrainLabel = new JLabel("Terrain");


        add(this.sportLabel);
        add(this.sportList);
        add(this.terrainLabel);
        add(this.terrainList);
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
        rset.close();
        System.out.println("ResultSet closed.");
        stmt.close();
        System.out.println("Stmt closed.");
        connection.close();
        System.out.println("Connection closed.");
        return sports;

    }

    public void updateTerrainMenu() throws SQLException {
        Connection conn = connectionBD.getConnection();
        String[] terrain_commune = new String[100];
        int i = 0;
        String PRE_STMT1 = "select nomTerrain, commune from (select typeTerrain from PeutSeJouerSur where NomSport = '" + map.get("sport") + "') typeT, Terrain T where typeT.typeTerrain = T.typeTerrain";
        PreparedStatement stmt = conn.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        while (rset.next()) {
            terrain_commune [i] = rset.getString(1) + " - " + rset.getString(2);
            i++;
        }
        this.terrainList.removeAllItems();
        for (String terrain : terrain_commune) {
            this.terrainList.addItem(terrain);
        }
        stmt.close();
        System.out.println("Stmt closed.");
        rset.close();
        System.out.println("ResultSet closed.");
        conn.close();
        System.out.println("Connection closed.");
    }


    public void afficheHoraires() throws SQLException {
        Connection conn = connectionBD.getConnection();
        String terrain_com = map.get("terrain");
        String terrain = terrain_com.split(" - ")[0];
        String commune = terrain_com.split(" - ")[1];
        String PRE_STMT1 = "select heureouverture, heurefermeture from TERRAIN where NOMTERRAIN ='" + terrain + "' AND COMMUNE = '" + commune + "';";
        PreparedStatement stmt = conn.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        String horaire = "Le terrain " + terrain + "est ouvert de " + rset.getString(1) + " à " + rset.getString(2);
        System.out.println(horaire);
        stmt.close();
        System.out.println("Stmt closed.");
        rset.close();
        System.out.println("ResultSet closed.");
        conn.close();
        System.out.println("Connection closed.");


    }

    /*
    public void updateMoniteur() throws SQLException{
        Connection conn = connectionBD.getConnection();
        String[] moniteur = new String[100];
        int i = 0;
        String PRE_STMT1 = "";
        PreparedStatement stmt = conn.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
    }*/

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sportList){
            JComboBox cb = (JComboBox)e.getSource();
            String sportName = (String)cb.getSelectedItem();
            map.put("sport",sportName);
            try {
            	System.out.println(map.get("sport"));
                updateTerrainMenu();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            System.out.println("Select "+sportName);
        }
        if (e.getSource() == terrainList){
            System.out.println("hello");
            JComboBox cb = (JComboBox)e.getSource();
            String terrainName = (String)cb.getSelectedItem();
            map.put("terrain",terrainName);
            System.out.println("Select " + terrainName);
        }
    }
}