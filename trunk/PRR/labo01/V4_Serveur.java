import java.io.*;
import java.net.*;
import java.util.Random;

/**
 * Communication Multicast entre un serveur et N clients (avec UDP pour la synchro).
 * 
 * Ce programme va multiplier deux matrices contenant N x N nombres entiers
 * 
 * La classe abstraite Config permet de recuperer les parametres de ports et
 * le nombre de travailleurs, mais le client recuperera le nombre de travailleurs
 * avec un message du serveur, pour avoir un peu de souplesse.
 * 
 * Le client va utiliser PORT(Config) pour son socket multicast, GROUPE(Config) pour l'adresse
 * de multicast ou il va s'abonner, et PORT_UDP(Config) pour communiquer en UDP avec le
 * serveur. Son port de communication UDP sera recupere par le serveur pour
 * pouvoir communiquer.
 * 
 * Le coordinateur (serveur) va envoyer à chaque travailleur (client) leurs numero
 * respectifs en UDP, par un socket UDP sur le port PORT_UDP(Config) -en recuperant leur 
 * adresse et port avec le paquet de connexion qu'il a recu pour router le paquet qu'il 
 * doit leur renvoyer-, puis il va leur envoyer les deux matrices a l'adresse GROUPE(Config) 
 * sur son port multicast PORT_MULTICAST(Config).
 * 
 * Chaque client va calculer la ligne de C et la remettre au serveur.
 * 
 * Note: Les clients peuvent etre lances avant le serveur.
 * 
 * @author Romain de Wolff
 * @author Simon Hintermann
 * @date 26 octobre 2007
 */
public class V4_Serveur extends Config
{

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

	public static void main(String[] args) throws IOException
	{
		
		/*
		 * Initialisation
		 */
		
		System.out.println("*** Serveur demarre ***");
		
		// Les matrices a traiter
		int[][]  matA, matB, matC;

		// Initialisation des dimensions des matrices
		matA = new int[N][N];
		matB = new int[N][N];
		matC = new int[N][N];

		// Insertion de valeurs aleatoire dans le tableau
		Random hasard = new Random();
		for (short i=0; i<N; i++) {
			for (short j=0; j<N; j++) {
				matA[i][j] = hasard.nextInt(10);
				matB[i][j] = hasard.nextInt(10);
			}
		}

		/*
		 * Connexions UDP
		 */
		
		// Parametres du multicast
		InetAddress groupe = InetAddress.getByName(GROUPE);
		MulticastSocket socket = new MulticastSocket(PORT_MULTICAST);
		
		// Variables pour la connexion d'un client
		byte[] tampon = new byte[1];
		DatagramPacket paquet = new DatagramPacket(tampon,tampon.length,groupe,PORT);
		
		// Parametres pour la communication UDP
		DatagramSocket socketS = new DatagramSocket(PORT_UDP);
		DatagramPacket paquetUDP = new DatagramPacket(tampon, tampon.length);
		byte[] tamponUDP = new byte[TAILLE_INT*2];

		/*
		 * Attends que les N travailleurs se presentent 
		 */
		
		// Tableau des clients a connecter
		Clients[] clients = new Clients[N];
		// Communications initiales client-serveur (UDP)
		for(short i=0; i<N; i++) 
		{
			// Synchronisation avec les clients
			socketS.receive(paquetUDP);
			// Stocke les informations du client dans un objet prevu a cet effet			
			clients[i] = new Clients(i, paquetUDP.getAddress(), paquetUDP.getPort());
			// Envoie le nombre de travailleurs et le numero de travailleur
			IntToBytes.intToBytes(N, tamponUDP, 0);
			IntToBytes.intToBytes(i, tamponUDP, TAILLE_INT);
			paquetUDP = new DatagramPacket(tamponUDP, tamponUDP.length, clients[i].getAddress(), clients[i].getPort());
			socketS.send(paquetUDP);
			System.out.println("Client " + (i+1) + " connecte");
		}
		
		/*
		 * Emet les deux matrices au travailleurs
		 */

		// Creation du message a diffuser aux clients
		tampon = new byte[(N*N + N*N)*TAILLE_INT];
		int offset = 0;

		// Insertion de la matrice A
		for(short i=0; i<N; i++)
			for(short j=0; j<N; j++)
			{
				IntToBytes.intToBytes(matA[i][j], tampon, offset*TAILLE_INT);
				offset++;
			}

		// Insertion de la matrice B
		for(int i=0; i<N; i++)
			for(int j=0; j<N; j++)
			{
				IntToBytes.intToBytes(matB[i][j], tampon, offset*TAILLE_INT);
				offset++;
			}

		// Envoi des deux matrices aux clients en multicast
		paquet = new DatagramPacket(tampon,tampon.length,groupe,PORT);
		socket.send(paquet);

		/*
		 * Attends les N lignes de C 
		 */
		
		// Reception des lignes calculees des clients
		for(short i=0; i<N; i++) 
		{
			tamponUDP = new byte[N*TAILLE_INT + TAILLE_INT];
			paquetUDP = new DatagramPacket(tamponUDP, tamponUDP.length);
			socketS.receive(paquetUDP);
			byte[] ligneRecue = paquetUDP.getData();
			// Le premier int represente le numero du client
			int noClient = IntToBytes.bytesToInt(ligneRecue, 0);
			// Mise a jour de la matrice C
			for(int j=0; j<N; j++)
				matC[noClient][j] = IntToBytes.bytesToInt(ligneRecue, j*TAILLE_INT + TAILLE_INT);
		}
		
		/*
		 * Affichages
		 */

		System.out.println("Matrice A: ");
		afficheMatrice(matC);

		System.out.println("\nMatrice B: ");
		afficheMatrice(matC);

		System.out.println("\nMatrice recue: ");
		afficheMatrice(matC);

		// Reinitialisation de la matrice C
		for(int i=0; i<N; i++)
			for(int j=0; j<N; j++)
				matC[i][j] = 0;

		// Calcul les valeurs localement, pour comparer avec les informations 
		// recues des clients
		for (short i=0; i<N; i++) { 
			for (short j=0; j<N; j++) { 
				for (short k=0; k<N; k++) {
					matC[i][j] += matA[i][k] * matB[k][j];
				}    		   
			}
		}

		// Affiche la matrice C calculee en local pour comparaison
		System.out.println("\nMatrice C = A x B (calcul local)");
		afficheMatrice(matC);

		/*
		 * Fin
		 */
		
		socket.close();
		socketS.close();
		System.out.println("\n*** Serveur termine ***");
	}
}
