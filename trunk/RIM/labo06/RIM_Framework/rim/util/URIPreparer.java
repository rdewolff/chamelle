package rim.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Permet de parser une URI pour la rendre navigable
 * correctement.
 *  - /qqch => http://racine/qqch
 *  - #qqch => supprimé
 *  - /qqch/page.ext => http://racine/qqch/page.ext
 *  - page.ext => http://racine/page.ext
 *  - ...
 *  
 * @author J. Schmid & L. Prévost
 */
public class URIPreparer {
	/**
	 * Méthode qui permet de préparer les URI dans un format
	 * navigable
	 * @param reference URI de base (page courante par exemple)
	 * @param uriToPrepare URI à parser
	 * @return URI transformée, null si invalide
	 */
	public static String urlToPrepare(String reference, String uriToPrepare) {
		// URI préparée
		String preparedURI = reference;
		
		// Détection des ancres
		String[] temp = uriToPrepare.split("#"); 
		
		// URI ne contient pas de chemin exploitable
		if (temp == null || temp.length == 0)
			return preparedURI;
		
		// Récupération partie navigable
		uriToPrepare = temp[0];
		
		// Préparation d'un pattern pour détecter si un fichier 
		// est présent dans l'URI ou si c'est un répertoire
		Pattern file = Pattern.compile("//.*/.*\\..*$");
		Matcher fileMatcher = file.matcher(reference);
		
		// L'URI est vide
		if (uriToPrepare.length() == 0)
			preparedURI = null;
		
		// URI débute par HTTP, pas de transformation supplémentaire
		else if (uriToPrepare.startsWith("http://"))
			preparedURI = uriToPrepare;
		
		// URI à préparer débute par un /
		else if (uriToPrepare.startsWith("/")) {
			// Vérifie si l'URI de référence contient un page ou un répertoire
			// Suppression de la page le cas échéant
			if (fileMatcher.find())
				reference = 
					reference.substring(0, reference.lastIndexOf("/") + 1);
			
			// Suppression du http://
			reference = reference.replace("http://", "");
			
			// Vérifie si l'URI de réf contient /
			// Le cas échéant, suppression du /
			if (reference.contains("/"))
				reference = reference.substring(0, reference.indexOf("/"));
			
			// URI recomposée du HTTP:// + référence sans / final + 
			// URI à préparer avec / au début
			preparedURI = "http://" + reference + uriToPrepare;
		}
		
		// Référence contient une page
		else if (fileMatcher.find())
			// Suppresison de la page et ajout de la page de l'URI à préparer
			preparedURI = reference.substring(0, reference.lastIndexOf("/") + 1) 
				+ uriToPrepare;
		
		// Référence est terminée comme l'URI à préparer dans son intégralité
		else if (reference.endsWith(uriToPrepare))
			preparedURI = reference;
		
		// Autrement, retourner la concaténation de la reference avec l'URI
		// à préparer en évitant un double / dans l'URI en dehors du http://
		else 
			preparedURI = reference + 
				(reference.endsWith("/") ? "" : "/") +
				(uriToPrepare.startsWith("/") ? 
					uriToPrepare.substring(1) : uriToPrepare);

		// Traitement des URI relatives (../../..)
		if (preparedURI.contains("..")) {
			preparedURI = preparedURI.replace(reference, "");
			
			// Transformation de l'URI de référence
			String tempReference = reference.replace("http://", "");
	
			do {
				// Traitement de la remontée dans les répertoires
				if (tempReference.endsWith("/"))
					tempReference = 
						tempReference.substring(0, tempReference.length() - 1);
				
				// Supression sous répertoire
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
