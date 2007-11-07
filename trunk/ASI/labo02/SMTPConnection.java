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

    /* Flux pour lire et �crire dans la socket */
    public BufferedReader fromServer;
    public DataOutputStream toServer;

    /* Port SMTP et fin de ligne */
    private static final int SMTP_PORT = 25;
    private static final String CRLF = "\r\n";

    /* Sommes-nous connect�s? Utilis� dans close() pour d�terminer que faire. */
    private boolean isConnected = false;

    /* Constructeur. Cr�er la socket et les flux associ�s. 
       Envoyer la commande HELO et contr�ler s'il y a des erreurs. */
    public SMTPConnection(Envelope envelope) throws IOException {
	// socketConnection = /* compl�ter */;
	fromServer = /* compl�ter */;
	toServer = /* compl�ter */;

	// Lire une ligne du serveur et v�rifier que le code de r�ponse est 220.
	/* compl�ter */
	
	// Echange (handshake) SMTP. Nous avons besoin du nom de la machine
	// locale. Envoyer la commande SMTP initiale appropri�e.
	String localhost = /* compl�ter */;
	try {
	    sendCommand(/* compl�ter */);
	} catch (IOException e) {
	    System.out.println(/* compl�ter */);
	    return;
	}
	isConnected = true;
    }

    /* Envoyer le message. Ecrire simplement les commandes SMTP correctes
       dans le bon ordre. Pas de contr�le d'erreur, on les lance vers
       l'appelant... */
    public void send(Envelope envelope) throws IOException {
	// Envoyer toutes les commandes n�cessaires pour envoyer un
	// message. Appeler sendCommand() pour faire le travail. Ne pas
	// attraper d'exception lanc�e par sendCommand() ici � l'int�rieur.
	/* compl�ter */
	
    }

    /* Fermer la connexion. Envoyer la commande QUIT et fermer la socket.*/
    public void close() {
	isConnected = false;
	try {
	    sendCommand(/* compl�ter */);
	    socketConnection.close();
	} catch (IOException e) {
	    System.out.println("Impossible de fermer la connexion: " + e);
	    isConnected = true;
	}
    }

    /* Envoyer une commande SMTP au serveur. Contr�ler le code de r�ponse.
       Ne contr�le pas les codes de r�ponse multiples (requis pour RCPT TO). */
    private void sendCommand(String command, int rc) throws IOException {
	// Ecrire la commande au serveur et lire la r�ponse du serveur
	/* compl�ter */
	
	// V�rifier que la r�ponse du serveur est la m�me que le param�tre
	// rc. Si ce n'est pas le cas, lancer une IOException.
	/* compl�ter */

    }

    /* Parser la ligne de r�ponse du serveur. Retourner le code de r�ponse. */
    private int parseReply(String reply) {
	/* compl�ter */
	
    }

    /* Destructeur. Fermer la connexion si qqch anormal est arriv�. */
    protected void finalize() throws Throwable {
	if(isConnected) {
	    close();
	}
	super.finalize();
    }
}