

//import org.jdesktop.swingx.JXDatePicker;

import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
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
    Box stageBox = new Box(BoxLayout.Y_AXIS);

    JTextField startHours;
    JLabel startTime;
    JLabel endTime;
    JTextField endHours;
    JButton verifyTime;
    JLabel date;
    JXDatePicker picker;
    int openTime;
    int closeTime;

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

        this.startTime = new JLabel("Debut hhmm");
        this.startHours = new JTextField(2);
        this.endTime = new JLabel("Fin hhmm");
        this.endHours = new JTextField(2);
        this.verifyTime = new JButton("Verify");
        this.verifyTime.addActionListener(this);
        this.timeBox.add(startTime);
        this.timeBox.add(startHours);
        this.timeBox.add(endTime);
        this.timeBox.add(endHours);
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
        add(this.stageBox);
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
        conn = null;
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
        this.openTime = rset.getInt(1);
        this.closeTime = rset.getInt(2);

        String horaire = "Le terrain " + terrain + " est ouvert de " + this.openTime;

        horaire += " à " + this.closeTime;

        System.out.println(horaire);
        stmt.close();
        System.out.println("Stmt closed.");
        rset.close();
        System.out.println("ResultSet closed.");
        conn.close();
        conn = null;
        System.out.println("Connection closed.");
        this.horaireBox.removeAll();
        this.horaireBox.add(new JLabel(horaire));
        this.horaireBox.repaint();
        this.horaireBox.revalidate();

    }

    public void afficheStages() throws SQLException {
        Connection conn = connectionBD.getConnection();
        String terrain_com = this.selectedTerrain;
        String terrain = terrain_com.split(" - ")[0];
        String commune = terrain_com.split(" - ")[1];
        String PRE_STMT1 = "select codeStage, dateStageDeb, dateStageFin from STAGE where NOMTERRAIN ='"
                + terrain + "' AND COMMUNE = "+ " 'La Gerignette' ";
        System.out.println("PRE_STMT1 = " + PRE_STMT1);
        PreparedStatement stmt = conn.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();

        String[] tableColumnsName = {"codeStage","HeureDebut","HeureFin"};
        JTable aTable = new JTable();
        DefaultTableModel aModel = (DefaultTableModel) aTable.getModel();
        aModel.setColumnIdentifiers(tableColumnsName);
        ResultSetMetaData rsmd = rset.getMetaData();
        int colNo = rsmd.getColumnCount();
        Date tempdate;
        Calendar tempcal = Calendar.getInstance();
        while(rset.next()){
            tempdate = new Date(rset.getDate(2).getTime());
            tempcal.setTime(tempdate);
            if (tempcal.get(Calendar.DAY_OF_YEAR)==selectedDay.get(Calendar.DAY_OF_YEAR) &&
                    tempcal.get(Calendar.YEAR)==selectedDay.get(Calendar.YEAR)) {
                Object[] objects = new Object[colNo];
                for(int i=0;i<colNo;i++){
                    objects[i]=rset.getObject(i+1);
                }
                aModel.addRow(objects);
            }
        }
        aTable.setModel(aModel);
        aTable.setPreferredScrollableViewportSize(new Dimension(3,100));
        this.stageBox.add(new JScrollPane(aTable));

        stmt.close();
        System.out.println("Stmt closed.");
        rset.close();
        System.out.println("ResultSet closed.");
        conn.close();
        System.out.println("Connection closed.");
        this.stageBox.repaint();
        this.stageBox.revalidate();
    }

    public boolean verifyHoraires(int open, int close) {
        if (open < this.openTime || close > this.closeTime) {
        }
        return false;
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
            try {
                afficheStages();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        if (e.getSource() == verifyTime){
            int open = Integer.parseInt(this.startHours.getText());
            int close = Integer.parseInt(this.endHours.getText());
            if (!verifyHoraires(open,close)) {
                this.stageBox.add(new JLabel("Mauvais horaires"));
            }
        }
    }

    public int dateConvert(Date date) {
        Calendar tempcal = Calendar.getInstance();
        tempcal.setTime(date);
        int year = tempcal.get(Calendar.YEAR);
        int month = tempcal.get(Calendar.MONTH)+1;
        int day = tempcal.get(Calendar.DAY_OF_MONTH);
        int fulldate = year*10000+month*100+day-20000000;
        System.out.println("date convertie = " + fulldate);
        return (fulldate);
    }
}
