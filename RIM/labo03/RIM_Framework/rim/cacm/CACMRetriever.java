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
	static TreeMap<String, HashMap<Integer, Double>> indexInverse2 = 
		new TreeMap<String, HashMap<Integer, Double>>();

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
			index2 = (TreeMap<Integer, HashMap<String, Double>>)in.readObject();
			indexInverse2 = (TreeMap<String, HashMap<Integer, Double>>)in.readObject();
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
	
	public void reMem()
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
			index2 = (TreeMap<Integer, HashMap<String, Double>>)in.readObject();
			indexInverse2 = (TreeMap<String, HashMap<Integer, Double>>)in.readObject();
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
	 * Retourne les documents pertinents qui correspondent à la requête (ID)
	 * Croissant -> Decroissant 
	 * 
	 */
	public Map<Double,Integer> executeQuery (String query, boolean tfIdf)
	{
		Set<String> keys = null;
		double sommeProduit = 0.0;
		double sommePoidTerme = 0.0;
		double sommeTfIdf = 0.0;
		//Decapitalisation
		query = query.toLowerCase();

		//Tokenisation de la ligne passee en parametre tout en enlevant la ponctuation
		String[] tokens = query.split("[\\p{Punct} ]");
		HashMap<String, Double> set = new HashMap<String, Double>();

		//Parcours des termes du document et stockage de leur frequence
		for (String s: tokens) {
			//Selection des termes qui ne sont pas des stop words
			if(!commonwords.contains(s)) {
				if(set.containsKey(s))
					set.put(s, set.get(s)+1.0);
				else
					set.put(s, 1.0);
			}
		}
		
		//Recuperation des termes de la requete
		keys = set.keySet();
		
		//Table de stockage des cosinus de reponse
		TreeMap<Double, Integer> queryAnswer = new TreeMap<Double, Integer>(new Comparator<Double>() {
			public int compare(Double o1, Double o2) {
				return Double.compare(o2, o1);
			}
		});
		//Somme des poids des termes de la requete au carre
		for(String s: keys) {
			sommePoidTerme += Math.pow(set.get(s), 2.0);
		}
		
		int cpt=0;
		
		try {
			HashMap<Integer, Double> tmpIndexInverse = null;
			
			//Le set des documents concernes par la requete
			HashSet<Integer> setDocs = new HashSet<Integer>();
			//Creation du set de documents qui contiennent les termes de la requete
			for (String s: keys) {
				if(indexInverse.containsKey(s))
					if(tfIdf)
						setDocs.addAll(indexInverse2.get(s).keySet());
					else
						setDocs.addAll(indexInverse.get(s).keySet());
			}

			//Parcours des documents concernes par notre requete
			for(Integer id: setDocs) {
				//Parcours de tous les termes de la requete
				for(String s: keys) {
					if(tfIdf)
						tmpIndexInverse = indexInverse2.get(s);
					else
						tmpIndexInverse = indexInverse.get(s);
					//Si le terme existe dans notre collection et que le document existe
					if(tmpIndexInverse != null && tmpIndexInverse.get(id) != null) {
						sommeProduit += tmpIndexInverse.get(id) * set.get(s);
					}
				}
				
				Set<String> _keys;
				
				if(tfIdf)	
					_keys = index2.get(id).keySet();
				else
					_keys = index.get(id).keySet();
				
				HashMap<String, Double> h = new HashMap<String, Double>();
				
				if(tfIdf)
					h = index2.get(id);
				else
					h = index.get(id);
				
				for(String s: _keys)
				{
					sommeTfIdf += Math.pow(h.get(s), 2.0);
				}
				if(id==394)
					System.out.println("394::::::"+(sommeProduit / Math.sqrt(sommeTfIdf*sommePoidTerme)));
				queryAnswer.put((sommeProduit / Math.sqrt(sommeTfIdf*sommePoidTerme)), id);
				cpt++;

				// reset
				sommeProduit = 0.0;
				sommeTfIdf = 0.0;
			}
		} catch (Exception e) {
			System.out.println("Erreur dans la fonction executeQuery() : ");
			e.printStackTrace();
		}
		
		System.out.println("CPT : " + cpt);
		System.out.println("TREE : " + queryAnswer.size());
		return queryAnswer; //  HashMap<Double, Integer>();
	};
}
