//import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Test {

	private static boolean validMail(String email) {

		// il ne faut pas d'espace
		if (email.contains(" "))
			return false;

		// il faut un arobase
		if (!email.contains("@"))
			return false;

		// verifie qu'il n'y ait pas plus de 1 arobase
		if (email.indexOf("@") != email.lastIndexOf("@"))
			return false;

		// il ne doit pas être le dernier caractère
		if (email.lastIndexOf("@") == email.length()-1)
			return false; 



		// on coupe l'email en deux parties à analyser
		String[] emailDec = email.split("@");
		String emailUser = emailDec[0];
		String emailHost = emailDec[1];
		System.out.println("user:"+emailUser+"\nhost:"+emailHost);

		// verifie la longueur du user
		if (emailUser.length() < 1)
			return false;

		// TODO : verifie que le nom de domaine de premier niveau contienne
		// entre 2 et 6 caractères		 (le " .com   ")

		// seul les caractères alphanumériques et le '-' sont autorisé pour le host
		//if (!emailHost.matches("[a-zA-Z0-9]")) // \\.\\-]+@")) {
		//	return false;
		/*
		// this works!
		Pattern p;
		Matcher m;

		//p = Pattern.compile("[a-zA-Z]|\\-");
		p = Pattern.compile("[a-z0-9\\-_\\.]++@[a-z0-9\\-]++(\\.[a-z0-9\\-]++)++");
		m = p.matcher(emailHost);
		if (m.find()) {
			System.out.println("erreur");
			return false;
		}
		 */	

		/*
		p = Pattern.compile("^www\\.");
		m = p.matcher(emailUser);

		if (m.find()) {
			System.out.println("Email addresses don't start" +
			" with \"www.\", only web pages do.");
		}
		 */
		// du style
		// boolean isMailValid = Pattern.matches("a*b", email);


		return true;
	}

	

	private static String getSecondDomain(String host) {

		String domaine;
		String domaine2;

		// si le domaine contient un point on determine le deomaine secondaire
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
	
	// verifie une adresse email
	private static boolean checkMail(String email) {
		
		// on met tout en minuscule pour simplifier les verification
		email = email.toLowerCase(); 
		
		// verifie que le mail contienne bien un arobase
		if (!email.contains("@"))
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
		String host = "[a-z0-9\\-]{1,63}((\\.[a-z0-9\\-]{1,63})?)+"; 
		String dom = "\\.[a-z0-9]{2,6}"; // domaine entre 2 et 6 caractères
		String regexp = user + "@" + host + dom;
		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(email); // insenssible à la casse
		if (m.matches()) {
			return true;
		}
		return false;
	}
	
	public static void main (String args[]) {

		if (checkMail("romain.de-wolff@public.toilette.heig-vd.ch")) {
			System.out.println("Adresse E-Mail OK");
		} else {
			System.out.println("Adresse E-Mail INVALIDE");
		}

		
		System.out.println("\n" + getSecondDomain("a.b.c.net"));

	}
}