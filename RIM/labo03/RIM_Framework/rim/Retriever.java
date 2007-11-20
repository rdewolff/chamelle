package rim;
import java.util.Map;

/**
 * An object that aims to retrieve data from an indexed collection. Such a retriever
 * will probably need an access to the indexes created by an {@link Indexer}.
 * @see Indexer
 */
public interface Retriever {

	/**
	 * Retrieves the terms contained in the given document. Each term is mapped to
	 * its ponderation in the document.
	 * @param docmentId a document identifier.
	 * @return the terms mapped to their frequency in the document.
	 */
	public Map<String, Double> searchDocument(Integer docmentId);
	
	/**
	 * Retrieves the documents containing the given term. Each document is mapped to
	 * the ponderation of the term in the document.
	 * @param term a term.
	 * @return the document id's mapped to the frequency of the contained term.
	 */
	public Map<Integer,Double> searchTerm (String term);
	
	/**
	 * Retrieves the documents satisfying the given query. Each document is mapped to
	 * its cosinus similarity. The keys (cosinus similarities) are sorted by descendant 
	 * value, so that the first key returned by {@link Map#keySet()} is mapped to the 
	 * most similar document for the given query !
	 * @param query a string query, containing a list of words.
	 * @return the document id's (values) mapped to their cosinus similarity with the 
	 * query (key), sorted by descendant cosinus.
	 */
	public Map<Double,Integer> executeQuery (String query);
}
