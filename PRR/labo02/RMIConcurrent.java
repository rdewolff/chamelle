
import java.rmi.*;
public interface RMIConcurrent extends Remote
{
  public void Acces1() throws RemoteException;
  public void Acces2() throws RemoteException;
}
