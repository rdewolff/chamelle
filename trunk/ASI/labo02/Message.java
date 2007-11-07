import java.util.*;
import java.text.*;

/**
 * Le message email.
 */
public class Message {
    /* Les ent�tes et le corps du message. */
    public String Headers;
    public String Body;

    /* Serveur SMTP, exp�diteur et destinataire. */
    private String SMTPServer;
    private String From;
    private String To;

    /* Pour les fins de ligne. */
    private static final String CRLF = "\r\n";

    /* Cr�er l'objet Message en ins�rant les ent�tes requises par la RFC 2822. */
    public Message(String SMTPServer, String from, String to, String subject, String text) {
	/* Enlever les espaces vides */
	this.SMTPServer = SMTPServer.trim();
	From = from.trim();
	To = to.trim();
	Headers = "From: " + From + CRLF;
	Headers += "To: " + To + CRLF;
	Headers += "Subject: " + subject.trim() + CRLF;

	/* Une approximation du format requis. Seulement GMT. */
	SimpleDateFormat format = 
	    new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
	String dateString = format.format(new Date());
	Headers += "Date: " + dateString + CRLF;
	Body = text;
    }

    /* M�thodes pour acc�der le serveur SMTP, l'exp�diteur et le destinataire */
   
    public String getSMTPServer() {
	return SMTPServer;
    }
    
    public String getFrom() {
	return From;
    }

    public String getTo() {
	return To;
    }


    /* Contr�ler si le message est valide en v�rifiant que l'exp�diteur
       et le destinataire contiennent seulement un caract�re "@". */
    public boolean isValid() {
	int fromAt = From.indexOf('@');
	int toAt = To.indexOf('@');

	if(fromAt < 1 || (From.length() - fromAt) <= 1 || fromAt != From.lastIndexOf('@')) {
	    System.out.println("L'adresse exp�diteur est invalide");
	    return false;
	}
	if(toAt < 1 || (To.length() - toAt) <= 1 || toAt != To.lastIndexOf('@')) {
	    System.out.println("L'adresse destinataire est invalide");
	    return false;
	}	
	return true;
    }
    
    /* Pour imprimer le message. */
    public String toString() {
	String res;

	res = Headers + CRLF;
	res += Body;
	return res;
    }
}