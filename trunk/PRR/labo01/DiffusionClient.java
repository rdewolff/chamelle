import java.net.*; 
import java.io.*; 
public class DiffusionClient 
{ 
   public static void main(String args[]) throws IOException 
   { 
      byte[] tampon = new byte[256]; 
      // Joindre le groupe pour recevoir le message diffuse 
      MulticastSocket socket = new MulticastSocket(4445); 
      InetAddress groupe = InetAddress.getByName("228.5.6.7"); 
      socket.joinGroup(groupe); 
      // Attendre le message du serveur 
      DatagramPacket paquet = new DatagramPacket(tampon,tampon.length); 
      socket.receive(paquet); 
      String messageRecu = new String(paquet.getData(),0); 
      System.out.println("DiffusionClient: Message recu: " + messageRecu); 
      socket.leaveGroup(groupe); 
      socket.close(); 
   } 
} 
