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
	 * its frequency in the document.
	 * @param docmentId a document identifier.
	 * @return the terms mapped to their frequency in the document.
	 */
	public Map<String,Integer> searchDocument(Integer docmentId);
	
	/**
	 * Retrieves the documents containing the given term. Each document is mapped to
	 * the frequency of the term in the document.
	 * @param term a term.
	 * @return the document id's mapped to the frequency of the contained term.
	 */
	public Map<Integer,Integer> searchTerm (String term);
}
