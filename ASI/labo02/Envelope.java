import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Enveloppe SMTP envelope pour un message email.
 */
public class Envelope {
	/* L'expéditeur du message (ici, contenu de l'entête "De"). */
	public String Sender;

	/* Les destinataires SMTP. */
	public String Recipient;
	public String Cc; // carbon copie(s)
	public String Bcc; // blind carbon copie(s)
 
	/* Le host MX cible. */
	public String DestHost;
	public InetAddress DestAddr;

	/* Le message */
	public Message leMessage;

	/* Retourne domaine secondaire d'un host passé en parametre. Si le host
	 * ne comporte pas de point, il sera retourne tel quel, sans modification.
	 *  
	 * Exemple : a.b.c.com  : retourne c.com
	 * 			 abc.com    : retourne abc.com
	 * 			 pc1		: retourne pc1
	 */
	private String getSecondDomain(String host) {

		String domaine;
		String domaine2;
 
		// si le domaine contient un point on determine le domaine secondaire
		if (host.contains(".")) {
			// prend le dernier point jusqu'à la fin du host (ex: .ch)
			domaine =  host.substring(host.lastIndexOf('.'), host.length() );
			// on cherche l'avant dernier point dans le host
			domaine2 = host.substring(0, host.lastIndexOf('.'));
			domaine2 = domaine2.substring(domaine2.lastIndexOf('.')+1, domaine2.length());
			// on construit le domaine secondaire a partir des 2 informations
			domaine = domaine2 + domaine;

		} else {
			// sinon on retourne simplement le domaine passé en parametre
			domaine = host;
		}

		return domaine;
	}
	
	/* Créer l'enveloppe. */
	public Envelope(Message message) {
		/* Obtenir le serveur SMTP, l'expéditeur et le destinataire. */
		DestHost = message.getSMTPServer();

		// on va determiner le nom du user a partir de son nom d'utilisateur 
		// ainsi que le nom de domaine du second niveau du serveur SMTP
		String host = getSecondDomain(message.getSMTPServer());

		Sender = System.getProperty("user.name") + "@" + host ; 
		// Sender = message.getFrom();  // autre methode plus utilisee

		Recipient = message.getTo();
		Cc = message.getCc();
		Bcc = message.getBcc();

		/* Obtenir le message. Le parser et adapter pour être sur que 
	   il n'y a pas de point unique sur une ligne. Cela crérait un
	   problème lors le l'envoi de l'email. */
		leMessage = escapeMessage(message);

		/* Obtenir l'adresse IP du host MX */
		try {
			DestAddr = InetAddress.getByName(DestHost);
		} catch (UnknownHostException e) {
			System.out.println("Unknown host: " + DestHost);
			System.out.println(e);
			return;
		}
		return;
	}

	/* Parser le message et doubler tous les points au début d'une
       ligne, pour éviter que le serveur recoivent une fin de message
 	   avant que voulu */
	private Message escapeMessage(Message message) {
		
		String RetourLigne = "\n";
		String escapedBody = "";
		String[] parser = message.Body.split(RetourLigne);
		
		// passe en revue toutes les lignes du message 
		for (int x=0; x<parser.length; x++) {
			// si la ligne commence par un point, on duplique le point 
			if(parser[x].startsWith("."))
				parser[x] = ".." + parser[x].substring(1); 
			// ajoute le retour a la ligne
			parser[x] += RetourLigne;
			// ajoute dans le corps de message temporaire
			escapedBody += parser[x];
			
		}
		message.Body = escapedBody;
		return message;
	}

	/* Pour imprimer l'enveloppe et le message. Pour debug. */
	public String toString() {
		String res = "Expediteur: " + Sender + '\n';
		res += "Destinataire(s): " + Recipient + '\n';
		res += "Cc:" + Cc + '\n';
		res += "Host MX: " + DestHost + ", adresse: " + DestAddr + '\n';
		res += "Message:" + '\n';
		res += leMessage.toString();

		return res;
	}
}