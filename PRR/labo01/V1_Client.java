
import java.io.*; 
import java.net.*; 

/**
 * Client permettant de recevoir une ligne de matric et une matrice en TCP,
 * puis de renvoyer la ligne multipliee au serveur
 * 
 * @author Simon Hintermann & Romain de Wolff
 *
 */
class V1_Client extends Config
{

	public static void main(String argv[]) throws Exception { 

		/*
		 * Initialisations
		 */

		// Le port sur lequel se connecter au serveur 
		// (chaque client utilise un port different)
		int port = (int)Integer.decode( argv[0] );
		// Dimension de la matrice
		int n;
		// Le tableau de bytes de lecture d'un int
		byte[] in = new byte[TAILLE_INT];
		// Connection au serveur en utilisant les parametre passe au lancement du client
		Socket clientSocket;
		// Flux de donnees vers le serveur
		DataOutputStream outToServer;
		// Flux de donnees du serveur
		DataInputStream inFromServer; 

		while(true){
			try {

				/*
				 * Recuperation des donnees envoyee par le serveur
				 */
				clientSocket = new Socket(HOST, port);
				outToServer = new DataOutputStream(clientSocket.getOutputStream()); 
				inFromServer = new DataInputStream(clientSocket.getInputStream());
				// Recuperation de la dimension de la matrice
				for(short i=0; i<TAILLE_INT; i++)
					in[i] = inFromServer.readByte();
				n = IntToBytes.bytesToInt(in, 0);
				break;
			} catch (IOException e) {
				System.out.println("Tentative de connexion..."); 
			}
		}

		// Matrice recuperee par le client
		int[][] mat = new int[n][n];
		// Ligne recuperee par le client
		int[] ligne = new int[n];
		// La ligne resultat de la multiplication
		int[] ligneRetour = new int[n];
		// La ligne a renvoyer sous forme de tableau de bytes
		byte[] byteRetour = new byte[n*TAILLE_INT];

		// Recuperation de la ligne
		for(short i=0; i<n; i++)
		{
			for(short j=0; j<TAILLE_INT; j++)
				in[j] = inFromServer.readByte();
			ligne[i] = IntToBytes.bytesToInt(in, 0);
		}

		// Recuperation de la matrice
		for(short i=0; i<n; i++)
			for(short j=0; j<n; j++)
			{
				for(short k=0; k<TAILLE_INT; k++)
					in[k] = inFromServer.readByte();
				mat[j][i] = IntToBytes.bytesToInt(in, 0);
			}

		/*
		 * Calcul la ligne correspondante de C
		 */

		for (short i=0; i<n; i++) 
			for(short j=0; j<n; j++)
				ligneRetour[i] += mat[j][i] * ligne[j];

		// Creation du tableau de bytes a renvoyer au serveur
		for(short i=0; i<n; i++)
			IntToBytes.intToBytes(ligneRetour[i], byteRetour, i*TAILLE_INT);

		/*
		 * Emet la ligne calculee au coordinateur/serveur
		 */

		outToServer.write(byteRetour, 0, TAILLE_INT*n);

		// Fermeture de la connexion
		clientSocket.close();

		System.out.println("*** Client termine ***");

	} 
}
