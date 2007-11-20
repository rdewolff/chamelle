package rim.cacm;

import java.util.*;
import java.io.*;

import rim.Retriever;

/**
 * A retriever for the CACM collection.
 */
public class CACMRetriever implements Retriever
{
	//HashMap contenant les stop words a eliminer des documents
	static HashSet<String> commonwords = new HashSet<String>();
	
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
		String mot = null;
		
		try
		{
			//Ouverture du fichier de stop words
			FileInputStream in = new FileInputStream("rim/ressources/common_words");
			
			//Stockage des stop words
			BufferedReader d = new BufferedReader(new InputStreamReader(in));
			while((mot=d.readLine())!= null)
			{ 	
				commonwords.add(mot);
			}
		}
		catch(ClassCastException e)
		{System.out.println("ClassCast");}
		catch(FileNotFoundException e)
		{System.out.println("Le fichier <common_words> n'existe pas");}
		catch(IOException e)
		{System.out.println("Problème lors de la mise en mémoire des stop words...");}
		
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
	
	private class plusPetit implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			if((Double)o1<(Double)o2)
				return 1;
			else if((Double)o1 == (Double)o2)
				return 0;
			else
				return -1;
		}
		
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
		//Decapitalisation
		query = query.toLowerCase();
		
		//Tokenisation de la ligne passee en parametre tout en enlevant la ponctuation
		String[] tokens = query.split("[\\p{Punct} ]");
		HashSet<String> set = new HashSet<String>();
		
		//--Creation des indexs--//
		
		//Parcours des termes du document et sotckage de leur frequence
		for (String s: tokens)
		{
			//Selection des termes qui ne sont pas des stop words
			if(!commonwords.contains(s))
			{
				set.add(s);
			}
		}
		
		//Comparateur descendant
		plusPetit p = new plusPetit();
		//Table de stockage des cosinus de reponse
		TreeMap<Double, Integer> queryAnswer = new TreeMap<Double, Integer>(p);
		try {
			HashMap<Integer, Double> tmpIndexInverse = null;
			double sommeProduit = 0.0;
			double sommePoidTerme = 0.0;
			double sommeTfIdf = 0.0;
			HashSet<Integer> setDocs = new HashSet<Integer>();
			for (String s: set) {
				setDocs.addAll(indexInverse.get(s).keySet());
			}
			
			Set<Integer> _keys = index.keySet();
			for(Integer id: _keys)
			{
				// parcours tous les termes de la requete
				for (String s: set) {
					tmpIndexInverse = indexInverse.get(s);
					//Si le terme existe dans notre collection
					if (tmpIndexInverse != null) {
						//Si le document courant contient le terme courant de la requete
						if(tmpIndexInverse.get(id) != null) {
							
							sommeProduit += tmpIndexInverse.get(id) * 1.0;
							sommePoidTerme += Math.pow(1.0, 2.0);
							
						}
					}
				}
				
				Set<String> keys = index.get(id).keySet();
				HashMap<String, Double> h = new HashMap<String, Double>();
				h = index.get(id);
				
				if(sommeProduit != 0.0)
				{
					for(String s: keys)
					{
						sommeTfIdf += Math.pow(h.get(s), 2.0);
					}
				}
				
				//On insere le document ID ainsi que sa similarite par cosinus
				if(sommeProduit != 0.0) {
//					System.out.println(sommeProduit);
//					System.out.println(sommePoidTerme);
//					System.out.println(sommeTfIdf);
//					System.out.println(Math.sqrt(sommeTfIdf*sommePoidTerme));
//					System.out.println("--");
					queryAnswer.put(sommeProduit / Math.sqrt(sommeTfIdf*sommePoidTerme), id);
					//System.out.println(sommeProduit / Math.sqrt(sommeTfIdf*sommePoidTerme));
				}
				
				// reset
				sommeProduit = 0.0;
				sommePoidTerme = 0.0;
				sommeTfIdf = 0.0;
			}
		} catch (Exception e) {
			System.out.println("Erreur dans la fonction executeQuery() : ");
			e.printStackTrace();
		}

		return queryAnswer; //  HashMap<Double, Integer>();

	};
}
