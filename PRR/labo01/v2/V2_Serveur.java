package v2;

import java.net.*;
import java.io.*;

public class V2_Serveur 
{
	static int PORT = 6000;
	static int N = 3;
	
	
	
	public static void main (String args[]) throws IOException, SocketException 
	{
		Integer tailleTampon = 4; 
		Integer nbClientConnecte = 0;
		Integer tailleMatrice = N;
		
		byte[] tampon = new byte[tailleTampon];

		DatagramSocket socket = new DatagramSocket(PORT);
		DatagramPacket paquet = new DatagramPacket(tampon, tampon.length);
		System.out.println("DŽmarrage du serveur...");
		
		// TODO : multi client WHILE ... n .. 
		// attends tous les clients
		socket.receive(paquet); // attend la requete du client
		// System.out.println("Client " + new String(paquet.getData()));
		// leur envoie les donnŽes necessaires (N + ligne, numŽro de ligne et matrice B)
		nbClientConnecte++;
		System.out.println("client " + nbClientConnecte + " connectŽ");
		// renvoie le numero au client, qui correspond ˆ la ligne qu'il doit traiter
		InetAddress addresseClient = paquet.getAddress(); // l'addresse qu'utilise le client
		int portClient = paquet.getPort(); // le port qu'utilise le client
		
		byte[] tampon2 = new byte[nbClientConnecte.toString().length()];
		// System.out.println("Sur le point d'envoyer : " + Integer.);
		paquet = new DatagramPacket(tampon2, tampon2.length, addresseClient, portClient); // reinit le paquet
		// on lui envoie son ID qui correspond a la ligne qu'il va devoir calculer
		socket.send(paquet); // envoie de l'ID qui correspond ˆ la ligne ˆ calculer
		
		
		/*
		// rŽcupre toute les valeurs
		String messageRecu = new String(paquet.getData());
		System.out.println("Echo > " + messageRecu);
		
		messageRecu = "N"; // valeur de la ligne qu'il va calculer
		tampon = messageRecu.getBytes();
		
		// envoie les informations au client 
		
		socket.send(paquet); 
		*/
		
		socket.close();
		System.out.println("fin serveur");
	}
}

