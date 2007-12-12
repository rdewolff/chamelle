/**
 * Fichier : RMIClient.java
 * Date    : 12 decembre 2007
 * 
 * Le client va se connecter au serveur de nom puis mettre à disposition les
 * methodes necessaires pour que le serveur puisse effectuer les calculs
 * necessaire sur celui-ci.
 * 
 * Il determine aussi son propre nom unique afin que le coordinateur puisse
 * s'y connecter.
 * 
 * Etapes
 * ------
 *  1. Connexion au serveur de nom
 *  2. Se met a disposition pour le coordinateur (attente passive)
 *  3. Effectue les calculs sur les matrices une fois les informations necessaires
 *     disponibles.
 *     
 * Deux methodes sont mises a disposition : 
 * 
 * 	- remplirMatrice(int[] ligne, int[][] matrice) :
 * 		permet au coordinateur de placer la ligne et la matrice a calculer
 * 
 *  - calculs() :
 *  	utilise en local pour effectuer les calculs des matrices. Synchornisee 
 *  	afin de pouvoir mettre en attente tant que les donnees ne sont pas arrivee
 *  	du serveur.
 * 
 * @author Romain de Wolff
 * @author Simon Hintermann
 *
 */

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class RMIClient extends UnicastRemoteObject implements RMIClientInterface 
{
	// appel du constructeur parent
	protected RMIClient() throws RemoteException {
		super();
	}

	// identifiant de la version demande par java
	private static final long serialVersionUID = 0L;

	// variables utilises
	private static int 		id; // identifiant du client courant
	private int[] 			ligneA; 
	private int[][] 		matriceB; 
	private static int[] 	ligneC; // la ligne calculee
	private Host 			adrCoord;

	// pour la creation d'un nom unique de client
	private static int nano = (int) System.nanoTime();

	/**
	 * Permet au coordinateur de deposer ses valeurs
	 * 
	 * @param ligne	Tableau a une dimension de la ligne a calculer
	 * @param matrice La matrice que l'on va utiliser pour faire la multiplication
	 */
	synchronized public void remplirMatrice(
			int[] ligne, int[][] matrice, Host host) throws RemoteException {
		// assignation des variables
		this.ligneA = ligne; 
		this.matriceB = matrice;
		this.adrCoord = host; 
		// affichage
		System.out.println("Matrice recue");
		Outils.afficheMatrice(matriceB);
		System.out.println("Ligne a calculer recue");
		for (int i=0; i<ligne.length; i++) {
			System.out.print(ligneA[i] + " ");
		}
		System.out.print("\n");
		// on peut continuer l'execution et faire les calculs maintenant
		// qu'on a les donnees
		notify();
	}

	/**
	 * Effectue les calculs sur les matrices
	 */
	synchronized public void calculs() throws RemoteException {
		// mise en attente tant que les donnees ne sont pas presentes
		try {
			wait();
		} catch (InterruptedException e) {
			System.out.println(e);
		}
		/*
		 * Calcul la ligne correspondante de C
		 */

		System.out.println("Calculs");

		// initialise la variable a la bonne taille
		ligneC = new int[matriceB.length];

		// effectue les calculs
		for (short i=0; i<matriceB.length; i++) 
			for(short j=0; j<matriceB.length; j++)
				ligneC[i] += matriceB[j][i] * ligneA[j];

		// affiche la ligne calculee
		System.out.println("Ligne de C calculee : ");
		for (int i=0; i<ligneC.length;i++) 
			System.out.print(ligneC[i] + " ");
		System.out.println("\n");
	}

	public void retournerResultats() {

		System.out.println("Connexion au serveur/coordinateur");
		String serveurCoordinateur = "rmi://" + adrCoord.getHost() + "/" + 
			adrCoord.getNomAcces();
		RMIServeurInterface coordinateur = null; 
		try {
			coordinateur = (RMIServeurInterface)Naming.lookup(serveurCoordinateur);
		} catch (Exception e) {
			System.out.println("Erreur de connexion au serveur: " + e);
			System.exit(1);
		} 

		// utilise la methode du serveur/coordinateur pour retourner ses resultats
		try {
			coordinateur.mettreResultat(id, ligneC);
		} catch (Exception e) {
			System.out.println("Erreur de traitement: " + e);
		} 
	}
	
	/**
	 * Programme principal
	 */
	public static void main(String argv[])
	{
		System.out.println("Lancement du client");

		/*
		 * Connexion au serveur de nom
		 */

		// Securite (debug)
		// System.setSecurityManager(new RMISecurityManager());
		String serveurNom = "rmi://localhost/RMIServeurNomInterface";
		RMIServeurNomInterface serveur = null;
		try {	
			serveur = (RMIServeurNomInterface)Naming.lookup(serveurNom);
		} catch (Exception e) {
			System.out.println("Erreur de connexion au serveur: " + e);
			System.exit(1);
		}

		// Determine son nom qui sera accessible par le serveur/coordinateur
		String nomClientRMI = "Client" + nano;

		// inscription et recuperation de son identifiant
		try {
			id = serveur.inscription("localhost", nomClientRMI);
		} catch (Exception e) {
			System.out.println("Erreur de traitement: " + e);
		} 

		/* 
		 * Se met a disposition du serveur/coordinateur
		 */

		RMIClientInterface srv = null;
		try {
			srv = new RMIClient();
			Naming.rebind(nomClientRMI,srv);
			System.out.println(nomClientRMI + " pret pour la reception des donnees");
		} catch (Exception e) {
			System.out.println("Exception a l'enregistrement: " + e);
		}

		/*
		 * Appel la methode qui effectue les calculs sur les matrices 
		 */ 

		try {
			srv.calculs();
		} catch (RemoteException e) {
			System.out.println(e);
		} 

		/*
		 * Retourne les resultats au serveur
		 */

		srv.retournerResultats();

		// fin
		System.out.println("Fin du client");
		System.exit(1);
	}
}
