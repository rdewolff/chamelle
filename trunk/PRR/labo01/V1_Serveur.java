

import java.io.*; 
import java.net.*;
import java.util.*;

/**
 * Communication TCP avec un serveur et N clients.
 * 
 * Ce programme va multiplier deux matrices contenant N x N nombres entiers
 * 
 * La classe abstraite Config permet de recuperer les parametres de ports et
 * le nombre de travailleurs, mais le client recuperera le nombre de 
 * travailleurs avec un message du serveur, pour avoir un peu de souplesse.
 * 
 * Le coordinateur (serveur) va envoyer à chaque travailleur (client) une ligne 
 * de la matrice A et la matrice B.
 * 
 * Chaque client va calculer la ligne de C et la remettre au serveur.
 * 
 * Le serveur utilise la parametre PORT de la classe Config, et il faut lancer 
 * les clients avec PORT, PORT + 1 ... PORT + n-1 en parametre.
 * 
 * Note: Les clients peuvent etre lances avant le serveur.
 * 
 * @author Romain de Wolff
 * @author Simon Hintermann
 * @date 26 octobre 2007
 */
class V1_Serveur extends Config
{

	/* 
	 * Programme principal
	 */
	public static void main(String argv[]) throws Exception {

		// affiche info sur serveur
		System.out.println("Lancement du serveur sur " + 
						   InetAddress.getLocalHost());

		// creation des matrices sur lequelles on va faire des calculs
		int[][]  matA, matB, matC;
		matA = new int[N][N];
		matB = new int[N][N];
		matC = new int[N][N];

		// insertion de valeurs aleatoire dans le tableau
		Random hasard = new Random();
		// parcours les deux tableaux et insere les valeurs aleatoires
		for (short i=0; i<N; i++) {
			for (short j=0; j<N; j++) {
				matA[i][j] = hasard.nextInt(10);
				matB[i][j] = hasard.nextInt(10);
			}
		}
		
		// on lance les taches pour les 5 futures connexions.
		System.out.println("Attente de la connexion des " + N + " clients");

		ServerSocket[] welcomeSocket = new ServerSocket[N];
		Socket[] connectionSocket = new Socket[N];

		/*
		 * Attend que les N travailleurs se presentent
		 */
		
		for (int i=0; i<N; i++) {
			welcomeSocket[i] = new ServerSocket(PORT+i);
			connectionSocket[i] = welcomeSocket[i].accept();
			System.out.println("Client " + (i+1) + " connecte.");
		}

		System.out.println("Debut de la transmission...");
		// La taille du tableau de bytes a envoyer a chaque client
		int dimTab = TAILLE_INT + (N*TAILLE_INT) + (N*N*TAILLE_INT);
		// Tableau de bytes d'envoi des informations
		byte[] out = new byte[dimTab];
		
		/*
		 *  Emet une ligne et la matrice B a chaque travailleur
		 */
		
		for (int i=0; i<N; i++) 
		{
			// Le flux de donnees sortantes vers les clients */
			DataOutputStream fluxSortie = 
				new DataOutputStream(connectionSocket[i].getOutputStream());
			// Nombre de travailleurs (dimension matrice)
			Outils.intToBytes(N, out, 0);
			// Ligne de la matrice A que le client i utilisera
			for(int j=0; j<N; j++)
				Outils.intToBytes(matA[i][j], out, 
									  (j*TAILLE_INT + TAILLE_INT));
			// La matrice B
			for(int j=0; j<N; j++)
				for(int k=0; k<N; k++)
					Outils.intToBytes(matB[j][k], out, (j*TAILLE_INT + 
								k*N*TAILLE_INT + TAILLE_INT + N*TAILLE_INT));
			// Envoi du tableau de bytes aux clients
			fluxSortie.write(out, 0, dimTab);
			System.out.println("Client " + (i+1) + " envoye");
		}

		// Variable de lecture
		byte[] in = new byte[TAILLE_INT];
		
		/*
		 * Attend les n lignes des clients
		 */
		
		for(int i=0; i<N; i++)
		{
			// Flux des donnees recues des clients
			DataInputStream fluxEntree = 
				new DataInputStream(connectionSocket[i].getInputStream());
			// Recomposition de la matrice C avec les lignes 
			// calculees par les clients
			for(int j=0; j<N; j++)
			{
				for(int k=0; k<TAILLE_INT; k++)
					in[k] = fluxEntree.readByte();
				matC[i][j] = Outils.bytesToInt(in, 0);
			}
		}

		// affiche les tableaux ainsi que le resultat calcule
		System.out.println("Matrice A");
		Outils.afficheMatrice(matA);

		System.out.println("Matrice B");
		Outils.afficheMatrice(matB);
		
		/*
		 * Affiche la matrice C
		 */
		System.out.println("Matrice C = A x B");
		Outils.afficheMatrice(matC);
		
		// pour la verification, on recalcul la matrice localement
		// reinit la matrice avant le calcul local
		for (short i=0; i<N; i++) { // i = ligne
			for (short j=0; j<N; j++) { // j = colonne
				matC[i][j] = 0;
			}
		}

		// calcul les valeurs localement, pour comparer avec 
		// les informatiosn recus des clients
		for (short i=0; i<N; i++) { // i = ligne
			for (short j=0; j<N; j++) { // j = colonne
				// multiplie avec la colonne de la matrice B
				for (short k=0; k<N; k++) {
					matC[i][j] += matA[i][k] * matB[k][j];
				}    		   
			}
		}
		
		// affiche la matrice C calculee en local pour comparaison
		System.out.println("Matrice C = A x B (local calcul)");
		Outils.afficheMatrice(matC);

		// fin de l'execution du serveur
		System.out.println("Fin de l'execution du serveur.");
	} 
}
