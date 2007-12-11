
import java.rmi.*;
public interface RMIConcurrentServeurInterface extends Remote
{
	public void mettreResultat(int id, int[] val) throws RemoteException;
}
