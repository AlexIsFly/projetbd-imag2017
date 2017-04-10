import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class InscriptionStageUI extends JPanel implements ActionListener {


    private ConnectionBD connectionBD;

    private int codeMembre=-1;
    private String sport;
    private String selectedCommune;
    private String selectedSport;
    private Integer selectedDate;


    private JComboBox<String> membreList;
    private JLabel membreLabel;

    private Box triStageBox = new Box(BoxLayout.LINE_AXIS);
    private Box sportBox = new Box(BoxLayout.PAGE_AXIS);
    private JComboBox<String> sportList;
    private JLabel sportLabel;
    private Box communeBox = new Box(BoxLayout.PAGE_AXIS);
    private JComboBox<String> communeList;
    private JLabel communeLabel;

    private Box dateBox = new Box(BoxLayout.LINE_AXIS);
    private JXDatePicker picker;
    private JLabel date;
    private Calendar selectedDay;
    private JComboBox<String> stageList;
    private JLabel stageLabel;
    private JButton resetTime;

    private JLabel prixLabel = new JLabel("Prix :");

    private JButton inscription = new JButton("Inscription");

    InscriptionStageUI(ConnectionBD connectionBD) {
        this.connectionBD = connectionBD;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        try {
            String[] membres = createMembreList();
            this.membreList = new JComboBox<>(membres);
            membreList.addActionListener(this);
            this.membreLabel = new JLabel("Qui êtes vous ?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        add(this.membreLabel);
        add(this.membreList);

        try {
            String[] sports = createSportList();
            this.sportList = new JComboBox<>(sports);
            sportList.addActionListener(this);
            this.sportLabel = new JLabel("Liste des sports :");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.sportBox.add(this.sportLabel);
        this.sportBox.add(this.sportList);
        this.triStageBox.add(this.sportBox);

        try {
            String[] communes = createCommuneList();
            this.communeList = new JComboBox<>(communes);
            communeList.addActionListener(this);
            this.communeLabel = new JLabel("Liste des communes :");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.communeBox.add(this.communeLabel);
        this.communeBox.add(this.communeList);
        this.triStageBox.add(this.communeBox);

        add(this.triStageBox);

        try {
            String[] stages = createStageList();
            this.stageList = new JComboBox<>(stages);
            stageList.addActionListener(this);
            this.stageLabel = new JLabel("Liste des stages:");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        add(this.stageLabel);
        add(this.stageList);

        this.picker = new JXDatePicker();
        this.picker.setDate(Calendar.getInstance().getTime());
        this.picker.setFormats(new SimpleDateFormat("dd.MM.yyyy"));
        this.picker.addActionListener(this);
        this.date = new JLabel("Date : ");
        this.selectedDay = Calendar.getInstance();
        this.dateBox.add(this.date);
        this.dateBox.add(this.picker);

        this.resetTime = new JButton("Reset date");
        this.resetTime.addActionListener(this);
        this.dateBox.add(this.resetTime);
        add(this.dateBox);

        add(this.prixLabel);

        this.inscription.addActionListener(this);
        add(this.inscription);
    }

    private String[] createCommuneList() throws SQLException {
        Connection connection = connectionBD.getConnection();
        String[] communes = new String[100];
        int i = 1;
        communes[0]="Toutes";
        String PRE_STMT1 = "select distinct commune from terrain";
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        while (rset.next()) {
            communes[i]="";
            communes[i]+=rset.getString(1);
            i++;
        }
        stmt.close();
        System.out.println("Stmt closed.");
        rset.close();
        System.out.println("ResultSet closed.");
        connection.close();
        System.out.println("Connection closed.");
        return communes;
    }

    private String[] createStageList() throws SQLException {
        Connection connection = connectionBD.getConnection();
        String[] stages = new String[100];
        int i = 0;
        String PRE_STMT1 = "select stages.codeStage, nomSport, nomTerrain, Commune, dateStage, heureDebut, heureFin, counts2*10-counts places from Stage stages, (select codestage, COUNT(codePersonne) counts from EstInscritA group by codestage) inscrits, (select codestage, count(codePersonne) counts2 from ESTENCADREPAR group by codestage) encadrants where stages.codestage=inscrits.codestage and stages.codestage=encadrants.codestage and inscrits.codestage=encadrants.codestage";
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        while (rset.next()) {
            stages[i]="";
            for(int j=1; j<8; j++){
                stages[i]+=rset.getString(j);
                stages[i]+=", ";
            }
            stages[i]+="Places restantes: ";
            stages[i]+=rset.getInt(8);
            i++;
        }
        stmt.close();
        System.out.println("Stmt closed.");
        rset.close();
        System.out.println("ResultSet closed.");
        connection.close();
        System.out.println("Connection closed.");
        return stages;
    }

    private String[] createMembreList() throws SQLException {
        Connection connection = connectionBD.getConnection();
        String[] membres = new String[100];
        int i = 0;
        String PRE_STMT1 = "select codePersonne, prenom, nom from personne where codepersonne>1000";
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        while (rset.next()) {
            membres[i]="";
            for(int j=1; j<4; j++){
                membres[i]+=rset.getString(j)+" ";
            }
            i++;
        }
        stmt.close();
        System.out.println("Stmt closed.");
        rset.close();
        System.out.println("ResultSet closed.");
        connection.close();
        System.out.println("Connection closed.");
        return membres;
    }

    private String[] createSportList() throws SQLException {
        Connection connection = connectionBD.getConnection();
        String[] sports = new String[100];
        int i = 1;
        sports[0]="Tous";
        String PRE_STMT1 = "select nomSport from sport";
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        while (rset.next()) {
            sports[i]=rset.getString(1);
            i++;
        }
        stmt.close();
        System.out.println("Stmt closed.");
        rset.close();
        System.out.println("ResultSet closed.");
        connection.close();
        System.out.println("Connection closed.");
        return sports;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == membreList){
            JComboBox cb = (JComboBox)e.getSource();
            String membre = (String)cb.getSelectedItem();
            membre = membre.split(" ")[0];
            codeMembre = Integer.parseInt(membre);
            if (sport!=null) {
                try {
                    affichePrix();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (e.getSource() == sportList){
            JComboBox cb = (JComboBox)e.getSource();
            this.selectedSport = (String)cb.getSelectedItem();
            System.out.println(selectedSport);
            try {
                updateStageMenu();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        if (e.getSource() == communeList){
            JComboBox cb = (JComboBox)e.getSource();
            this.selectedCommune = (String)cb.getSelectedItem();
            System.out.println(selectedCommune);
            try {
                updateStageMenu();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        if (e.getSource() == picker){
            Date selectedDate = this.picker.getDate();
            this.selectedDate = dateConvert(selectedDate);
            try {
                updateStageMenu();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        if (e.getSource()== resetTime) {
            this.selectedDate=null;
            try {
                updateStageMenu();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        if (e.getSource() == stageList){
            JComboBox cb = (JComboBox)e.getSource();
            String stage = (String)cb.getSelectedItem();
            if(stage!=null) {
                sport = stage.split(",")[0];
                try {
                    affichePrix();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void updateStageMenu() throws SQLException {
        Connection connection = connectionBD.getConnection();
        String[] stages = new String[100];
        int i = 0;
        String requete="";
        if (this.selectedSport!=null && !this.selectedSport.equals("Tous")) {
            requete+=" and nomSport='" + this.selectedSport + "'";
        }
        if (this.selectedCommune!=null && !this.selectedCommune.equals("Toutes")) {
            requete+=" and commune='" + this.selectedCommune + "'";
        }
        if (this.selectedDate!=null) {
            requete+=" and dateStage=" + this.selectedDate;
        }
        String PRE_STMT1 = "select stages.codeStage, nomSport, nomTerrain, Commune, dateStage, heureDebut, heureFin, counts2*10-counts places from (select codestage, COUNT(codePersonne) counts from EstInscritA group by codestage) inscrits, (select codestage, count(codePersonne) counts2 from ESTENCADREPAR group by codestage) encadrants, Stage stages where stages.codestage=inscrits.codestage and stages.codestage=encadrants.codestage and inscrits.codestage=encadrants.codestage" + requete;
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        System.out.println(PRE_STMT1);
        while (rset.next()) {
            stages[i]="";
            for(int j=1; j<8; j++){
                stages[i]+=rset.getString(j);
                stages[i]+=", ";
            }
            stages[i]+="Places restantes: ";
            stages[i]+=rset.getInt(8);
            i++;
        }
        this.stageList.removeAllItems();
        for (String stage : stages) {
            this.stageList.addItem(stage);
        }
        stmt.close();
        System.out.println("Stmt closed.");
        rset.close();
        System.out.println("ResultSet closed.");
        connection.close();
        System.out.println("Connection closed.");
    }

    private void affichePrix() throws SQLException {
        Connection connection = connectionBD.getConnection();
        int prix;
        String PRE_STMT1 = "select tarifStage from sport where nomSport=(select nomSport from Stage where codeStage=" + Integer.parseInt(sport) + ")";
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        rset.next();
        prix = rset.getInt(1);
        System.out.println(prix);
        System.out.println(codeMembre);
        if(codeMembre>-1){
            String PRE_STMT2 = "select commune from stage where codestage=" + Integer.parseInt(sport);
            PreparedStatement stmt2 = connection.prepareStatement(PRE_STMT2);
            ResultSet rset2 = stmt2.executeQuery();
            rset2.next();
            String PRE_STMT3 = "select commune from personne where codepersonne=" + codeMembre;
            PreparedStatement stmt3 = connection.prepareStatement(PRE_STMT3);
            ResultSet rset3 = stmt3.executeQuery();
            rset3.next();
            if (rset2.getString(1).equals(rset3.getString(1))){
                prix = (int)(0.85*prix);
            }
            stmt2.close();
            stmt3.close();
            rset2.close();
            rset3.close();

        }
        this.prixLabel.setText("Prix : " + prix);
        stmt.close();
        System.out.println("Stmt closed.");
        rset.close();
        System.out.println("ResultSet closed.");
        connection.close();
        System.out.println("Connection closed.");
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
