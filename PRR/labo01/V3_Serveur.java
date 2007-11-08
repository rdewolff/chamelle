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

public class V2_Serveur 
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
		if (args.length < 2) {
			System.out.println("Erreur, manque parametres : java serveur <tailleMatrice> <port>");
		} else if (args.length > 2) {
			System.out.println("Trop de parametres : java serveur <tailleMatrice> <port>");
		} else {
			try {
				// declaration des variables
				int nbClientConnecte = 0;
				int tailleMatrice = Integer.parseInt(args[0]);
				int port = Integer.parseInt(args[1]);
				// defini la taille du tampon de communication (pour synchronisation et utilisation)
				int TAILLE_TAMPON = (2+tailleMatrice*tailleMatrice+tailleMatrice)*4;
				int TAILLE_TAMPON_SYNCHRO = 8; // message recu : "HELO"
				
				// creation des tableaux sur lequelles on va faire les calculs
				int[][]  tabA, tabB, tabC;
				tabA = new int[tailleMatrice][tailleMatrice];
				tabB = new int[tailleMatrice][tailleMatrice];
				tabC = new int[tailleMatrice][tailleMatrice];

				// insertion de valeurs aleatoires dans le tableau
				Random hasard = new Random();
				// parcours les deux talbeaux et insere les valeurs aleatoires
				for (short i=0; i<tailleMatrice; i++) {
					for (short j=0; j<tailleMatrice; j++) {
						tabA[i][j] = hasard.nextInt(10);
						tabB[i][j] = hasard.nextInt(10);
					}
				}

				// tampon utilise pour la communication lors de la synchro
				byte[] tampon = new byte[TAILLE_TAMPON_SYNCHRO];

				// ouverture d'un port en mode UDP
				DatagramSocket socket = new DatagramSocket(port);
				DatagramPacket paquet = new DatagramPacket(tampon, tampon.length);
				System.out.println("*** Serveur demarre ***");

				// attends que tous les clients soient connecté
				Clients[] clients = new Clients[tailleMatrice];
				while (nbClientConnecte < tailleMatrice) 
				{
					// synchronisation avec les clients
					socket.receive(paquet); // attend la requete du client 
					// stock les informations du client dans un objet prevu a cet effet			
					clients[nbClientConnecte] = new Clients(nbClientConnecte, paquet.getAddress(), paquet.getPort());
					nbClientConnecte++;
					System.out.println("Client " + nbClientConnecte + " connecte"); // afiche qu'un client est connecte
				}

				System.out.println("Envoie des donnees aux clients");
				// envoie les donnees a chaque client
				tampon = new byte[TAILLE_TAMPON]; // redefini la taille du tampon 
				int offset = 0; // variable utilisee pour savoir ou on en est dans le tambon
				for (short k=0; k<tailleMatrice; k++) {
					offset = 0;
					// met le numero au client en premier dans le tampon d'envoie,ce qui correspond a la ligne qu'il doit traiter ( 0 a N )
					IntToBytes.intToBytes(k, tampon, offset);
					offset++;

					// insere la taille des matrices dans le tampon
					IntToBytes.intToBytes(tailleMatrice, tampon, offset*4);
					offset++;

					// insere la ligne de la matrice A dans le tampon
					for (short i=0; i<tailleMatrice; i++) {
						IntToBytes.intToBytes(tabA[k][i], tampon, offset*4);
						offset++;
					}

					// insere la matrice B dans le tampon
					for (short i=0; i<tailleMatrice; i++) {
						for (short j=0; j<tailleMatrice; j++) {
							IntToBytes.intToBytes(tabB[i][j], tampon, offset*4);
							offset++;
						}
					}

					// envoie le paquet "tampon" contenant la taille des matrices ainsi que la ligne de A et la matrice B
					paquet = new DatagramPacket(tampon, tampon.length, clients[k].getAddress(), clients[k].getPort());
					socket.send(paquet);
				}				
				
				// recupere les valeurs calculees par les clients
				for (short k=0; k<tailleMatrice; k++) {
					offset = 0; // reset la position ou on se trouve dans le tampon
					paquet = new DatagramPacket(tampon, tampon.length);
					socket.receive(paquet); // attend la requete du client
					int ligneRecue = IntToBytes.bytesToInt(tampon, offset*4);
					offset++;
					for (short i=0; i<tailleMatrice; i++) {
						tabC[ligneRecue][i] = IntToBytes.bytesToInt(tampon, offset*4);
						offset++;
					}
				}
				 
				System.out.println("Toutes les lignes ont ete recues");
				
				System.out.println("Affichages des matrices");
				
				// affiche les tableaux ainsi que le resultats calcules
				System.out.println("Matrice A");
				afficheMatrice(tabA);

				System.out.println("Matrice B");
				afficheMatrice(tabB);

				System.out.println("Matrice C = A x B (recue ligne par ligne)");
				afficheMatrice(tabC);

				// pour la verification, on recalcul la matrice localement
				// reinit la matrice avant le calcul local
				for (short i=0; i<tailleMatrice; i++) { // i = ligne
					for (short j=0; j<tailleMatrice; j++) { // j = colonne
						tabC[i][j] = 0;
					}
				}
				
				// calcul les valeurs localement, pour comparer avec les informatiosn recus des clients
				for (short i=0; i<tailleMatrice; i++) { // i = ligne
					for (short j=0; j<tailleMatrice; j++) { // j = colonne
						// multiplie avec la colonne de la matrice B
						for (short k=0; k<tailleMatrice; k++) {
							tabC[i][j] += tabA[i][k] * tabB[k][j];
						}    		   
					}
				}
				
				// affiche la matrice C calculee en local pour comparaison
				System.out.println("Matrice C = A x B (local calcul)");
				afficheMatrice(tabC);

				// ferme le socket de connexion
				socket.close();
				System.out.println("*** fin serveur ***");

			} catch (IOException e) {
				System.err.println(e);

			}
		}
	}
}

