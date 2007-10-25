package v2;

import java.net.*;
import java.io.*;
import java.util.*;

public class V2_Client {	
	public static void main (String args[]) throws IOException {
		try {
			
			Integer TAILLE_TAMPON = 10;
			
			// exemple de donnée
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
			
			// envoie au serveur un paquet de requete, pour qu'il sache qu'il est connecté
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket paquet = new DatagramPacket(tampon, tampon.length, address, port);
			socket.send(paquet); // envoi du paquet a l'aide du socket
			
			
			// recoit les infos du serveur
			 	//int N = Integer.parseInt(packet.getData().toString()); // TODO : inutile ?
				// obtenir un socket de datagramme
				//System.out.println("Valeur de N : " + N + "\n");
			tampon = new byte[TAILLE_TAMPON]; // reinit le tampon
			paquet = new DatagramPacket(tampon, tampon.length);
			socket.receive(paquet); 
			// la connection est dès lors etablie, la communication fonctionne
			System.out.println("Connection avec le serveur établie");
			// String data = new String(paquet.getData()); // ou : 
			String data = new String(paquet.getData()); // old! 
			
			System.out.println("Paquet recu: " + data);

			// recoit une ligne, un numéro de ligne et la matrice B			
			// on a besoin de : 
			// N, ligne[], matB[][]

			// calcul les valeurs

			// renvoie les résultats au serveur
			//DatagramPacket paquet = new DatagramPacket(tampon, tampon.length, address, port);
			//socket.send(paquet);

			// ferme la connection
			socket.close();
			System.out.println("fin client");
			
		} catch (IOException e) {
			System.err.println(e);

		}
	}
}

