package rim.cacm;

import java.util.*;
import java.io.*;

import rim.Retriever;

/**
 * A retriever for the CACM collection.
 */
public class CACMRetriever implements Retriever
{
	//Les deux Maps triees repr�sentant les deux indexs
	static TreeMap<Integer, Object[][]> index = new TreeMap<Integer, Object[][]>();
	static TreeMap<String, Object[][]> indexInverse = new TreeMap<String, Object[][]>();
	
	//Traitements statiques
	static
	{
		File f = new File("index_object.txt");
		//Si l'index n'a pas ete construit, on effectue l'indexage
		if(!f.exists())
		{
			System.out.println("Creation des indexs...");
			CACMIndexer i = new CACMIndexer();
			CACMFeeder c = new CACMFeeder();
			c.parseCollection(java.net.URI.create("rim/ressources/cacm.all"), i);
			i.save();
		}
		
		//Lecture des deux TreeMaps stockes dans le fichier "index_object"
		try
		{
			System.out.println("Mise en memoire des indexs...");
			ObjectInputStream in = new ObjectInputStream(new FileInputStream("index_object.txt"));
			index = (TreeMap<Integer, Object[][]>)in.readObject();
			indexInverse = (TreeMap<String, Object[][]>)in.readObject();
			in.close();
		}
		catch(ClassNotFoundException e)
		{System.out.println("classnfoundlect2");}
		catch(ClassCastException e)
		{System.out.println("ClassCast");}
		catch(FileNotFoundException e)
		{System.out.println("Le fichier n'existe pas");}
		catch(IOException e)
		{System.out.println("IOException");}
	}
	
	/* (non-Javadoc)
	 * @see rim.Retriever#searchDocument(java.lang.Integer)
	 */
	public Map<String, Double> searchDocument(Integer documentId)
	{
		//Map de retour
		TreeMap<String, Double> result = new TreeMap<String, Double>();
		//Tableau representant la reponse a la requete
		Object[][] list = index.get(documentId);
		//Constructionde la reponse
		for(int i=0; i<list.length; i++)
		{
			result.put((String)list[i][0], (Double)list[i][1]);
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see rim.Retriever#searchTerm(java.lang.String)
	 */
	public Map<Integer, Double> searchTerm(String term)
	{
		//Map de retour
		TreeMap<Integer, Double> result = new TreeMap<Integer, Double>();
		//Tableau representant la reponse a la requete
		Object[][] list = indexInverse.get(term);
		//Construction de la reponse
		for(int i=0; i<list.length; i++)
		{
			result.put((Integer)list[i][0], (Double)list[i][1]);
		}
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see rim.Retriever#executeQuery(java.lang.String)
	 */
	public Map<Double,Integer> executeQuery (String query)
	{return new HashMap<Double, Integer>();};
}
