
import java.rmi.*;
public interface RMIServeurInterface extends Remote
{
	public void mettreResultat(int id, int[] val) throws RemoteException;
}
