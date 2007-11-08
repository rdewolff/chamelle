import java.net.*;
import java.io.*;

public class V4_Client
{
	static int portMulti = 4445;
	
	public static void main(String args[]) throws IOException
	{
		
		byte[] tampon = new byte[256];
		//Joindre le groupe pour recevoir le message diffuse
		MulticastSocket socket = new MulticastSocket(Integer.decode(args[0]));
		InetAddress groupe = InetAddress.getByName("228.5.6.7");
		//cptOccurence;
		socket.joinGroup(groupe);
//		 synchronisation avec le serveur : envoie au serveur un paquet de requete, pour qu'il sache qu'il est connecte
		DatagramSocket socketS = new DatagramSocket();
		DatagramPacket paquetS = new DatagramPacket(tampon, tampon.length, InetAddress.getByName("localhost"), 4446);
		socketS.send(paquetS); // envoi du paquet a l'aide du socket
		//Attendre le message du serveur
		DatagramPacket paquet = new DatagramPacket(tampon,tampon.length);
		socket.receive(paquet);
		String messageRecu = new String(paquet.getData(),0);
		System.out.println("DiffusionClient: Message recu: " + messageRecu);
		socket.leaveGroup(groupe);
		socket.close();
	}
}