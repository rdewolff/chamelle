import java.io.*; 
import java.net.*; 
public class DiffusionServeur 
{ 
    public static void main(String[] args) throws IOException 
    { 
      String message = "Allo"; 
      byte[] tampon = new byte[256]; 
      // Associer un port de communication a un groupe 
      InetAddress groupe = InetAddress.getByName("228.5.6.7"); 
      MulticastSocket socket = new MulticastSocket(4446); 
      // Creation du message a diffuser aux clients 
      tampon = message.getBytes(); 
      DatagramPacket paquet = new DatagramPacket(tampon,tampon.length,groupe,4445); 
      // Envoyer le message aux membres du groupe 
      socket.send(paquet); 
      socket.close(); 
    } 
}