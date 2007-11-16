import java.rmi.*;

public interface MultMatrice extends Remote 
{
	Outils getParams();
	void setLigneUpdate(int[], int n);
}
