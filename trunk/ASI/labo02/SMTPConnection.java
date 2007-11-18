import java.net.*;
import java.io.*;

/**
 * Ouvrir une connexion SMTP vers une machine distante et envoyer des messages
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
		fromServer = new BufferedReader(
				new InputStreamReader(socketConnection.getInputStream()));
		toServer = new DataOutputStream(
				new DataOutputStream(socketConnection.getOutputStream()));

		// la connexiona avec le serveur est établie, vérifie que le code est 
		// bien le 220
		if (parseReply(fromServer.readLine()) != 220) {
			throw new IOException("Erreur de connection avec le serveur SMTP");
		}

		// Echange (handshake) SMTP. Nous avons besoin du nom de la machine
		// locale. Envoyer la commande SMTP initiale appropriée.
		String localhost = (InetAddress.getLocalHost()).getHostAddress(); 
		try {
			sendCommand("HELO " + localhost, 250, 0);
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

		// l'adresse de l'expediteur
		sendCommand("MAIL FROM:" + envelope.Sender, 250, 0);

		// plusieurs destinataires possibles (code retourne soit 250 soit 251)
		for (String cc : (envelope.Recipient.split(","))) {
			sendCommand("RCPT TO:" + cc.trim(), 250, 251); 
		}

		// les CC
		if (envelope.Cc.length() > 0) {
			for (String cc : (envelope.Cc.split(","))) {
				sendCommand("RCPT TO:" + cc.trim(), 250, 251); 
			}
		}
		// idem avec BCC
		if (envelope.Bcc.length() > 0) {
			for (String bcc : (envelope.Bcc.split(","))) {
				sendCommand("RCPT TO:" + bcc.trim(), 250, 251); 
			}
		}
		// les données
		sendCommand("DATA", 354, 0);
		sendCommand(envelope.leMessage + CRLF + ".", 250, 0);
	}

	/* Fermer la connexion. Envoyer la commande QUIT et fermer la socket.*/
	public void close() {
		isConnected = false;
		try {
			sendCommand("QUIT", 221, 0); /* compléter */
			socketConnection.close();
		} catch (IOException e) {
			System.out.println("Impossible de fermer la connexion: " + e);
			isConnected = true;
		}
	}

	/* Envoyer une commande SMTP au serveur. Contrôler le code de réponse, il doit
	 * etre RC et eventuellement RC2.
	 * Si RC2 n'est pas utilise, il faut lui donner la valeur 0 */
	private void sendCommand(String command, int rc, int rc2) throws IOException {

		// Ecrire la commande au serveur et lire la réponse du serveur
		toServer.writeBytes(command + CRLF);

		// affiche dans la console la commande
		System.out.println(command); 

		// Vérifier que la réponse du serveur est la même que le paramètre
		// rc. Si ce n'est pas le cas, lancer une IOException.
		int codeReponse = parseReply(fromServer.readLine());
		if (rc2 != 0) {
			if (codeReponse != rc && codeReponse != rc2)
				throw new IOException();
		} else {
			if (codeReponse != rc) 
				throw new IOException();
		}
	}

	/* Parser la ligne de réponse du serveur. Retourner le code de réponse. */
	private int parseReply(String reply) {
		// le code de réponse est le premier élément de la ligne
		
		// affiche le code de réponse du serveur
		System.out.println("Code de status: " + reply.split(" ")[0]); 
		
		// retourne la valeur
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