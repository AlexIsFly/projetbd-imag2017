

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
import java.util.*;
import java.util.Date;
import java.util.List;

import static javax.swing.ListSelectionModel.*;

/**
 * Created by gacela on 4/3/17.
 */
public class RajoutStageUI extends JPanel implements ActionListener {
    ConnectionBD connectionBD;

    JComboBox<String> sportList;
    JLabel sportLabel;
    JComboBox<String> terrainList;
    JLabel terrainLabel;

    Box timeBox;
    Box horaireBox;
    Box stageBox;
    Box errorBox;
    Box moniteurBox;
    Box createBox;
    Box errorBox2;

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

    JList monoList;
    JButton createButton;
    ArrayList stageTimeArray;
    Calendar selectedDay;

    //element selected by the user to create SQL statement
    String selectedSport;
    String selectedTerrain;
    int selectedDate;
    int selectedStart;
    int selectedEnd;
    int selectedSupervisor;
    ArrayList codeMonos;

    public RajoutStageUI(ConnectionBD connectionBD) {

        this.timeBox = new Box(BoxLayout.LINE_AXIS);
        this.horaireBox = new Box(BoxLayout.LINE_AXIS);
        this.stageBox = new Box(BoxLayout.Y_AXIS);
        this.errorBox = new Box(BoxLayout.Y_AXIS);
        this.moniteurBox = new Box(BoxLayout.PAGE_AXIS);
        this.errorBox2 = new Box(BoxLayout.LINE_AXIS);
        this.createBox = new Box(BoxLayout.LINE_AXIS);
        
        this.stageTimeArray = new ArrayList<>();
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
        this.monoList = new JList<>(new DefaultListModel<String>());
        this.monoList.setSelectionMode(MULTIPLE_INTERVAL_SELECTION);
        this.createButton = new JButton("Create");
        this.createButton.addActionListener(this);
        this.codeMonos = new ArrayList();
        this.selectedDate = 010101;

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
        add(this.createBox);
        add(this.errorBox2);

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
        stmt.close();
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
        rset.close();
        conn.close();
        System.out.println("Connection closed.");
    }


    public void afficheHoraires() throws SQLException {
        Connection conn = connectionBD.getConnection();
        String terrain_com = this.selectedTerrain;
        String terrain = terrain_com.split(" - ")[0];
        String commune = terrain_com.split(" - ")[1];
        String PRE_STMT1 = "select heureouverture, heurefermeture from TERRAIN where NOMTERRAIN ='" + terrain + "' AND COMMUNE = '" + commune + "'";
        PreparedStatement stmt = conn.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        rset.next();
        this.openTime = rset.getInt(1);
        this.closeTime = rset.getInt(2);

        String horaire = "Le terrain " + terrain + " est ouvert de a " + this.openTime;

        horaire += " à " + this.closeTime;

        stmt.close();
        rset.close();
        conn.close();
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
        this.stageTimeArray = new ArrayList();
        while(rset.next()){
            int tempdate = rset.getInt(2);
            System.out.println("tempdate = " + tempdate);
            if (tempdate == dateConvert(this.selectedDay.getTime())) {
                Object[] objects = new Object[colNo];
                for(int i=0;i<colNo;i++){
                    objects[i]=rset.getObject(i+1);
                }
                aModel.addRow(objects);
                this.stageTimeArray.add(rset.getInt(3));
                this.stageTimeArray.add(rset.getInt(4));
            }
        }
        System.out.println("this.stageTimeArray = " + this.stageTimeArray);
        aTable.setModel(aModel);
        aTable.setPreferredScrollableViewportSize(new Dimension(3,100));
        this.stageBox.removeAll();
        this.stageBox.add(new JScrollPane(aTable));

        stmt.close();
        rset.close();
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
        if(this.stageTimeArray.isEmpty()){
            System.out.println("Array Time Stage is empty");
            return true;
        }
        for (int i = 0; i<this.stageTimeArray.size()-1; i++) {
            int op = (int)this.stageTimeArray.get(i);
            int cl = (int)this.stageTimeArray.get(i+1);
            if (open > op && open < cl){
                this.valid = false;
                return false;
            }
            if (close > op && close < cl){
                this.valid = false;
                return false;
            }
            if (close == cl && open == op) {
                this.valid = false;
                return false;
            }
            if (open > close) {
                this.valid = false;
                return false;
            }
        }
        this.valid = true;
        return true;
    }


    public void updateMoniteur() throws SQLException{
        Connection conn = connectionBD.getConnection();
        String PRE_STMT1 = "select iden.codepersonne, iden.nom, iden.prenom from PERSONNE iden, (select distinct form.CODEPERSONNE " +
                "from ESTFORMEPOUR form, ((select distinct mon.codepersonne from MONITEUR mon) minus " +
                "(select distinct st.CODEPERSONNE from STAGE st where st.DATESTAGE = "+ this.selectedDate +" and ((st.HEUREDEBUT<"+ this.selectedEnd +" and st.HEUREFIN>"+this.selectedEnd+") or (st.HEUREDEBUT<"+ this.selectedStart +" and st.HEUREFIN>"+this.selectedStart+") or (st.HEUREDEBUT="+ this.selectedStart +" and st.HEUREFIN="+this.selectedEnd+")))) cod " +
                "where form.NOMSPORT='"+ this.selectedSport +"' and form.CODEPERSONNE = cod.codepersonne) codeMon where codeMon.CODEPERSONNE = iden.CODEPERSONNE";
        PreparedStatement stmt = conn.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        DefaultListModel model = (DefaultListModel) this.monoList.getModel();
        model.removeAllElements();
        this.moniteurBox.removeAll();
        while (rset.next()) {
            String mono = ""+rset.getInt(1)+"-" + rset.getString(2) + " " + rset.getString(3);
            ((DefaultListModel)this.monoList.getModel()).addElement(mono);
        }


        this.moniteurBox.add(new JLabel("Veuillez choisir les moniteurs",JLabel.CENTER));
        this.moniteurBox.add(this.monoList);

        stmt.close();
        rset.close();
        conn.close();
        System.out.println("Connection closed.");
        this.moniteurBox.repaint();
        this.moniteurBox.revalidate();
    }

