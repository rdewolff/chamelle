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

	}

	/**
	 * Affiche les valeurs des matrices
	 */
	private void afficheMatrices() {
		System.out.println("Affichages des matrices : ");
	}

	public static void main(String argv[])
	{
		if (argv.length != 1) {
			System.out.println("Demarrer en passant le parametre N (taille des matrices)");
			System.exit(1);
		}
		
		// determine la taille des matrices en fonction de l'argument passe
		// qui va correspondre aussi au nombre de clients necessaires
		int N = 0;
		try {
			N = Integer.parseInt(argv[0]);
		} catch (NumberFormatException e) {
			System.out.println(e);
		}

		// obitent les clients depuis le serveur de noms
		
		// Connexion
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
		LinkedList<String> clients = null;
		try {
			clients = serveur.getClients(N); 
		} catch (Exception e) {
			System.out.println("Erreur de traitement: " + e);
		} 
		
		// pour debug, affichage du nombre de clients recus
		System.out.println("Nombre de client recupere : " + clients.size());
		
		// creation des matrices sur lequelles on va faire des calculs
		// et les remplis avec des valeurs aleatoires
		int[][]  matriceA, matriceB, matriceC;
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
		
		// envoie les donnees aux clients (matrice + ligne a calculer)
		int cmp = 0; // compteur
		RMIClientInterface serveurClient = null;
		int[] ligneA = new int[N]; // la ligne de B envoyee
		for ( String cli : clients) {
			cmp++;
			System.out.println( "Connexion au client " + cmp + "("+cli+")");
			serveurNom = "rmi://" + cli + "/Client"+cmp;
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
		
		// attente des donnees de tous les clients
		
		// fin
		System.out.println("Fin du serveur/coordinateur");
		
	}
}
