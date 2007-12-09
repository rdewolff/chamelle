/**
 * 
 * Le client va se connecter au serveur de nom puis mettre à disposition les
 * methodes necessaires pour que le serveur puisse effectuer les calculs
 * necessaire sur celui-ci.
 * 
 * Etapes
 * ------
 *  1. Connexion au serveur de nom
 *  2. Mise a disposition des methodes de calcul des matrices
 *  3. Quitte proprement le programme
 * 
 * @author Romain de Wolff
 * @author Simon Hintermann
 *
 */

import java.rmi.*;
public class RMIConcurrentClient
{
	private String adrServeur;
	private int id;
	private int[] ligneA;
	private int[][] matriceB;
	private int[] ligneC;
	
	synchronized public void remplirMatrice(int id, int[] ligneA, int[][] matriceB) {
		
	}
	
	private void calcul(){
		// miaw
		System.out.println("Client " + id + " calcul la matrice");
	}
	

	public static void main(String argv[])
	{
		// Initialisation
		String siteServeur = "localhost"; // inutile car pas de securite
		if (argv.length != 1) {
			System.out.println("Usage 1 RMIConcurrentClient {1|2}");
			System.exit(1);
		}
		if (argv[0].charAt(0) != '1' && argv[0].charAt(0) != '2') {
			System.out.println("Usage RMIConcurrentClient {1|2}");
			System.exit(1);
		}
		// Connexion
		// pas de sécurité pour nos test // System.setSecurityManager(new RMISecurityManager());
		String serveurNom = "rmi://" + siteServeur + "/RMIConcurrent";
		RMIConcurrent serveur = null;
		try {
			serveur = (RMIConcurrent)Naming.lookup(serveurNom);
		} catch (Exception e) {
			System.out.println("Erreur de connexion au serveur: " + e);
			System.exit(1);
		} 
		// calculs
		try {
			for (int i = 0; i < 10; i++) {
				if (argv[0].charAt(0) == '1')
					serveur.Acces1();
				else
					serveur.Acces2();
				for (int j = 0; j < 100000; j++);
			}
		} catch (Exception e) {
			System.out.println("Erreur de traitement: " + e);
		} 
		// fin
	}
}
