package rim.cacm;

import rim.Indexer;
import java.util.*;
import java.io.*;
import java.net.*;
import java.math.*;

/**
 * An indexer for the CACM collection.
 */
public class CACMIndexer implements Indexer//, Comparator<String>
{	
	//HashMap contenant les stop words a eliminer des documents
	static HashSet<String> commonwords = new HashSet<String>();
	
	//Les deux Maps triees servant de memoire d'indexage frequence normalisee
	static TreeMap<Integer, HashMap<String, Integer>> frequences = 
		new TreeMap<Integer, HashMap<String, Integer>>();
	
	//Les deux Maps triees servant de memoire d'indexage frequence normalisee
	static TreeMap<Integer, HashMap<String, Double>> index = 
		new TreeMap<Integer, HashMap<String, Double>>();
	static TreeMap<String, HashMap<Integer, Double>> indexInverse = 
		new TreeMap<String, HashMap<Integer, Double>>();
	
	//Les deux Maps triees servant de memoire d'indexage tf-idf normalise
	static TreeMap<Integer, HashMap<String, Double>> index2 = 
		new TreeMap<Integer, HashMap<String, Double>>();
	static TreeMap<String, HashMap<Integer, Integer>> indexInverse2 = 
		new TreeMap<String, HashMap<Integer, Integer>>();
	
	//Variable dernier ID indexer
	static int lastId = 0;
	static double n = 0;
	
