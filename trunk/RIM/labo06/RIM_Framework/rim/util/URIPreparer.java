package rim.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Permet de parser une URI pour la rendre navigable
 * correctement.
 *  - /qqch => http://racine/qqch
 *  - #qqch => supprime
 *  - /qqch/page.ext => http://racine/qqch/page.ext
 *  - page.ext => http://racine/page.ext
 *  - ...
 *  
 * @author J. Schmid & L. Prevost
 */
public class URIPreparer {
	/**
	 * Methode qui permet de preparer les URI dans un format
	 * navigable
	 * @param reference URI de base (page courante par exemple)
	 * @param uriToPrepare URI a parser
	 * @return URI transformee, null si invalide
	 */
	public static String urlToPrepare(String reference, String uriToPrepare) {
		// URI preparee
		String preparedURI = reference;
		
		// Detection des ancres
		String[] temp = uriToPrepare.split("#"); 
		
		// URI ne contient pas de chemin exploitable
		if (temp == null || temp.length == 0)
			return preparedURI;
		
		// Recuperation partie navigable
		uriToPrepare = temp[0];
		
		// Preparation d'un pattern pour detecter si un fichier 
		// est present dans l'URI ou si c'est un repertoire
		Pattern file = Pattern.compile("//.*/.*\\..*$");
		Matcher fileMatcher = file.matcher(reference);
		
		// L'URI est vide
		if (uriToPrepare.length() == 0)
			preparedURI = null;
		
		// URI debute par HTTP, pas de transformation supplementaire
		else if (uriToPrepare.startsWith("http://"))
			preparedURI = uriToPrepare;
		
		// URI a preparer debute par un /
		else if (uriToPrepare.startsWith("/")) {
			// Verifie si l'URI de reference contient un page ou un repertoire
			// Suppression de la page le cas echeant
			if (fileMatcher.find())
				reference = 
					reference.substring(0, reference.lastIndexOf("/") + 1);
			
			// Suppression du http://
			reference = reference.replace("http://", "");
			
			// Verifie si l'URI de ref contient /
			// Le cas echeant, suppression du /
			if (reference.contains("/"))
				reference = reference.substring(0, reference.indexOf("/"));
			
			// URI recomposee du HTTP:// + reference sans / final + 
			// URI a preparer avec / au debut
			preparedURI = "http://" + reference + uriToPrepare;
		}
		
		// Reference contient une page
		else if (fileMatcher.find())
			// Suppresison de la page et ajout de la page de l'URI a preparer
			preparedURI = reference.substring(0, reference.lastIndexOf("/") + 1) 
				+ uriToPrepare;
		
		// Reference est terminee comme l'URI a preparer dans son integralite
		else if (reference.endsWith(uriToPrepare))
			preparedURI = reference;
		
		// Autrement, retourner la concatenation de la reference avec l'URI
		// a preparer en evitant un double / dans l'URI en dehors du http://
		else 
			preparedURI = reference + 
				(reference.endsWith("/") ? "" : "/") +
				(uriToPrepare.startsWith("/") ? 
					uriToPrepare.substring(1) : uriToPrepare);

		// Traitement des URI relatives (../../..)
		if (preparedURI.contains("..")) {
			preparedURI = preparedURI.replace(reference, "");
			
			// Transformation de l'URI de reference
			String tempReference = reference.replace("http://", "");
	
			do {
				// Traitement de la remontee dans les repertoires
				if (tempReference.endsWith("/"))
					tempReference = 
						tempReference.substring(0, tempReference.length() - 1);
				
				// Supression sous repertoire
				if (tempReference.contains("/"))
					tempReference = tempReference.substring(0, 
						tempReference.lastIndexOf("/") + 1);

				// Supression d'un niveau
				if (preparedURI.startsWith("../"))
					preparedURI = preparedURI.substring(3);
			} while (preparedURI.startsWith("../"));
			
			// Finalisation
			preparedURI = reference + "/" + preparedURI;
		}
		
		return preparedURI;
	}
	
}
