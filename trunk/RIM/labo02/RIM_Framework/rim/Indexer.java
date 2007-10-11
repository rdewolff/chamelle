package rim;

/**
 * An object that aims to index the content of documents. The created index(es) will 
 * then probably be used by a {@link Retriever} to perform efficient searches.
 * @see Retriever
 */
public interface Indexer {

	/**
	 * Indexes a document. The content is given as a raw content and should first be 
	 * cleaned before indexation. Operations such as splitting into words, stop-words
	 * removal or stemming should be applied in this method.
	 * @param id id of the document to be indexed.
	 * @param content raw content of the document.
	 * @see Feeder#parseCollection(java.net.URI, Indexer)
	 */
	public void index (Integer id, String content);
}
