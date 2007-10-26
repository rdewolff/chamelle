package v2;
// comment 1 : I love malwina!

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
			
			Integer TAILLE_TAMPON = 10;
			
			// exemple de donnŽe
			String query = "HELO";
			byte[] tampon = new byte[TAILLE_TAMPON];
			tampon = query.getBytes();

			// introduction 
			// TODO : attente de pression clavier
			//System.out.println("Appuyer sur une touche pour continuer .. ");
			System.out.println("*** Client ***");
			
			// parametre de la connexion
			InetAddress address = InetAddress.getByName("localhost");
			int port = 6000;
			
			// envoie au serveur un paquet de requete, pour qu'il sache qu'il est connectŽ
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket paquet = new DatagramPacket(tampon, tampon.length, address, port);
			socket.send(paquet); // envoi du paquet a l'aide du socket
			
			// recoit les infos du serveur
			tampon = new byte[TAILLE_TAMPON]; // reinit le tampon
			paquet = new DatagramPacket(tampon, tampon.length);
			socket.receive(paquet); 
			// la connection est ds lors etablie, la communication fonctionne
			System.out.println("Connection avec le serveur Žtablie");
			
			// recoit les infos qui sont dans un seul character (deux nombre < 10)
			Integer ligneACalculer = Integer.parseInt(new String(paquet.getData()).trim());
			
			socket.receive(paquet); 
			Integer tailleMatrice = Integer.parseInt(new String(paquet.getData()).trim());
			
			//socket.receive(paquet);
			//Integer tailleMatrice = Integer.parseInt(new String(paquet.getData()).trim());
			
			System.out.println("ligneACalculer: " + ligneACalculer + "\ntailleMatrice: " + tailleMatrice);
			
			Integer[] ligneA = new Integer[tailleMatrice];
			Integer[][] tabB = new Integer[tailleMatrice][tailleMatrice];
			
			// recoit la ligne A
			for (int i=0; i<tailleMatrice; i++) {
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
			
			System.out.println("Ligne de A a calculer");
			for (int i=0; i<ligneA.length; i++) {
				System.out.print(ligneA[i] + " ");
			}
			
			System.out.println("\nMatrice B");
			afficheMatrice(tabB);
			
			// calcul les valeurs

			// renvoie les rŽsultats au serveur
			//DatagramPacket paquet = new DatagramPacket(tampon, tampon.length, address, port);
			//socket.send(paquet);
			
			/*
			// en utilisant des Stream
			Socket s = new Socket("localhost", 6000);
			s.getInputStream();
			s.getOutputStream();
			*/
			
			// ferme la connection
			socket.close();
			System.out.println("fin client");
			
		} catch (IOException e) {
			System.err.println(e);

		}
	}
}

