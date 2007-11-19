package rim.cacm;

import java.util.*;
import java.io.*;

import rim.Retriever;

/**
 * A retriever for the CACM collection.
 */
public class CACMRetriever implements Retriever
{
	//Les deux Maps triees servant de memoire d'indexage
	static TreeMap<Integer, HashMap<String, Double>> index = 
		new TreeMap<Integer, HashMap<String, Double>>();
	static TreeMap<String, HashMap<Integer, Double>> indexInverse = 
		new TreeMap<String, HashMap<Integer, Double>>();

	//Les deux Maps triees servant de memoire d'indexage tf-idf normalise
	static TreeMap<Integer, HashMap<String, Double>> index2 = 
		new TreeMap<Integer, HashMap<String, Double>>();
	static TreeMap<String, HashMap<Integer, Integer>> indexInverse2 = 
		new TreeMap<String, HashMap<Integer, Integer>>();

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
			i.finalizeIndexation();
		}

		//Lecture des deux TreeMaps stockes dans le fichier "index_object"
		try
		{
			System.out.println("Mise en memoire des indexs...");
			ObjectInputStream in = new ObjectInputStream(new FileInputStream("index_object.txt"));
			index = (TreeMap<Integer, HashMap<String, Double>>)in.readObject();
			indexInverse = (TreeMap<String, HashMap<Integer, Double>>)in.readObject();
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
		//Tableau representant la reponse a la requete
		HashMap<String, Double> list = index.get(documentId);

		return list;
	}

	/* (non-Javadoc)
	 * @see rim.Retriever#searchTerm(java.lang.String)
	 */
	public Map<Integer, Double> searchTerm(String term)
	{
		//Tableau representant la reponse a la requete
		HashMap<Integer, Double> list = indexInverse.get(term);

		return list;
	}

	/* (non-Javadoc)
	 * @see rim.Retriever#executeQuery(java.lang.String)
	 * 
	 * Retourn les documents pertinant qui corresponde à la requête (ID)
	 * Croissant -> Decroissant 
	 * 
	 */
	public Map<Double,Integer> executeQuery (String query)
	{
		HashMap<Double, Integer> queryAnswer = new HashMap<Double, Integer>();
		try {
			// on découpe la chaine de caractere (chaque terme separe par un espace)
			String[] arQuery = query.split(" ");
			HashMap<Integer, Double> tmpIndexInverse;
			double sommeProduit = 0;
			double sommePoidTerme = 0;
			double sommeTfIdf = 0;

			Set _keys = index.keySet();
			for(Object id: _keys)
			{
				// parcours tous les termes de la requete
				for (int i=0; i<arQuery.length; i++) {
					tmpIndexInverse = indexInverse.get(arQuery[i]);
					if (tmpIndexInverse != null) {
						if(tmpIndexInverse.get((Integer)id) != null) {
							sommeProduit = tmpIndexInverse.get((Integer)id) * 1.0;
							sommePoidTerme += Math.pow(1.0, 2.0);
							sommeTfIdf += Math.pow(tmpIndexInverse.get((Integer)id), 2.0); 
						} else {
							sommeProduit = 0.0;
						}
					} else {
						sommeProduit = 0.0;
					}

				}
				// on insere le document ID ainsi que sa similarite par cosinus
				queryAnswer.put(sommeProduit / Math.sqrt(sommeTfIdf*sommePoidTerme), (Integer)id);
				// reset
				sommeProduit = 0;
				sommePoidTerme = 0;
				sommeTfIdf = 0;
			}
		} catch (Exception e) {
			System.out.println("Erreur dans la fonction executeQuery() : ");
			e.printStackTrace();
		}

		return queryAnswer; //  HashMap<Double, Integer>();

	};
}
