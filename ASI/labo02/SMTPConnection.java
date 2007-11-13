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
	public BufferedWriter toServer;

	/* Port SMTP et fin de ligne */
	private static final int SMTP_PORT = 25;
	private static final String CRLF = "\r\n";

	/* Sommes-nous connectés? Utilisé dans close() pour déterminer que faire. */
	private boolean isConnected = false;

	/* Constructeur. Créer la socket et les flux associés. 
       Envoyer la commande HELO et contrôler s'il y a des erreurs. */
	public SMTPConnection(Envelope envelope) throws IOException {

		// ouvre la connection avec le serveur distant
		socketConnection = new Socket(envelope.DestHost, SMTP_PORT);

		// défini les flux entrant et sortant
		fromServer = new BufferedReader(new InputStreamReader(socketConnection.getInputStream()));
		toServer = new BufferedWriter(new OutputStreamWriter(socketConnection.getOutputStream()));

		// temporaire, on utilise la console comme entrée/sortie
		//fromServer = new BufferedReader(new InputStreamReader(System.in));
		//toServer = new DataOutputStream(System.out);

		// Lire une ligne du serveur et vérifier que le code de réponse est 220.
		if (parseReply(fromServer.readLine()) != 220) {
			throw new IOException("Erreur de connection avec le serveur SMTP");
		}

		// Echange (handshake) SMTP. Nous avons besoin du nom de la machine
		// locale. Envoyer la commande SMTP initiale appropriée.
		// TODO nom ou adresse IP ? /* compléter */;
		String localhost = (InetAddress.getLocalHost()).getHostAddress(); 
		
		try {
			sendCommand("HELO " + localhost, 250);
		} catch (IOException e) {
			System.out.println("Erreur lors du handshake SMTP");
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
		sendCommand("MAIL From: " + envelope.Sender, 250);
		sendCommand("RCPT To: " + envelope.Recipient, 250);
		// TODO : subject, date-time
		sendCommand("DATA", 254);
		sendCommand(envelope.leMessage + CRLF + "." + CRLF, 250);
	}

	/* Fermer la connexion. Envoyer la commande QUIT et fermer la socket.*/
	public void close() {
		isConnected = false;
		try {
			sendCommand("QUIT", 250); /* compléter */
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
		toServer.write(command);
		// toServer.flush();
		System.out.println(command);
		
		// Vérifier que la réponse du serveur est la même que le paramètre
		// rc. Si ce n'est pas le cas, lancer une IOException.
		if (parseReply(fromServer.readLine()) != rc) {
			System.out.println("erreur sendCommand();");
			throw new IOException();
		}
		System.out.println("FIN sendCommand();");
	}

	/* Parser la ligne de réponse du serveur. Retourner le code de réponse. */
	private int parseReply(String reply) {
		/* compléter */
		// le code de réponse est le premier élément de la ligne
		return Integer.parseInt(reply.split(" ")[0]);
	}

	/* Destructeur. Fermer la connexion si qqch anormal est arrivé. */
	protected void finalize() throws Throwable {
		if(isConnected) {
			close();
		}
		super.finalize();
	}
}