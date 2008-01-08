package rim;
import java.net.URI;
 
/**
 * An object that aims to parse a collection and <i>feed</i> an indexer.
 */
public interface Feeder {

	/**
	 * Parses the collection of documents denoted by the given {@link URI} and 
	 * indexes its content (documents), using the given {@link Indexer} by calling 
	 * its {@link Indexer#index(String, String)} method.
	 * @param collection an identifier for a collection of documents.
	 * @param indexer the indexer to be used to index each document of the 
	 * collection. In some way, the indexer is a <i>visitor</i> of the collection.
	 * @return the number of parsed documents.
	 * @see Indexer#index(String, String)
	 */
	public int parseCollection (URI collection, Indexer indexer);
}
