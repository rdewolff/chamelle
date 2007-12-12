/**
 * Interface du client
 */

import java.rmi.*;
public interface RMIClientInterface extends Remote
{
	public void remplirMatrice(int[] ligne, int[][] matrice) throws RemoteException;
	public void calculs() throws RemoteException;
}
