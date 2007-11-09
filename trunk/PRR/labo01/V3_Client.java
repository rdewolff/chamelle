
import java.net.*;
import java.io.*;

/**
 * Communication UDP avec un serveur et N clients.
 * 
 * Ce programme va multiplier deux matrices contenant N x N nombres entiers. Les
 * parametres de configuration du serveur se trouve dans le fichier Config.java
 * dont herite chaque configuration.
 * 
 * Dans cette version (V3), le fonctionnement est identique a celui de la V2. La
 * difference reside dans le faite que la matrice B est diffusee aux 
 * travailleurs, puis chaque travailleur recoit une ligne de A et l'indice a
 * calculer.
 * 
 * On utilise une classe de configuration appelee "Config". Celle-ci determine 
 * la taille des matrices (N), et les ports utilisés lors de la communication 
 * UDP.
 * 
 * Nous communiquons de maniere direct avec les client en UDP et la diffusion 
 * se fait en UDP Multicast. Le groupe de multicast est defini a l'aide de la 
 * constante
 * 
 * Note: les clients peuvent etres lances avant le serveur.
 * 
 * @author 	Romain de Wolff
 * @author 	Simon Hintermann
 * @date 	26 octobre 2007
 */

public class V3_Client extends Config 
{	

	/** 
	 * Affichage de la matrice carrée passée en parametre (2 dimensions)
	 * @param int [] [] tableau
	 */
	private static void afficheMatrice(int [][] tableau) {
		for (short i=0; i<tableau.length; i++) {
			for (short j=0; j<tableau.length; j++) {
				System.out.print(tableau[i][j] + " ");
			}
			System.out.print('\n');
		}
	}

	/*
	 * Methode principale contenant tout le client
	 */
	public static void main (String args[]) throws IOException {
		try {
			/* 
			 * Déclarations et initialisations
			 */
			// adresse et port de connexion passe en parametre
			InetAddress address = InetAddress.getByName(HOST);

			// rejoint le groupe de diffusion au debut pour etre sur d'y etre
			// connecté quand le serveur enverra les informations
			MulticastSocket socketMulti = new MulticastSocket(PORT);
			InetAddress groupe = InetAddress.getByName(GROUPE);
			socketMulti.joinGroup(groupe);

			String query = "HELO"; // pour se sychroniser avec le serveur
			int TAILLE_TAMPON_SYNCHRO = query.length()*2; // message recu :"HELO"
			// defini la taille miniumm necessaire. Un char = 2 bytes
			byte[] tampon = new byte[TAILLE_TAMPON_SYNCHRO]; 
			tampon = query.getBytes();

			System.out.println("*** Client ***");

			DatagramSocket socket = new DatagramSocket();
			DatagramPacket paquet; 

			// Boucle d'attente de l'existence du serveur
			while (true) {
				try {
					// synchronisation avec le serveur : envoie au serveur un 
					//paquet de requete, pour qu'il sache qu'il est connecte
					paquet = new DatagramPacket(tampon, tampon.length, address, 
							PORT_UDP);
					socket.send(paquet); // envoi du paquet a l'aide du socket

					/*
					 * Recoit les informations du serveur
					 * - la taille des matrices a traiter
					 * - la matrice B
					 * - l'indice de B a calculer
					 * - la ligne de A 
					 */
					// reception de la taille des matrices
					tampon = new byte[TAILLE_INT]; // recoit un int de 4 bytes
					paquet = new DatagramPacket(tampon, tampon.length);
					socket.setSoTimeout(1000);
					socket.receive(paquet);
					break; // quitte la boucle si la connexion est effectuee
				} catch (Exception e) {
					System.out.println("Tentative de connexion...");
				}
			}

			int n = IntToBytes.bytesToInt(tampon, 0);
			System.out.println("Taille de la matrice : " + n);

			// recoit la matrice B du serveur en diffusion
			tampon = new byte[n*n*TAILLE_INT];
			paquet = new DatagramPacket(tampon, tampon.length);
			socketMulti.receive(paquet);

			System.out.println("Matrice B recue du serveur!");

			// reconstruit la matrice B
			int offset = 0;
			int[][] matB = new int[n][n];
			for (short i=0; i<n; i++) {
				for (short j=0; j<n; j++) {
					matB[i][j] = IntToBytes.bytesToInt(tampon, 
							offset*TAILLE_INT);
					offset++;
				}
			}

			// recoit l'indice de B et la ligne de A
			int[] ligneA = new int[n];
			int[] ligneC = new int[n];
			// redefini le tampon avec la bonne taille
			tampon = new byte[(n*n+1)*TAILLE_INT]; 
			paquet = new DatagramPacket(tampon, tampon.length);
			socket.receive(paquet); 

			// recupere l'indice de B qui est dans le tampon
			offset = 0; 
			int ligneACalculer = IntToBytes.bytesToInt(tampon, offset);
			offset++;

			// idem avec la ligne A
			for (short i=0; i<n; i++) {
				ligneA[i] = IntToBytes.bytesToInt(tampon, offset*TAILLE_INT);
				offset++;
			}

			// affiche la matrice
			afficheMatrice(matB);
			// et la ligne a calculer
			System.out.println("Ligne a calculer : " + ligneACalculer);

			/*
			 * Calcul la ligne correspondante de C
			 */
			// calcul les valeurs
			for (short j=0; j<n; j++) { // j = colonne
				// multiplie avec la colonne de la matrice B
				for (short k=0; k<n; k++) {
					ligneC[j] += ligneA[k] * matB[k][j];
				}    		   
			}

			// reset le décalage
			offset = 0;  
			tampon = new byte[(n*n+1)*4];

			/*
			 * Emet la ligne calculee au serveur/coordinateur
			 */
			IntToBytes.intToBytes(ligneACalculer, tampon, offset);
			offset++;

			// met la ligne calculee dans le tampon d'envoi
			for (short i=0; i<n; i++) {
				IntToBytes.intToBytes(ligneC[i], tampon, offset*TAILLE_INT);
				offset++;
			}

			// envoie tout
			paquet = new DatagramPacket(tampon, tampon.length, address, 
					PORT_UDP);
			socket.send(paquet); // envoi du paquet a l'aide du socket

			/*
			 * Affiche les resultats a titre informatif
			 */
			System.out.println("Ligne de A");
			for (int i=0; i<ligneA.length; i++) {
				System.out.print(ligneA[i] + " ");
			}

			// affiche la matrice B
			System.out.println("\nMatrice B");
			afficheMatrice(matB);

			// affiche la ligne C calculee par ce client
			System.out.println("Ligne de C calculee");
			for (int i=0; i<ligneC.length; i++) {
				System.out.print(ligneC[i] + " ");
			}

			// ferme les connections
			socketMulti.leaveGroup(groupe); // quite le serveur de diffusion
			socket.close();
			socketMulti.close();

			System.out.println("\n*** fin client ***");

		} catch (IOException e) {
			System.err.println(e);
		}
	}
}

