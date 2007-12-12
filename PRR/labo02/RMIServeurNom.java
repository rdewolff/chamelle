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
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// tableau contenant l'adresse des clients
	private LinkedList<Host> clients = new LinkedList<Host>();
	private Host coordinateur = null;
	private int nbClientsVoulus = 0;

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
	synchronized public int inscription(String adr, String nom) throws RemoteException {
		
		// cree un object Client qui stock les information
		System.out.println("Clients " + (clients.size()+1) + " inscrit!");
		Host cli = new Host(adr, nom);
		
		// ajoute ce client a la linkedlist local
		clients.add(cli); 
		
		// si un serveur/coordinateur est deja venu et qu'il n'y a pas encore
		// le nombre de clients voulu, verifie si ce nombre est atteint
		if (nbClientsVoulus != 0) {
			if (clients.size() == nbClientsVoulus) {
				// si c'est le cas, on notifie la methode en attente
				System.out.println("C'est partie le serveur!");
				// notifie la methode qui renvoie les clients (RMI)
				notify();
			}
		}
		// renvoie un numero qui correspond a l'identifiant du client
		return clients.size();
		
	}
	
	synchronized public void inscriptionCoordinateur(Host cli) throws RemoteException {
		coordinateur = cli; 
	}
	
	synchronized public Host getCoordinateur() throws RemoteException {
		return coordinateur;
	}

	/**
	 * Renvoie les adresses des clients inscrits sur le serveur de nom 
	 * @return 
	 */
	synchronized public LinkedList<Host> getClients(int n) throws RemoteException {
		if (clients.size() < n) {
			nbClientsVoulus = n;
			try {
				// met en attente car le nombre de client n'est pas encore disponible
				wait();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		// retourne les clients 
		return clients;	  
		
	}
 
	public static void main(String args[])
	{	
		/*
		 * Lance le serveur de nom et met ses methodes a dispositions
		 */
		
		System.out.println("Lancement du serveur de noms");
		// pas de sécurité pour nos test
		//System.setSecurityManager(new RMISecurityManager());
		try {
			String serveurNom = "RMIServeurNomInterface";
			RMIServeurNomInterface serveur = new RMIServeurNom();
			Naming.rebind(serveurNom,serveur);
			System.out.println("Serveur " + serveurNom + " pret");
		} catch (Exception e) {
			System.out.println("Exception a l'enregistrement: " + e);
		}
		
	}
}