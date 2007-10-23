package v2;

import java.net.*;
import java.io.*;

public class V2_Client
{
	public static void main (String args[]) throws IOException
	{
		String message = "Allo";
		byte[] tampon = new byte[256];
		
		// creation du message a envoyer au serveur
		tampon = message.getBytes();
		InetAddress address = InetAddress.getByName("localhost");
		DatagramPacket paquet = new DatagramPacket(tampon, tampon.length, address, 4445);
		
		// obtenir un socket de datagramme
		DatagramSocket socket = new DatagramSocket();
		// envoyer le message
		socket.send(paquet);
		
		// obtenir l'echo
		DatagramPacket packet = new DatagramPacket(tampon, tampon.length);
		socket.receive(packet);
		String msg = new String(packet.getData(), 0);
		
		System.out.println("Paquet recu:" + msg);
		
		socket.close();
		
	}
}

