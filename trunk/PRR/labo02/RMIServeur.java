/**
 * Fichier : RMIServeur.java
 * Date    : 12 decembre 2007
 * 
 * Le serveur prend un parametre N (entier entre 3 et 5) qui permet de definir 
 * avec quelle taille de matrice on desire travailler.
 * 
 * Avant de recuperer les clients, nous allons demarrer le "serveur RMI" du 
 * coordinateur/serveur afin que les clients puissent retourner les valeurs 
 * calculee au plus vite.
 * 
 * Puis le serveur se connecte au serveur de nom et tente d'y recuperer le 
 * nombre de clients voulus. Si les clients ne sont pas disponibles, le serveur
 * sera mis en attente passive. 
 * 
 * Une fois que le nombre de clients voulus se seront presente au serveur de
 * noms, le serveur recuperera leur nom et adresses afin de pouvoir s'y connecter
 * en RMI.
 * 
 * Apres avoir genere des matrices aleatoires, le serveur envoie a chaque client 
 * une ligne de A et la matrice B.  
 * 
 * Il met une methode a disposition qui permet a chaque client de retourner les
 * valeurs qu'il a calculee.
 * 
 *  
 * @author Romain de Wolff
 * @author Simon Hintermann
 *
 */

import java.rmi.*;
import java.rmi.server.*;
import java.util.LinkedList;
import java.util.Random;

public class RMIServeur extends UnicastRemoteObject 
		implements RMIServeurInterface
{
	// identifiant
	private static final long serialVersionUID = -7229048203890999957L;
	
	// taille des matrices
	static int N;
	
	// les matrices
	static int[][]  matriceA, matriceB, matriceC;
	int nombreLignesRecues = 0;

	// appel de la classe parente
	public RMIServeur() throws RemoteException
	{
		super();
	}

	/**
	 * Remplis la ligne "id" de la matrice C avec les valeurs passee en 
	 * parametre dans le tableau val. Cette methode est appelee par les clients
	 * a l'aide de RMI.
	 * 
	 * Une fois que le dernier client a rendu ses informations, on affiche 
	 * les resultats.
	 * 
	 * @param id	L'identifant du client qui correspond a la ligne de la 
	 * 				matrice calculee
	 * @param val	Tableau a une dimension d'entier qui correspond a la ligne 
	 * 				calculee
	 */
	synchronized public void mettreResultat(int id, int[] val) 
				throws RemoteException {
		// remplis la ligne 
		for (int i=0; i<val.length; i++) 
			matriceC[id-1][i] = val[i];
		// compte le nombre de lignes recues
		nombreLignesRecues++;
		
		// affiche qu'on a bien recu des resultats
		System.out.println("Resultats recus du client " + id);
		
		// si on a recu tout les resultats, on affiche les matrices 
		if (nombreLignesRecues == val.length) {
			// affiche les matrices
			System.out.println("Matrice A : ");
			Outils.afficheMatrice(matriceA);
			System.out.println("Matrice B : ");
			Outils.afficheMatrice(matriceB);
			System.out.println("Matrice C : ");
			Outils.afficheMatrice(matriceC);
		}
	}

	/**
	 * Demarre l'execution du serveur/coordinateur
	 */
	synchronized public void demarre() {

		/*
		 * Demarrage du mode serveur afin que les clients puisse se connecter
		 * tout de suite
		 */

		RMIServeurInterface srv = null;
		try {
			String srvCoord = "Coordinateur"; // TODO changer
			srv = new RMIServeur();
			Naming.rebind(srvCoord,srv);
			System.out.println(srvCoord + " pret pour la reception des donnees");
		} catch (Exception e) {
			System.out.println("Exception a l'enregistrement: " + e);
		}

		/*
		 * Obtient la liste des clients depuis le serveur de noms
		 */

		// Connexion au serveur de nom
		// System.setSecurityManager(new RMISecurityManager()); 
		String serveurNom = "rmi://localhost/RMIServeurNomInterface";
		RMIServeurNomInterface serveur = null;
		try {	
			serveur = (RMIServeurNomInterface)Naming.lookup(serveurNom);
		} catch (Exception e) {
			System.out.println("Erreur de connexion au serveur (1) : " + e);
			System.exit(1);
		} 

		// inscription et recuperation des addresses des clients
		LinkedList<Host> clients = null;
		try {
			clients = serveur.getClients(N); 
		} catch (Exception e) {
			System.out.println("Erreur de traitement: " + e);
		} 

		// pour debug, affichage du nombre de clients recus
		System.out.println("Nombre de client recupere : " + clients.size());

		/* 
		 * Initialise les matrices
		 */

		// creation des matrices sur lequelles on va faire des calculs
		// et les remplis avec des valeurs aleatoires
		matriceA = new int[N][N];
		matriceB = new int[N][N];
		matriceC = new int[N][N];

		// insertion de valeurs aleatoire dans le tableau
		Random hasard = new Random();
		// parcours les deux tableaux et insere les valeurs aleatoires
		for (short i=0; i<N; i++) {
			for (short j=0; j<N; j++) {
				matriceA[i][j] = hasard.nextInt(10);
				matriceB[i][j] = hasard.nextInt(10);
			}
		}			

		System.out.println("Connexion aux clients pour lui envoyer les infos");

		/*
		 * Envoie les donnees aux clients
		 */

		int cmp = 0; // compteur
		RMIClientInterface serveurClient = null;
		int[] ligneA = new int[N]; // la ligne de B envoyee
		for ( Host cli : clients) {
			cmp++;
			serveurNom = "rmi://" + cli.getHost() + "/" + cli.getNomAcces();
			System.out.println("Connexion au client " + cmp + " ("+serveurNom+")");
			try {
				serveurClient = (RMIClientInterface)Naming.lookup(serveurNom);
			} catch (Exception e) {
				System.out.println("Erreur de connexion au serveur (2) : " + e);
				System.exit(1);
			}
			// ligne de A envoyee
			for (int i=0;i<N;i++) {
				ligneA[i] = matriceA[cmp-1][i];
			}

			Host moi = new Host("localhost", "Coordinateur");
			try {
				serveurClient.remplirMatrice(ligneA, matriceB, moi);
			} catch (Exception e) {
				System.out.println("Erreur de traitement: " + e);
			} 
		}
		
	}

	public static void main(String argv[]) throws InterruptedException
	{

		// determine la taille des matrice a l'aide de l'argument de lancement
		// du programme
		if (argv.length != 1) {
			System.out.println("Demarrer en passant le parametre N " +
					"(taille des matrices)");
			System.exit(1);
		}

		// determine la taille des matrices en fonction de l'argument passe
		// qui va correspondre aussi au nombre de clients necessaires
		N = 0;
		try {
			N = Integer.parseInt(argv[0]);
		} catch (NumberFormatException e) {
			System.out.println(e);
		}
		// verifie que le nombre est entre 3 et 5 (compris)
		if (N<3 || N>5) {
			System.out.println("Entrer une valeur entre 3 et 5 (borne comprises)");
			System.exit(1);
		}

		// lance l'execution du serveur 
		RMIServeur monCoordinateur = null;
		try {
			monCoordinateur = new RMIServeur();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		monCoordinateur.demarre();
		
		// fin
		System.out.println("Fin du serveur/coordinateur");
		System.exit(1);

	}
}
