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


public class RMIServeurNom extends UnicastRemoteObject implements RMIServeurNomInterface
{
	// tableau contenant l'adresse des clients
	private LinkedList<String> clients = new LinkedList<String>();
	private String	 adrServeur;

	public RMIServeurNom() throws RemoteException
	{
		super();
	}

	/** 
	 * Permet a un client de s'inscrire aupres du serveur de nom
	 * @param id	L'id du client qui va correspondre a la ligne qu'il va
	 * 				calculer
	 * @param adr 	L'adresse du client
	 */
	synchronized public void inscription( String adr) throws RemoteException {
		clients.add(adr);
	}

	/**
	 * Renvoie les adresses des clients inscrits sur le serveur de nom 
	 * @return 
	 */
	synchronized public LinkedList<String> getClients() throws RemoteException {
		return clients;	  
	}

	public static void main(String args[])
	{
		// pas de sécurité pour nos test
		// System.setSecurityManager(new RMISecurityManager());
		try {
			String serveurNom = "ServeurNoms";
			RMIServeurNomInterface serveur = new RMIServeurNom();
			Naming.rebind(serveurNom,serveur);
			System.out.println("Serveur " + serveurNom + " pret");
		} catch (Exception e) {
			System.out.println("Exception a l'enregistrement: " + e);
		}
	}
}
