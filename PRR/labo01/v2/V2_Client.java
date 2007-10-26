package v2;

import java.net.*;
import java.io.*;

public class V2_Client {	
	
	/* 
	 * Affichage de la matrice passee en parametre (2D)
	 */
	private static void afficheMatrice(Integer [][] tableau) {
		for (short i=0; i<tableau.length; i++) {
			for (short j=0; j<tableau.length; j++) {
				System.out.print(tableau[i][j] + " ");
			}
			System.out.print('\n');
		}
	}

	public static void main (String args[]) throws IOException {
		try {
			
			// adresse et port de connexion passe en parametre
			InetAddress address = InetAddress.getByName(args[0]); 
			int port = Integer.parseInt(args[1]); 
			
			Integer TAILLE_TAMPON = 10;
			String query = "HELO";
			byte[] tampon = new byte[TAILLE_TAMPON];
			tampon = query.getBytes();
 
			System.out.println("*** Cylient ***");
			BufferedReader stdin= new BufferedReader(new InputStreamReader(System.in)); 
			System.out.print("Appuyer sur une <enter> pour vous connecter au serveur"); 
			stdin.readLine(); // attend touche <enter> 
			
			// envoie au serveur un paquet de requete, pour qu'il sache qu'il est connecte
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket paquet = new DatagramPacket(tampon, tampon.length, address, port);
			socket.send(paquet); // envoi du paquet a l'aide du socket
			
			// recoit les infos du serveur
			tampon = new byte[TAILLE_TAMPON]; // reinit le tampon
			paquet = new DatagramPacket(tampon, tampon.length);
			socket.receive(paquet); 
			// la connection est dès lors etablie, la communication fonctionne
			System.out.println("Connection avec le serveur etablie");
			
			// recoit les infos qui sont dans un seul character (deux nombre < 10)
			Integer ligneACalculer = Integer.parseInt(new String(paquet.getData()).trim());
			
			socket.receive(paquet); 
			Integer tailleMatrice = Integer.parseInt(new String(paquet.getData()).trim());
			
			System.out.println("ligneACalculer: " + ligneACalculer + "\ntailleMatrice: " + tailleMatrice);
			
			Integer[] ligneA = new Integer[tailleMatrice];
			Integer[] ligneC = new Integer[tailleMatrice];			
			Integer[][] tabB = new Integer[tailleMatrice][tailleMatrice];
			
			// recoit la ligne A
			for (short i=0; i<tailleMatrice; i++) {
				socket.receive(paquet);
				Integer val = Integer.parseInt(new String(paquet.getData()).trim());
				ligneA[i] = val;
			}
			
			// recoit la matrice B
			for (short i=0; i<tailleMatrice; i++) {
				for (short j=0; j<tailleMatrice; j++) {
					socket.receive(paquet);
					Integer val = Integer.parseInt(new String(paquet.getData()).trim());
					tabB[i][j] = val;
				}
			}
			
			// calcul les valeurs
			ligneC[0] = 0; ligneC[1] = 0; ligneC[2] = 0; // TODO : autre moyen d'initialiser ces valeurs directement ?
			for (short j=0; j<tailleMatrice; j++) { // j = colonne
				// multiplie avec la colonne de la matrice B
				for (short k=0; k<tailleMatrice; k++) {
					ligneC[j] += ligneA[k] * tabB[k][j];
				}    		   
			}
			
			// renvoie les resultats au serveur
			tampon = (ligneACalculer.toString()).getBytes(); // on declare son ID, la ligne calculee
			// socket = new DatagramSocket();
			paquet = new DatagramPacket(tampon, tampon.length, address, port);
			socket.send(paquet); // envoi du paquet a l'aide du socket
			
			// envoie la ligne calculee au serveur
			for (short i=0; i<tailleMatrice; i++) {
				tampon = (ligneC[i].toString()).getBytes();
				paquet = new DatagramPacket(tampon, tampon.length, address, port);
				socket.send(paquet);
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

