package rim.index;

import static java.lang.Math.log10;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Gestion de l'index et index inverse
 * @author J. Schmid & L. Prévost
 */
public class Index {
	// Log naturel de 2
	private static double LOG2 = log10(2.0);
	
	// Index inverse
	private GenericIndex<String, String> ridx;
	
	// Index
	private GenericIndex<String, String> idx;
	
	// Type d'index
	private WeightingType type;
	
	/**
	 * Constructeur
	 */
	public Index(WeightingType type) {
		ridx = new GenericIndex<String, String>();
		idx = new GenericIndex<String, String>();
		this.type = type;
	}

	/**
	 * Récupère le type d'index
	 * @return Type de l'index
	 */
	public WeightingType type() {
		return type;
	}
	
	/**
	 * Récupère le nombre d'élément dans
	 * l'index
	 * @return Nombre d'éléments
	 */
	public int idxSize() {
		return idx.size();
	}
	
	/**
	 * Récupère le nombre d'élément dans
	 * l'index inverse
	 * @return Nombre d'éléments
	 */
	public int ridxSize() {
		return ridx.size();
	}

	/**
	 * Ajoute un élément avec sa fréquence aux
	 * index normal et inverse
	 * @param uri Chemin du document
	 * @param term Terme détecter
	 * @param freq Fréquence du terme
	 */
	public void add(String uri, String term, Double freq) {
		idx.put(uri, term, freq);
		ridx.put(term, uri, freq);
	}
	
	/**
	 * Recherche tous les documents contenant
	 * le terme donné
	 * @param termToFind Terme recherché
	 * @return Null si aucun document ne contient
	 * ce terme, autrement tous les documents
	 * le contenant
	 */
	public Map<String, Double> findDocs(String termToFind) {
		return ridx.find(termToFind);
	}
	
	/**
	 * Recherche tous les termes contenu dans un
	 * document donné
	 * @param uri Document recherché
	 * @return Null si aucun terme présent pour le document,
	 * tous les termes dans le cas contraire.
	 */
	public Map<String, Double> findTerms(String uri) {
		return idx.find(uri);
	}
	
	/**
	 * Calcule la pondération de l'index
	 */
	public void calculate() {
		type.finalizeIndexation(this);
	}
	
	/**
	 * Sauvegarde des index normal et inverse
	 * @param path Chemin de destination pour la sauvegarde
	 */
	public void save(String path) {
		idx.save(path, "_idx");
		ridx.save(path, "_ridx");
	}
	
	public String toString() {
		return "Index :\r\n" + idx.toString() + 
			"\r\nReverse Index :\r\n" + ridx.toString();
	}

	/**
	 * Classe interne gérant la structure de données pour
	 * la gestion des index
	 * 
	 * @author J. Schmid & L. Prévost
	 *
	 * @param <ExternalKey> Clé externe 
	 * @param <InternalKey> Clé interne
	 */
	private class GenericIndex<ExternalKey, InternalKey> {
		// Arbre pour le stockage de l'indexe. Contient un 
		// second arbre pour le stockage elément/fréquence
		TreeMap<ExternalKey, TreeMap<InternalKey, Double>> index = 
			new TreeMap<ExternalKey, TreeMap<InternalKey,Double>>();

		/**
		 * Récupère la taille de l'index
		 * @return Taille de l'index
		 */
		public int size() {
			return index.size();
		}
		
		/**
		 * Ajoute un élément dans la structure
		 * @param ek Clé externe
		 * @param ik Clé interne
		 * @param frequence Fréquence d'apparition de la clé interne
		 */
		private void put(ExternalKey ek, InternalKey ik, Double frequence) {
			// Vérifie l'existence de la clé externe pour ajout si nécessaire
			if (!index.containsKey(ek)) {
				index.put(ek, new TreeMap<InternalKey, Double>());
			}
			
			// Ajoute l'élément au contenu de la clé externe
			index.get(ek).put(ik, frequence);
		}
		
		/**
		 * Effectue une recherche sur la clé externe
		 * @param key Clé externe recherchée
		 * @return Null si clé non trouvée, données trouvées 
		 * dans le cas contraire.
		 */
		private Map<InternalKey, Double> find(ExternalKey key) {
			return index.get(key);
		}

