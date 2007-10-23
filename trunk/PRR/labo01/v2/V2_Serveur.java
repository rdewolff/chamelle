package v2;

import java.net.*;
import java.io.*;

public class V2_Serveur 
{
	public static void main (String args[]) throws IOException, SocketException 
	{
		byte[] tampon = new byte[256];
		
		DatagramSocket socket = new DatagramSocket(4445);
		
		DatagramPacket paquet = new DatagramPacket(tampon, tampon.length);
		socket.receive(paquet);
		
		String messageRecu = new String(paquet.getData(), 0);
		System.out.println("Echo: " + messageRecu);
		
		InetAddress addresseClient = paquet.getAddress();
		int portClient = paquet.getPort();
		
		tampon = messageRecu.getBytes();
		paquet = new DatagramPacket(tampon, tampon.length, addresseClient, portClient);
		
		socket.send(paquet);
		
		socket.close();
		
		
	}
}

