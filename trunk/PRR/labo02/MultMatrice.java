import java.rmi.*;

public interface MultMatrice extends Remote 
{
	int[] getParams();
	void setLigneUpdate(int[], int n);
}
