
import java.rmi.*;
import java.util.LinkedList;

public interface RMIServeurNomInterface extends Remote
{
	public void inscription( String adr) throws RemoteException;
	public LinkedList<String> getClients() throws RemoteException;
	
}
