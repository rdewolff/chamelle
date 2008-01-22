package rim.cacm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe utilitaire pour le package CACM
 * @author J. Schmid & L. Prevost
 */
public final class CACMUtil {
	/**
	 * Permet de preparer un contenu pour le nettoyer
	 * des mots communs et supprimer la ponctuation
	 * @param content Contenu a nettoyer
	 * @param commonWords Mots communs de reference
	 * @return Tableau des mots pret a etre exploite
	 */
	protected static String[] contentPreparer(String content, 
		HashSet<String> commonWords) {
		
		// Contenu a traiter convertit en minuscule
		String contentTemp = new String(content.toLowerCase());
		
		// Traitement des caracteres de ponctuation et des espaces consecutifs
		contentTemp = contentTemp.replaceAll("\\p{Punct}", " ");
		contentTemp = contentTemp.replaceAll(" {1,}", " ").trim();
	
		// Separation des mots
		List<String> contentTerms = Arrays.asList(contentTemp.split(" "));
		
		// Liste temporaire pour les mots valides
		List<String> validTerms = new LinkedList<String>();

		// Verification des mots valides
		for (String t : contentTerms)
			if (!commonWords.contains(t.trim()))
				validTerms.add(t.trim());
		
		// Preparation du retour des termes valides
		String[] orderedTerms = new String[validTerms.size()]; 
		orderedTerms = validTerms.toArray(orderedTerms);
		
		// Tri des termes valides
		Arrays.sort(orderedTerms);
		
		return orderedTerms;
	}
}
