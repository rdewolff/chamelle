package rim.cacm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe utilitaire pour le package CACM
 * @author J. Schmid & L. Prévost
 */
public final class CACMUtil {
	/**
	 * Permet de préparer un contenu pour le nettoyer
	 * des mots communs et supprimer la ponctuation
	 * @param content Contenu à nettoyer
	 * @param commonWords Mots communs de référence
	 * @return Tableau des mots prêt à être exploité
	 */
	protected static String[] contentPreparer(String content, 
		HashSet<String> commonWords) {
		
		// Contenu à traiter convertit en minuscule
		String contentTemp = new String(content.toLowerCase());
		
		// Traitement des caractères de ponctuation et des espaces consécutifs
		contentTemp = contentTemp.replaceAll("\\p{Punct}", " ");
		contentTemp = contentTemp.replaceAll(" {1,}", " ").trim();
	
		// Séparation des mots
		List<String> contentTerms = Arrays.asList(contentTemp.split(" "));
		
		// Liste temporaire pour les mots valides
		List<String> validTerms = new LinkedList<String>();

		// Vérification des mots valides
		for (String t : contentTerms)
			if (!commonWords.contains(t.trim()))
				validTerms.add(t.trim());
		
		// Préparation du retour des termes valides
		String[] orderedTerms = new String[validTerms.size()]; 
		orderedTerms = validTerms.toArray(orderedTerms);
		
		// Tri des termes valides
		Arrays.sort(orderedTerms);
		
		return orderedTerms;
	}
}
