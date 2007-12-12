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
	private String 			adrServeur;
	private static int 		id;
	private int[] 			ligneA;
	private int[][] 		matriceB;
	private static int[] 	ligneC;
	
	private static int nano = (int) System.nanoTime();

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
		// initialise la variable a la bonne taille
		ligneC = new int[matriceB.length];
		
		/*
		 * Effectue les calculs
		 */
		for (short i=0; i<matriceB.length; i++) 
			for(short j=0; j<matriceB.length; j++)
				ligneC[i] += matriceB[j][i] * ligneA[j];
		
		// affiche la ligne calculee
		System.out.println("Ligne de C calculee : ");
		for (int i=0; i<ligneC.length;i++) 
			System.out.print(ligneC[i] + " ");
		System.out.println("\n");
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
		
		// Determine son nom qui sera accessible par le serveur/coordinateur
		String nomClientRMI = "Client" + nano;
		
		// inscription et recuperation de son identifiant
		try {
			id = serveur.inscription("localhost", nomClientRMI);
		} catch (Exception e) {
			System.out.println("Erreur de traitement: " + e);
		} 

		/* 
		 * se met a disposition du serveur/coordinateur
		 */
		
		// pas de sécurité pour nos test
		//System.setSecurityManager(new RMISecurityManager());
		RMIClientInterface srv = null;
		try {
			srv = new RMIClient();
			Naming.rebind(nomClientRMI,srv);
			System.out.println(nomClientRMI + " pret pour la reception des donnees");
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

		// TODO Change
		System.out.println("Connexion au serveur/coordinateur");
		String serveurCoordinateur = "rmi://localhost/Coordinateur";
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

		// fin
		System.out.println("Fin du client");
		System.exit(1);
	}
}
