import java.net.*;
import java.io.*;

public class V2_Serveur 
{
	public static void main (String args[]) throws IOException, SocketException 
	{
		byte[] tampon = new byte[256];
		
		DatagramSocket socket = new DatagramSocket(4445);
		
		DatagramPacket paquet = new DatagramPacket(tampon, tampon.length);
		
		
		
	}
}

