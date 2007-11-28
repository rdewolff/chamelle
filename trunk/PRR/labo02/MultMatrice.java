import java.rmi.*;

public interface MultMatrice extends Remote 
{
	public Message getParams() throws RemoteException;
	public void setLigneUpdate(int[] l, int n) throws RemoteException;
}
