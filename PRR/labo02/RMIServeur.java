/**
 * Avant de lancer le serveur il faut utiliser la commande : 
 *  rmiregistry
 * et avant, il a fallu lancer la commande sur les fichier compilé (*.class) 
 * suivante :
 *  rmic RMIConcurrentClient
 */

import java.rmi.*;
import java.rmi.server.*;

public class RMIServeur extends UnicastRemoteObject implements RMIServeurInterface
{
	// tableau contenant l'adresse des clients
	private String[] 			adrClients;
	private int[][] matriceA;
	private int[][] matriceB;
	private int[][] matriceC;

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


	synchronized public void Acces1() throws RemoteException
	{
		// Signaler qu'un processus est dedans
		System.out.println("Serveur -- dedans Acces1");
		for (int i = 0; i < 100000; i++)
			for (int j = 0; j < 10000; j++);

		System.out.println("Serveur -- sortie Acces1");
	}

	synchronized public void Acces2() throws RemoteException
	{	
		// Signaler qu'un processus est dedans
		System.out.println("Serveur -- dedans Acces2");
		for (int i = 0; i < 100000; i++)
			for (int j = 0; j < 10000; j++);

		System.out.println("Serveur -- sortie Acces2");
	}

	public static void main(String argv[])
	{
		if (argv.length != 1) {
			System.out.println("Demarrer en passant le parametre N (taille des matrices)");
			System.exit(1);
		}
		
		// determine la taille des matrices en fonction de l'argument passe
		try {
			int N = Integer.parseInt(argv[0]);
		} catch (NumberFormatException e) {
			System.out.println(e);
		}

		// obitent les cliens depuis le serveur de noms
		
		
		// pas de sécurité pour nos test
		// System.setSecurityManager(new RMISecurityManager());
		try {
			String serveurNom = "RMIConcurrent";
			RMIServeurInterface serveur = new RMIServeur();
			Naming.rebind(serveurNom,serveur);
			System.out.println("Serveur " + serveurNom + " pret");
		} catch (Exception e) {
			System.out.println("Exception a l'enregistrement: " + e);
		}
	}
}
