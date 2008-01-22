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
 * @author J. Schmid & L. Prevost
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
	 * Recupere le type d'index
	 * @return Type de l'index
	 */
	public WeightingType type() {
		return type;
	}
	
	/**
	 * Recupere le nombre d'element dans
	 * l'index
	 * @return Nombre d'elements
	 */
	public int idxSize() {
		return idx.size();
	}
	
	/**
	 * Recupere le nombre d'element dans
	 * l'index inverse
	 * @return Nombre d'elements
	 */
	public int ridxSize() {
		return ridx.size();
	}

	/**
	 * Ajoute un element avec sa frequence aux
	 * index normal et inverse
	 * @param uri Chemin du document
	 * @param term Terme detecter
	 * @param freq Frequence du terme
	 */
	public void add(String uri, String term, Double freq) {
		idx.put(uri, term, freq);
		ridx.put(term, uri, freq);
	}
	
	/**
	 * Recherche tous les documents contenant
	 * le terme donne
	 * @param termToFind Terme recherche
	 * @return Null si aucun document ne contient
	 * ce terme, autrement tous les documents
	 * le contenant
	 */
	public Map<String, Double> findDocs(String termToFind) {
		return ridx.find(termToFind);
	}
	
	/**
	 * Recherche tous les termes contenu dans un
	 * document donne
	 * @param uri Document recherche
	 * @return Null si aucun terme present pour le document,
	 * tous les termes dans le cas contraire.
	 */
	public Map<String, Double> findTerms(String uri) {
		return idx.find(uri);
	}
	
	/**
	 * Calcule la ponderation de l'index
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
	 * Classe interne gerant la structure de donnees pour
	 * la gestion des index
	 * 
	 * @author J. Schmid & L. Prevost
	 *
	 * @param <ExternalKey> Cle externe 
	 * @param <InternalKey> Cle interne
	 */
	private class GenericIndex<ExternalKey, InternalKey> {
		// Arbre pour le stockage de l'indexe. Contient un 
		// second arbre pour le stockage element/frequence
		TreeMap<ExternalKey, TreeMap<InternalKey, Double>> index = 
			new TreeMap<ExternalKey, TreeMap<InternalKey,Double>>();

		/**
		 * Recupere la taille de l'index
		 * @return Taille de l'index
		 */
		public int size() {
			return index.size();
		}
		
		/**
		 * Ajoute un element dans la structure
		 * @param ek Cle externe
		 * @param ik Cle interne
		 * @param frequence Frequence d'apparition de la cle interne
		 */
		private void put(ExternalKey ek, InternalKey ik, Double frequence) {
			// Verifie l'existence de la cle externe pour ajout si necessaire
			if (!index.containsKey(ek)) {
				index.put(ek, new TreeMap<InternalKey, Double>());
			}
			
			// Ajoute l'element au contenu de la cle externe
			index.get(ek).put(ik, frequence);
		}
		
		/**
		 * Effectue une recherche sur la cle externe
		 * @param key Cle externe recherchee
		 * @return Null si cle non trouvee, donnees trouvees 
		 * dans le cas contraire.
		 */
		private Map<InternalKey, Double> find(ExternalKey key) {
			return index.get(key);
		}

		/**
		 * Sauvegarde l'index au chemin voulu
		 * avec un prefixe de son choix
		 * @param path Chemin voulu
		 * @param suffix Prefixe choisi
		 */
		private void save(String path, String suffix) {
			try {
				// Fichier pour l'index
				PrintWriter pw = new PrintWriter(new BufferedWriter(
					new FileWriter(new File(path + type.name() + 
						suffix + ".txt"))));

				// Ecriture des fichiers
				// Creation de la sortie formatee
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
	 * Enumeration pour representer les differents types de ponderation PS : 
	 * Les methodes sont decrites que pour leur definition abstraite.
	 * @author   Laurent Prevost
	 */
	public enum WeightingType {
		// Ponderation TF
		tf {
			protected void finalizeIndexation(Index index) {
				// Raccourcis d'ecriture
				GenericIndex<String, String> idx = index.idx;
				GenericIndex<String, String> ridx = index.ridx;
				
				// Chaque document de l'index
				for (String eKey : idx.index.keySet()) {
					// Trouve le terme le plus frequent dans le document et
					// recupere la valeur de sa frequence dans le document
					double max = Collections.max(idx.index.get(eKey).values());
					
					// Calcul et maj de la frequence ponderee de chaque terme en 
					// rapport au plus frequent trouve precedement
					for (String iKey : idx.index.get(eKey).keySet()) {
						// Calcul tf normalise
						double tf = idx.index.get(eKey).get(iKey) / max;
						
						// Modification index et index inverse
						idx.index.get(eKey).put(iKey, tf); 
						ridx.index.get(iKey).put(eKey, tf);
					}
				}
			}
		},
		
		// Ponderation TF-IDF
		tfidf {
			protected void finalizeIndexation(Index index) {
				// Raccourcis d'ecriture
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
						// Calcul tf-idf (non normalise)
						// tf = log2(frequenceDuTermeDsDocCt + 1)
						// idf = log(nbDocumentsCollection / 
						//       nbDocumentPourLeTermeChoisi)
						// tf-idf = tf * idf
						double tfidf = 
							(log10(idx.index.get(eKey).get(iKey) + 1.0) / LOG2)
							* (log10(D / ridx.index.get(iKey).size()) / LOG2);

						// Calcul du tfidf maximal
						if (tfidf > maxtfidf) maxtfidf = tfidf;
					
						// Modification de la frequence du terme choisi
						// pour le document courrant
						idx.index.get(eKey).put(iKey, tfidf);
					}

					// Normalisation des resultats pour le document courrant
					for (String iKey : idx.index.get(eKey).keySet()) {
						// Normalisation
						double tfIdfNorm = 
							idx.index.get(eKey).get(iKey) / maxtfidf;
					
						// Modification index et index inverse
						idx.index.get(eKey).put(iKey, tfIdfNorm);
						ridx.index.get(iKey).put(eKey, tfIdfNorm);
					}
				}
			}
		},
		
		// Sans ponderation
		normal {
			protected void finalizeIndexation(Index index) {
				return;
			}
		};

		/**
		 * Calcule la ponderation de l'index
		 * @param index Index a mettre a jour
		 */
		abstract protected void finalizeIndexation(Index index);
	}
}
