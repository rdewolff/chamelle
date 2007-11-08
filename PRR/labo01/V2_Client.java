import java.net.*;
import java.io.*;

public class V2_Client {	
	
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

	/*
	 * Methode principale contenatn tout le client
	 */
	public static void main (String args[]) throws IOException {
		try {
			
			// adresse et port de connexion passe en parametre
			InetAddress address = InetAddress.getByName(args[0]); 
			int port = Integer.parseInt(args[1]); 
			
			int TAILLE_TAMPON = 56; // TODO : change en fonction des données max
			String query = "HELO"; // pour se sychroniser avec le serveur
			byte[] tampon = new byte[query.length()*2]; // defini la taille miniumm necessaire. Un char = 2 bytes
			tampon = query.getBytes();
			
			System.out.println("*** Client ***");
			BufferedReader stdin= new BufferedReader(new InputStreamReader(System.in)); 
			System.out.print("Appuyer sur une <enter> pour vous connecter au serveur"); 
			stdin.readLine(); // attend touche <enter> 
			
			// synchronisation avec le serveur : envoie au serveur un paquet de requete, pour qu'il sache qu'il est connecte
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket paquet = new DatagramPacket(tampon, tampon.length, address, port);
			socket.send(paquet); // envoi du paquet a l'aide du socket
			
			// recoit les infos du serveur
			tampon = new byte[TAILLE_TAMPON]; // reinit le tampon
			paquet = new DatagramPacket(tampon, tampon.length);
			socket.receive(paquet); 
			
			// la connection est des lors etablie, la communication fonctionne
			System.out.println("Connection avec le serveur etablie");
			
			// décomponse les element recu par le serveur
			int offset = 0; 
			// recoit les infos qui sont dans un seul character (deux nombre < 10)
			int ligneACalculer = IntToBytes.bytesToInt(tampon, offset);
			offset++;
			int tailleMatrice = IntToBytes.bytesToInt(tampon, offset*4);
			offset++;
			
			System.out.println("ligneACalculer: " + ligneACalculer + "\ntailleMatrice: " + tailleMatrice);
			
			int[] ligneA = new int[tailleMatrice];
			int[] ligneC = new int[tailleMatrice];			
			int[][] tabB = new int[tailleMatrice][tailleMatrice];
			
			// reconstruit la ligne A
			for (short i=0; i<tailleMatrice; i++) {
				ligneA[i] = IntToBytes.bytesToInt(tampon, offset*4);
				offset++;
			}
			
			// reconstruit la matrice B
			for (short i=0; i<tailleMatrice; i++) {
				for (short j=0; j<tailleMatrice; j++) {
					tabB[i][j] = IntToBytes.bytesToInt(tampon, offset*4);
					offset++;
				}
			}
			
			// calcul les valeurs
			for (short j=0; j<tailleMatrice; j++) { // j = colonne
				// multiplie avec la colonne de la matrice B
				for (short k=0; k<tailleMatrice; k++) {
					ligneC[j] += ligneA[k] * tabB[k][j];
				}    		   
			}
			
			// recréer un nouveau tampon de la bonne taille
			tampon = new byte[TAILLE_TAMPON]; // reinit le tampon
			offset = 0; // reset la variable
			
			// renvoie les resultats au serveur
			IntToBytes.intToBytes(ligneACalculer, tampon, offset);
			offset++;
			
			// envoie la ligne calculee au serveur
			for (short i=0; i<tailleMatrice; i++) {
				IntToBytes.intToBytes(ligneC[i], tampon, offset*4);
				offset++;
			}

			// affiche les resultats
			System.out.println("Ligne de A");
			for (int i=0; i<ligneA.length; i++) {
				System.out.print(ligneA[i] + " ");
			}
			
			System.out.println("\nMatrice B");
			afficheMatrice(tabB);
			
			System.out.println("Ligne de C calculee");
			for (int i=0; i<ligneC.length; i++) {
				System.out.print(ligneC[i] + " ");
			}
			
			// envoie tout
			paquet = new DatagramPacket(tampon, tampon.length, address, port);
			socket.send(paquet); // envoi du paquet a l'aide du socket
			
			/*
			// en utilisant des Stream
			Socket s = new Socket("localhost", 6000);
			s.getInputStream();
			s.getOutputStream();
			*/
			
			// ferme la connection
			socket.close();
			System.out.println("\n*** fin client ***");
			
		} catch (IOException e) {
			System.err.println(e);

		}
	}
}

