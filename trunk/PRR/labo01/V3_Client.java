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
	
	static final int tailleInt = 4;
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
			
			// reception de la taille des matrices
			// TODO : minimiser la taille du tampon 
			paquet = new DatagramPacket(tampon, tampon.length); 
			socket.receive(paquet);
			int tailleMatrice = IntToBytes.bytesToInt(tampon, 0);
			System.out.println(tailleMatrice);
			
			// rejoint le groupe de diffusion
			MulticastSocket socketMulti = new MulticastSocket(port+2);
			InetAddress groupe = InetAddress.getByName("230.230.230.230");  // TODO : placer dans config avec le serveur ?
			socketMulti.joinGroup(groupe);
			
			// recoit la matrice B de serveur en diffusion
			tampon = new byte[tailleMatrice*tailleMatrice*tailleInt];
			paquet = new DatagramPacket(tampon, tampon.length);
			socketMulti.receive(paquet); 
			
			System.out.println("Matrice B recue du serveur!");
			
			// reconstruit la matrice B
			int offset = 0;
			int[][] tabB = new int[3][3];
			for (short i=0; i<3; i++) {
				for (short j=0; j<3; j++) {
					tabB[i][j] = IntToBytes.bytesToInt(tampon, offset*tailleInt);
					offset++;
				}
			}

			//TODO : on a fini avec le groupe de diffusion, on peut quitter le groupe
			
			// recoit l'indice de B et la ligne de A
			int[] ligneA = new int[tailleMatrice];
			int[] ligneC = new int[tailleMatrice];
			tampon = new byte[(tailleMatrice*tailleMatrice+1)*tailleInt]; // reinit le tampon avec la bonne taille
			paquet = new DatagramPacket(tampon, tampon.length);
			socket.receive(paquet); 
			
			// recupere l'indice de B qui est dans le tampon
			offset = 0; 
			int ligneACalculer = IntToBytes.bytesToInt(tampon, offset);
			offset++;
			
			// idem avec la ligne A
			for (short i=0; i<tailleMatrice; i++) {
				ligneA[i] = IntToBytes.bytesToInt(tampon, offset*tailleInt);
				offset++;
			}
			
			// matrice
			afficheMatrice(tabB);

			System.out.println("ligneACalculer: " + ligneACalculer + "\ntailleMatrice: " + tailleMatrice);
			
			// calcul les valeurs
			for (short j=0; j<tailleMatrice; j++) { // j = colonne
				// multiplie avec la colonne de la matrice B
				for (short k=0; k<tailleMatrice; k++) {
					ligneC[j] += ligneA[k] * tabB[k][j];
				}    		   
			}
			
			// reset la décalage
			offset = 0;  
			tampon = new byte[(tailleMatrice*tailleMatrice+1)*4];
			// renvoie les resultats au serveur
			IntToBytes.intToBytes(ligneACalculer, tampon, offset);
			offset++;
			
			// envoie la ligne calculee au serveur
			for (short i=0; i<tailleMatrice; i++) {
				IntToBytes.intToBytes(ligneC[i], tampon, offset*tailleInt);
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

