import java.io.*;
import java.net.*;
import java.util.Random;

public class V4_Serveur
{
	static int startPort = 4446;
	static int receivePort = 4445;
	
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
		matA = new int[n][n];
		matB = new int[n][n];
		matC = new int[n][n];
		
		// insertion de valeurs aleatoire dans le tableau
		Random hasard = new Random();
		// parcours les deux tableaux et insere les valeurs aleatoires
		for (short i=0; i<n; i++) {
			for (short j=0; j<n; j++) {
				matA[i][j] = hasard.nextInt(10);
				matB[i][j] = hasard.nextInt(10);
			}
		}
		
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
		
		String[] message = {"Allo","ed","ad"};
		byte[] tampon = new byte[256];
		/** Le tableau des sockets de multicast */
		MulticastSocket socket;
		InetAddress groupes;
		
		// Associer un port de communication a un groupe
		groupes = InetAddress.getByName("228.5.6.7");
		socket = new MulticastSocket(startPort);

		
		short nbClientConnecte=0;
		Clients[] clients = new Clients[n];
		while (nbClientConnecte < n) 
		{
			// tampon utilise pour la communication lors de la synchro
			byte[] tamponS = new byte[8];
			DatagramPacket paquet = new DatagramPacket(tamponS, tamponS.length);
			// synchronisation avec les clients
			socket.receive(paquet); // attend la requete du client 
			// stock les informations du client dans un objet prevu a cet effet			
			clients[nbClientConnecte] = new Clients(nbClientConnecte, paquet.getAddress(), paquet.getPort());
			nbClientConnecte++;
			System.out.println("Client " + nbClientConnecte + " connecte"); // afiche qu'un client est connecte
		}
		
		/** Tampon de lecture du clavier */
		BufferedReader inFromUser = 
			new BufferedReader(new InputStreamReader(System.in));
		try
		{
			Integer.valueOf(inFromUser.readLine());
		}
		// En cas d'erreur de format ou de bornes, on signale l'erreur
		catch(NumberFormatException e)
		{System.out.println("Veuillez entrer un nombr");}
		catch(IOException e)
		{System.out.println("IO exception");}
		
		//Envoyer le message aux membres du groupe
		for(short i=0; i<n; i++)
		{
			// Creation du message a diffuser aux clients
			tampon = message[i].getBytes();
			DatagramPacket paquet = new DatagramPacket(tampon,tampon.length,groupes,receivePort);
			socket.send(paquet);
		}
		socket.close();
		System.out.println("yop");
	}
}