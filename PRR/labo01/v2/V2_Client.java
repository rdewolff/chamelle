package v2;

import java.net.*;
import java.io.*;
import java.util.*;

public class V2_Client {	
	public static void main (String args[]) throws IOException {
		try {
			
			// exemple de donn�e
			String query = "q";
			byte[] tampon = new byte[4];
			tampon = query.getBytes();

			// introduction
			// TODO : attente de pression clavier
			//System.out.println("Appuyer sur une touche pour continuer .. ")
			
			// parametre de la connexion
			InetAddress address = InetAddress.getByName("localhost");
			int port = 6000;
			
			// envoie au serveur un paquet de requete, pour qu'il sache qu'il est connect�
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket paquet = new DatagramPacket(tampon, tampon.length, address, port);
			socket.send(paquet); // envoi du paquet a l'aide du socket
			
			
			// recoit les infos du serveur
			 	//int N = Integer.parseInt(packet.getData().toString()); // TODO : inutile ?
				// obtenir un socket de datagramme
				//System.out.println("Valeur de N : " + N + "\n");
			paquet = new DatagramPacket(tampon, tampon.length);
			socket.receive(paquet); 
			// la connection est d�s lors etablie, la communication fonctionne
			System.out.println("Connection avec le serveur �tablie");
			String data = new String(paquet.getData()); // ou : String data = new String(paquet.getData(), 0); // old! 
			
			System.out.println("Paquet recu: " + data);

			// recoit une ligne, un num�ro de ligne et la matrice B			
			// on a besoin de : 
			// N, ligne[], matB[][]

			// calcul les valeurs

			// renvoie les r�sultats au serveur
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

