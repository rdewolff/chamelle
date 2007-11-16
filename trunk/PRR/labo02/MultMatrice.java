import java.rmi.*;

public interface MultMatrice extends Remote 
{
	public Message getParams();
	public void setLigneUpdate(int[], int n);
}
