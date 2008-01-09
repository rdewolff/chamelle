/**
 * Interface du client
 */

import java.rmi.*;
public interface RMIClientInterface extends Remote
{
	public void remplirMatrice(int[] ligne, int[][] matrice, Host host) 
		throws RemoteException;
	public void calculs() throws RemoteException;
	public void retournerResultats() throws RemoteException;;
}
