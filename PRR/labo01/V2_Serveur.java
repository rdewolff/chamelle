import java.net.*;
import java.util.Random;
import java.io.*;

// je suis une petite cochomme :P héhéhé
public class V2_Serveur 
{
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
		// TODO check valeur de N entre 3 et 5
		if (args.length < 2) {
			System.out.println("Erreur, manque parametres : java serveur <tailleMatrice> <port>");
		} else {
			try {
				// déclaration des variables
				Integer TAILLE_TAMPON = 10; // Integer.MAX_VALUE = 2147483647 => 10 chars
				Integer nbClientConnecte = -1; // de 0 a N-1
				Integer tailleMatrice = Integer.parseInt(args[0]);
				Integer port = Integer.parseInt(args[1]);
				
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

				// ouverture d'un port en mode UDP
				DatagramSocket socket = new DatagramSocket(port);
				DatagramPacket paquet = new DatagramPacket(tampon, tampon.length);
				System.out.println("*** Serveur demarre ***");

				// TODO : multi client WHILE ... n .. 
				//while (nbClientConnecte < tailleMatrice-1) {
					
				//}
				
				// attends tous les clients
				socket.receive(paquet); // attend la requete du client
				System.out.println("Recu : " + new String(paquet.getData()));
				// leur envoie les donnees necessaires (N + ligne, numero de ligne et matrice B)
				nbClientConnecte++; // TODO: static dans classe client
				System.out.println("client " + nbClientConnecte + " connecte");
				// stock les information du client
				
				Clients clients = new Clients(nbClientConnecte, paquet.getAddress(), paquet.getPort());
				// renvoie le numero au client, qui correspond a la ligne qu'il doit traiter ( 0 a N )
				tampon = (nbClientConnecte.toString()).getBytes(); // met l'information a transmettre en octets
				paquet = new DatagramPacket(tampon, tampon.length, clients.getAddress(), clients.getPort());
				socket.send(paquet); // envoie de l'ID qui correspond a la ligne a calculer

				// renvoie la tailleMatrice
				tampon = (tailleMatrice.toString()).getBytes();
				paquet = new DatagramPacket(tampon, tampon.length, clients.getAddress(), clients.getPort());
				socket.send(paquet);

				// envoie la ligne de la matrice A
				for (short i=0; i<tailleMatrice; i++) {
					tampon = (tabA[nbClientConnecte][i].toString()).getBytes();
					paquet = new DatagramPacket(tampon, tampon.length, clients.getAddress(), clients.getPort());
					socket.send(paquet);
				}

				// envoie la matrice B
				for (short i=0; i<tailleMatrice; i++) {
					for (short j=0; j<tailleMatrice; j++) {
						tampon = (tabB[i][j].toString()).getBytes();
						paquet = new DatagramPacket(tampon, tampon.length, clients.getAddress(), clients.getPort());
						socket.send(paquet);
					}
				}

				// TODO boucle sur tout les client
				// recupere toute les valeurs
				tampon = new byte[TAILLE_TAMPON];
				paquet = new DatagramPacket(tampon, tampon.length);
				socket.receive(paquet); // attend la requete du client
				Integer ligneRecue = Integer.parseInt(new String(paquet.getData()).trim());
				System.out.println("Recu ligne " + ligneRecue);

				for (short i=0; i<tailleMatrice; i++) {
					paquet = new DatagramPacket(tampon, tampon.length);
					socket.receive(paquet);
					tabC[ligneRecue][i] = Integer.parseInt(new String(paquet.getData()).trim());
				}

				// affiche les tableaux ainsi que le resultat calcule
				System.out.println("Matrice A");
				afficheMatrice(tabA);

				System.out.println("Matrice B");
				afficheMatrice(tabB);

				System.out.println("Matrice C = A x B");
				afficheMatrice(tabC);

				// calcul les valeurs localement, pour comparer avec les informatiosn recus des clients
				for (short i=0; i<tailleMatrice; i++) { // i = ligne
					for (short j=0; j<tailleMatrice; j++) { // j = colonne
						// multiplie avec la colonne de la matrice B
						tabC[i][j] = 0;
						for (short k=0; k<tailleMatrice; k++) {
							tabC[i][j] += tabA[i][k] * tabB[k][j];
						}    		   
					}
				}

				System.out.println("Matrice C = A x B (local calcul)");
				afficheMatrice(tabC);

				/* Complementaire : util a la fin ?
				socket.setSoTimeout(1000); // TODO gere les timeout de connection 
				 */

				socket.close();
				System.out.println("*** fin serveur ***");

			} catch (IOException e) {
				System.err.println(e);

			}
		}
	}
}

