import java.io.*;
import java.net.*;
import java.util.Random;

public class V4_Serveur extends Config
{
	// Port UDP
	static final int portUDP = 4447;
	// Port multicast source
	static final int startPort = 4446;
	// Port multicast destination
	static final int receivePort = 4445;
	// Taille d'un integer en bytes
	static final int tailleInt = 4;

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

	public static void main(String[] args) throws IOException
	{
		// Le nombre de travailleurs
		int n = 0;
		// Les matrices a traiter
		int[][]  matA, matB, matC;

		try {
			// le premier parametre passe au programme correspond au nombre de travailleurs
			n = Integer.decode( args[0] );
		}
		catch(NumberFormatException e)
		{System.out.println("Valeur numerique attendue, 3 travailleurs par defaut");
		n = 3;}
		// verification de la taille min et max et changement des valeurs si necessaire
		if (n < 3) {
			System.out.println("Parametre N trop petit, 3 defini par default.");
			n = 3;
		} else if (n > 5) {
			System.out.println("Parametre N trop grand, 5 defini par default.");
			n = 5;
		}

		// Initialisation des dimensions des matrices
		matA = new int[n][n];
		matB = new int[n][n];
		matC = new int[n][n];

		// Insertion de valeurs aleatoire dans le tableau
		Random hasard = new Random();
		for (short i=0; i<n; i++) {
			for (short j=0; j<n; j++) {
				matA[i][j] = hasard.nextInt(10);
				matB[i][j] = hasard.nextInt(10);
			}
		}

		// Parametres du multicast
		InetAddress groupe = InetAddress.getByName("228.5.6.7");
		MulticastSocket socket = new MulticastSocket(startPort);
		// Variables pour la connexion d'un client
		byte[] tampon = new byte[1];
		DatagramPacket paquet = new DatagramPacket(tampon,tampon.length,groupe,receivePort);
		// Parametres pour la communication UDP
		DatagramSocket socketS = new DatagramSocket(portUDP);
		DatagramPacket paquetUDP = new DatagramPacket(tampon, tampon.length);
		byte[] tamponUDP = new byte[tailleInt*2];

		// Tableau des clients a connecter
		Clients[] clients = new Clients[n];
		// Communications initiales client-serveur (UDP)
		for(short i=0; i<n; i++) 
		{
			// Synchronisation avec les clients
			socketS.receive(paquetUDP);
			// Stocke les informations du client dans un objet prevu a cet effet			
			clients[i] = new Clients(i, paquetUDP.getAddress(), paquetUDP.getPort());
			// Envoie le nombre de travailleurs et le numero de travailleur
			IntToBytes.intToBytes(n, tamponUDP, 0);
			IntToBytes.intToBytes(i, tamponUDP, tailleInt);
			paquetUDP = new DatagramPacket(tamponUDP, tamponUDP.length, clients[i].getAddress(), clients[i].getPort());
			socketS.send(paquetUDP);
			System.out.println("Client " + (i+1) + " connecte");
		}

		// Creation du message a diffuser aux clients
		tampon = new byte[(n*n + n*n)*tailleInt];
		int offset = 0;

		// Insertion de la matrice A
		for(short i=0; i<n; i++)
			for(short j=0; j<n; j++)
			{
				IntToBytes.intToBytes(matA[i][j], tampon, offset*tailleInt);
				offset++;
			}

		// Insertion de la matrice B
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
			{
				IntToBytes.intToBytes(matB[i][j], tampon, offset*tailleInt);
				offset++;
			}

		// Envoi des deux matrices aux clients en multicast
		paquet = new DatagramPacket(tampon,tampon.length,groupe,receivePort);
		socket.send(paquet);

		// Reception des lignes calculees des clients
		for(short i=0; i<n; i++) 
		{
			tamponUDP = new byte[n*tailleInt + tailleInt];
			paquetUDP = new DatagramPacket(tamponUDP, tamponUDP.length);
			socketS.receive(paquetUDP);
			byte[] ligneRecue = paquetUDP.getData();
			// Le premier int represente le numero du client
			int noClient = IntToBytes.bytesToInt(ligneRecue, 0);
			// Mise a jour de la matrice C
			for(int j=0; j<n; j++)
				matC[noClient][j] = IntToBytes.bytesToInt(ligneRecue, j*tailleInt + tailleInt);
		}

		System.out.println("Matrice A: ");
		afficheMatrice(matC);

		System.out.println("\nMatrice B: ");
		afficheMatrice(matC);

		System.out.println("\nMatrice recue: ");
		afficheMatrice(matC);

		// Reinitialisation de la matrice C
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
				matC[i][j] = 0;

		// Calcul les valeurs localement, pour comparer avec les informations recues des clients
		for (short i=0; i<n; i++) { 
			for (short j=0; j<n; j++) { 
				for (short k=0; k<n; k++) {
					matC[i][j] += matA[i][k] * matB[k][j];
				}    		   
			}
		}

		// Affiche la matrice C calculee en local pour comparaison
		System.out.println("\nMatrice C = A x B (calcul local)");
		afficheMatrice(matC);

		socket.close();
		socketS.close();
		System.out.println("\n*** Serveur termine ***");
	}
}
