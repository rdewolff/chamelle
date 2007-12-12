import java.rmi.*;
import java.util.LinkedList;

public interface RMIServeurNomInterface extends Remote
{ 
	public int inscription(String adr, String nom) throws RemoteException;
	public LinkedList<Host> getClients(int n) throws RemoteException;
	public void inscriptionCoordinateur(Host cli) throws RemoteException; 
	public Host getCoordinateur() throws RemoteException;
	
}
