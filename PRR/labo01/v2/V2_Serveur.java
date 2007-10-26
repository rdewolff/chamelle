package v2;

import java.net.*;
import java.util.Random;
import java.io.*;

public class V2_Serveur 
{
	static int PORT = 6000;
	static int N = 3;

	/* 
	 * Affichage de la matrice carrée passée en parametre (2 dimensions)
	 */
	private static void afficheMatrice(Integer [][] tableau) {
		for (short i=0; i<tableau.length; i++) {
			for (short j=0; j<tableau.length; j++) {
				System.out.print(tableau[i][j] + " ");
			}
			System.out.print('\n');
		}
	}

	public static void main (String args[]) throws IOException, SocketException 
	{
		// déclaration des variables
		Integer TAILLE_TAMPON = 10; // Integer.MAX_VALUE = 2147483647 => 10 chars
		Integer nbClientConnecte = -1; // de 0 a N
		Integer tailleMatrice = N;

		// creation des 2 tableaux sur lequelles on va faire des calculs
		Integer[][]  tabA, tabB, tabC;
		tabA = new Integer[tailleMatrice][tailleMatrice];
		tabB = new Integer[tailleMatrice][tailleMatrice];
		tabC = new Integer[tailleMatrice][tailleMatrice];

		// insertion de valeurs aleatoires dans le tableau
		Random hasard = new Random();
		// parcours les deux talbeaux et insere les valeurs aleatoires
		for (short i=0; i<tailleMatrice; i++) {
			for (short j=0; j<tailleMatrice; j++) {
				tabA[i][j] = hasard.nextInt(10);
				tabB[i][j] = hasard.nextInt(10);
			}
		}

		// tampon utilisé pour la communication
		byte[] tampon = new byte[TAILLE_TAMPON];

		DatagramSocket socket = new DatagramSocket(PORT);
		DatagramPacket paquet = new DatagramPacket(tampon, tampon.length);
		System.out.println("*** Serveur démarré ***");

		// TODO : multi client WHILE ... n .. 
		// attends tous les clients
		socket.receive(paquet); // attend la requete du client
		System.out.println("Recu : " + new String(paquet.getData()));
		// leur envoie les données necessaires (N + ligne, numéro de ligne et matrice B)
		
		System.out.println("client " + nbClientConnecte + " connecté");
		nbClientConnecte++;
		// renvoie le numero au client, qui correspond à la ligne qu'il doit traiter ( 0 à N )
		InetAddress addresseClient = paquet.getAddress(); // l'addresse qu'utilise le client
		int portClient = paquet.getPort(); // le port qu'utilise le client

		tampon = (nbClientConnecte.toString()).getBytes(); // met l'information a transmettre en octets
		paquet = new DatagramPacket(tampon, tampon.length, addresseClient, portClient); // reinit le paquet
		// on lui envoie son ID qui correspond a la ligne qu'il va devoir calculer
		socket.send(paquet); // envoie de l'ID qui correspond à la ligne à calculer

		// renvoie la tailleMatrice
		tampon = (tailleMatrice.toString()).getBytes();
		paquet = new DatagramPacket(tampon, tampon.length, addresseClient, portClient); 
		socket.send(paquet);

		// envoie la ligne de la matrice A
		for (short i=0; i<tailleMatrice; i++) {
			tampon = (tabA[nbClientConnecte][i].toString()).getBytes();
			paquet = new DatagramPacket(tampon, tampon.length, addresseClient, portClient);
			socket.send(paquet);
		}
		
		// envoie la matrice B
		for (short i=0; i<tailleMatrice; i++) {
			for (short j=0; j<tailleMatrice; j++) {
				tampon = (tabB[i][j].toString()).getBytes();
				paquet = new DatagramPacket(tampon, tampon.length, addresseClient, portClient);
				socket.send(paquet);
			}
		}
		
		// récupère toute les valeurs
		
		// affiche les tableaux ainsi que le resultat calcule
		System.out.println("Matrice A");
		afficheMatrice(tabA);

		System.out.println("Matrice B");
		afficheMatrice(tabB);

		System.out.println("Matrice C = A x B");
		afficheMatrice(tabC);
		
		
		/* Complémentaire : util à la fin ?
		socket.setSoTimeout(1000); // TODO gere les timeout de connection 
		 */

		socket.close();
		System.out.println("fin serveur");
	}
}

