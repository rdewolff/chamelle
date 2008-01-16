package rim.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Permet de parser une URI pour la rendre navigable
 * correctement.
 *  - /qqch => http://racine/qqch
 *  - #qqch => supprim�
 *  - /qqch/page.ext => http://racine/qqch/page.ext
 *  - page.ext => http://racine/page.ext
 *  - ...
 *  
 * @author J. Schmid & L. Pr�vost
 */
public class URIPreparer {
	/**
	 * M�thode qui permet de pr�parer les URI dans un format
	 * navigable
	 * @param reference URI de base (page courante par exemple)
	 * @param uriToPrepare URI � parser
	 * @return URI transform�e, null si invalide
	 */
	public static String urlToPrepare(String reference, String uriToPrepare) {
		// URI pr�par�e
		String preparedURI = reference;
		
		// D�tection des ancres
		String[] temp = uriToPrepare.split("#"); 
		
		// URI ne contient pas de chemin exploitable
		if (temp == null || temp.length == 0)
			return preparedURI;
		
		// R�cup�ration partie navigable
		uriToPrepare = temp[0];
		
		// Pr�paration d'un pattern pour d�tecter si un fichier 
		// est pr�sent dans l'URI ou si c'est un r�pertoire
		Pattern file = Pattern.compile("//.*/.*\\..*$");
		Matcher fileMatcher = file.matcher(reference);
		
		// L'URI est vide
		if (uriToPrepare.length() == 0)
			preparedURI = null;
		
		// URI d�bute par HTTP, pas de transformation suppl�mentaire
		else if (uriToPrepare.startsWith("http://"))
			preparedURI = uriToPrepare;
		
		// URI � pr�parer d�bute par un /
		else if (uriToPrepare.startsWith("/")) {
			// V�rifie si l'URI de r�f�rence contient un page ou un r�pertoire
			// Suppression de la page le cas �ch�ant
			if (fileMatcher.find())
				reference = 
					reference.substring(0, reference.lastIndexOf("/") + 1);
			
			// Suppression du http://
			reference = reference.replace("http://", "");
			
			// V�rifie si l'URI de r�f contient /
			// Le cas �ch�ant, suppression du /
			if (reference.contains("/"))
				reference = reference.substring(0, reference.indexOf("/"));
			
			// URI recompos�e du HTTP:// + r�f�rence sans / final + 
			// URI � pr�parer avec / au d�but
			preparedURI = "http://" + reference + uriToPrepare;
		}
		
		// R�f�rence contient une page
		else if (fileMatcher.find())
			// Suppresison de la page et ajout de la page de l'URI � pr�parer
			preparedURI = reference.substring(0, reference.lastIndexOf("/") + 1) 
				+ uriToPrepare;
		
		// R�f�rence est termin�e comme l'URI � pr�parer dans son int�gralit�
		else if (reference.endsWith(uriToPrepare))
			preparedURI = reference;
		
		// Autrement, retourner la concat�nation de la reference avec l'URI
		// � pr�parer en �vitant un double / dans l'URI en dehors du http://
		else 
			preparedURI = reference + 
				(reference.endsWith("/") ? "" : "/") +
				(uriToPrepare.startsWith("/") ? 
					uriToPrepare.substring(1) : uriToPrepare);

		// Traitement des URI relatives (../../..)
		if (preparedURI.contains("..")) {
			preparedURI = preparedURI.replace(reference, "");
			
			// Transformation de l'URI de r�f�rence
			String tempReference = reference.replace("http://", "");
	
			do {
				// Traitement de la remont�e dans les r�pertoires
				if (tempReference.endsWith("/"))
					tempReference = 
						tempReference.substring(0, tempReference.length() - 1);
				
				// Supression sous r�pertoire
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
