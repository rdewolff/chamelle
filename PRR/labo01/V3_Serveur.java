
import java.net.*;
import java.util.Random;
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
public class V3_Serveur extends Config
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

	/** 
	 * Programme principal
	 */
	public static void main (String args[]) throws IOException, SocketException 
	{
		try {
			/* 
			 * Déclarations et initialisations
			 */
			int nbClientConnecte = 0;
			// Nombre de travailleurs
			int n = N;
			// defini la taille du tampon de communication pour synchronisation
			int TAILLE_TAMPON_SYNCHRO = 4*TAILLE_CHAR; // message recu : "HELO"

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

			/*
			 * Attend que les n travailleurs se présentent
			 */
			// ouverture d'un port en mode UDP
			DatagramSocket socket = new DatagramSocket(PORT_UDP);
			DatagramPacket paquet; 
			System.out.println("*** Serveur demarre ***");
			System.out.println("Attends que les travailleurs se presentent");

			// attend que tous les clients soient connectes et stock leur information
			Clients[] clients = new Clients[n];
			// synchronisation avec tous les clients
			while (nbClientConnecte < n) 
			{
				paquet = new DatagramPacket(tampon, tampon.length); // paquet de reception reception
				socket.receive(paquet); // attend la requete du client 
				// stock les informations du client dans un objet prevu a cet effet			
				clients[nbClientConnecte] = new Clients(nbClientConnecte, paquet.getAddress(), paquet.getPort());
				nbClientConnecte++;
				System.out.println("Client n. " + nbClientConnecte + " synchronise "); // afiche qu'un client est connecte
				// renvoie la taille des matrices au client qui vient de se connecte
				IntToBytes.intToBytes(n, tampon, 0);
				paquet = new DatagramPacket(tampon, tampon.length, paquet.getAddress(), paquet.getPort()); // paquet d'envoi
				socket.send(paquet);
			}

			// associe un port de communication au groupe
			InetAddress groupe = InetAddress.getByName(GROUPE);
			MulticastSocket socketMulti = new MulticastSocket(PORT_UDP);

			/* 
			 * Diffusion de la matrice B a tout les clients
			 */
			// met la matrice B dans un tampon pour la diffusion par la suite
			tampon = new byte[n*n*4]; // le tampon fait une taille de n^2
			int offset = 0; // le pointeur d'insertion dans le tableau de byte 
			for (short i=0; i<n; i++) {
				for (short j=0; j<n; j++) {
					IntToBytes.intToBytes(matB[i][j], tampon, (offset++)*4);
				}
			}

			// diffuse la matrice B a tout le groupe
			paquet = new DatagramPacket(tampon, tampon.length, groupe, PORT);
			socketMulti.send(paquet);
			System.out.println("Matrice B difusee");

			/*
			 * Envoie a chaque travailleur/client l'indice de la ligne a 
			 * calculer et la ligne A 
			 */
			// defini le tampon a la taille de la ligne de A
			tampon = new byte[(n+1)*4]; 

			// envoie a chaque client la ligne de A qu'il va utiliser pour ses calculs
			for (short i=0; i<n; i++) {
				offset = 0; // reinitialise le pointeur d'insertion
				// insere la ligne a calculer dans le tampon 
				IntToBytes.intToBytes(i, tampon, offset++);
				// insere la ligne de A dans le tampon
				for (short j=0; j<n; j++)
					IntToBytes.intToBytes(matA[i][j], tampon, (offset++)*4);
				paquet = new DatagramPacket(tampon, tampon.length, clients[i].getAddress(), clients[i].getPort()); // preparation du paquet d'envoi
				socket.send(paquet); // envoie le paquet au client
			}

			/*
			 * Attends les n lignes de C
			 */
			tampon = new byte[(n*n+1)*4]; // redefinit la taille du tampon
			for (short k=0; k<n; k++) {
				offset = 0; // reset la position ou on se trouve dans le tampon
				paquet = new DatagramPacket(tampon, tampon.length);	
				socket.receive(paquet); // attend la requete du client
				int ligneRecue = IntToBytes.bytesToInt(tampon, (offset++)*TAILLE_INT);
				System.out.println("Ligne recue : " + ligneRecue);
				for (short i=0; i<n; i++) {
					matC[ligneRecue][i] = IntToBytes.bytesToInt(tampon, (offset++)*TAILLE_INT);
				}
			}

			/*
			 * Affiche les resultats
			 */
			System.out.println("Affichages des matrices");
			// affiche les tableaux ainsi que le resultats calcules
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
			// calcul les valeurs localement, pour comparer avec les informations recus des clients
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
			socketMulti.close();
			socket.close();

			System.out.println("*** fin serveur ***");

		} catch (IOException e) {
			System.err.println(e);

		}
	}
}

