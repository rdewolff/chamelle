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
public class RMIClient
{
	// variables utilises
	private String 	adrServeur;
	private int 	id;
	private int[] 	ligneA;
	private int[][] matriceB;
	private int[] 	ligneC;
	
	/*
	// permet d'introduire les informations dans le client (RMI)
	synchronized public void remplirMatrice(int id, int[] ligneA, int[][] matriceB) throws RemoteException {
		this.id = id;
		this.ligneA = ligneA;
		this.matriceB = matriceB;
	}
	*/
	
	// effectue les calculs sur les matrices necessaires
	private void calcul(){
		System.out.println("Client " + id + " calcul la matrice");
	}
	
	// programme principal
	public static void main(String argv[])
	{
		System.out.println("Lancement du client");
		
		// Connexion
		// pas de sécurité pour nos test TODO mettre la securite
		// System.setSecurityManager(new RMISecurityManager());
		
		String serveurNom = "rmi://localhost/RMIServeurNomInterface";
		RMIServeurNomInterface serveur = null;
		try {	
			serveur = (RMIServeurNomInterface)Naming.lookup(serveurNom);
		} catch (Exception e) {
			System.out.println("Erreur de connexion au serveur: " + e);
			System.exit(1);
		} 
		// connection
		try {
			serveur.inscription("localhost");
		} catch (Exception e) {
			System.out.println("Erreur de traitement: " + e);
		} 
		// inscription
	}
}
