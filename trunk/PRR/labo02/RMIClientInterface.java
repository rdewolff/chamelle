
import java.rmi.*;
public interface RMIClientInterface extends Remote
{
  
  public void remplirMatrice(int id, int[] ligneA, int[][] matriceB) throws RemoteException;

}
