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
	JLabel terrainsLabel3;
	JLabel terrainsLabel4;
	JLabel ratioLabel1;
	JLabel ratioLabel2;
	JLabel recettesLabel1;
	JLabel recettesLabel2;
	
	public Statistiques() {
		this.conn = new ConnectionBD();
		
		this.inscritsLabel1 = new JLabel("Nombre moyen d'inscrits par stage : ");
		this.stagiairesLabel1 = new JLabel("Nombre total de stagiaires : ");
		this.terrainsLabel1 = new JLabel("Terrains les plus utilisés : ");
		this.ratioLabel1 = new JLabel("Ratio supervision/encadrement des moniteurs : ");
		this.recettesLabel1 = new JLabel("Recettes totales : ");
		
		afficheStats();
	}
	
	private void afficheStats() {
		
		calculStats();
		
		Box b1 = Box.createHorizontalBox();
	    b1.add(this.inscritsLabel1);
	    this.inscritsLabel2 = new JLabel(String.valueOf(nbInscrits));
	    b1.add(this.inscritsLabel2);
		
	    Box b2 = Box.createHorizontalBox();
		b2.add(this.stagiairesLabel1);
		this.stagiairesLabel2 = new JLabel(String.valueOf(nbStagiaires));
		b2.add(this.stagiairesLabel2);
		
		Box b3 = Box.createHorizontalBox();
		b3.add(this.terrainsLabel1);
		this.terrainsLabel2 = new JLabel(listTerrains[0][0]+listTerrains[0][1]);
		b3.add(this.terrainsLabel2);
		this.terrainsLabel3 = new JLabel(listTerrains[1][0]+listTerrains[1][1]);
		b3.add(this.terrainsLabel3);
		this.terrainsLabel4 = new JLabel(listTerrains[2][0]+listTerrains[2][1]);
		b3.add(this.terrainsLabel4);
		
		Box b4 = Box.createHorizontalBox();
		b4.add(this.ratioLabel1);
		this.ratioLabel2 = new JLabel(String.valueOf(ratio));
		b4.add(this.ratioLabel2);
		
		Box b5 = Box.createHorizontalBox();
		b5.add(this.recettesLabel1);
		this.recettesLabel2 = new JLabel(String.valueOf(recettes));
		b5.add(this.recettesLabel2);
	}
	
	
	private void calculStats() {
		
		try {
			this.nbInscrits=calculInscrits();
			this.nbStagiaires=calculStagiaires();
			this.listTerrains=calculTerrains();
			this.ratio=calculRatio();
			this.recettes=calculRecettes();
		} catch (SQLException e ){
			//Exceptions levées par le setAutoCommit ?
			System.err.println(("setAutoCommit error"));
		}
	}
	
	//Renvoie -1 si erreur
 	private int calculInscrits() throws SQLException {
		
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
	private int calculStagiaires() throws SQLException {
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
	
	//Renvoie un tableau à 2 dimensions contenant la commune puis le nom des 3 terrains les plus utilisés
	private String[][] calculTerrains() throws SQLException {
		
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
				while (i<3 && terrainsRes.next()) {
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
	private float calculRatio() throws SQLException {
			
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
	private int calculRecettes() throws SQLException {
		
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
