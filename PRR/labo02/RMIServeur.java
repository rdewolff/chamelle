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

public class RMIServeur extends UnicastRemoteObject implements RMIServeurInterface
{
	// tableau contenant l'adresse des clients
	private String[] 			adrClients;
	private int[][] matriceA;
	private int[][] matriceB;
	private int[][] matriceC;
	private static LinkedList<String> clients;

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
		
		int N = 0;
		
		// determine la taille des matrices en fonction de l'argument passe
		try {
			N = Integer.parseInt(argv[0]);
		} catch (NumberFormatException e) {
			System.out.println(e);
		}

		// obitent les cliens depuis le serveur de noms
		
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
			clients = serveur.getClients(N); 
		} catch (Exception e) {
			System.out.println("Erreur de traitement: " + e);
		} 
		
		System.out.println("Nombre de clients : " + clients.size());
		
		// Effectue le calcul de la matrice pour chaque client
		for ( String cli : clients) {
			System.out.println( "Client" + 1);
		}
		
		// fin
		System.out.println("Fin du serveur/coordinateur");
		
	}
}
