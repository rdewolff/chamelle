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
public class RMIClient implements RMIClientInterface
{
	// variables utilises
	private String 		adrServeur;
	private static int 	id;
	private int[] 		ligneA;
	private int[][] 	matriceB;
	private int[] 		ligneC;
	
	// permet d'introduire les informations dans le client (RMI)
	synchronized public void remplirMatrice(int id, int[] ligneA, int[][] matriceB) throws RemoteException {
		this.id = id;
		this.ligneA = ligneA;
		this.matriceB = matriceB;
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
			
		// inscription et recuperation de son identifiant
		try {
			id = serveur.inscription("localhost");
		} catch (Exception e) {
			System.out.println("Erreur de traitement: " + e);
		} 
		
		// calculs
		System.out.println("Clients " + id);
		
		// se met a disposition du serveur/coordinateur
		// pas de sécurité pour nos test
		//System.setSecurityManager(new RMISecurityManager());
		try {
			String srvClient = "Client"+id;
			RMIServeurNomInterface srv = new RMIServeurNom();
			Naming.rebind(srvClient,srv);
			System.out.println("Serveur " + srvClient + " pret");
		} catch (Exception e) {
			System.out.println("Exception a l'enregistrement: " + e);
		}
		
		
		
		// fin
		System.out.println("Fin du client");
	}

}
