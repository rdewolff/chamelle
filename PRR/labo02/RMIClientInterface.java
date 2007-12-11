
import java.rmi.*;
public interface RMIConcurrentClientInterface extends Remote
{
  
  public void remplirMatrice(int id, int[] ligneA, int[][] matriceB) throws RemoteException;

}
