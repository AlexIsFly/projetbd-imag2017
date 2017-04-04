import java.sql.*;
import java.util.Objects;

import javax.swing.*;

public class Statistiques extends JPanel{
	
	private int nbInscrits;
	private int nbStagiaires;
	private String[][] listTerrains;
	private float ratio;
	private int recettes;
	
	ConnectionBD conn;
	Connection con;
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
		try {
			this.con = conn.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
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
		if (!(listTerrains[0][0]==null)) {
			String s1 = listTerrains[0][1]+" à "+listTerrains[0][0];
			this.terrainsLabel2 = new JLabel(s1);
			b3.add(this.terrainsLabel2);
		}
		if (!(listTerrains[1][0]==null)) {
			String s2 = ", "+listTerrains[1][1]+" à "+listTerrains[1][0];
			this.terrainsLabel3 = new JLabel(s2);
			b3.add(this.terrainsLabel3);
		}
		if (!(listTerrains[2][0]==null)) {
			String s3 = ", "+listTerrains[2][1]+" à "+listTerrains[2][0];
			this.terrainsLabel4 = new JLabel(s3);
			b3.add(this.terrainsLabel4);
		}
		
		Box b4 = Box.createHorizontalBox();
		b4.add(this.ratioLabel1);
		this.ratioLabel2 = new JLabel(String.valueOf(ratio));
		b4.add(this.ratioLabel2);
		
		Box b5 = Box.createHorizontalBox();
		b5.add(this.recettesLabel1);
		this.recettesLabel2 = new JLabel(String.valueOf(recettes));
		b5.add(this.recettesLabel2);
		
	    
		Box b6 = Box.createVerticalBox();
		b6.add(b1);
		b6.add(b2);
		b6.add(b3);
		b6.add(b4);
		b6.add(b5);
		add(b6);
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
		
		Statement inscrits = null;
		Statement stages = null;
		
		//Le nombre moyen d'inscrit par stage est calculé après coup à partir du nombre de stages et du nombre d'inscrits
		final String inscritsStr = "SELECT COUNT(*) FROM EstInscritA";
		final String stagesStr = "SELECT COUNT(*) FROM Stage";
		
		try {
			con.setAutoCommit(true);
			
			inscrits = con.createStatement();
			stages = con.createStatement();
			
			ResultSet inscritsRes = inscrits.executeQuery(inscritsStr);
			ResultSet stagesRes = stages.executeQuery(stagesStr);
			inscritsRes.next();
			stagesRes.next();
			int nbInscrits = inscritsRes.getInt(1);
			int nbStages = stagesRes.getInt(1);
			if (nbStages == 0) {
				return 0;
			} else {
				return (nbInscrits/nbStages);
			}
			
		} catch (SQLException e ) {
	        e.printStackTrace();	
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
		
			Statement stagiaires = null;
			
			//La requête doit retourner le nombre total de stagiaires sous la forme d'un entier
			final String stagiairesStr = "SELECT COUNT(DISTINCT codePersonne) FROM EstInscritA";
			
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
		
		
		Statement terrains = null;
		
		//La requête doit renvoyer la liste des terrains par ordre décroissant d'utilisation, 
		//avec la commune en 1ère colonne et le nom du terrain en second
		final String terrainsStr = "SELECT s.commune, s.nomTerrain, COUNT(*) AS nbUtilisation FROM Stage s GROUP BY s.commune, s.nomTerrain ORDER BY nbUtilisation DESC";
		
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
	                System.err.print("Transaction Terrains is being rolled back");
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
			
			
			Statement stmt_supervision = null;
			Statement stmt_encadrement = null;
			
			//La requête doit renvoyer le ratio supervision/encadrement des moniteurs
			//sous la forme d'un flottant
			final String supervisionStr = "SELECT COUNT(*) FROM Stage";
			final String encadrementStr = "SELECT COUNT(*) FROM EstEncadrePar";
			
			try {
				con.setAutoCommit(true);
				
				stmt_supervision = con.createStatement();
				stmt_encadrement = con.createStatement();
				
				ResultSet supervisionRes = stmt_supervision.executeQuery(supervisionStr);
				ResultSet encadrementRes = stmt_encadrement.executeQuery(encadrementStr);
				supervisionRes.next();
				encadrementRes.next();
				int supervision = supervisionRes.getInt(1);
				int encadrement = encadrementRes.getInt(1);
				float ratio = supervision/encadrement;
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
		
		Statement stmt_recettes = null;
		
		//La requête doit renvoyer les recettes totales sous forme d'un entier
		final String recettesStr = "SELECT SUM(tarifStage) FROM Stage st, Sport sp WHERE st.nomSport=sp.nomSport";
		
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
