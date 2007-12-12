import java.rmi.*;
import java.util.LinkedList;

public interface RMIServeurNomInterface extends Remote
{ 
	public int inscription(String adr, String nom) throws RemoteException;
	public LinkedList<Client> getClients(int n) throws RemoteException;

}
