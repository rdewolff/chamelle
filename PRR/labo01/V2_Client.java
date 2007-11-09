import java.net.*;
import java.io.*;

public class V2_Client extends Config
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
			// adresse et port de connexion passe en parametre
			InetAddress address = InetAddress.getByName(HOST);

			// synchronisation avec le serveur
			String query = "HELO";
			int TAILLE_TAMPON = 2*TAILLE_INT;
			int TAILLE_TAMPON_SYNCHRO = query.length()*TAILLE_CHAR; // message recu : "HELO"
			byte[] tampon;

			System.out.println("*** Client ***");
			// Variables UDP
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket paquet;

			// Boucle d'attente de l'existence du serveur
			while (true) {
				try {
					tampon = new byte[TAILLE_TAMPON_SYNCHRO]; // definit la taille miniumm necessaire.
					tampon = query.getBytes();
					// synchronisation avec le serveur : envoie au serveur un paquet de requete, 
					// pour qu'il sache qu'il est connecte
					paquet = new DatagramPacket(tampon, tampon.length, address, PORT);
					socket.send(paquet); // envoi du paquet a l'aide du socket

					/* 
					 * Recoit les infos du serveur
					 */
					tampon = new byte[TAILLE_TAMPON]; // reinit le tampon avec la bonne taille
					paquet = new DatagramPacket(tampon, tampon.length);
					socket.setSoTimeout(1000);
					socket.receive(paquet); 
					break;
				} catch (Exception e) {
					System.out.println("Tentative de connexion...");
				}
			}
			// Enleve le chien de garde de la socket
			socket.setSoTimeout(0);

			// la connection est des lors etablie, la communication fonctionne
			System.out.println("Connection avec le serveur etablie");

			// decomponse les elements recus par le serveur
			int offset = 0; // le pointeur d'insertion dans le tableau de byte
			// recoit les infos qui sont dans un seul character (deux nombre < 10)
			int ligneACalculer = IntToBytes.bytesToInt(tampon, (offset++));
			int n = IntToBytes.bytesToInt(tampon, (offset++)*4);

			System.out.println("ligneACalculer: " + ligneACalculer + "\nn: " + n);

			offset = 0; // Reset de l'offset de lecture
			tampon = new byte[(n*n+n)*TAILLE_INT]; // reinit le tampon avec la bonne taille
			paquet = new DatagramPacket(tampon, tampon.length);
			socket.receive(paquet); 
			
			// Variables pour le calcul de la ligne
			int[] ligneA = new int[n];
			int[] ligneC = new int[n];			
			int[][] tabB = new int[n][n];

			// reconstruit la ligne A
			for (short i=0; i<n; i++) {
				ligneA[i] = IntToBytes.bytesToInt(tampon, (offset++)*TAILLE_INT);
			}

			// reconstruit la matrice B
			for (short i=0; i<n; i++) {
				for (short j=0; j<n; j++) {
					tabB[i][j] = IntToBytes.bytesToInt(tampon, (offset++)*TAILLE_INT);
				}
			}

			/*
			 * Calcul la ligne correspondante de C
			 */
			for (short j=0; j<n; j++) { // j = colonne
				// multiplie avec la colonne de la matrice B
				for (short k=0; k<n; k++) {
					ligneC[j] += ligneA[k] * tabB[k][j];
				}    		   
			}

			// reset la décalage
			offset = 0;  

			/* 
			 * Emet la ligne calculee au coordinateur
			 */
			IntToBytes.intToBytes(ligneACalculer, tampon, (offset++));

			// envoie la ligne calculee au serveur
			for (short i=0; i<n; i++) {
				IntToBytes.intToBytes(ligneC[i], tampon, (offset++)*4);
			}

			// affiche les resultats
			System.out.println("Ligne de A");
			for (int i=0; i<ligneA.length; i++) {
				System.out.print(ligneA[i] + " ");
			}

			// affiche la matrice B
			System.out.println("\nMatrice B");
			afficheMatrice(tabB);

			// affiche la ligne C calculee par ce client
			System.out.println("Ligne de C calculee");
			for (int i=0; i<ligneC.length; i++) {
				System.out.print(ligneC[i] + " ");
			}

			// envoie tout
			paquet = new DatagramPacket(tampon, tampon.length, address, PORT);
			socket.send(paquet); // envoi du paquet a l'aide du socket

			// ferme la connection
			socket.close();
			System.out.println("\n*** fin client ***");

		} catch (IOException e) {
			System.err.println(e);

		}
	}
}

