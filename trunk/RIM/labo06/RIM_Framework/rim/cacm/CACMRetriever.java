package rim.cacm;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import rim.Retriever;
import rim.index.Index;

/**
 * A retriever for the CACM collection.
 */
public class CACMRetriever implements Retriever {
	// Index de recherche
	Index index;
	
	HashSet<String> commonWords;
	
	/**
	 * Constructeur
	 * @param idx Index pour la recherche
	 * @param commonWords Liste des mots communs
	 */
	public CACMRetriever(Index idx, HashSet<String> commonWords) {
		index = idx;
		this.commonWords = commonWords;
	}
	
	/* (non-Javadoc)
	 * @see rim.Retriever#searchDocument(java.lang.Integer)
	 */
	public Map<String, Double> searchDocument(String uri) {
		return index.findTerms(uri);
	}

	/* (non-Javadoc)
	 * @see rim.Retriever#searchTerm(java.lang.String)
	 */
	public Map<String, Double> searchTerm(String term) {
		return index.findDocs(term);
	}

	/* (non-Javadoc)
	 * @see rim.Retriever#executeQuery(java.lang.String)
	 */
	public Map<Double, String> executeQuery(String query) {
		// "Vecteur" requete
		HashMap<String, Double> pQuery = new HashMap<String, Double>();
		
		// Preparation du contenu
		String[] contentQuery = CACMUtil.contentPreparer(query, commonWords);
		
		// Calcul de frequence
		double freq = 1.0;
	
		// Comptage des termes par document et suppression des mots communs
		for (int i = 0; i < contentQuery.length; i++) {
			// Comptage
			if (i+1 < contentQuery.length && 
					contentQuery[i].equalsIgnoreCase(contentQuery[i+1])) {
				freq += 1.0;
			}
			else {
				// Ajout du terme / document a l'index
				pQuery.put(contentQuery[i], freq);
				freq = 1.0;
			}
		}
		
		return querySimilarities(pQuery);
	}
	
	/**
	 * Determine les documents similaires a la requete
	 * fournie en parametres
	 * @param pQuery Requete demandee
	 * @return Documents classes par similarite
	 */
	private Map<Double, String> 
		querySimilarities(HashMap<String, Double> pQuery) {
		
		// Tous les documents qui contiennent 
		// au moins un terme de la requete
		Map<String, Double> docs = new TreeMap<String, Double>();
		
		// Documents avec degre de similarite par 
		// rapport a la requete
		Map<Double, String> sims = new TreeMap<Double, String>(
			// Classe anonyme pour le tri de l'arbre
			new Comparator<Double>() {
				public int compare(Double a, Double b) {
					return a < b ? 1 : a > b ? -1 : 0;
				}
			}
		);
		
		// Somme des carres des frequences 
		// des termes de la requete
		double ssq = 0.0;

		// Recherche des documents qui contiennent au moins 
		// un des termes present dans la requete
		// et calcul du ssq
		for (Entry<String, Double> qItem : pQuery.entrySet()) {
			Map<String, Double> tempResult = searchTerm(qItem.getKey());
			if (tempResult != null)
				docs.putAll(tempResult);

			// Calcul de ssq
			ssq += pow(qItem.getValue(), 2.0);
		}
				
		// Pour chaque document contenant au moins un terme
		// on calcul spdq et ssd
		for (String uri : docs.keySet()) {
			// Somme des produit des termes des 
			// vecteurs de document et requete
			double spdq = 0.0;
			
			// Somme des carres des frequences des termes
			// du document
			double ssd = 0.0;
			
			// Pour chaque terme du document
			for (Entry<String, Double> termFreq : 
				searchDocument(uri).entrySet()) {
				
				// Terme present dans la requete, ajoute le produit
				// du terme document et du terme requete
				// (frequence ponderee * frequence)
				if (pQuery.containsKey(termFreq.getKey()))
					spdq += termFreq.getValue() * 
						pQuery.get(termFreq.getKey());
			
				// Ajoute le carre de la frequence 
				// ponderee du document
				ssd += pow(termFreq.getValue(), 2.0);
			}

			// Ajoute la similarite relative au 
			// document courrant
			sims.put(spdq / sqrt(ssd * ssq), uri);
		}
		
		return sims;
	}
}
