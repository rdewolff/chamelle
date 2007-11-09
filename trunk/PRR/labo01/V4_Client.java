import java.net.*;
import java.io.*;

/**
 * Communication Multicast entre un serveur et N clients 
 * (avec UDP pour la synchro).
 * 
 * Ce programme va multiplier deux matrices contenant N x N nombres entiers
 * 
 * La classe abstraite Config permet de recuperer les parametres de ports et
 * le nombre de travailleurs, mais le client recuperera le nombre de 
 * travailleurs avec un message du serveur, pour avoir un peu de souplesse.
 * 
 * Le client va utiliser PORT(Config) pour son socket multicast, GROUPE(Config) 
 * pour l'adresse de multicast ou il va s'abonner, et PORT_UDP(Config) pour 
 * communiquer en UDP avec le serveur. Son port de communication UDP sera 
 * recupere par le serveur pour pouvoir communiquer.
 * 
 * Le coordinateur (serveur) va envoyer à chaque travailleur (client) leurs 
 * numero respectifs en UDP, par un socket UDP sur le port PORT_UDP(Config) 
 * -en recuperant leur adresse et port avec le paquet de connexion qu'il a recu 
 * pour router le paquet qu'il doit leur renvoyer-, puis il va leur envoyer les 
 * deux matrices a l'adresse GROUPE(Config) sur son port multicast 
 * PORT_MULTICAST(Config).
 * 
 * Chaque client va calculer la ligne de C et la remettre au serveur.
 * 
 * Note: Les clients peuvent etre lances avant le serveur.
 * 
 * @author Romain de Wolff
 * @author Simon Hintermann
 * @date 26 octobre 2007
 */
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
		DatagramPacket paquetS = new DatagramPacket(tampon, tampon.length, 
								 InetAddress.getByName(HOST), PORT_UDP);
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
				matA[i][j] = IntToBytes.bytesToInt(tampon, 
										(j*TAILLE_INT + i*n*TAILLE_INT));
			}
		
		// Recuperation de la matrice B
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
			{
				matB[i][j] = IntToBytes.bytesToInt(tampon, 
							 (j*TAILLE_INT + i*n*TAILLE_INT + n*n*TAILLE_INT));
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
		paquetS = new DatagramPacket(byteRetour, byteRetour.length, 
									 InetAddress.getByName(HOST), PORT_UDP);
		
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