    public void chooseSupervisor() throws SQLException {
        System.out.println("Choosing Supervisor and building CodeMonos");
        Connection conn = connectionBD.getConnection();
        String PRE_STMT2 = "select p.codepersonne, p.nom, p.prenom from PERSONNE p, ESTEXPERTEN e where p.CODEPERSONNE=e.CODEPERSONNE and e.NOMSPORT = '" + this.selectedSport + "'";
        PreparedStatement stmt = conn.prepareStatement(PRE_STMT2);
        ResultSet rset = stmt.executeQuery();
        List monos = this.monoList.getSelectedValuesList();
        if (monos.isEmpty()) {
            this.valid = false;
            return;
        }
        this.selectedSupervisor = Integer.parseInt(monos.get(0).toString().split("-")[0]);
        System.out.println("this.selectedSupervisor = " + this.selectedSupervisor);
        System.out.println(monos);
        while (rset.next()) {
            if (monos.contains("" + rset.getInt(1) + "-" + rset.getString(2) + " " + rset.getString(3))) {
                this.selectedSupervisor = rset.getInt(1);
                break;
            }
        }
        ListIterator ite = monos.listIterator();
        this.codeMonos = new ArrayList();
        while (ite.hasNext()){
            this.codeMonos.add(ite.next().toString().split("-")[0]);
        }
        this.valid = true;
        stmt.close();
        rset.close();
        conn.close();
        System.out.println("Connection closed.");
    }

    public void createEntry() throws SQLException {
        Connection conn = connectionBD.getConnection();
        conn.setAutoCommit(false);
        conn.setTransactionIsolation(conn.TRANSACTION_SERIALIZABLE);
        String terrain_com = this.selectedTerrain;
        String terrain = terrain_com.split(" - ")[0];
        String commune = terrain_com.split(" - ")[1];
        String PRE_STMT1 = "";
        PRE_STMT1 = "INSERT into Stage(nomSport, dateStage, heureDebut, heureFin, nomTerrain, Commune, codePersonne) values ";
        PRE_STMT1 += "('"+ this.selectedSport+"',"
                + this.selectedDate + ","
                + this.selectedStart + ","
                + this.selectedEnd + ","
                + "'"+ terrain + "'" + ","
                + "'"+ commune + "'" + ","
                + this.selectedSupervisor
                +")";
        PreparedStatement stmt = conn.prepareStatement(PRE_STMT1,
                new String[] {"CODESTAGE"});
        stmt.executeUpdate();
        System.out.println("PRE_STMT1 final = " + PRE_STMT1);
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        int code;
        if (generatedKeys.next()) {
            code = generatedKeys.getInt(1);
            System.out.println("CODESTAGE = " + code);
        }
        else {
            throw new SQLException("no ID obtained.");
        }

        ListIterator ite = this.codeMonos.listIterator();
        String PRE_STMT2 = "blank";
        while (ite.hasNext()) {
            PRE_STMT2 = "INSERT into ESTENCADREPAR(codePersonne, codeStage) values ";
            PRE_STMT2 += "(" + ite.next() + ","
                    + code
                    +")";
            stmt = conn.prepareStatement(PRE_STMT2);
            stmt.executeUpdate();
            System.out.println("PRE_STMT2 final = " + PRE_STMT2);
        }
        conn.commit();
        stmt.close();
        conn.close();
        System.out.println("Connection closed.");
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
                afficheStages();
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
            int open = 0;
            int close = 0;
            try {
                open = Integer.parseInt(this.startHours.getText());
                close = Integer.parseInt(this.endHours.getText());
            } catch (NumberFormatException e1) {
                JOptionPane.showMessageDialog(this,
                        "Ne pas laisser les champps horaires vides");
            }
            this.errorBox.removeAll();

            if (!verifyHoraires(open,close)) {
                JLabel error = new JLabel("Mauvais horaires", JLabel.CENTER);
                error.setForeground(Color.RED);
                this.errorBox.add(error);
            }
            else {
                this.selectedStart = open;
                this.selectedEnd = close;
                try {
                    updateMoniteur();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                this.createBox.add(this.createButton);
            }
            this.errorBox.revalidate();
            this.errorBox.repaint();
        }
        if (e.getSource() == createButton) {
            this.errorBox2.removeAll();
            try {
                chooseSupervisor();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            if (this.valid) {
                try {
                    createEntry();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            else {
                JLabel error = new JLabel("Erreur de saisie", JLabel.CENTER);
                error.setForeground(Color.RED);
                this.errorBox2.add(error);
            }
            this.sportList.setSelectedItem(this.selectedSport);
            this.createBox.removeAll();
            this.moniteurBox.removeAll();
            this.createBox.revalidate();
            this.createBox.repaint();
            this.errorBox2.revalidate();
            this.errorBox2.repaint();
            this.moniteurBox.revalidate();
            this.moniteurBox.repaint();
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
