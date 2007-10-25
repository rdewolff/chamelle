package v2;

import java.io.*; 
import java.net.*;
import java.util.*;
//import java.math.*;
//import java.lang.Runnable; // threads Runnable ;p

/**
 * PRR - Laboratoire 1 - Implemnation n 1
 * Romain de Wolff et Simon Hintermann
 * 
 * Le client se lance avec 2 arguments :
 *  - 1 : l'adresse du serveur
 *  - 2 : le port sur lequel se connecter
 * 
 * Premiere implementation 
 * @author rdewolff
 * @date  5.10.2007
 */
 
/**
 * 
 * On va departager les differentes taches du serveur 
 * selon la donnee du laboratoire. 
 * Ceci autant pour le serveur que pour le client.
 * 
 * -- UDP Datagrames - String to Bytes ^^
 * String dString = "Es-tu la?";
       byte[] buf = dString.getBytes();
       DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
       socket.send(packet);
 * 
 */

/* 
 * Serveur acceptant n connexions (3-5)
 * 
 * */
class V1_Serveur { 

	static int startPort = 8000;

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

	/* 
	 * Programme principal
	 */
	public static void main(String argv[]) throws Exception {

		// affiche info sur nom + ip serveur
		System.out.println("Lancement du serveur sur " + InetAddress.getLocalHost());

		// le premier parametre passe au programme correspond a la taille des matrices que 
		// l'on va calculer
		int n = Integer.decode( argv[0] ); 
		// verification de la taille min et max et changement des valeurs si necessaire
		if (n < 3) {
			System.out.println("Parametre N trop petit, 3 defini par default.");
			n = 3;
		} else if (n > 5) {
			System.out.println("Parametre N trop grand, 5 defini par default.");
			n = 5;
		}

		// creation des 2 tableaux sur lequelles on va faire des calculs
		int[][]  tabA, tabB, tabC;
		tabA = new int[n][n];
		tabB = new int[n][n];
		tabC = new int[n][n];

		// insertion de valeurs aleatoire dans le tableau
		Random hasard = new Random();
		// parcours les deux talbeaux et insere les valeurs aleatoires
		for (short i=0; i<n; i++) {
			for (short j=0; j<n; j++) {
				tabA[i][j] = hasard.nextInt(10);
				tabB[i][j] = hasard.nextInt(10);
			}
		}

		// TODO : remove this, c'est juste la methode de calcul
		// calcul les valeurs
		for (short i=0; i<n; i++) { // i = ligne
			for (short j=0; j<n; j++) { // j = colonne
				// multiplie avec la colonne de la matrice B
				for (short k=0; k<n; k++) {
					tabC[i][j] += tabA[i][k] * tabB[k][j];
				}    		   
			}
		}

		// on lance les taches pour les 5 futurs connexions.
		System.out.println("Attente de la connexion des " + n + " clients");
	
		ServerSocket[] welcomeSocket = new ServerSocket[n];
		Socket[] connectionSocket = new Socket[n];
		
		// creation tableaux
		for (int i=0; i<n; i++) {
			welcomeSocket[i] = new ServerSocket(startPort+i);
			connectionSocket[i] = welcomeSocket[i].accept();
			System.out.println("Client " + (i+1) + " connecte.");
		}
		
		System.out.println("Debut de la transmission...");

		/*
		// BufferedReader
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 
		DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream()); 

		phraseClient = inFromClient.readLine(); 
		phraseEnMaj = phraseClient.toUpperCase() + '\n'; 
		outToClient.writeBytes(phraseEnMaj); 

		// fermeture des sockets
		welcomeSocket.close();
		connectionSocket.close();
		inFromClient.close();
		outToClient.close();
*/
		// --------------------------------------------------------------------
		// affiche les tableaux ainsi que le resultat calcule
		System.out.println("Matrice A");
		afficheMatrice(tabA);

		System.out.println("Matrice B");
		afficheMatrice(tabB);

		System.out.println("Matrice C = A x B");
		afficheMatrice(tabC);

		// fin de l'execution du serveur
		System.out.println("Fin de l'execution du serveur.");
	} 
}
