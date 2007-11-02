import java.net.*;
import java.io.*;

public class V3_Client
{	
	public static void main (String args[]) throws IOException
	{
		try {

			// introduction
			// tente de se connecter au serveur
			System.out.println("Appuyer sur une touche pour continuer .. ");
			// etablis la connection
			// recoit la valeur de N
			// recoit une ligne, un numéro de ligne et la matrice B
			// calcul les valeurs
			// renvoie les résultats au serveur

			
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
			// ferme la connection
			socket.close();

		} catch (IOException e) {
			System.out.println(e);
		}
	}
}

