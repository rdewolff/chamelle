import java.util.*;
import java.text.*;

import sun.misc.Regexp;

/**
 * Le message email.
 */
public class Message {
	
	/* Les entêtes et le corps du message */
	public String Headers;
	public String Body;

	/* Serveur SMTP, expéditeur et destinataires */
	private String SMTPServer;
	private String From;
	private String To;
	// champs pour le CC et le BCC
	private String Cc;
	private String Bcc;

	/* Pour les fins de ligne. */
	private static final String CRLF = "\r\n";

	/* Créer l'objet Message en insérant les entêtes requises par la RFC 2822. */
	public Message(
			String SMTPServer, 
			String from, 
			String to, 
			String cc,
			String bcc,
			String subject, 
			String text) {
		
		/* Enlever les espaces vides */
		this.SMTPServer = SMTPServer.trim();
		From = from.trim();
		To = to.trim();
		this.Cc = cc; 
		this.Bcc = bcc;
		
		Headers = "From: " + From + CRLF;
		Headers += "To: " + To + CRLF;
		// si il y a des Carbon Copy, on les ajoutes
		if (cc.trim().length() != 0) {
			Headers += "Cc: " + cc + CRLF;
		}
		Headers += "Subject: " + subject.trim() + CRLF;
		// on met la priorité au max pour tous les emails
		Headers += "X-Priority: 1" + CRLF;
		Headers += "X-MSMail-Priority: High" + CRLF; 
		// on ajoute le nom du client email
		Headers += "X-Mailer: deWolff Mail Client 0.1" + CRLF;
		
		/* Une approximation du format requis. Seulement GMT. */
		SimpleDateFormat format = 
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
		String dateString = format.format(new Date());
		Headers += "Date: " + dateString + CRLF;
		
		// si le message contient la balise <HTML>, le type de message est HTML
		if ((text.toLowerCase()).indexOf("<html>") == -1) {
			Headers += "Content-Type: text/plain;";
		} else {
			Headers += "Content-Type: text/html;";
		}

		Body = text;
	}

	/* Méthodes pour accéder le serveur SMTP, l'expéditeur et le destinataire */

	public String getSMTPServer() {
		return SMTPServer;
	}

	public String getFrom() {
		return From;
	}

	public String getTo() {
		return To;
	}
	
	public String getCc() {
		return Cc;
	}
	
	public String getBcc() {
		return Bcc;
	}

	/* Contrôler si le message est valide en vérifiant que l'expéditeur
       et le destinataire contiennent seulement un caractère "@". */
	public boolean isValid() {
		
		
		// Regexp reg2 = "^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$";
		//Regexp reg = "/^[a-zA-Z0-9._-]+@([a-zA-Z0-9.-]+\.)+[a-zA-Z0-9.-]{2,4}$/";
		//  if (regexp.test(emailAdrr)) alert('the adress is valid');
		//  else alert('the adress is invalid');
		
		int fromAt = From.indexOf('@');
		int toAt = To.indexOf('@');

		if(fromAt < 1 || (From.length() - fromAt) <= 1 || fromAt != From.lastIndexOf('@')) {
			System.out.println("L'adresse expéditeur est invalide");
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