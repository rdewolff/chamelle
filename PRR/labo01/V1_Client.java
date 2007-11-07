

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

	public static void main(String argv[]) throws Exception { 

		while(true){
			try {
				/** Domaine dans lequel est le serveur */
				String host = argv[0];
				/** Le port sur lequel il faut se connecter au serveur */
				int port = (int)Integer.decode( argv[1] );
				/** Variable de lecture  */
				int recept;
				Socket clientSocket = new Socket(host, port); // connection en utilisant les parametre passe au lancement du client
				DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); 
				DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());
				recept = inFromServer.read();
				System.out.println("DU SERVEUR: " + recept);
				clientSocket.close();
				break;
			} catch (IOException e) {
				System.out.println(e); 
			}
		} 
	}
}