		/**
		 * Sauvegarde l'index au chemin voulu
		 * avec un préfixe de son choix
		 * @param path Chemin voulu
		 * @param suffix Préfixe choisi
		 */
		private void save(String path, String suffix) {
			try {
				// Fichier pour l'index
				PrintWriter pw = new PrintWriter(new BufferedWriter(
					new FileWriter(new File(path + type.name() + 
						suffix + ".txt"))));

				// Ecriture des fichiers
				// Création de la sortie formatée
				for (ExternalKey ek : index.keySet()) {
					pw.write("[" + ek + "] {");
					
					for (InternalKey ik : index.get(ek).keySet()) {
						pw.write("<" + ik + "," + index.get(ek).get(ik) + ">");
					}
					
					pw.write("}\r\n");
				}

				// Fermeture des fichiers
				pw.close();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}		
			
		}
	}
	
	/**
	 * Enumeration pour représenter les différents types de pondération PS : 
	 * Les méthodes sont décrites que pour leur définition abstraite.
	 * @author   Laurent Prévost
	 */
	public enum WeightingType {
		// Pondération TF
		tf {
			protected void finalizeIndexation(Index index) {
				// Raccourcis d'écriture
				GenericIndex<String, String> idx = index.idx;
				GenericIndex<String, String> ridx = index.ridx;
				
				// Chaque document de l'index
				for (String eKey : idx.index.keySet()) {
					// Trouve le terme le plus frèquent dans le document et
					// récupère la valeur de sa fréquence dans le document
					double max = Collections.max(idx.index.get(eKey).values());
					
					// Calcul et maj de la fréquence pondérée de chaque terme en 
					// rapport au plus fréquent trouvé précédement
					for (String iKey : idx.index.get(eKey).keySet()) {
						// Calcul tf normalisé
						double tf = idx.index.get(eKey).get(iKey) / max;
						
						// Modification index et index inversé
						idx.index.get(eKey).put(iKey, tf); 
						ridx.index.get(iKey).put(eKey, tf);
					}
				}
			}
		},
		
		// Pondération TF-IDF
		tfidf {
			protected void finalizeIndexation(Index index) {
				// Raccourcis d'écriture
				GenericIndex<String, String> idx = index.idx;
				GenericIndex<String, String> ridx = index.ridx;
				
				// Nombre de documents et de termes de la collection
				double D = idx.index.size();
				
				// Chaque document de l'index
				for (String eKey : idx.index.keySet()) {
					// tf-idf maximal lors du calcul
					double maxtfidf = 0.0;
					
					// Chaque terme du document
					for (String iKey : idx.index.get(eKey).keySet()) {					
						// Calcul tf-idf (non normalisé)
						// tf = log2(frequenceDuTermeDsDocCt + 1)
						// idf = log(nbDocumentsCollection / 
						//       nbDocumentPourLeTermeChoisi)
						// tf-idf = tf * idf
						double tfidf = 
							(log10(idx.index.get(eKey).get(iKey) + 1.0) / LOG2)
							* (log10(D / ridx.index.get(iKey).size()) / LOG2);

						// Calcul du tfidf maximal
						if (tfidf > maxtfidf) maxtfidf = tfidf;
					
						// Modification de la fréquence du terme choisi
						// pour le document courrant
						idx.index.get(eKey).put(iKey, tfidf);
					}

					// Normalisation des résultats pour le document courrant
					for (String iKey : idx.index.get(eKey).keySet()) {
						// Normalisation
						double tfIdfNorm = 
							idx.index.get(eKey).get(iKey) / maxtfidf;
					
						// Modification index et index inversé
						idx.index.get(eKey).put(iKey, tfIdfNorm);
						ridx.index.get(iKey).put(eKey, tfIdfNorm);
					}
				}
			}
		},
		
		// Sans pondération
		normal {
			protected void finalizeIndexation(Index index) {
				return;
			}
		};

		/**
		 * Calcule la pondération de l'index
		 * @param index Index à mettre à jour
		 */
		abstract protected void finalizeIndexation(Index index);
	}
}
