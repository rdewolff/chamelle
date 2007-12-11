
import java.rmi.*;
import java.util.LinkedList;

public interface RMIServeurNomInterface extends Remote
{
	public int inscription( String adr) throws RemoteException;
	public LinkedList<String> getClients(int n) throws RemoteException;
	
}
