package v2;

import java.net.*;
import java.io.*;

public class V2_Serveur 
{
	static int PORT = 6000;
	static int N = 3;
	
	public static void main (String args[]) throws IOException, SocketException 
	{
		byte[] tampon = new byte[256];
		
		DatagramSocket socket = new DatagramSocket(PORT);
		DatagramPacket paquet = new DatagramPacket(tampon, tampon.length);
		System.out.println("OK \n");
		socket.receive(paquet); // attend la requete du client
		System.out.println("client 1 connect�");
		// attends tous les clients
		// leur envoie les donn�es necessaires (N + ligne, num�ro de ligne et matrice B)
		
		// r�cup�re toute les valeurs
		String messageRecu = new String(paquet.getData(), 0);
		System.out.println("Echo > " + messageRecu);
		
		InetAddress addresseClient = paquet.getAddress();
		int portClient = paquet.getPort();
		
		messageRecu = "N"; // valeur de la ligne qu'il va calculer
		tampon = messageRecu.getBytes();
		
		paquet = new DatagramPacket(tampon, tampon.length, addresseClient, portClient);
		
		// envoie les informations au client 
		
		socket.send(paquet); 
		
		socket.close();
		System.out.println("fin serveur");
	}
}

