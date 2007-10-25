package v2;

import java.net.*;
import java.io.*;

public class V2_Serveur 
{
	public static void main (String args[]) throws IOException, SocketException 
	{
		
		Integer N = 3;
		
		byte[] tampon = new byte[256];
		
		DatagramSocket socket = new DatagramSocket(4445);
		
		DatagramPacket paquet = new DatagramPacket(tampon, tampon.length);
		
		//socket.receive(paquet); // bloquant
		
		//String messageRecu = new String(paquet.getData(), 0);
		//System.out.println("Echo: " + messageRecu);
		
		// 1 - InetAddress addresseClient = paquet.getAddress();
		// 2 - 
		InetAddress addresseClient = InetAddress.getByName("localhost");
		//int portClient = paquet.getPort();
		
		// 1 - tampon = messageRecu.getBytes();
		// 2 -
		tampon[0] = N.byteValue();
		
		
		// 1 - paquet = new DatagramPacket(tampon, tampon.length, addresseClient, portClient);
		// 2 -
		paquet = new DatagramPacket(tampon, tampon.length, addresseClient, 4445);
		
		socket.send(paquet);
		
		socket.close();
	}
}

