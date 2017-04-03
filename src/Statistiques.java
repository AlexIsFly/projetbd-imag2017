import java.sql.*;
import javax.swing.*;

public class Statistiques extends JPanel{
	
	private int nbInscrits;
	private int nbStagiaires;
	private String[][] listTerrains;
	private float ratio;
	private int recettes;
	
	ConnectionBD conn;
	JLabel inscritsLabel1;
	JLabel inscritsLabel2;
	JLabel stagiairesLabel1;
	JLabel stagiairesLabel2;
	JLabel terrainsLabel1;
	JLabel terrainsLabel2;
	JLabel ratioLabel1;
	JLabel ratioLabel2;
	JLabel recettesLabel1;
	JLabel recettesLabel2;
	
	public Statistiques() {
		// TODO Auto-generated constructor stub
	}
	
	
	//Renvoie -1 si erreur
 	public int calculInscrits() throws SQLException {
		
		ConnectionBD connection = new ConnectionBD();
		Connection con = connection.getConnection();
		
		Statement inscrits = null;
		
		//La requête doit retourner le nombre d'inscrits sous la forme d'un entier
		final String inscritsStr = "";
		
		try {
			con.setAutoCommit(true);
			
			inscrits = con.createStatement();
			
			ResultSet inscritsRes = inscrits.executeQuery(inscritsStr);
			inscritsRes.next();
			int nbInscrits = inscritsRes.getInt(1);
			return nbInscrits;
			
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
	        return -1;
	    } finally {
	        con.setAutoCommit(true);
	    }
	}
	
 	//Renvoie -1 si erreur
	public int calculStagiaires() throws SQLException {
			//Renvoie le nombre total de stagiaires, -1 si erreur
		
			ConnectionBD connection = new ConnectionBD();
			Connection con = connection.getConnection();

			Statement stagiaires = null;
			
			//La requête doit retourner le nombre total de stagiaires sous la forme d'un entier
			final String stagiairesStr = "";
			
			try {
				con.setAutoCommit(true);
				
				stagiaires = con.createStatement();
				
				ResultSet stagiairesRes = stagiaires.executeQuery(stagiairesStr);
				stagiairesRes.next();
				int nbStagiaires = stagiairesRes.getInt(1);
				return nbStagiaires;
				
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
		        return -1;
		    } finally {
		        con.setAutoCommit(true);
		    }
		}
	
	//Renvoie un tableau à 2 dimensions contenant la commune puis le nom des 5 terrains les plus utilisés
	public String[][] calculTerrains() throws SQLException {
		
		ConnectionBD connection = new ConnectionBD();
		Connection con = connection.getConnection();
		
		Statement terrains = null;
		
		//La requête doit renvoyer la liste des terrains par ordre décroissant d'utilisation, 
		//avec la commune en 1ère colonne et le nom du terrain en second
		final String terrainsStr = "";
		
		try {
			con.setAutoCommit(true);
			
			terrains = con.createStatement();
			
			ResultSet terrainsRes = terrains.executeQuery(terrainsStr);
			String[][] listTerrains = new String[5][2];
			if (terrainsRes != null) {
				int i=0;
				while (i<5 && terrainsRes.next()) {
					listTerrains[i][0]=terrainsRes.getString(1); //Commune
					listTerrains[i][1]=terrainsRes.getString(2); //Nom terrain
					terrainsRes.next();
				}
			}	
			
			return listTerrains;
			
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
	        return null;
	    } finally {
	        con.setAutoCommit(true);
	    }
	}
	
	//Renvoie -1 si erreur
	public float calculRatio() throws SQLException {
			
			ConnectionBD connection = new ConnectionBD();
			Connection con = connection.getConnection();
			
			Statement stmt_ratio = null;
			
			//La requête doit renvoyer le ratio supervision/encadrement des moniteurs
			//sous la forme d'un flottant
			final String ratioStr = "";
			
			try {
				con.setAutoCommit(true);
				
				stmt_ratio = con.createStatement();
				
				ResultSet ratioRes = stmt_ratio.executeQuery(ratioStr);
				ratioRes.next();
				float ratio = ratioRes.getFloat(1);
				
				return ratio;
				
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
		        return -1;
		    } finally {
		        con.setAutoCommit(true);
		    }
	}

	//Renvoie -1 si erreur
	public int calculRecettes() throws SQLException {
		
		ConnectionBD connection = new ConnectionBD();
		Connection con = connection.getConnection();
		
		Statement stmt_recettes = null;
		
		//La requête doit renvoyer les recettes totales sous forme d'un entier
		final String recettesStr = "";
		
		try {
			con.setAutoCommit(true);
			
			stmt_recettes = con.createStatement();
			
			ResultSet recettesRes = stmt_recettes.executeQuery(recettesStr);
			recettesRes.next();
			int recettes = recettesRes.getInt(1);
			return recettes;
			
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
	        return -1;
	    } finally {
	        con.setAutoCommit(true);
	    }
	}

	
}
