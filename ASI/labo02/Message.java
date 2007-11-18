import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.*;

/**
 * Le message email.
 */
public class Message {

	//verifie une adresse email
	public boolean checkMail(String email) {

		// on met tout en minuscule pour simplifier les verification
		email = email.toLowerCase(); 

		// verifie que le mail contienne bien un arobase
		if (!email.contains("@"))
			return false;

		// le mail complet doit comporter au minimum 5 caracteres
		if (email.length()<5)
			return false;

		// verifie que le nom de domaine complet n'excede pas 255 caracteres
		String[] domaine = email.split("@");
		if (domaine[1].length() > 255)
			return false;

		/*Compose l'expression reguliere qui permet de determiner si un email
		 * est bien valide ou non */

		// le nom d'utilisateur doit comporter au moins un caractère
		// il doit comporter des lettres
		String user = "[a-z0-9\\-\\_]++((\\.?[a-z0-9\\-\\_]++)+)?";

		// traiter les sous-domaines
		String host = "[a-z0-9\\-]{2,63}((\\.[a-z0-9\\-]{1,63})?)+"; 
		String dom = "\\.[a-z0-9]{2,6}"; // domaine entre 2 et 6 caractères
		String regexp = user + "@" + host + dom;
		Pattern p = Pattern.compile(regexp); // on prepare l'expression regulière
		Matcher m = p.matcher(email); // insenssible à la casse
		if (m.matches()) { // on verifie que le format est valide
			return true; // ok
		}
		// si on arrive ici, l'email n'est pas valable
		return false;
	}

	// vérifie la validité de plusieurs emails separe par des virgules
	private boolean validMultipleMail(String mails) {

		for (String s :mails.split(",") ) {
			s = s.trim();
			if (!checkMail(s)) {
				System.out.println("Adresse invalide : "+s);
				return false;
			}
		}
		return true;
	}


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
		// on met la priorité au max pour tous les emails envoyes
		Headers += "X-Priority: 1" + CRLF; // methode generale
		Headers += "X-MSMail-Priority: High" + CRLF; // pour les clients microsoft
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

		// verifie la validité de l'email de l'expéditeur
		if (!checkMail(From)) {
			System.out.println("L'adresse expéditeur est invalide");
			return false;
		}

		if (To.length() > 0) {
			if (!validMultipleMail(To)) {
				System.out.println("Le champs 'A:' comporte une adresse email invalide");
				return false;
			}
		}

		if (Cc.length() > 0) {
			if (!validMultipleMail(Cc)) {
				System.out.println("Le champs 'CC:' comporte une adresse email invalide");
				return false;
			}
		}

		if (Bcc.length() > 0) {
			if (!validMultipleMail(Bcc)) {
				System.out.println("Le champs 'BCC:' comporte une adresse email invalide");
				return false;
			}
		}

		// si on passe les tests precedant, le mail est valide
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