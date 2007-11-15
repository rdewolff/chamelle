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

		// ouvre la connection avec le serveur distant
		socketConnection = new Socket(envelope.DestHost, SMTP_PORT);

		// défini les flux entrant et sortant
		fromServer = new BufferedReader(new InputStreamReader(socketConnection.getInputStream()));
		toServer = new DataOutputStream(new DataOutputStream(socketConnection.getOutputStream()));

		// la connexiona avec le serveur est établie, vérifie que le code est bien le 220
		if (parseReply(fromServer.readLine()) != 220) {
			throw new IOException("Erreur de connection avec le serveur SMTP");
		}

		// Echange (handshake) SMTP. Nous avons besoin du nom de la machine
		// locale. Envoyer la commande SMTP initiale appropriée.
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
		sendCommand("MAIL FROM:" + envelope.Sender, 250);
		sendCommand("RCPT TO:" + envelope.Recipient, 250); // TODO : ou 251 si destinaire pas local 

		if (envelope.Cc.length() != 0) {
			StringTokenizer ccToken = new StringTokenizer(envelope.Cc,",");
			while(ccToken.hasMoreTokens()){
				System.out.println(ccToken.nextToken());
			}
		}

		// TODO message a destinataires multiples
		sendCommand("DATA", 354);
		sendCommand(envelope.leMessage + CRLF + ".", 250);
	}

	/* Fermer la connexion. Envoyer la commande QUIT et fermer la socket.*/
	public void close() {
		isConnected = false;
		try {
			sendCommand("QUIT", 221); /* compléter */
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
		toServer.writeBytes(command + CRLF);

		// pour debug, affiche dans la console
		System.out.println(command); 

		// Vérifier que la réponse du serveur est la même que le paramètre
		// rc. Si ce n'est pas le cas, lancer une IOException.
		if (parseReply(fromServer.readLine()) != rc) {
			throw new IOException();
		}
	}

	/* Parser la ligne de réponse du serveur. Retourner le code de réponse. */
	private int parseReply(String reply) {
		/* compléter */
		// le code de réponse est le premier élément de la ligne
		System.out.println("réponse : " + reply.split(" ")[0]);
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