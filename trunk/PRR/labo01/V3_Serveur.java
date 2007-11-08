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
/* Ports utilisé : 6000 étant la valeur d'exemple passée en parametre
 * 6000 UDP basic
 * 6001 UDP Multicast (groupe) 
 * 6002 UDP Multicast (transmission)
 */
public class V3_Serveur 
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
			System.out.println("Erreur, manque parametres\nSyntaxe: java serveur <tailleMatrice> <port>");
		} else if (args.length > 2) {
			System.out.println("Trop de parametres\nSyntaxe: java serveur <tailleMatrice> <port>");
		} else {
			try {
				// declaration des variables
				int nbClientConnecte = 0;
				int tailleMatrice = Integer.parseInt(args[0]);
				int port = Integer.parseInt(args[1]);
				// defini la taille du tampon de communication (pour synchronisation et utilisation)
				//int TAILLE_TAMPON = (2+tailleMatrice*tailleMatrice+tailleMatrice)*4;
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
				DatagramPacket paquet; // = new DatagramPacket(tampon, tampon.length);
				System.out.println("*** Serveur demarre ***");

				// attends que tous les clients soient connectes et stock leur information
				Clients[] clients = new Clients[tailleMatrice];
				// synchronisation avec tous les clients
				while (nbClientConnecte < tailleMatrice) 
				{
					paquet = new DatagramPacket(tampon, tampon.length); // paquet de reception reception
					socket.receive(paquet); // attend la requete du client 
					// stock les informations du client dans un objet prevu a cet effet			
					clients[nbClientConnecte] = new Clients(nbClientConnecte, paquet.getAddress(), paquet.getPort());
					nbClientConnecte++;
					System.out.println("Client " + nbClientConnecte + " connecte"); // afiche qu'un client est connecte
					// renvoie la taille des matrices au client qui vient de se connecte
					IntToBytes.intToBytes(tailleMatrice, tampon, 0);
					paquet = new DatagramPacket(tampon, tampon.length, paquet.getAddress(), paquet.getPort()); // paquet d'envoi
					socket.send(paquet);
				}
				
				// associe un port de communication au groupe
				InetAddress groupe = InetAddress.getByName("230.230.230.230");
				MulticastSocket socketMulti = new MulticastSocket(port+1);
				
				// met la matrice B dans un tampon pour la diffusion par la suite
				tampon = new byte[tailleMatrice*tailleMatrice*4]; // le tampon fait une taille de tailleMatrice^2
				int offset = 0; // le pointeur d'insertion dans le tableau de byte 
				for (short i=0; i<tailleMatrice; i++) {
					for (short j=0; j<tailleMatrice; j++) {
						IntToBytes.intToBytes(tabB[i][j], tampon, offset*4);
						offset++;
					}
				}
				
				// diffuse la matrice B a tout le groupe
				paquet = new DatagramPacket(tampon, tampon.length, groupe, port+2);
				socketMulti.send(paquet);
				System.out.println("Matrice B difusee");
				
				// envoie a chaque client l'indice de la ligne a calculer et la ligne A
				tampon = new byte[(tailleMatrice+1)*4];
				for (short i=0; i<tailleMatrice; i++) {
					offset = 0; // reinitialise le pointeur d'insertion
					// insere la ligne a calculer dans le tampon 
					IntToBytes.intToBytes(i, tampon, offset++);
					// insere la ligne de A dans le tampon
					for (short j=0; j<tailleMatrice; j++)
						IntToBytes.intToBytes(tabA[i][j], tampon, (offset++)*4);
					paquet = new DatagramPacket(tampon, tampon.length, clients[i].getAddress(), clients[i].getPort()); // preparation du paquet d'envoi
					socket.send(paquet); // envoie le paquet au client
				}
				
				// TODO : envoie a chaque client individuellement la ligne qu'il va devoir traiter
			
				// recupere les valeurs calculees par les clients
				tampon = new byte[(tailleMatrice*tailleMatrice+1)*4];
				for (short k=0; k<tailleMatrice; k++) {
					offset = 0; // reset la position ou on se trouve dans le tampon
					paquet = new DatagramPacket(tampon, tampon.length);	
					socket.receive(paquet); // attend la requete du client
					int ligneRecue = IntToBytes.bytesToInt(tampon, (offset++)*4);
					for (short i=0; i<tailleMatrice; i++) {
						tabC[ligneRecue][i] = IntToBytes.bytesToInt(tampon, (offset++)*4);
					}
				}
				
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
				
				// calcul les valeurs localement, pour comparer avec les informations recus des clients
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
				socketMulti.close();
				socket.close();
				
				System.out.println("*** fin serveur ***");

			} catch (IOException e) {
				System.err.println(e);

			}
		}
	}
}

