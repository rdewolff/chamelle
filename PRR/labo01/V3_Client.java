
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

	// Constante
	static final int tailleInt = 4;
	static final String adresseGroupe = "230.230.230.230";

	public static void main (String args[]) throws IOException {
		try {
			/* 
			 * Déclarations et initialisations
			 */
			// adresse et port de connexion passe en parametre
			InetAddress address = InetAddress.getByName(args[0]); // TODO : check param juste
			int port = Integer.parseInt(args[1]); 

			// rejoint le groupe de diffusion au debut pour etre sur d'y etre
			// connecté quand le serveur enverra les informations
			MulticastSocket socketMulti = new MulticastSocket(port+2);
			InetAddress groupe = InetAddress.getByName(adresseGroupe);
			socketMulti.joinGroup(groupe);

			String query = "HELO"; // pour se sychroniser avec le serveur
			// int TAILLE_TAMPON = 56; TODO: taille tampon dynamique ?
			int TAILLE_TAMPON_SYNCHRO = query.length()*2; // message recu : "HELO"
			byte[] tampon = new byte[TAILLE_TAMPON_SYNCHRO]; // defini la taille miniumm necessaire. Un char = 2 bytes
			tampon = query.getBytes();

			System.out.println("*** Client ***");
			// Evite que le client se connecte au lancement, la touche
			// enter est requise pour continuer l'execution du client
			BufferedReader stdin= new BufferedReader(new InputStreamReader(System.in)); 
			System.out.print("Appuyer sur une <enter> pour vous connecter au serveur"); 
			//stdin.readLine(); // attend touche <enter> 

			DatagramSocket socket = new DatagramSocket();
			DatagramPacket paquet; 

			while (true) {
				try {
					// synchronisation avec le serveur : envoie au serveur un paquet de requete, pour qu'il sache qu'il est connecte
					paquet = new DatagramPacket(tampon, tampon.length, address, port);
					socket.send(paquet); // envoi du paquet a l'aide du socket

					/*
					 * Recoit les informations du serveur
					 * - la taille des matrices a traiter
					 * - la matrice B
					 * - l'indice de B a calculer
					 * - la ligne de A 
					 */
					// reception de la taille des matrices
					tampon = new byte[tailleInt]; // recoit un int, qui fait 4 bytes
					paquet = new DatagramPacket(tampon, tampon.length);
					socket.setSoTimeout(1000);
					socket.receive(paquet);
					break; // quitte la boucle si la connexion est effectuee
				} catch (Exception e) {
					System.out.println("Tentative de connexion...");
				}
			}
			
			int tailleMatrice = IntToBytes.bytesToInt(tampon, 0);
			System.out.println("Taille de la matrice : " + tailleMatrice);

			// recoit la matrice B du serveur en diffusion
			tampon = new byte[tailleMatrice*tailleMatrice*tailleInt];
			paquet = new DatagramPacket(tampon, tampon.length);
			System.out.println("5");
			socketMulti.receive(paquet); // TODO : n-ième client ne recoit pas la matrice du serveur
			System.out.println("6");

			System.out.println("Matrice B recue du serveur!");

			// reconstruit la matrice B
			int offset = 0;
			int[][] tabB = new int[tailleMatrice][tailleMatrice];
			for (short i=0; i<tailleMatrice; i++) {
				for (short j=0; j<tailleMatrice; j++) {
					tabB[i][j] = IntToBytes.bytesToInt(tampon, offset*tailleInt);
					offset++;
				}
			}

			// recoit l'indice de B et la ligne de A
			int[] ligneA = new int[tailleMatrice];
			int[] ligneC = new int[tailleMatrice];
			// redefini le tampon avec la bonne taille
			tampon = new byte[(tailleMatrice*tailleMatrice+1)*tailleInt]; 
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

			// affiche la matrice
			afficheMatrice(tabB);
			// et la ligne a calculer
			System.out.println("Ligne a calculer : " + ligneACalculer);

			/*
			 * Calcul la ligne correspondante de C
			 */
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

			/*
			 * Emet la ligne calculee au serveur/coordinateur
			 */
			IntToBytes.intToBytes(ligneACalculer, tampon, offset);
			offset++;

			// met la ligne calculee dans le tampon d'envoi
			for (short i=0; i<tailleMatrice; i++) {
				IntToBytes.intToBytes(ligneC[i], tampon, offset*tailleInt);
				offset++;
			}

			// envoie tout
			paquet = new DatagramPacket(tampon, tampon.length, address, port);
			socket.send(paquet); // envoi du paquet a l'aide du socket

			/*
			 * Affiche les resultats a titre informatif
			 */
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

			// ferme les connections
			socketMulti.leaveGroup(groupe); // quite le serveur de diffusion
			socket.close();
			socketMulti.close();

			System.out.println("\n*** fin client ***");

		} catch (IOException e) {
			System.err.println(e);

		}
	}
}

