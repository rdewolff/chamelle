package rim.cacm;

import rim.Indexer;
import java.util.*;
import java.io.*;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;


/**
 * An indexer for the CACM collection.
 */
public class CACMIndexer implements Indexer//, Comparator<String>
{	
	private IndexWriter iwriter;
	private Document doc;
	private Analyzer analyzer;

	public CACMIndexer() {

		// Lucens
		analyzer = new StandardAnalyzer();

		// Store the index in memory
		// Directory directory = new RAMDirectory();
		// To store an index on disk, use this instead:
		Directory directory = null;

		try {
			directory = FSDirectory.getDirectory("./LucensIndex");
		} catch (IOException e) {
			System.out.println(e.toString());
		}

		try {
			iwriter = new IndexWriter(directory, analyzer, true);
		} catch (CorruptIndexException e) {
			System.out.println(e.toString());
		} catch (LockObtainFailedException e) {
			System.out.println(e.toString());
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		System.out.println("Indexeur instancié");
	}


	/* (non-Javadoc)
	 * @see rim.Indexer#index(java.lang.Integer, java.lang.String)
	 */
	public void index(Integer id, String content){

		/**
		 * Implémentation de la librairie Lucens
		 */
		doc = new Document(); // reinitialise

		// ajoute l'id du document dans un champs prevu a cet effet
		doc.add(new Field("id", String.valueOf(id), Field.Store.YES, Field.Index.TOKENIZED));

		// ajoute le contenu du document
		doc.add(new Field("content", content, Field.Store.YES, Field.Index.TOKENIZED));

		// ajoute a l'index
		try {
			iwriter.addDocument(doc);
		} catch (CorruptIndexException e) {
			System.out.println(e.toString());
		} catch (IOException e) {
			System.out.println(e.toString());
		}

	}

	/**
	 * Methode permettant de sauver les variables statiques d'indexs dans
	 * leurs fichiers respectifs: "index.txt" et "index_inverse.txt"
	 * Sauve aussi les deux indexs sous forme d'objet dans "index_object.txt"
	 */
	public void finalizeIndexation()
	{
		// Lucens : finalisation de l'indexation
		try {
			// optimisation de l'index
			iwriter.optimize();
		} catch (CorruptIndexException e) {
			System.out.println(e.toString());
		} catch (IOException e) {
			System.out.println(e.toString());
		}

		try {
			// fermeture
			iwriter.close();
		} catch (CorruptIndexException e) {
			System.out.println(e.toString());
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
}
