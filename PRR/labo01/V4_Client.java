import java.net.*;
import java.io.*;

public class V4_Client
{
	/** Le port utilise pour le multicast */
	static int portMulti = 4445;
	/** Taille d'un integer en bytes */
	static final int tailleInt = 4;
	
	/* 
	 * Affichage de la matrice passee en parametre (2D)
	 */
	private static void afficheMatrice(int [][] tableau) {
		for (short i=0; i<tableau.length; i++) {
			for (short j=0; j<tableau.length; j++) {
				System.out.print(tableau[i][j] + " ");
			}
			System.out.print('\n');
		}
	}
	
	public static void main(String args[]) throws IOException
	{
		byte[] tampon = new byte[1];
		byte[] tamponDim = new byte[tailleInt*2];
		int n = 0;
		int numero =0;
		int[][]  matA, matB;
		
		//Joindre le groupe pour recevoir le message diffuse
		MulticastSocket socket = new MulticastSocket(portMulti);
		InetAddress groupe = InetAddress.getByName("228.5.6.7");
		socket.joinGroup(groupe);
		
		// synchronisation avec le serveur : envoie au serveur un paquet de requete, pour qu'il sache qu'il est connecte
		DatagramSocket socketS = new DatagramSocket();
		DatagramPacket paquetS = new DatagramPacket(tampon, tampon.length, InetAddress.getByName("localhost"), 4447);
		socketS.send(paquetS); // envoi du paquet a l'aide du socket
		
		paquetS = new DatagramPacket(tamponDim, tamponDim.length);
		socketS.receive(paquetS);
		
		n = IntToBytes.bytesToInt(tamponDim, 0);
		numero = IntToBytes.bytesToInt(tamponDim, tailleInt);
		matA = new int[n][n];
		matB = new int[n][n];
		
		tampon = new byte[(n*n + n*n)*tailleInt];
		
		//Attendre le message du serveur
		DatagramPacket paquet = new DatagramPacket(tampon,tampon.length);
		
		socket.receive(paquet);
		tampon = paquet.getData();
		System.out.println(numero);
		// Recuperation de la matrice
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
			{
				matA[i][j] = IntToBytes.bytesToInt(tampon, (j*tailleInt + i*n*tailleInt));
			}
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
			{
				matB[i][j] = IntToBytes.bytesToInt(tampon, (j*tailleInt + i*n*tailleInt + n*n*tailleInt));
			}
		
		int[] ligneRetour = new int[n+1];
		byte[] byteRetour = new byte[n*tailleInt + tailleInt];
		
		ligneRetour[0] = numero;
		// Calcul de la ligne a renvoyer
		for (short i=0; i<n; i++) 
			for(short j=0; j<n; j++)
				ligneRetour[i+1] += matB[j][i] * matA[numero][j];
		
		// Creation du tableau de bytes a renvoyer au serveur
		for(short i=0; i<n+1; i++)
			IntToBytes.intToBytes(ligneRetour[i], byteRetour, i*tailleInt);
		
		paquetS = new DatagramPacket(byteRetour, byteRetour.length, InetAddress.getByName("localhost"), 4447);
		socketS.send(paquetS);
		
		afficheMatrice(matA);
		afficheMatrice(matB);
		System.out.println(ligneRetour[1]+" "+ligneRetour[2]+" "+ligneRetour[3]);
		socket.leaveGroup(groupe);
		socket.close();
	}
}