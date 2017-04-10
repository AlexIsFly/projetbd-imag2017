

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
    Box errorBox = new Box(BoxLayout.Y_AXIS);
    Box moniteurBox = new Box(BoxLayout.PAGE_AXIS);

    JTextField startHours;
    JLabel startTime;
    JLabel endTime;
    JTextField endHours;
    JButton verifyTime;
    JLabel date;
    JXDatePicker picker;
    int openTime;
    int closeTime;
    boolean valid = true;

    Calendar selectedDay;

    String selectedSport;
    String selectedTerrain;
    int selectedDate;
    int selectedStart;
    int selectedEnd;

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
            createSportList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.sportLabel = new JLabel("Sport");


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
        add(this.errorBox);
        add(this.moniteurBox);

    }

    private void createSportList() throws SQLException {
        Connection connection = connectionBD.getConnection();
        this.sportList = new JComboBox<>();
        String PRE_STMT1 = "select nomSport from sport";
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        while (rset.next()) {
            this.sportList.addItem(rset.getString(1));
        }
        sportList.addActionListener(this);
        rset.close();
        System.out.println("ResultSet closed.");
        stmt.close();
        System.out.println("Stmt closed.");
        connection.close();
        System.out.println("Connection closed.");
    }

    public void updateTerrainMenu() throws SQLException {
        Connection conn = connectionBD.getConnection();
        String PRE_STMT1 = "select nomTerrain, commune from (select typeTerrain from PeutSeJouerSur where NomSport = '" +
                this.selectedSport + "') typeT, Terrain T where typeT.typeTerrain = T.typeTerrain";
        PreparedStatement stmt = conn.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        this.terrainList.removeActionListener(this);
        this.terrainList.removeAllItems();
        this.terrainList.addActionListener(this);
        while (rset.next()) {
            this.terrainList.addItem(rset.getString(1) + " - " + rset.getString(2));

        }
        this.terrainList.revalidate();
        this.terrainList.repaint();
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
        String PRE_STMT1 = "select codeStage, DateStage, heureDebut, heureFin from STAGE where NOMTERRAIN ='"
                + terrain + "' AND COMMUNE = "+ "'" + commune +  "'";
        System.out.println("PRE_STMT1 = " + PRE_STMT1);
        PreparedStatement stmt = conn.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();

        String[] tableColumnsName = {"codeStage","Jour","HeureDebut","HeureFin"};
        JTable aTable = new JTable();
        DefaultTableModel aModel = (DefaultTableModel) aTable.getModel();
        aModel.setColumnIdentifiers(tableColumnsName);
        ResultSetMetaData rsmd = rset.getMetaData();
        int colNo = rsmd.getColumnCount();
        while(rset.next()){
            int tempdate = rset.getInt(2);
            if (tempdate == dateConvert(this.selectedDay.getTime())) {
                Object[] objects = new Object[colNo];
                for(int i=0;i<colNo;i++){
                    objects[i]=rset.getObject(i+1);
                }
                aModel.addRow(objects);
            }
        }
        aTable.setModel(aModel);
        aTable.setPreferredScrollableViewportSize(new Dimension(3,100));
        this.stageBox.removeAll();
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
            this.valid = false;
            return this.valid;
        }
        this.valid = true;
        return this.valid;
    }


    public void updateMoniteur() throws SQLException{
        Connection conn = connectionBD.getConnection();
        String[] moniteur = new String[100];
        int i = 0;
        String PRE_STMT1 = "select iden.nom, iden.prenom from PERSONNE iden, (select distinct form.CODEPERSONNE " +
                "from ESTFORMEPOUR form, ((select distinct mon.codepersonne from MONITEUR mon) minus " +
                "(select distinct st.CODEPERSONNE from STAGE st where st.DATESTAGE = 170520 and (st.HEUREDEBUT<1200 or st.HEUREFIN>1000))) cod " +
                "where form.NOMSPORT='Athletisme' and form.CODEPERSONNE = cod.codepersonne) codeMon where codeMon.CODEPERSONNE = iden.CODEPERSONNE";
        PreparedStatement stmt = conn.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();


        String PRE_STMT2 = "select p.nom, p.prenom from PERSONNE p, ESTEXPERTEN e where p.CODEPERSONNE=e.CODEPERSONNE and e.NOMSPORT = 'Athletisme'";
    }

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
            this.selectedDate = dateConvert(selectedDate);
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
            this.errorBox.removeAll();
            if (!verifyHoraires(open,close)) {
                JLabel error = new JLabel("Mauvais horaires", JLabel.CENTER);
                error.setForeground(Color.RED);
                this.errorBox.add(error);
            }
            else {
                this.selectedStart = open;
                this.selectedEnd = close;
                //updateMoniteur();
            }
            this.errorBox.revalidate();
            this.errorBox.repaint();
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
