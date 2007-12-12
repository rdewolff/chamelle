/**
 * Avant de lancer le serveur il faut utiliser la commande : 
 *  rmiregistry
 * et avant, il a fallu lancer la commande sur les fichier compilé (*.class) 
 * suivante :
 *  rmic RMIConcurrentClient
 */

import java.rmi.*;
import java.rmi.server.*;
import java.util.LinkedList;
import java.util.Random;

public class RMIServeur extends UnicastRemoteObject implements RMIServeurInterface
{
	private static final long serialVersionUID = -7229048203890999957L;
	
	// taille de la matrice
	static int N;
	// les matrices
	static int[][]  matriceA, matriceB, matriceC;
	int nombreLignesRecues = 0;

	public RMIServeur() throws RemoteException
	{
		super();
	}

	/**
	 * Remplis la ligne "id" de la matrice C avec les valeurs passee en 
	 * parametre dans le tableau val. Cette methode est appelee par les clients
	 * a l'aide de RMI.
	 * @param id
	 * @param val
	 */
	synchronized public void mettreResultat(int id, int[] val) throws RemoteException {
		// remplis la ligne 
		for (int i=0; i<val.length; i++) 
			matriceC[id-1][i] = val[i];

		nombreLignesRecues++;
		System.out.println("Resultats recus du client " + id);
		
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
	 * Affiche les valeurs des matrices
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
		// System.setSecurityManager(new RMISecurityManager()); // TODO security!
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
			Host moi = new Host("localhost", "Coordinateur"); // TODO determiner adresse IP
			serveur.inscriptionCoordinateur(moi);
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
			System.out.println( "Connexion au client " + cmp + " ("+serveurNom+")");
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

			try {
				serveurClient.remplirMatrice(ligneA, matriceB);
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
			System.out.println("Demarrer en passant le parametre N (taille des matrices)");
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

		// demarre
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
