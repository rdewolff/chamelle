package rim.cacm;

import java.util.HashSet;

import rim.Indexer;
import rim.index.Index;

/**
 * An indexer for the CACM collection.
 */
public class CACMIndexer implements Indexer {
	// Index des documents
	Index index;
	
	// Mots communs pour élimination
	HashSet<String> commonWords;
	
	/**
	 * Constructeur
	 * @param idx Index à compléter
	 * @param commonWords Liste des mots communs
	 */
	public CACMIndexer(Index idx, HashSet<String>commonWords) {
		index = idx;
		this.commonWords = commonWords;
	}
	
	/* (non-Javadoc)
	 * @see rim.Indexer#index(java.lang.Integer, java.lang.String)
	 */
	public void index(String uri, String content) {
		// Préparation du contenu
		String[] contentWords = 
			CACMUtil.contentPreparer(content, commonWords);
		
		// Calcul de fréquence
		double freq = 1.0;
	
		// Comptage des termes par document et suppression des mots communs
		for (int i = 0; i < contentWords.length; i++) {
			// Comptage
			if (i+1 < contentWords.length && 
				contentWords[i].equalsIgnoreCase(contentWords[i+1])) {
				freq += 1.0;
			}
			else {
				// Ajout du terme / document à l'index
				index.add(uri, contentWords[i], freq);
				freq = 1.0;
			}
		}
	}

	/* (non-Javadoc)
	 * @see rim.Indexer#finalizeIndexation()
	 */
	public void finalizeIndexation() {
		index.calculate();
	}
}
