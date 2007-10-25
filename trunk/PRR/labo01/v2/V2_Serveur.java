package v2;

import java.net.*;
import java.io.*;

public class V2_Serveur 
{
	static int PORT = 6000;
	static int N = 3;
	
	public static void main (String args[]) throws IOException, SocketException 
	{
		
		Integer TAILLE_TAMPON = 10; // Integer.MAX_VALUE = 2147483647 => 10 chars
		Integer nbClientConnecte = 0;
		Integer tailleMatrice = N;
		
		byte[] tampon = new byte[TAILLE_TAMPON];

		DatagramSocket socket = new DatagramSocket(PORT);
		DatagramPacket paquet = new DatagramPacket(tampon, tampon.length);
		System.out.println("*** Serveur dŽmarrŽ ***");
		
		
		// TODO : multi client WHILE ... n .. 
		// attends tous les clients
		socket.receive(paquet); // attend la requete du client
		System.out.println("Recu : " + new String(paquet.getData()));
		// leur envoie les donnŽes necessaires (N + ligne, numŽro de ligne et matrice B)
		nbClientConnecte++;
		System.out.println("client " + nbClientConnecte + " connectŽ");
		
		// renvoie le numero au client, qui correspond ˆ la ligne qu'il doit traiter
		InetAddress addresseClient = paquet.getAddress(); // l'addresse qu'utilise le client
		int portClient = paquet.getPort(); // le port qu'utilise le client
		// met l'information a transmettre en octets
		tampon = (nbClientConnecte.toString()).getBytes();
		paquet = new DatagramPacket(tampon, tampon.length, addresseClient, portClient); // reinit le paquet
		// on lui envoie son ID qui correspond a la ligne qu'il va devoir calculer
		socket.send(paquet); // envoie de l'ID qui correspond ˆ la ligne ˆ calculer
		
		// renvoie la tailleMatrice
		tampon = (tailleMatrice.toString()).getBytes();
		paquet = new DatagramPacket(tampon, tampon.length, addresseClient, portClient); 
		socket.send(paquet);
		
		/*
		// rŽcupre toute les valeurs
		String messageRecu = new String(paquet.getData());
		System.out.println("Echo > " + messageRecu);
		
		messageRecu = "N"; // valeur de la ligne qu'il va calculer
		tampon = messageRecu.getBytes();
		
		// envoie les informations au client 
		
		socket.send(paquet);
		
		// socket.setSoTimeout(1000); // TODO gere les timeout de connection 
		*/
		
		socket.close();
		System.out.println("fin serveur");
	}
}

