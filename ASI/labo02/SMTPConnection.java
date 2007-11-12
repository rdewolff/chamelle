import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Ouvrir une connexion SMTP vers une machine distante et envoyer un seul
   message.
 */
public class SMTPConnection {
	/* La socket de connection */
	public Socket socketConnection;

	/* Flux pour lire et écrire dans la socket */
	public BufferedReader fromServer;
	public DataOutputStream toServer;

	/* Port SMTP et fin de ligne */
	private static final int SMTP_PORT = 25;
	private static final String CRLF = "\r\n";

	/* Sommes-nous connectés? Utilisé dans close() pour déterminer que faire. */
	private boolean isConnected = false;

	/* Constructeur. Créer la socket et les flux associés. 
       Envoyer la commande HELO et contrôler s'il y a des erreurs. */
	public SMTPConnection(Envelope envelope) throws IOException {
		// socketConnection = // TODO /* compléter */;
		// on va mettre ici les InputStream et OutputStream (cf code labo 01)
		fromServer = new BufferedReader(new InputStreamReader(System.in)); // TODO /* compléter */;
		toServer = new DataOutputStream(System.out);	// TODO /* compléter */;

		// Lire une ligne du serveur et vérifier que le code de réponse est 220.
		// TODO : /* compléter */

		// Echange (handshake) SMTP. Nous avons besoin du nom de la machine
		// locale. Envoyer la commande SMTP initiale appropriée.
		String localhost = // TODO /* compléter */;
			try {
				sendCommand(  /* compléter */);
			} catch (IOException e) {
				System.out.println(  /* compléter */);
						return;
			}
			isConnected = true;
	}

	/* Envoyer le message. Ecrire simplement les commandes SMTP correctes
       dans le bon ordre. Pas de contrôle d'erreur, on les lance vers
       l'appelant... */
	public void send(Envelope envelope) throws IOException {
		// Envoyer toutes les commandes nécessaires pour envoyer un
		// message. Appeler sendCommand() pour faire le travail. Ne pas
		// attraper d'exception lancée par sendCommand() ici à l'intérieur.
	    /* compléter */

	}

	/* Fermer la connexion. Envoyer la commande QUIT et fermer la socket.*/
	public void close() {
		isConnected = false;
		try {
			sendCommand(/* compléter */);
			socketConnection.close();
		} catch (IOException e) {
			System.out.println("Impossible de fermer la connexion: " + e);
			isConnected = true;
		}
	}

	/* Envoyer une commande SMTP au serveur. Contrôler le code de réponse.
       Ne contrôle pas les codes de réponse multiples (requis pour RCPT TO). */
	private void sendCommand(String command, int rc) throws IOException {
		// Ecrire la commande au serveur et lire la réponse du serveur
		/* compléter */

		// Vérifier que la réponse du serveur est la même que le paramètre
		// rc. Si ce n'est pas le cas, lancer une IOException.
		/* compléter */

	}

	/* Parser la ligne de réponse du serveur. Retourner le code de réponse. */
	private int parseReply(String reply) {
		/* compléter */
		return 0;
	}

	/* Destructeur. Fermer la connexion si qqch anormal est arrivé. */
	protected void finalize() throws Throwable {
		if(isConnected) {
			close();
		}
		super.finalize();
	}
}