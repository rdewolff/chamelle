import java.rmi.*;
import java.rmi.server.*;

public class Serveur extends UnicastRemoteObject implements MultMatrice {
	
	int[] l = new int[1];
	int[][] m = new int [1][1];
	
	Serveur() throws RemoteException
	{super();}
	
	public Message getParams() throws RemoteException
	{
		return new Message(3, l, m);
	}
	
	public void setLigneUpdate(int[] ligne, int n) throws RemoteException
	{;}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.setSecurityManager(new RMISecurityManager());
		// TODO Auto-generated method stub
		try {
			String serveurNom = "MultMatrice";
			MultMatrice serveur = new Serveur();
			Naming.rebind(serveurNom, serveur);
			System.out.println("Serveur " + serveurNom + " pret...");
		} catch(Exception e)
		{System.out.println("Probleme lors de l'execution du serveur" + e);
		System.exit(1);}
	}

}
