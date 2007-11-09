import java.net.*;
import java.util.Random;
import java.io.*;


/**
 * Communication UDP avec un serveur et N clients.
 * 
 * @param x <code>int</code> un entier qui représente le nombre de lignes à calculer ainsi que le nombre de clients qu'il sera necessaire pour calculer la matrice
 * @author Romain de Wolff
 * @author Simon Hintermann
 */

public class V2_Serveur extends Config
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

	public static void main (String args[]) throws IOException, SocketException 
	{
		try {
			// declaration des variables
			int nbClientConnecte = 0;
			int n = N;
			// defini la taille du tampon de communication (pour synchronisation et utilisation)
			final int TAILLE_TAMPON = 2*TAILLE_INT;
			final int TAILLE_TAMPON_SYNCHRO = TAILLE_CHAR; // message recu : "HELO"

			// creation des tableaux sur lequelles on va faire les calculs
			int[][]  matA, matB, matC;
			matA = new int[n][n];
			matB = new int[n][n];
			matC = new int[n][n];

			// insertion de valeurs aleatoires dans le tableau
			Random hasard = new Random();
			// parcours les deux talbeaux et insere les valeurs aleatoires
			for (short i=0; i<n; i++) {
				for (short j=0; j<n; j++) {
					matA[i][j] = hasard.nextInt(10);
					matB[i][j] = hasard.nextInt(10);
				}
			}

			// tampon utilise pour la communication lors de la synchro
			byte[] tampon = new byte[TAILLE_TAMPON_SYNCHRO];

			// ouverture d'un port en mode UDP
			DatagramSocket socket = new DatagramSocket(PORT);
			DatagramPacket paquet = new DatagramPacket(tampon, tampon.length);
			System.out.println("*** Serveur demarre ***");

			/*
			 * Attends que les n travailleurs se presentent
			 */
			// attends que tous les clients soient connecté
			Clients[] clients = new Clients[n];
			while (nbClientConnecte < n) 
			{
				// synchronisation avec les clients
				socket.receive(paquet); // attend la requete du client 
				// stock les informations du client dans un objet prevu a cet effet			
				clients[nbClientConnecte] = new Clients(nbClientConnecte, paquet.getAddress(), paquet.getPort());
				tampon = new byte[TAILLE_TAMPON]; // redefinit la taille du tampon 
				IntToBytes.intToBytes(nbClientConnecte, tampon, 0);
				IntToBytes.intToBytes(n, tampon, TAILLE_INT);
				// envoie le paquet "tampon" contenant la taille des matrices ainsi que la ligne de A et la matrice B
				paquet = new DatagramPacket(tampon, tampon.length, clients[nbClientConnecte].getAddress(), 
						clients[nbClientConnecte].getPort());
				socket.send(paquet);
				System.out.println("Client " + nbClientConnecte + " connecte"); // afiche qu'un client est connecte
				nbClientConnecte++;
			}

			/*
			 * Emet une ligne de A et la matrice B a chaque travailleur
			 */
			System.out.println("Envoie des donnees aux clients");
			tampon = new byte[(n*n+n)*TAILLE_INT]; // redefini la taille du tampon 
			int offset = 0; // variable utilisee pour savoir ou on en est dans le tambon
			for (short k=0; k<n; k++) {
				offset = 0; // reinitialise pour chaque client la position dans le tampon

				// insere la ligne de la matrice A dans le tampon
				for (short i=0; i<n; i++) {
					IntToBytes.intToBytes(matA[k][i], tampon, offset*TAILLE_INT);
					offset++;
				}

				// insere la matrice B dans le tampon
				for (short i=0; i<n; i++) {
					for (short j=0; j<n; j++) {
						IntToBytes.intToBytes(matB[i][j], tampon, offset*TAILLE_INT);
						offset++;
					}
				}

				// envoie le paquet "tampon" contenant la taille des matrices ainsi que la ligne de A et la matrice B
				paquet = new DatagramPacket(tampon, tampon.length, clients[k].getAddress(), clients[k].getPort());
				socket.send(paquet);
			}				

			/*
			 * Attends les n lignes de C
			 */
			for (short k=0; k<n; k++) {
				offset = 0; // reset la position ou on se trouve dans le tampon
				paquet = new DatagramPacket(tampon, tampon.length);
				socket.receive(paquet); // attend la requete du client
				int ligneRecue = IntToBytes.bytesToInt(tampon, offset*TAILLE_INT);
				offset++;
				for (short i=0; i<n; i++) {
					matC[ligneRecue][i] = IntToBytes.bytesToInt(tampon, offset*TAILLE_INT);
					offset++;
				}
			}

			System.out.println("Toutes les lignes ont ete recues");

			System.out.println("Affichages des matrices");

			/*
			 * Affiche la matrice
			 */
			System.out.println("Matrice A");
			afficheMatrice(matA);

			System.out.println("Matrice B");
			afficheMatrice(matB);

			System.out.println("Matrice C = A x B (recue ligne par ligne)");
			afficheMatrice(matC);

			// pour la verification, on recalcul la matrice localement
			// reinit la matrice avant le calcul local
			for (short i=0; i<n; i++) { // i = ligne
				for (short j=0; j<n; j++) { // j = colonne
					matC[i][j] = 0;
				}
			}

			// calcul les valeurs localement, pour comparer avec les informatiosn recus des clients
			for (short i=0; i<n; i++) { // i = ligne
				for (short j=0; j<n; j++) { // j = colonne
					// multiplie avec la colonne de la matrice B
					for (short k=0; k<n; k++) {
						matC[i][j] += matA[i][k] * matB[k][j];
					}    		   
				}
			}

			// affiche la matrice C calculee en local pour comparaison
			System.out.println("Matrice C = A x B (local calcul)");
			afficheMatrice(matC);

			// ferme le socket de connexion
			socket.close();
			System.out.println("*** fin serveur ***");

		} catch (IOException e) {
			System.err.println(e);

		}
	}
}

