import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by riffardn on 4/4/17.
 */
public class InscriptionStageUI extends JPanel implements ActionListener {
    ConnectionBD connectionBD;
    int codeMembre=-1;
    JComboBox<String> membreList;
    JLabel membreLabel;
    JComboBox<String> sportList;
    JLabel sportLabel;
    JComboBox<String> stageList;
    JLabel stageLabel;
    JLabel prixLabel;

    public InscriptionStageUI(ConnectionBD connectionBD) {
        this.connectionBD = connectionBD;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        try {
            String[] membres = createMembreList();
            this.membreList = new JComboBox<String>(membres);
            membreList.addActionListener(this);
            this.membreLabel = new JLabel("Qui êtes vous ?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        add(this.membreLabel);
        add(this.membreList);
        try {
            String[] sports = createSportList();
            this.sportList = new JComboBox<String>(sports);
            sportList.addActionListener(this);
            this.sportLabel = new JLabel("Liste des sports");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        add(this.sportLabel);
        add(this.sportList);
        try {
            String[] stages = createStageList();
            this.stageList = new JComboBox<String>(stages);
            stageList.addActionListener(this);
            this.stageLabel = new JLabel("Liste des stages:");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        add(this.stageLabel);
        add(this.stageList);
    }

    private String[] createStageList() throws SQLException {
        Connection connection = connectionBD.getConnection();
        String[] stages = new String[100];
        int i = 0;
        String PRE_STMT1 = "select stages.codeStage, nomSport, nomTerrain, Commune, dateStageDeb, dateStageFin, counts2*10-counts places from (select codestage, COUNT(codePersonne) counts from EstInscritA group by codestage) inscrits, (select codestage, count(codePersonne) counts2 from ESTENCADREPAR group by codestage) encadrants, Stage stages where stages.codestage=inscrits.codestage and stages.codestage=encadrants.codestage and inscrits.codestage=encadrants.codestage";
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        while (rset.next()) {
            stages[i]="";
            for(int j=1; j<5; j++){
                stages[i]+=rset.getString(j);
                stages[i]+=", ";
            }
            stages[i]+=rset.getDate(5).toString();
            stages[i]+=": ";
            stages[i]+=rset.getTime(5).toString();
            stages[i]+="-";
            stages[i]+=rset.getTime(6).toString();
            stages[i]+=", Places restantes: ";
            stages[i]+=rset.getInt(7);
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
        int i = 0;
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
            System.out.println(membre);
            codeMembre = Integer.parseInt(membre);
        }
        if (e.getSource() == stageList){
            System.out.println(e.getActionCommand());
            JComboBox cb = (JComboBox)e.getSource();
            String stage = (String)cb.getSelectedItem();
            if(stage!=null) {
                stage = stage.split(",")[0];
                try {
                    affichePrix(stage);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (e.getSource() == sportList){
            JComboBox cb = (JComboBox)e.getSource();
            String sport = (String)cb.getSelectedItem();
            System.out.println(sport);
            try {
                updateStageMenu(sport);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void updateStageMenu(String sport) throws SQLException {
        Connection connection = connectionBD.getConnection();
        String[] stages = new String[100];
        int i = 0;
        String PRE_STMT1 = "select stages.codeStage, nomSport, nomTerrain, Commune, dateStageDeb, dateStageFin, counts2*10-counts places from (select codestage, COUNT(codePersonne) counts from EstInscritA group by codestage) inscrits, (select codestage, count(codePersonne) counts2 from ESTENCADREPAR group by codestage) encadrants, Stage stages where stages.codestage=inscrits.codestage and stages.codestage=encadrants.codestage and inscrits.codestage=encadrants.codestage and nomSport='" + sport + "'";
        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1);
        ResultSet rset = stmt.executeQuery();
        while (rset.next()) {
            stages[i]="";
            for(int j=1; j<5; j++){
                stages[i]+=rset.getString(j);
                stages[i]+=", ";
            }
            stages[i]+=rset.getDate(5).toString();
            stages[i]+=": ";
            stages[i]+=rset.getTime(5).toString();
            stages[i]+="-";
            stages[i]+=rset.getTime(6).toString();
            stages[i]+=", Places restantes: ";
            stages[i]+=rset.getInt(7);
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

    private void affichePrix(String sport) throws SQLException {
        Connection connection = connectionBD.getConnection();
        int prix;
        System.out.println(sport);
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
                prix = (int)(0.9*prix);
            }
            stmt2.close();
            stmt3.close();
            rset2.close();
            rset3.close();

        }
        this.prixLabel = new JLabel("Prix :" + Integer.toString(prix));
        this.add(this.prixLabel);
        stmt.close();
        System.out.println("Stmt closed.");
        rset.close();
        System.out.println("ResultSet closed.");
        connection.close();
        System.out.println("Connection closed.");
    }
}
