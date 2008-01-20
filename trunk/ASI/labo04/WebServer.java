import java.io.FileInputStream;
import java.net.ServerSocket;
import java.security.KeyStore;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;

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
 * @author Cyril Maulini (version initiale)
 * @author Romain de Wolff (modification et implémentation SSL)
 */
public final class WebServer {

	// -------------------------------------------------------------------------
	// Parametres (constantes)

	// fichiers contenant les certificats et mot de passe associés
	// dans le cas d'un serveur SSL sans authentification du client
	final static String MYPRIVATEKEY = 
		"/Users/rdewolff/Documents/HEIG-VD/eclipse/svnChamelle/ASI/labo04/key/" +
		"myPrivateKeystore";
	final static String MYPRIVATEKEYPASS = "keystorePass";

	// dans le cas d'un serveur SSL avec authentification du client
	final static String MYPRIVATEKEYWITHCLIENTAUTH = 
		"/Users/rdewolff/Documents/HEIG-VD/eclipse/svnChamelle/ASI/labo04/key/" +
		"myPrivateKeystoreConfiance";
	final static String MYPRIVATEKEYWITHCLIENTAUTHPASS = "keystorePass";

	// liste des cipher utilise par le serveur
	final static String[] CIPHERSUITES = {
		"SSL_RSA_WITH_3DES_EDE_CBC_SHA",  
		"SSL_RSA_WITH_RC4_128_SHA",
		"TLS_RSA_WITH_RC4_128_MD5"
	};

	// est-ce que l'on veut utiliser le serveur comme systeme d'authentification
	// uniquement ? (defaut : false)
	final static boolean SERVICEAUTHENTIFICATIONUNIQUEMENT = false;

	// -------------------------------------------------------------------------

	/**
	 * Traitement de toutes les nouvelles connexions TCP.
	 * Des threads separees seront chargees de traiter les requetes,
	 * permettant ainsi un traitement parallele de plusieurs requetes.
	 * @param argv Contient le numero de port d'ecoute du serveur web.
	 * @throws Exception
	 */
	public static void main (String argv[]) {

		// Verification du parametre (authentification du client et numero de port)
		if (argv.length == 2) {

			// Obtenir le numero de port depuis la ligne de commande
			int port = Integer.valueOf(argv[1]);

			// Obtenir le booleen qui defini si l'on desire authetifier les clients
			// ou non
			boolean requiertAuthentificationClient = 
				( Integer.valueOf(argv[0]) == 0 ? false : true );

			try {

				/**
				 *  implémentation de la gestion du SSL   
				 */

				// recuperation d'une instance d'un keystore JKS
				KeyStore ks = KeyStore.getInstance("JKS");

				// chargement d'un keystore a l'aide de son mot de passe et du chemin
				// vers celui-ci
				if (requiertAuthentificationClient) {
					ks.load(new FileInputStream(MYPRIVATEKEYWITHCLIENTAUTH), 
							MYPRIVATEKEYWITHCLIENTAUTHPASS.toCharArray());
				} else {
					ks.load(new FileInputStream(MYPRIVATEKEY), 
							MYPRIVATEKEYPASS.toCharArray());
				}

				// creation d'un magasin de keymanager du type SunX509
				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509"); 

				// Le TrustManager dans le cas ou le client doit s'authentifier
				TrustManagerFactory tmf = null;
				if (requiertAuthentificationClient) {

					// on le defini
					tmf = TrustManagerFactory.getInstance("SunX509");

					// et on l'initialise a l'aide du keystore
					tmf.init(ks);

					// Intitialisation de celui-ci à l'aide du keystore à 
					// utiliser et de la phrase
					// de passe de la cle privee a utiliser
					kmf.init(ks, "keyPass".toCharArray());

				} else {

					// Intitialisation de celui-ci à l'aide du keystore à 
					// utiliser et de la phrase
					// de passe de la cle privee a utiliser
					kmf.init(ks, "keyPwd".toCharArray());

				}	

				// Creation du contexte voulus, c'est ici que l'on defini la 
				// version de SSL ou TLS que l'on desire utiliser
				// Par exemple "SSLv3" ou encore "TLS".
				SSLContext sslContext = SSLContext.getInstance("SSLv3");


				// initialise le context SSL avec le TrustManager si l'on
				// desire authentifier les clients, sinon sans rien (dans le 2eme
				// cas, le serveur s'occupe de gerer cela lui-meme.
				if (requiertAuthentificationClient) {
					// initialisation de celui ci a l'aide d'un keymanager
					sslContext.init(kmf.getKeyManagers(), 
							/* trust manager */ tmf.getTrustManagers(), null);	
				} else {
					sslContext.init(kmf.getKeyManagers(), 
							/* trust manager */ null, null);
				}

				// initialisation du magasin de serversocket
				ServerSocketFactory ssf = sslContext.getServerSocketFactory();

				// creation du socket en mode SSL
				SSLServerSocket server = (SSLServerSocket) ssf.createServerSocket(port);

				// Uniquemen un service d'authentification ?
				server.setEnableSessionCreation(SERVICEAUTHENTIFICATIONUNIQUEMENT);

				// Defini les ciphers autorise par le serveur
				server.setEnabledCipherSuites(CIPHERSUITES);

				// Si l'on desire que les clients soient authentifie, il faut 
				// activer cette option sur le socket SSL du serveur
				if (requiertAuthentificationClient) {
					server.setNeedClientAuth(true);
				}

				// affichage des protocols et des ciphers supportes par le serveur
				System.out.println("********************************************");
				System.out.println("Enabled Session Creation ? " + 
						server.getEnableSessionCreation());
				System.out.println("Protocols actifs:");
				for (short i=0; i<server.getEnabledProtocols().length; i++) 
					System.out.println(" - " + 
							server.getEnabledProtocols()[i].toString());
				System.out.println("Ciphers actifs:");
				for (short i=0; i<server.getEnabledCipherSuites().length; i++) 
					System.out.println(" - " + 
							server.getEnabledCipherSuites()[i].toString());
				System.out.println("********************************************\n");

				System.out.println("Serveur Web Demarre sur le port " + port);

				// Traiter les connexions TCP dans une boucle infinie
				while (true) {
					// Ecouter en attente d'une demande de connexion TCP,
					// construire un objet pour traiter la requete HTTP
					// et lancer le traitement
					new HTTPProcessing(server.accept()).start();
					System.out.println("*** Client connecte ***");
				}

			}
			catch (Exception e) {
				System.out.println(e);
			}
		}

		// Erreur de syntaxe lors du lancement
		else {
			System.out.println("Syntax error: WebServer " +
					"<requiert authentification du client? " +
			"(0=non, autre entier=oui)> <port (default SSL=443>");
		}
	}
}
