import java.net.*;
import java.io.*;

public class V3_Client {	
	
	/* 
	 * Affichage de la matrice passee en parametre (2D)
	 */
	private static void afficheMatrice(int [][] tableau) {
		for (short i=0; i<tableau.length; i++) {
			for (short j=0; j<tableau.length; j++) {
				System.out.print(tableau[i][j] + " ");
			}
			System.out.print('\n');
		}
	}

	/*
	 * Methode principale contenatn tout le client
	 */
	public static void main (String args[]) throws IOException {
		try {
			
			// adresse et port de connexion passe en parametre
			InetAddress address = InetAddress.getByName(args[0]); 
			int port = Integer.parseInt(args[1]); 
			
			String query = "HELO"; // pour se sychroniser avec le serveur
			int TAILLE_TAMPON = 56;
			int TAILLE_TAMPON_SYNCHRO = query.length()*2; // message recu : "HELO"
			byte[] tampon = new byte[TAILLE_TAMPON_SYNCHRO]; // defini la taille miniumm necessaire. Un char = 2 bytes
			tampon = query.getBytes();
			
			System.out.println("*** Client ***");
			BufferedReader stdin= new BufferedReader(new InputStreamReader(System.in)); 
			System.out.print("Appuyer sur une <enter> pour vous connecter au serveur"); 
			stdin.readLine(); // attend touche <enter> 
			
			// synchronisation avec le serveur : envoie au serveur un paquet de requete, pour qu'il sache qu'il est connecte
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket paquet = new DatagramPacket(tampon, tampon.length, address, port);
			socket.send(paquet); // envoi du paquet a l'aide du socket
			
			System.out.println("1");
			// rejoint le groupe de diffusion
			MulticastSocket socketMulti = new MulticastSocket(port+2);
			System.out.println("2");
			InetAddress groupe = InetAddress.getByName("230.230.230.230");  // TODO : placer dans config avec le serveur ?
			System.out.println("3");
			socketMulti.joinGroup(groupe);
			System.out.println("4");
			
			// recoit la matrice B de serveur en diffusion
			tampon = new byte[3*3*4];
			System.out.println("5");
			paquet = new DatagramPacket(tampon, tampon.length);
			System.out.println("6");
			socketMulti.receive(paquet); 
			
			System.out.println("Matrice B recue du serveur!");
			
			// on a fini avec le groupe de diffusion, on peut quitter le groupe
			
			// ensuite on recupere le num de 
		
			
			
			
			int offset = 0;
			int[][] tabB = new int[3][3];
			// reconstruit la matrice B
			for (short i=0; i<3; i++) {
				for (short j=0; j<3; j++) {
					tabB[i][j] = IntToBytes.bytesToInt(tampon, offset*4);
					offset++;
				}
			}
			
			// 
			afficheMatrice(tabB);
			
			
			// recoit les infos du serveur
			tampon = new byte[TAILLE_TAMPON]; // reinit le tampon avec la bonne taille
			paquet = new DatagramPacket(tampon, tampon.length);
			socket.receive(paquet); 
			
			// la connection est des lors etablie, la communication fonctionne
			System.out.println("Connection avec le serveur etablie");
			
			// décomponse les elements recus par le serveur
		    offset = 0; 
			// recoit les infos qui sont dans un seul character (deux nombre < 10)
			int ligneACalculer = IntToBytes.bytesToInt(tampon, offset);
			offset++;
			int tailleMatrice = IntToBytes.bytesToInt(tampon, offset*4);
			offset++;
			
			System.out.println("ligneACalculer: " + ligneACalculer + "\ntailleMatrice: " + tailleMatrice);
			
			int[] ligneA = new int[tailleMatrice];
			int[] ligneC = new int[tailleMatrice];			
			//int[][] tabB = new int[tailleMatrice][tailleMatrice];
			
			// reconstruit la ligne A
			for (short i=0; i<tailleMatrice; i++) {
				ligneA[i] = IntToBytes.bytesToInt(tampon, offset*4);
				offset++;
			}
			
			// reconstruit la matrice B
			for (short i=0; i<tailleMatrice; i++) {
				for (short j=0; j<tailleMatrice; j++) {
					tabB[i][j] = IntToBytes.bytesToInt(tampon, offset*4);
					offset++;
				}
			}
			
			// calcul les valeurs
			for (short j=0; j<tailleMatrice; j++) { // j = colonne
				// multiplie avec la colonne de la matrice B
				for (short k=0; k<tailleMatrice; k++) {
					ligneC[j] += ligneA[k] * tabB[k][j];
				}    		   
			}
			
			// reset la décalage
			offset = 0;  

			// renvoie les resultats au serveur
			IntToBytes.intToBytes(ligneACalculer, tampon, offset);
			offset++;
			
			// envoie la ligne calculee au serveur
			for (short i=0; i<tailleMatrice; i++) {
				IntToBytes.intToBytes(ligneC[i], tampon, offset*4);
				offset++;
			}

			// affiche les resultats
			System.out.println("Ligne de A");
			for (int i=0; i<ligneA.length; i++) {
				System.out.print(ligneA[i] + " ");
			}
			
			// affiche la matrice B
			System.out.println("\nMatrice B");
			afficheMatrice(tabB);
			
			// affiche la ligne C calculee par ce client
			System.out.println("Ligne de C calculee");
			for (int i=0; i<ligneC.length; i++) {
				System.out.print(ligneC[i] + " ");
			}
			
			// envoie tout
			paquet = new DatagramPacket(tampon, tampon.length, address, port);
			socket.send(paquet); // envoi du paquet a l'aide du socket
			
			// ferme les connections
			socketMulti.leaveGroup(groupe); // serveur de diffusion
			socket.close();
			System.out.println("\n*** fin client ***");
			
		} catch (IOException e) {
			System.err.println(e);

		}
	}
}

