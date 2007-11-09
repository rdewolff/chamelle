import java.net.*;
import java.io.*;


public class V4_Client extends Config
{

	public static void main(String args[]) throws IOException
	{
		/*
		 * Initialisation
		 */
		System.out.println("*** Client ***");
		
		// Tampons pour la communication
		byte[] tampon = new byte[1];
		byte[] tamponDim = new byte[TAILLE_INT*2];
		int n; // Le nombre de travailleurs
		// Le numero de la ligne qu'il va calculer (correspond a son id)
		int numero;
		// Les matrices a traiter
		int[][]  matA, matB;

		// Parametres du multicast
		MulticastSocket socket = new MulticastSocket(PORT);
		InetAddress groupe = InetAddress.getByName(GROUPE);
		
		// Rejoint le groupe de multicast
		socket.joinGroup(groupe);

		// synchronisation avec le serveur : envoie au serveur un paquet de 
		// requete, pour qu'il sache qu'il est connecte
		DatagramSocket socketS = new DatagramSocket();
		DatagramPacket paquetS = new DatagramPacket(tampon, tampon.length, InetAddress.getByName(HOST), PORT_UDP);
		socketS.send(paquetS); // envoi du paquet a l'aide du socket

		/*
		 * Recoit les informations du serveur
		 */
		
		// Reception du nombre de travailleurs et de numero de ce travailleur
		paquetS = new DatagramPacket(tamponDim, tamponDim.length);
		socketS.receive(paquetS);
		n = IntToBytes.bytesToInt(tamponDim, 0);
		numero = IntToBytes.bytesToInt(tamponDim, TAILLE_INT);

		// Initialisation des dimensions des matrices
		matA = new int[n][n];
		matB = new int[n][n];
		
		// Taille du tampon de reception pour les deux matrices
		tampon = new byte[(n*n + n*n)*TAILLE_INT];
		DatagramPacket paquet = new DatagramPacket(tampon,tampon.length);

		// Attend les matrices
		socket.receive(paquet);
		tampon = paquet.getData();

		// Recuperation de la matrice A
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
			{
				matA[i][j] = IntToBytes.bytesToInt(tampon, (j*TAILLE_INT + i*n*TAILLE_INT));
			}
		
		// Recuperation de la matrice B
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
			{
				matB[i][j] = IntToBytes.bytesToInt(tampon, (j*TAILLE_INT + i*n*TAILLE_INT + n*n*TAILLE_INT));
			}

		// Variables pour le retour de la ligne calculee
		int[] ligneRetour = new int[n+1];
		byte[] byteRetour = new byte[n*TAILLE_INT + TAILLE_INT];
		
		// Numero du travailleur
		ligneRetour[0] = numero;
		
		/*
		 * Calcul de la ligne a renvoyer
		 */
		
		for (short i=0; i<n; i++) 
			for(short j=0; j<n; j++)
				ligneRetour[i+1] += matB[j][i] * matA[numero][j];
		
		/*
		 * Emet la ligne calculee au coordinateur
		 */
		
		// Creation du tableau de bytes a renvoyer au serveur
		for(short i=0; i<n+1; i++)
			IntToBytes.intToBytes(ligneRetour[i], byteRetour, i*TAILLE_INT);

		// Construit le paquet a envoyer
		paquetS = new DatagramPacket(byteRetour, byteRetour.length, InetAddress.getByName(HOST), PORT_UDP);
		
		// L'envoie
		socketS.send(paquetS);

		
		/*
		 * Fin, deconnexion et fermeture
		 */
		
		socket.leaveGroup(groupe);
		socket.close();
		socketS.close();
		
		System.out.println("*** Client termine ***");
	}
}