package rim.cacm;

import java.util.*;
import java.io.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;

import rim.Retriever;

/**
 * A retriever for the CACM collection.
 */
public class CACMRetriever implements Retriever
{

	/* (non-Javadoc)
	 * @see rim.Retriever#searchDocument(java.lang.Integer)
	 */
	public Map<String, Double> searchDocument(Integer documentId)
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see rim.Retriever#searchTerm(java.lang.String)
	 */
	public Map<Integer, Double> searchTerm(String term)
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see rim.Retriever#executeQuery(java.lang.String)
	 * 
	 * Retourne les documents pertinents qui correspondent à la requête (ID)
	 * 
	 */
	public Map<Double,Integer> executeQuery (String query, boolean tfIdf)
	{
		// Variable utilisee pour stocker le resultat temporaire qui 
		// gere l'ordonnancement lors de l'insertion (valeur plus grande =
		// au debut de la liste
		TreeMap<Double, Integer> result = new TreeMap<Double, Integer>( 
				new Comparator<Double>()
				{   
					public int compare( Double x, Double y ) 
					{
						return x < y ? 1 : x > y ? -1 : 0;
					}
				});  

		// Pour la recherche
		Searcher searcher = null;

		// on va faire une recherche dans l'index deja creer
		try {
			searcher = new IndexSearcher("./LucensIndex");
		} catch (CorruptIndexException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}

		Analyzer analyzer = new StandardAnalyzer();
		// on fait une recherche sur le contenu d'un document
		QueryParser parser = new QueryParser("content", analyzer);
		Query q;
		try {
			// on parse la requete
			q = parser.parse(query);
			Hits hits = searcher.search(q);

			// Iterate through the results:
			for (int i = 0; i < hits.length(); i++) {
				// recupere le i-eme document
				Document hitDoc = hits.doc(i);
				// ainsi que son score
				double Score = hits.score(i);
				// l'insere dans la variable de resultat temporaire
				result.put(Score , Integer.parseInt(hitDoc.get("id")));
			}
			// termine 
			searcher.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// retourne les valeurs
		return result;
	}
}
