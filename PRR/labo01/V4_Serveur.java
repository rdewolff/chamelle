import java.io.*;
import java.net.*;
import java.util.Random;

public class V4_Serveur
{
	static int startPort = 4446;
	static int receivePort = 4445;
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
		int n = 0;
		
		// creation des matrices sur lequelles on va faire des calculs
		int[][]  matA, matB, matC;
		
		try
		{
			// le premier parametre passe au programme correspond a la taille des matrices que 
			// l'on va calculer
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
		
		matA = new int[n][n];
		matB = new int[n][n];
		matC = new int[n][n];
		
//		 insertion de valeurs aleatoire dans le tableau
		Random hasard = new Random();
		// parcours les deux tableaux et insere les valeurs aleatoires
		for (short i=0; i<n; i++) {
			for (short j=0; j<n; j++) {
				matA[i][j] = hasard.nextInt(10);
				matB[i][j] = hasard.nextInt(10);
			}
		}
		
		String message = "Allo";
		byte[] tampon = new byte[1];
		
		/** Associer un port de communication a un groupe */
		InetAddress groupe = InetAddress.getByName("228.5.6.7");
		/** La socket de communication de groupe */
		MulticastSocket socket = new MulticastSocket(startPort);
		
		tampon = message.getBytes();
		DatagramPacket paquet = new DatagramPacket(tampon,tampon.length,groupe,receivePort);
		
		DatagramSocket socketS = new DatagramSocket(startPort+1);
		byte[] tamponDim = new byte[4];
		IntToBytes.intToBytes(n, tamponDim, 0);
		
		/** Tableau des clients a connecter */
		Clients[] clients = new Clients[n];
		for(short i=0; i<n; i++) 
		{
			// tampon utilise pour la communication lors de la synchro
			byte[] tamponS = new byte[1];
			DatagramPacket paquetS = new DatagramPacket(tamponS, tamponS.length);
			// synchronisation avec les clients
			socketS.receive(paquetS); // attend la requete du client 
			// stocke les informations du client dans un objet prevu a cet effet			
			clients[i] = new Clients(i, paquetS.getAddress(), paquetS.getPort());
			tamponS = new byte[tailleInt*2];
			IntToBytes.intToBytes(n, tamponS, 0);
			IntToBytes.intToBytes(i, tamponS, tailleInt);
			paquetS = new DatagramPacket(tamponS, tamponS.length, clients[i].getAddress(), clients[i].getPort());
			socketS.send(paquetS);
			System.out.println("Client " + (i+1) + " connecte"); // afiche qu'un client est connecte
		}
		
		// Envoyer les deux matrices aux clients en multicast
		afficheMatrice(matA);
		afficheMatrice(matB);
		// Creation du message a diffuser aux clients
		tampon = new byte[(n*n + n*n)*tailleInt];
		
		int offset = 0;
		
		// La matrice A
		for(short i=0; i<n; i++)
			for(short j=0; j<n; j++)
			{
				IntToBytes.intToBytes(matA[i][j], tampon, offset*tailleInt);
				offset++;
			}
		
		// La matrice B
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
			{
				IntToBytes.intToBytes(matB[i][j], tampon, offset*tailleInt);
				offset++;
			}
		
		paquet = new DatagramPacket(tampon,tampon.length,groupe,receivePort);
		socket.send(paquet);
		
		for(short i=0; i<n; i++) 
		{
			// tampon utilise pour la communication lors de la synchro
			byte[] tamponS = new byte[n*tailleInt + tailleInt];
			DatagramPacket paquetS = new DatagramPacket(tamponS, tamponS.length);
			// synchronisation avec les clients
			socketS.receive(paquetS); // attend la requete du client
			byte[] ligneRecue = paquetS.getData();
			int noClient = IntToBytes.bytesToInt(ligneRecue, 0);
			System.out.println(noClient);
			for(int j=0; j<n; j++)
				matC[noClient][j] = IntToBytes.bytesToInt(ligneRecue, j*tailleInt + tailleInt);
		}
		
		System.out.println("Matrice recue: ");
		afficheMatrice(matC);
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
				matC[i][j] = 0;
		
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

		socket.close();
		System.out.println("yop");
	}
}
