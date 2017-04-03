import java.sql.*;

public class Statistiques {
	
	public void calculStats() throws SQLException {
		
		ConnectionBD connection = new ConnectionBD();
		Connection con = connection.getConnection();
		
		Statement inscrits = null;
		Statement stagiaires = null;
		Statement terrains = null;
		Statement ratio = null;
		Statement recettes = null;
		
		String inscritsStr = "";
		String stagiairesStr = "";
		String terrainsStr = "";
		String ratioStr = "";
		String recettesStr = "";
		
		try {
			con.setAutoCommit(true);
			
			inscrits = con.createStatement();
			stagiaires = con.createStatement();
			terrains = con.createStatement();
			ratio = con.createStatement();
			recettes = con.createStatement();
			
			
			ResultSet inscritsRes = inscrits.executeQuery(inscritsStr);
			ResultSet stagiairesRes = stagiaires.executeQuery(stagiairesStr);
			ResultSet terrainsRes = terrains.executeQuery(terrainsStr);
			ResultSet ratioRes = ratio.executeQuery(ratioStr);
			ResultSet recettesRes = recettes.executeQuery(recettesStr);
			
		} catch (SQLException e ) {
	        //Afficher exception	
	        if (con != null) {
	            try {
	                System.err.print("Transaction is being rolled back");
	                con.rollback();
	            } catch(SQLException excep) {
	            	//Afficher exception
	            }
	        }
	    } finally {
	        //Affichage des résultats sur l'interface graphique
	        con.setAutoCommit(true);
	    }
	}

}
