
import java.rmi.*;
import java.util.LinkedList;

public interface RMIConcurrentServeurNomInterface extends Remote
{
	public void inscription( String adr) throws RemoteException;
	public LinkedList<String> getClients() throws RemoteException;
	
	
	public void Acces1() throws RemoteException;
	public void Acces2() throws RemoteException;
}
