package v2;

import java.net.*;
import java.io.*;

public class V2_Client {	
	public static void main (String args[]) throws IOException {
		try {
			// exemple de donnée
			String message = "caca";
			byte[] tampon = new byte[256];
			tampon = message.getBytes();
			
			// introduction
			// TODO : attente de pression clavier
			//System.out.println("Appuyer sur une touche pour continuer .. ")
			// parametre de la connexion
			InetAddress address = InetAddress.getByName("localhost");
			int port = 4445;
			// envoie au serveur un paquet de requete, pour qu'il sache qu'il est connecté
			
			// recoit la valeur de N
			DatagramSocket socket = new DatagramSocket(port);
			DatagramPacket packet = new DatagramPacket(tampon, tampon.length);
			socket.receive(packet);
			
			int N = Integer.parseInt(packet.getData().toString()); // TODO : inutile ?
			// obtenir un socket de datagramme
			System.out.println("Valeur de N : " + N + "\n");
			
			// envoyer le message
			socket.receive(packet);

			String msg = new String(packet.getData(), 0);

			
			System.out.println("Paquet recu:" + msg);
			
			// recoit une ligne, un numéro de ligne et la matrice B
			// calcul les valeurs
			// renvoie les résultats au serveur
			DatagramPacket paquet = new DatagramPacket(tampon, tampon.length, address, port);
			socket.send(paquet);

			// ferme la connection
			socket.close();

		} catch (IOException e) {
			System.out.println(e);
		}
	}
}

