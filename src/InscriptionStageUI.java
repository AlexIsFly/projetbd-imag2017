import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class InscriptionStageUI extends JPanel implements ActionListener {

    private ConnectionBD connectionBD;

    private int codeMembre=-1;
    private int places;
    private String selectedStage;
    private String selectedCommune;
    private String selectedSport;
    private Integer selectedDate;
    private Integer prix;


    private JComboBox<String> membreList;
    private JLabel membreLabel;

    private Box triStageBox = new Box(BoxLayout.X_AXIS);
    private Box sportBox = new Box(BoxLayout.Y_AXIS);
    private JComboBox<String> sportList;
    private JLabel sportLabel;
    private Box communeBox = new Box(BoxLayout.Y_AXIS);
    private JComboBox<String> communeList;
    private JLabel communeLabel;
    private Box dateBox = new Box(BoxLayout.Y_AXIS);
    private Box resetDateBox = new Box(BoxLayout.X_AXIS);
    private JXDatePicker picker;
    private JLabel date;
    private Calendar selectedDay;


    private JComboBox<String> stageList;
    private JLabel stageLabel;
    private JButton resetTime;

    private JLabel placesRestantes = new JLabel("Places restantes : ");
    private JLabel prixLabel = new JLabel("Prix :", SwingConstants.CENTER);

    private JButton inscription = new JButton("Inscription");

    private JLabel messageErreur = new JLabel("");
    InscriptionStageUI(ConnectionBD connectionBD) {
        this.connectionBD = connectionBD;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        try {
            this.membreList = new JComboBox<>();
            createMembreList();
            membreList.addActionListener(this);
            this.membreLabel = new JLabel("Qui êtes vous ?", SwingConstants.CENTER);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        add(this.membreLabel);
        add(this.membreList);
        add(new JLabel(" "));

        try {
            this.sportList = new JComboBox<>();
            createSportList();
            sportList.addActionListener(this);
            this.sportLabel = new JLabel("Liste des sports :", SwingConstants.CENTER);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.sportBox.add(this.sportLabel);
        this.sportBox.add(this.sportList);
        this.triStageBox.add(this.sportBox);
        this.triStageBox.add(new JLabel("       "));

        try {
            this.communeList = new JComboBox<>();
            createCommuneList();
            communeList.addActionListener(this);
            this.communeLabel = new JLabel("Liste des communes :", SwingConstants.CENTER);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.communeBox.add(this.communeLabel);
        this.communeBox.add(this.communeList);
        this.triStageBox.add(this.communeBox);
        this.triStageBox.add(new JLabel("       "));

        this.picker = new JXDatePicker();
        this.picker.setDate(Calendar.getInstance().getTime());
        this.picker.setFormats(new SimpleDateFormat("dd.MM.yyyy"));
        this.picker.addActionListener(this);
        this.date = new JLabel("Date : ", SwingConstants.CENTER);
        this.selectedDay = Calendar.getInstance();
        this.resetDateBox.add(this.picker);
        this.resetDateBox.add(new JLabel("      "));
        this.resetTime = new JButton("Reset date");
        this.resetTime.addActionListener(this);
        this.resetDateBox.add(this.resetTime);
        this.dateBox.add(this.date);
        this.dateBox.add(this.resetDateBox);
        this.triStageBox.add(this.dateBox);

        add(this.triStageBox);
        add(new JLabel(" "));

        try {
            this.stageList = new JComboBox<>();
            createStageList();
            stageList.addActionListener(this);
            this.stageLabel = new JLabel("Liste des stages:", SwingConstants.CENTER);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        add(this.stageLabel);
        add(this.stageList);
        add(new JLabel(" "));

        add(this.placesRestantes);
        add(this.prixLabel);
        add(new JLabel(" "));

        this.inscription.addActionListener(this);
        add(this.inscription);
        add(new JLabel(" "));

        add(this.messageErreur);
    }

    private void createCommuneList() throws SQLException {
        Connection connection = connectionBD.getConnection();
        String PRE_STMT1 = "select distinct commune from terrain";
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        this.communeList.addItem("Toutes");
        while (rset.next()) {
            this.communeList.addItem(rset.getString(1));
        }
        stmt.close();
        System.out.println("Stmt closed.");
        rset.close();
        System.out.println("ResultSet closed.");
        connection.close();
        System.out.println("Connection closed.");
    }

    /*private void createStageList() throws SQLException {
        Connection connection = connectionBD.getConnection();
        //String PRE_STMT1 = "select stages.codeStage, nomSport, nomTerrain, Commune, dateStage, heureDebut, heureFin, counts2*10-counts places from Stage stages, (select codestage, COUNT(codePersonne) counts from EstInscritA group by codestage) inscrits, (select codestage, count(codePersonne) counts2 from ESTENCADREPAR group by codestage) encadrants where stages.codestage=inscrits.codestage and stages.codestage=encadrants.codestage and inscrits.codestage=encadrants.codestage and dateStage>" + dateConvert(Calendar.getInstance().getTime());
        String PRE_STMT1 = "select codeStage, nomSport, nomTerrain, Commune, dateStage, heureDebut, heureFin from Stage where dateStage>" + dateConvert(Calendar.getInstance().getTime());
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        String stage;
        int length;
        while (rset.next()) {
            stage="";
            for(int j=1; j<5; j++){
                stage+=rset.getString(j);
                stage+=", ";
            }
            stage+=rset.getString(5).substring(4,6)+"/";
            stage+=rset.getString(5).substring(2,4)+"/";
            stage+=rset.getString(5).substring(0,2)+" ";
            length=rset.getString(6).length();
            stage+=rset.getString(6).substring(0,length-2)+":";
            stage+=rset.getString(6).substring(length-2,length)+"-";
            length=rset.getString(7).length();
            stage+=rset.getString(7).substring(0,length-2)+":";
            stage+=rset.getString(7).substring(length-2,length)+", ";
            this.stageList.addItem(stage);
        }
        stmt.close();
        System.out.println("Stmt closed.");
        rset.close();
        System.out.println("ResultSet closed.");
        connection.close();
        System.out.println("Connection closed.");
    }*/

    private void createMembreList() throws SQLException {
        Connection connection = connectionBD.getConnection();
        String PRE_STMT1 = "select codePersonne, prenom, nom from personne where codepersonne>1000";
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        String membre;
        while (rset.next()) {
            membre="";
            for(int j=1; j<4; j++){
                membre+=rset.getString(j)+" ";
            }
            this.membreList.addItem(membre);
        }
        stmt.close();
        System.out.println("Stmt closed.");
        rset.close();
        System.out.println("ResultSet closed.");
        connection.close();
        System.out.println("Connection closed.");
    }

    private void createSportList() throws SQLException {
        Connection connection = connectionBD.getConnection();
        String PRE_STMT1 = "select nomSport from sport";
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        this.sportList.addItem("Tous");
        while (rset.next()) {
            this.sportList.addItem(rset.getString(1));
        }
        stmt.close();
        System.out.println("Stmt closed.");
        rset.close();
        System.out.println("ResultSet closed.");
        connection.close();
        System.out.println("Connection closed.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == membreList){
            JComboBox cb = (JComboBox)e.getSource();
            String membre = (String)cb.getSelectedItem();
            membre = membre.split(" ")[0];
            this.codeMembre = Integer.parseInt(membre);
            if (selectedStage!=null) {
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
                createStageList();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        if (e.getSource() == communeList){
            JComboBox cb = (JComboBox)e.getSource();
            this.selectedCommune = (String)cb.getSelectedItem();
            System.out.println(selectedCommune);
            try {
                createStageList();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        if (e.getSource() == picker){
            Date selectedDate = this.picker.getDate();
            this.selectedDate = dateConvert(selectedDate);
            try {
                createStageList();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        if (e.getSource()== resetTime) {
            this.selectedDate=null;
            try {
                createStageList();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        if (e.getSource() == stageList){
            JComboBox cb = (JComboBox)e.getSource();
            this.selectedStage = (String)cb.getSelectedItem();
            if(selectedStage!=null) {
                selectedStage = selectedStage.split(",")[0];
                try {
                    affichePlacesRestantes();
                    affichePrix();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (e.getSource() == inscription){
            try {
                createEntry();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void affichePlacesRestantes() throws SQLException {
        Connection connection = connectionBD.getConnection();
        String PRE_STMT1 = "select Case When counts2*10-counts<capa-counts Then counts2*10-counts Else capa-counts end as places from (select Capacite capa from Terrain terrain, Stage stage where terrain.nomTerrain=stage.nomTerrain and terrain.commune=stage.commune and stage.codestage="+ Integer.parseInt(selectedStage) +"), (select COUNT(codePersonne) counts from EstInscritA where codestage="+ Integer.parseInt(selectedStage) +" group by codestage), (select count(codePersonne) counts2 from ESTENCADREPAR where codestage="+ Integer.parseInt(selectedStage) +" group by codestage)";
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        if(rset.next()) {
            places=rset.getInt(1);
            this.placesRestantes.setText("Places restantes: " + places);
        }
        else {
            PRE_STMT1="select Case When 10*counts<capa then 10*counts Else capa end as places from (select Capacite capa from Terrain terrain, Stage stage where codestage="+ Integer.parseInt(selectedStage) +" and terrain.nomTerrain=stage.nomTerrain and terrain.commune=stage.commune), (select count(codePersonne) counts from ESTENCADREPAR where codestage="+ Integer.parseInt(selectedStage) +" group by codestage)";
            stmt = connection.prepareStatement(PRE_STMT1);
            rset = stmt.executeQuery();
            rset.next();
            places=rset.getInt(1);
            this.placesRestantes.setText("Places restantes: " + places);
        }
        stmt.close();
        System.out.println("Stmt closed.");
        rset.close();
        System.out.println("ResultSet closed.");
        connection.close();
        System.out.println("Connection closed.");
    }

    private void createEntry() throws SQLException {
        if (codeMembre<0) {
            this.messageErreur.setText("Veuillez vous sélectionner dans la liste des membres");
        }
        else {
            if (selectedStage == null) {
                this.messageErreur.setText("Veuillez sélectionner un stage");
            } else {
                if (prix == null) {
                    affichePrix();
                }
                if (places<1){
                    this.messageErreur.setText("Il n'y a plus de places disponibles pour ce stage");
                }
                else {
                    Connection conn = connectionBD.getConnection();
                    String PRE_STMT1 = "";
                    Statement stmt;
                    stmt = conn.createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
                    PRE_STMT1 = "INSERT into EstInscritA(codePersonne, codeStage, prixInscription, dateInscription) values ";
                    PRE_STMT1 += "(" + this.codeMembre + ","
                            + this.selectedStage + ","
                            + this.prix + ","
                            + dateConvert(Calendar.getInstance().getTime())
                            + ")";
                    System.out.println("PRE_STMT1 = " + PRE_STMT1);
                    stmt.executeUpdate(PRE_STMT1);
                    conn.close();
                }
            }
        }
    }

    private void createStageList() throws SQLException {
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
        //String PRE_STMT1 = "select stages.codeStage, nomSport, nomTerrain, Commune, dateStage, heureDebut, heureFin, counts2*10-counts places from (select codestage, COUNT(codePersonne) counts from EstInscritA group by codestage) inscrits, (select codestage, count(codePersonne) counts2 from ESTENCADREPAR group by codestage) encadrants, Stage stages where stages.codestage=inscrits.codestage and stages.codestage=encadrants.codestage and inscrits.codestage=encadrants.codestage and dateStage>" + dateConvert(Calendar.getInstance().getTime()) + requete;
        String PRE_STMT1 = "select codeStage, nomSport, nomTerrain, Commune, dateStage, heureDebut, heureFin from Stage where dateStage>" + dateConvert(Calendar.getInstance().getTime()) + requete;
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        System.out.println(PRE_STMT1);
        String stage;
        int length;
        this.stageList.removeAllItems();
        while (rset.next()) {
            stage="";
            for(int j=1; j<5; j++){
                stage+=rset.getString(j);
                stage+=", ";
            }
            stage+=rset.getString(5).substring(4,6)+"/";
            stage+=rset.getString(5).substring(2,4)+"/";
            stage+=rset.getString(5).substring(0,2)+" ";
            length=rset.getString(6).length();
            stage+=rset.getString(6).substring(0,length-2)+":";
            stage+=rset.getString(6).substring(length-2,length)+"-";
            length=rset.getString(7).length();
            stage+=rset.getString(7).substring(0,length-2)+":";
            stage+=rset.getString(7).substring(length-2,length)+", ";
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
        String PRE_STMT1 = "select tarifStage from sport where nomSport=(select nomSport from Stage where codeStage=" + Integer.parseInt(selectedStage) + ")";
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        rset.next();
        prix = rset.getInt(1);
        System.out.println(prix);
        System.out.println(codeMembre);
        if(codeMembre>-1){
            String PRE_STMT2 = "select commune from stage where codestage=" + Integer.parseInt(selectedStage);
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
