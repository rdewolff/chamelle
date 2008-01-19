import java.io.FileInputStream;
import java.net.ServerSocket;
import java.security.KeyStore;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

import HTTP.HTTPProcessing;

/**
 * Implementation partielle d'un serveur web HTTP/1.0.
 * Cette classe contient le programme principale qui traite toutes
 * les nouvelles connexions TCP entrantes. Il sera associe a chacune
 * d'elle une thread chargee de son traitement.
 * 
 * Implémentation des instructions requise pour permettre a ce serveur 
 * d'offrir un service HTTPS (avec SSL).
 * 
 * @author Cyril Maulini
 * @author Romain de Wolff
 */
public final class WebServer {
	/**
	 * Traitement de toutes les nouvelles connexions TCP.
	 * Des threads separees seront chargees de traiter les requetes,
	 * permettant ainsi un traitement parallele de plusieurs requetes.
	 * @param argv Contient le numero de port d'ecoute du serveur web.
	 * @throws Exception
	 */
	public static void main (String argv[]) {
		// Verification du parametre (numero de port)
		if (argv.length == 2) {
			// Obtenir le numero de port depuis la ligne de commande
			int port = Integer.valueOf(argv[0]);
			boolean requiertAuthentificationClient = 
				( Integer.valueOf(argv[1]) == 0 ? false : true );
			
			try {
				/* implémentation de la gestion du SSL */
				
				// chemin du keystore
				String myPrivateKey = "/Users/rdewolff/Documents/HEIG-VD/eclipse/svnChamelle/ASI/labo04/key/myPrivateKeystore";
				
				// recuperation d'une instance d'un keystore JKS
				KeyStore ks = KeyStore.getInstance("JKS");
				
				// chargement d'un keystore a l'aide de son mot de passe et du chemin
				// vers celui-ci
				ks.load(new FileInputStream(myPrivateKey), "keystorePass".toCharArray());
				
				// creation d'un magasin de keymanager du type SunX509
				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509"); // 2 import possible ?
				// Intitialisation de celui-ci à l'aide du keystore à utiliser et de la phrase
				// de passe de la cle privee a utiliser
				kmf.init(ks, "keyPwd".toCharArray());
				// Creation du contexte voulus
				SSLContext sslContext = SSLContext.getInstance("SSLv3");
				
				// initialisation de celui ci a l'aide d'un keymanager
				sslContext.init(kmf.getKeyManagers(), /* trust manager */ null, null);
				
				// initialisation du magasin de serversocket
				ServerSocketFactory ssf = sslContext.getServerSocketFactory();
				
				// creation du socket en mode SSL
				SSLServerSocket server = (SSLServerSocket) ssf.createServerSocket(port);
						
				// Traiter les connexions TCP dans une boucle infinie
				while (true) {
						// Ecouter en attente d'une demande de connexion TCP,
						// construire un objet pour traiter la requete HTTP
						// et lancer le traitement
						new HTTPProcessing(server.accept()).start();
				}
			}
			catch (Exception e) {
				System.out.println(e);
			}
		}
		
		// Erreur de syntaxe lors du lancement
		else {
			System.out.println("Syntax error: WebServer <requiert authentification du client? (0=non, autre entier=oui)> <port (default SSL=443>");
		}
	}
}
