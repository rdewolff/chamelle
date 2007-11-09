import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Enveloppe SMTP envelope pour un message email.
 */
public class Envelope {
    /* L'expéditeur du message (ici, contenu de l'entête "De"). */
    public String Sender;

    /* Le destinataire SMTP. */
    public String Recipient;

    /* Le host MX cible. */
    public String DestHost;
    public InetAddress DestAddr;

    /* Le message */
    public Message leMessage;

    /* Créer lenveloppe. */
    public Envelope(Message message) {
	/* Obtenir le serveur SMTP, l'expéditeur et le destinataire. */
	DestHost = message.getSMTPServer();
	Sender = message.getFrom();
	Recipient = message.getTo();

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
       ligne. */
    private Message escapeMessage(Message message) {
	String escapedBody = "";
	String[] parser = message.Body.split("\n");
	
	for (int x=0; x<parser.length; x++) {
	    parser[x] += "\n";
	    if(parser[x].startsWith(".")) { parser[x] += "."; }
	    escapedBody += parser[x];
	}
	message.Body = escapedBody;
	return message;
    }

    /* Pour imprimer l'enveloppe et le message. Seulement pour debug. */
    public String toString() {
	String res = "Expediteur: " + Sender + '\n';
	res += "Destinataire: " + Recipient + '\n';
	res += "Host MX: " + DestHost + ", adresse: " + DestAddr + '\n';
	res += "Message:" + '\n';
	res += leMessage.toString();
	
	return res;
    }
}