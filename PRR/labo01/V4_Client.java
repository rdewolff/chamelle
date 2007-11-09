import java.net.*;
import java.io.*;


public class V4_Client
{
	// Le port utilise pour le multicast
	static final int portMulti = 4445;
	// Le port utilise pour l'UDP
	static final int portUDP = 4447;
	// Taille d'un integer en bytes
	static final int tailleInt = 4;
	
	public static void main(String args[]) throws IOException
	{
		// Tampons pour la communication
		byte[] tampon = new byte[1];
		byte[] tamponDim = new byte[tailleInt*2];
		// Le nombre de travailleurs
		int n = 0;
		// Le numero attribue de cette instance de travailleur
		int numero =0;
		// Les matrices a traiter
		int[][]  matA, matB;
		
		// Parametres du mlticast
		MulticastSocket socket = new MulticastSocket(portMulti);
		InetAddress groupe = InetAddress.getByName("228.5.6.7");
		// Rejoint le groupe de multicast
		socket.joinGroup(groupe);
		
		// synchronisation avec le serveur : envoie au serveur un paquet de requete, pour qu'il sache qu'il est connecte
		DatagramSocket socketS = new DatagramSocket();
		DatagramPacket paquetS = new DatagramPacket(tampon, tampon.length, InetAddress.getByName("localhost"), portUDP);
		socketS.send(paquetS); // envoi du paquet a l'aide du socket
		
		// Reception du nombre de travailleurs et de numero de ce travailleur
		paquetS = new DatagramPacket(tamponDim, tamponDim.length);
		socketS.receive(paquetS);
		n = IntToBytes.bytesToInt(tamponDim, 0);
		numero = IntToBytes.bytesToInt(tamponDim, tailleInt);
		
		// Initialisation des dimensions des matrices
		matA = new int[n][n];
		matB = new int[n][n];
		// Taille du tampon de reception pour les deux matrices
		tampon = new byte[(n*n + n*n)*tailleInt];
		DatagramPacket paquet = new DatagramPacket(tampon,tampon.length);
		
		// Attend les matrices
		socket.receive(paquet);
		tampon = paquet.getData();
		
		// Recuperation de la matrice A
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
			{
				matA[i][j] = IntToBytes.bytesToInt(tampon, (j*tailleInt + i*n*tailleInt));
			}
		// Recuperation de la matrice B
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
			{
				matB[i][j] = IntToBytes.bytesToInt(tampon, (j*tailleInt + i*n*tailleInt + n*n*tailleInt));
			}
		
		// Variables pour le retour de la ligne calculee
		int[] ligneRetour = new int[n+1];
		byte[] byteRetour = new byte[n*tailleInt + tailleInt];
		// Numero du travailleur
		ligneRetour[0] = numero;
		// Calcul de la ligne a renvoyer
		for (short i=0; i<n; i++) 
			for(short j=0; j<n; j++)
				ligneRetour[i+1] += matB[j][i] * matA[numero][j];
		// Creation du tableau de bytes a renvoyer au serveur
		for(short i=0; i<n+1; i++)
			IntToBytes.intToBytes(ligneRetour[i], byteRetour, i*tailleInt);
		
		paquetS = new DatagramPacket(byteRetour, byteRetour.length, InetAddress.getByName("localhost"), portUDP);
		socketS.send(paquetS);
		
		socket.leaveGroup(groupe);
		socket.close();
		socketS.close();
		System.out.println("*** Client termine ***");
	}
}