	static
	{
		String mot = null;
		File f = null;
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
			
			//Reinitialisation du fichier d'index
			f = new File("index.txt");
			f.delete();
			//Reinitialisation du fichier d'index inverse
			f = new File("index_inverse.txt");
			f.delete();
			//Reinitialisation du fichier du TreeMap de l'index
			f = new File("index_object.txt");
			f.delete();
		}
		catch(ClassCastException e)
		{System.out.println("ClassCast");}
		catch(FileNotFoundException e)
		{System.out.println("Le fichier <common_words> n'existe pas");}
		catch(IOException e)
		{System.out.println("Problème lors de la mise en mémoire des stop words...");}
	}
	
	/* (non-Javadoc)
	 * @see rim.Indexer#index(java.lang.Integer, java.lang.String)
	 */
	public void index(Integer id, String content){
	
		String tok = null;
		String phrase = null;
		Set keys = null;
		double freqMax = 0;
		//Table des termes avec leur frequence
		HashMap<String, Integer> elements = new HashMap<String, Integer>();
		
		//Decapitalisation
		content = content.toLowerCase();
		//Suppression de la ponctuation // TODO : regexp
		content = content.replace("(","");
		content = content.replace(")","");
		content = content.replace(".","");
		content = content.replace(",","");
		content = content.replace(";","");
		content = content.replace(":","");
		content = content.replace("'s","");
		content = content.replace("\"","");
		//content.replaceAll("\\p", "");
		
		//Tokenisation de la ligne passee en parametre
		StringTokenizer tokens = new StringTokenizer(content);
		
		//--Creation des indexs--//
		
		//Parcours des termes du document et sotckage de leur frequence
		while (tokens.hasMoreTokens())
		{
			tok = tokens.nextToken();
			//Selection des termes qui ne sont pas des stop words
			if(!commonwords.contains(tok))
			{
				elements.put(tok, elements.containsKey(tok)?elements.get(tok)+1:1);
			}
		}
		
		//Recuperation de la liste des termes
		keys = elements.keySet();
		
		//Recherche de la frequence maximale
		for(Object s: keys)
		{
			if(elements.get(s) > freqMax)
				freqMax = elements.get(s);
		}
		
		//
		HashMap<String, Double> h = new HashMap<String, Double>();
		HashMap<String, Integer> h2 = new HashMap<String, Integer>();
		
		//Construction de la ligne d'index correspondant au document en cours de traitement
		for(Object s: keys)
		{
			h.put((String)s, (double)elements.get(s)/freqMax);
			//Memorisation des frequences
			h2.put((String)s, elements.get(s));
		}
		
		//Stockage dans la variable statique
		index.put(id, h);
		// Stockage des frequences
		frequences.put(id, h2);
		
		//La liste des documents avec leur frequence que l'on va creer pour les termes
		for(Object s: keys)
		{
			//Si l'index inverse contient deja un terme, il faut recuperer son tableau
			//et y appondre le terme courant
			if(indexInverse.containsKey(s))
			{
				indexInverse.get(s).put(id, elements.get((String)s)/freqMax);
			}
			//Sinon, on cree un tableau avec les valeurs courantes
			else
			{
				HashMap<Integer, Double> h3 = new HashMap<Integer, Double>();
				h3.put(id, (double)elements.get((String)s)/freqMax);
				indexInverse.put((String)s,h3);
			}
		}
	}
	
	/**
	* Methode permettant de sauver les variables statiques d'indexs dans
	* leurs fichiers respectifs: "index.txt" et "index_inverse.txt"
	* Sauve aussi les deux indexs sous forme d'objet dans "index_object.txt"
	*/
	public void finalizeIndexation()
	{
		Set keys = null;
		Set keys2 = null;
		Set _keys = null;
		Set _keys2 = null;
		String ligne = null;
		
		try
		{
			//Recuperation de la liste des lignes d'index
			keys = index.keySet();
			
			//Nombre total de documents
			n = index.size();
			
			//Ouverture du fichier d'index des frequences normalisees
			BufferedWriter os = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("index.txt")));
			
			/*--------------------------------------------------
			*Remplissage du fichier d'index frequence normalisee
			--------------------------------------------------*/
			for(Object o: keys)
			{
				_keys = index.get(o).keySet();
				ligne = "[" + o + "]{"; //Debut de ligne avec l'ID du document
				
				//Liste des terme/frequence
				for(Object o2: _keys)
				{
					ligne = ligne + "<" + (String)o2 + "," + index.get(o).get(o2) + ">";
				}
				
				ligne = ligne + "}\r\n";	
				os.write(ligne); //Ecriture de la ligne
	   		}
		    os.flush();
	        os.close();
	        
	        keys = index.keySet();
	        
	        /*--------------------------------------------------
	        *Mise a jour du treemap d'index de fréquences tf-idf
	        --------------------------------------------------*/
			for(Object o: keys)
			{
				//Le tfIdf max de tous les documents
		        double maxTfIdf = 0.0;
		        //Les cles representant les termes dans un document
				_keys = index.get(o).keySet();
				//La variable du tf-idf
				double tfidf = 0;
				//Table intermediaire pour les termes et leur tf-idf
				HashMap<String, Double> h = new HashMap<String, Double>();
				
				//Calcul des tfidf et de tfidf maximum
				for(Object o2: _keys)
				{
					tfidf = (Math.log(index.get(o).get(o2)+1.0)/Math.log(2))*
							(Math.log1p(n/(double)frequences.get(o).get(o2)+1.0)/Math.log(2));
					if(tfidf > maxTfIdf) //Mise a jour tfidf Max
						maxTfIdf = tfidf;
					h.put((String)o2, tfidf);
				}
				_keys = h.keySet();
				
				//Normalisation du tfidf
				for(Object o2: _keys)
				{
					h.put((String)o2, h.get(o2)/maxTfIdf);
				}
				//Stockage dans l'objet d'index
				index2.put((Integer)o, h);
	   		}
			
			//Ouverture du fichier des tf-idf normalisees
			os = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("index2.txt")));
			
			/*--------------------------------------------------
			*Remplissage du fichier d'index frequence normalisee
			--------------------------------------------------*/
			for(Object o: keys)
			{
				_keys = index2.get(o).keySet();
				ligne = "[" + o + "]{"; //Debut de ligne avec l'ID du document
				
				//Liste des terme/frequence
				for(Object o2: _keys)
				{
					ligne = ligne + "<" + (String)o2 + "," + index2.get(o).get(o2) + ">";
				}
				
				ligne = ligne + "}\r\n";	
				os.write(ligne); //Ecriture de la ligne
	   		}
		    os.flush();
	        os.close();
			
			//Recuperation de la liste des lignes d'index
			keys2 = indexInverse.keySet();
			
			//Ouverture du fichier
			BufferedWriter os2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("index_inverse.txt")));
			
			/*-------------------------------------
			*Remplissage du fichier d'index inverse
			-------------------------------------*/
			for(Object o: keys2)
			{
				ligne = "[" + o + "]{"; //Debut de ligne avec le terme
				_keys2 = indexInverse.get(o).keySet();
				
				//Liste des document/frequence
				for(Object o2: _keys2)
				{
					ligne = ligne + "<" + (Integer)o2 + "," + indexInverse.get(o).get(o2) + ">";
				}

				ligne = ligne + "}\r\n";
				//Ecriture de la ligne
				os2.write(ligne);
	   		}
		    os2.flush();
	        os2.close();
			
			ObjectOutputStream os3 = new ObjectOutputStream(new FileOutputStream("index_object.txt"));
			//Ecriture des deux TreeMaps
			os3.writeObject(index2);
			os3.writeObject(indexInverse);
			os3.flush();
			os3.close();
		}
		catch(FileNotFoundException e)
		{System.out.println("Filenfound");}
		catch(IOException e)
		{System.out.println("IOexception2");}
	}
}
