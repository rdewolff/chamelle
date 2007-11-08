

import java.io.*; 
import java.net.*; 

/**
 * Client permettant de recevoir une ligne de matric et une matrice en TCP,
 * puis de renvoyer la ligne multipliee au serveur
 * 
 * @author Simon Hintermann & Romain de Wolff
 *
 */
class V1_Client { 
	
	private static void afficheMatrice(int [][] tableau) {
		for (short i=0; i<tableau.length; i++) {
			for (short j=0; j<tableau.length; j++) {
				System.out.print(tableau[i][j] + " ");
			}
			System.out.print('\n');
		}
	}
	
	public static void main(String argv[]) throws Exception { 
		
		final int tailleInt = 4;
		
		while(true){
			try {
				/** Domaine dans lequel est le serveur */
				String host = argv[0];
				/** Le port sur lequel il faut se connecter au serveur */
				int port = (int)Integer.decode( argv[1] );
				/** Dimension de la matrice  */
				int n;
				/** Le tableau de bytes de lecture d'un int */
				byte[] in = new byte[tailleInt];
				/** Connection au serveur en utilisant les parametre passe au lancement du client */
				Socket clientSocket = new Socket(host, port);
				/** Flux de donnees vers le serveur */
				DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); 
				/** Flux de donnees du serveur */
				DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());
				
				// Recuperation de la dimension de la matrice
				for(short i=0; i<tailleInt; i++)
					in[i] = inFromServer.readByte();
				n = IntToBytes.bytesToInt(in, 0);
				
				/** Matrice recuperee par le client */
				int[][] mat = new int[n][n];
				/** Ligne recuperee par le client */
				int[] ligne = new int[n];
				/** La ligne resultat de la multiplication */
				int[] ligneRetour = new int[n];
				/** La ligne a renvoyer sous forme de tableau de bytes */
				byte[] byteRetour = new byte[n*tailleInt];
				
				// Recuperation de la ligne
				for(short i=0; i<n; i++)
				{
					for(short j=0; j<tailleInt; j++)
						in[j] = inFromServer.readByte();
					ligne[i] = IntToBytes.bytesToInt(in, 0);
				}
				
				// Recuperation de la matrice
				for(short i=0; i<n; i++)
					for(short j=0; j<n; j++)
					{
						for(short k=0; k<tailleInt; k++)
							in[k] = inFromServer.readByte();
						mat[j][i] = IntToBytes.bytesToInt(in, 0);
					}
				
				// Calcul de la ligne a renvoyer
				for (short i=0; i<n; i++) 
					for(short j=0; j<n; j++)
						ligneRetour[i] += mat[j][i] * ligne[j];
				
				afficheMatrice(mat);
				
				System.out.println("\n" + ligne[0] + " " +ligne[1]+ " " +ligne[2]+ " " +ligne[3]);
				
				System.out.println("\n" + ligneRetour[0] + " " +ligneRetour[1]+ " " +ligneRetour[2]+ " " +ligneRetour[3]);
				
				// Creation du tableau de bytes a renvoyer au serveur
				for(short i=0; i<n; i++)
					IntToBytes.intToBytes(ligneRetour[i], byteRetour, i*tailleInt);
				
				outToServer.write(byteRetour, 0, tailleInt*n);
				
				clientSocket.close();
				break;
			} catch (IOException e) {
				System.out.println(e); 
			}
		} 
	}
}
