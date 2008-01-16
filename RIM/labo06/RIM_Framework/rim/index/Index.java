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
 * @author J. Schmid & L. Pr�vost
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
	 * R�cup�re le type d'index
	 * @return Type de l'index
	 */
	public WeightingType type() {
		return type;
	}
	
	/**
	 * R�cup�re le nombre d'�l�ment dans
	 * l'index
	 * @return Nombre d'�l�ments
	 */
	public int idxSize() {
		return idx.size();
	}
	
	/**
	 * R�cup�re le nombre d'�l�ment dans
	 * l'index inverse
	 * @return Nombre d'�l�ments
	 */
	public int ridxSize() {
		return ridx.size();
	}

	/**
	 * Ajoute un �l�ment avec sa fr�quence aux
	 * index normal et inverse
	 * @param uri Chemin du document
	 * @param term Terme d�tecter
	 * @param freq Fr�quence du terme
	 */
	public void add(String uri, String term, Double freq) {
		idx.put(uri, term, freq);
		ridx.put(term, uri, freq);
	}
	
	/**
	 * Recherche tous les documents contenant
	 * le terme donn�
	 * @param termToFind Terme recherch�
	 * @return Null si aucun document ne contient
	 * ce terme, autrement tous les documents
	 * le contenant
	 */
	public Map<String, Double> findDocs(String termToFind) {
		return ridx.find(termToFind);
	}
	
	/**
	 * Recherche tous les termes contenu dans un
	 * document donn�
	 * @param uri Document recherch�
	 * @return Null si aucun terme pr�sent pour le document,
	 * tous les termes dans le cas contraire.
	 */
	public Map<String, Double> findTerms(String uri) {
		return idx.find(uri);
	}
	
	/**
	 * Calcule la pond�ration de l'index
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
	 * Classe interne g�rant la structure de donn�es pour
	 * la gestion des index
	 * 
	 * @author J. Schmid & L. Pr�vost
	 *
	 * @param <ExternalKey> Cl� externe 
	 * @param <InternalKey> Cl� interne
	 */
	private class GenericIndex<ExternalKey, InternalKey> {
		// Arbre pour le stockage de l'indexe. Contient un 
		// second arbre pour le stockage el�ment/fr�quence
		TreeMap<ExternalKey, TreeMap<InternalKey, Double>> index = 
			new TreeMap<ExternalKey, TreeMap<InternalKey,Double>>();

		/**
		 * R�cup�re la taille de l'index
		 * @return Taille de l'index
		 */
		public int size() {
			return index.size();
		}
		
		/**
		 * Ajoute un �l�ment dans la structure
		 * @param ek Cl� externe
		 * @param ik Cl� interne
		 * @param frequence Fr�quence d'apparition de la cl� interne
		 */
		private void put(ExternalKey ek, InternalKey ik, Double frequence) {
			// V�rifie l'existence de la cl� externe pour ajout si n�cessaire
			if (!index.containsKey(ek)) {
				index.put(ek, new TreeMap<InternalKey, Double>());
			}
			
			// Ajoute l'�l�ment au contenu de la cl� externe
			index.get(ek).put(ik, frequence);
		}
		
		/**
		 * Effectue une recherche sur la cl� externe
		 * @param key Cl� externe recherch�e
		 * @return Null si cl� non trouv�e, donn�es trouv�es 
		 * dans le cas contraire.
		 */
		private Map<InternalKey, Double> find(ExternalKey key) {
			return index.get(key);
		}

		/**
		 * Sauvegarde l'index au chemin voulu
		 * avec un pr�fixe de son choix
		 * @param path Chemin voulu
		 * @param suffix Pr�fixe choisi
		 */
		private void save(String path, String suffix) {
			try {
				// Fichier pour l'index
				PrintWriter pw = new PrintWriter(new BufferedWriter(
					new FileWriter(new File(path + type.name() + 
						suffix + ".txt"))));

				// Ecriture des fichiers
				// Cr�ation de la sortie format�e
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
	 * Enumeration pour repr�senter les diff�rents types de pond�ration PS : 
	 * Les m�thodes sont d�crites que pour leur d�finition abstraite.
	 * @author   Laurent Pr�vost
	 */
	public enum WeightingType {
		// Pond�ration TF
		tf {
			protected void finalizeIndexation(Index index) {
				// Raccourcis d'�criture
				GenericIndex<String, String> idx = index.idx;
				GenericIndex<String, String> ridx = index.ridx;
				
				// Chaque document de l'index
				for (String eKey : idx.index.keySet()) {
					// Trouve le terme le plus fr�quent dans le document et
					// r�cup�re la valeur de sa fr�quence dans le document
					double max = Collections.max(idx.index.get(eKey).values());
					
					// Calcul et maj de la fr�quence pond�r�e de chaque terme en 
					// rapport au plus fr�quent trouv� pr�c�dement
					for (String iKey : idx.index.get(eKey).keySet()) {
						// Calcul tf normalis�
						double tf = idx.index.get(eKey).get(iKey) / max;
						
						// Modification index et index invers�
						idx.index.get(eKey).put(iKey, tf); 
						ridx.index.get(iKey).put(eKey, tf);
					}
				}
			}
		},
		
		// Pond�ration TF-IDF
		tfidf {
			protected void finalizeIndexation(Index index) {
				// Raccourcis d'�criture
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
						// Calcul tf-idf (non normalis�)
						// tf = log2(frequenceDuTermeDsDocCt + 1)
						// idf = log(nbDocumentsCollection / 
						//       nbDocumentPourLeTermeChoisi)
						// tf-idf = tf * idf
						double tfidf = 
							(log10(idx.index.get(eKey).get(iKey) + 1.0) / LOG2)
							* (log10(D / ridx.index.get(iKey).size()) / LOG2);

						// Calcul du tfidf maximal
						if (tfidf > maxtfidf) maxtfidf = tfidf;
					
						// Modification de la fr�quence du terme choisi
						// pour le document courrant
						idx.index.get(eKey).put(iKey, tfidf);
					}

					// Normalisation des r�sultats pour le document courrant
					for (String iKey : idx.index.get(eKey).keySet()) {
						// Normalisation
						double tfIdfNorm = 
							idx.index.get(eKey).get(iKey) / maxtfidf;
					
						// Modification index et index invers�
						idx.index.get(eKey).put(iKey, tfIdfNorm);
						ridx.index.get(iKey).put(eKey, tfIdfNorm);
					}
				}
			}
		},
		
		// Sans pond�ration
		normal {
			protected void finalizeIndexation(Index index) {
				return;
			}
		};

		/**
		 * Calcule la pond�ration de l'index
		 * @param index Index � mettre � jour
		 */
		abstract protected void finalizeIndexation(Index index);
	}
}
