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
import java.rmi.server.UnicastRemoteObject;

public class RMIClient extends UnicastRemoteObject implements RMIClientInterface 
{
	protected RMIClient() throws RemoteException {
		super();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;

	private static final String RMIClient = null;

	// variables utilises
	private String 		adrServeur;
	private static int 	id;
	private int[] 		ligneA;
	private int[][] 	matriceB;
	private int[] 		ligneC;

	/**
	 * Permet d'introduire les informations dans le client (RMI) 
	 */
	synchronized public void remplirMatrice(int[] ligne, int[][] matrice) throws RemoteException {
		this.ligneA = ligne;
		this.matriceB = matrice;
		System.out.println("Matrice recue");
		Outils.afficheMatrice(matriceB);
		// affiche la ligne calculee
		System.out.println("Ligne a calculer recue");
		for (int i=0; i<ligne.length; i++) {
			System.out.print(ligneA[i] + " ");
		}
		System.out.print("\n");
		notify();
	}

	/**
	 * Effectue les calculs sur les matrices
	 */
	synchronized public void calculs() throws RemoteException {
		// met en attente si les donnes ne sont pas disponibles
		try {
			wait();
		} catch (InterruptedException e) {
			System.out.println(e);
		}
		System.out.println("Calculs");

		/*
		 * Calcul la ligne correspondante de C
		 */
		ligneC = new int[matriceB.length];
		
		for (short i=0; i<matriceB.length; i++) 
			for(short j=0; j<matriceB.length; j++)
				ligneC[i] += matriceB[j][i] * ligneC[j];
	}
	
	// programme principal
	public static void main(String argv[])
	{
		System.out.println("Lancement du client");

		/*
		 * Connexion au serveur de nom
		 */
		
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
		
		/* 
		 * se met a disposition du serveur/coordinateur
		 */
		System.out.println("Client " + id);
		// pas de sécurité pour nos test
		//System.setSecurityManager(new RMISecurityManager());
		RMIClientInterface srv = null;
		try {
			String srvClient = "Client"+id;
			srv = new RMIClient();
			Naming.rebind(srvClient,srv);
			System.out.println(srvClient + " pret pour la reception des donnees");
		} catch (Exception e) {
			System.out.println("Exception a l'enregistrement: " + e);
		}

		/*
		 * Effectue les calculs sur les matrices 
		 */ 
		try {
			srv.calculs();
		} catch (RemoteException e) {
			System.out.println(e);
		} 
		
		/*
		 * Retourne les resultats au serveur
		 */
		
		
		
		

		// fin
		System.out.println("Fin du client");
		System.exit(1);
	}
}
