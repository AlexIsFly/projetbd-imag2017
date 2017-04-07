

import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by gacela on 4/3/17.
 */
public class RajoutStageUI extends JPanel implements ActionListener {
    ConnectionBD connectionBD;

    JComboBox<String> sportList;
    JLabel sportLabel;
    JComboBox<String> terrainList;
    JLabel terrainLabel;

    Box timeBox = new Box(BoxLayout.LINE_AXIS);
    Box horaireBox = new Box(BoxLayout.LINE_AXIS);

    JTextField startHours;
    JTextField startMinutes;
    JLabel startTime;
    JLabel endTime;
    JTextField endHours;
    JTextField endMinutes;
    JButton verifyTime;
    JLabel date;
    JXDatePicker picker;

    String selectedSport;
    String selectedTerrain;
    Calendar selectedDay;


    public RajoutStageUI(ConnectionBD connectionBD) {

        this.picker = new JXDatePicker();
        this.picker.setDate(Calendar.getInstance().getTime());
        this.picker.setFormats(new SimpleDateFormat("dd.MM.yyyy"));
        this.picker.addActionListener(this);
        this.date = new JLabel("Date");
        this.selectedDay = Calendar.getInstance();

        this.startTime = new JLabel("Début hh:mm");
        this.startHours = new JTextField(2);
        this.startMinutes = new JTextField(2);
        this.endTime = new JLabel("Fin hh:mm");
        this.endHours = new JTextField(2);
        this.endMinutes = new JTextField(2);
        this.verifyTime = new JButton("Verify");
        this.verifyTime.addActionListener(this);
        this.timeBox.add(startTime);
        this.timeBox.add(startHours);
        this.timeBox.add(startMinutes);
        this.timeBox.add(endTime);
        this.timeBox.add(endHours);
        this.timeBox.add(endMinutes);
        this.timeBox.add(verifyTime);


        this.connectionBD = connectionBD;
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
        terrainList.addActionListener(this);
        this.terrainLabel = new JLabel("Terrain");


        add(this.sportLabel);
        add(this.sportList);
        add(this.terrainLabel);
        add(this.terrainList);
        add(this.horaireBox);
        add(this.date);
        add(this.picker);
        add(this.timeBox);
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
        String PRE_STMT1 = "select nomTerrain, commune from (select typeTerrain from PeutSeJouerSur where NomSport = '" +
                this.selectedSport + "') typeT, Terrain T where typeT.typeTerrain = T.typeTerrain";
        PreparedStatement stmt = conn.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        while (rset.next()) {
            terrain_commune [i] = rset.getString(1) + " - " + rset.getString(2);
            i++;
        }
        this.terrainList.removeActionListener(this);
        this.terrainList.removeAllItems();
        this.terrainList.addActionListener(this);
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
        String terrain_com = this.selectedTerrain;
        String terrain = terrain_com.split(" - ")[0];
        String commune = terrain_com.split(" - ")[1];
        String PRE_STMT1 = "select heureouverture, heurefermeture from TERRAIN where NOMTERRAIN ='" + terrain + "' AND COMMUNE = '" + commune + "'";
        System.out.println("PRE_STMT1 = " + PRE_STMT1);
        PreparedStatement stmt = conn.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        rset.next();
        Date tempDate = new Date(rset.getDate(1).getTime());
        Calendar tempCal = Calendar.getInstance();
        tempCal.setTime(tempDate);

        String horaire = "Le terrain " + terrain + " est ouvert de "
                + tempCal.get(Calendar.HOUR_OF_DAY) + ":"
                + tempCal.get(Calendar.MINUTE);

        tempDate = new Date(rset.getDate(2).getTime());
        tempCal.setTime(tempDate);
        horaire += " à " + tempCal.get(Calendar.HOUR_OF_DAY) + ":"
                + tempCal.get(Calendar.MINUTE);

        System.out.println(horaire);
        stmt.close();
        System.out.println("Stmt closed.");
        rset.close();
        System.out.println("ResultSet closed.");
        conn.close();
        System.out.println("Connection closed.");
        this.horaireBox.removeAll();
        this.horaireBox.add(new JLabel(horaire));
        this.horaireBox.repaint();
        this.horaireBox.revalidate();

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
            this.selectedSport = sportName;
            System.out.println("Select sport "+sportName);
            try {
                updateTerrainMenu();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        if (e.getSource() == terrainList){
            JComboBox cb = (JComboBox)e.getSource();
            String terrainName = (String)cb.getSelectedItem();
            this.selectedTerrain = terrainName;
            System.out.println("Select terrain " + terrainName);
            try {
                afficheHoraires();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        if (e.getSource() == picker){
            Date selectedDate = this.picker.getDate();
            this.selectedDay.setTime(selectedDate);
            System.out.println("selectedDate = " + this.selectedDay.toString());
        }
        if (e.getSource() == verifyTime){
        }
    }
}
