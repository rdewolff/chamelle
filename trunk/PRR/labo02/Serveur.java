import java.rmi.*;

public class Serveur extends UnicastRemoteObject implements MultMatrice {
	
	public Message getParams()
	{
		return new Message(3, {1}, {{1},{1}});
	}
	
	public void setLigneUpdate(int[] ligne, int n)
	{}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
