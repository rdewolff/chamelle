package v2;

import java.net.*;
import java.io.*;

public class V2_Client {	
	public static void main (String args[]) throws IOException {
		try {
			
			// exemple de donnée
			String query = "q";
			byte[] tampon = new byte[4];
			tampon = query.getBytes();

			// introduction
			// TODO : attente de pression clavier
			//System.out.println("Appuyer sur une touche pour continuer .. ")
			// parametre de la connexion
			InetAddress address = InetAddress.getByName("localhost");
			int port = 6000;
			
			// envoie au serveur un paquet de requete, pour qu'il sache qu'il est connecté
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket paquet = new DatagramPacket(tampon, tampon.length, address, port);
			socket.send(paquet); // envoi

			System.out.println("Connection avec le serveur établie");

			//int N = Integer.parseInt(packet.getData().toString()); // TODO : inutile ?
			// obtenir un socket de datagramme
			//System.out.println("Valeur de N : " + N + "\n");

			// recoit les infos du serveur
			DatagramPacket paquet2 = new DatagramPacket(tampon, tampon.length);
			socket.receive(paquet); 

			String msg = new String(paquet2.getData(), 0);


			System.out.println("Paquet recu:" + msg);

			// recoit une ligne, un numéro de ligne et la matrice B
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

