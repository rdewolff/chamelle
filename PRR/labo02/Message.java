
public class Message {
	
	private int n;
	private int[]][] matrice;
	private int[] ligne;
	
	Message(int n, int[] ligne, int[][] matrice)
	{
		this.n = n;
		this.ligne = ligne;
		this.matrice = matrice;
	}
	
	public int getN()
	{
		return n;
	}
	
	public int[] getLigne()
	{
		return ligne;
	}
	
	public int[][] getMatrice()
	{
		return matrice;
	}
	
//	public void setN(int n)
//	{
//		this.n = n;
//	}
	
	public void setLigne(int[] ligne)
	{
		this.ligne = ligne;
	}
	
	public void setMatrice(int[][] matrice)
	{
		this.matrice = matrice;
	}
}
