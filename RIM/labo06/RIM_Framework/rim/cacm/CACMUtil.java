package rim.cacm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe utilitaire pour le package CACM
 * @author J. Schmid & L. Pr�vost
 */
public final class CACMUtil {
	/**
	 * Permet de pr�parer un contenu pour le nettoyer
	 * des mots communs et supprimer la ponctuation
	 * @param content Contenu � nettoyer
	 * @param commonWords Mots communs de r�f�rence
	 * @return Tableau des mots pr�t � �tre exploit�
	 */
	protected static String[] contentPreparer(String content, 
		HashSet<String> commonWords) {
		
		// Contenu � traiter convertit en minuscule
		String contentTemp = new String(content.toLowerCase());
		
		// Traitement des caract�res de ponctuation et des espaces cons�cutifs
		contentTemp = contentTemp.replaceAll("\\p{Punct}", " ");
		contentTemp = contentTemp.replaceAll(" {1,}", " ").trim();
	
		// S�paration des mots
		List<String> contentTerms = Arrays.asList(contentTemp.split(" "));
		
		// Liste temporaire pour les mots valides
		List<String> validTerms = new LinkedList<String>();

		// V�rification des mots valides
		for (String t : contentTerms)
			if (!commonWords.contains(t.trim()))
				validTerms.add(t.trim());
		
		// Pr�paration du retour des termes valides
		String[] orderedTerms = new String[validTerms.size()]; 
		orderedTerms = validTerms.toArray(orderedTerms);
		
		// Tri des termes valides
		Arrays.sort(orderedTerms);
		
		return orderedTerms;
	}
}
