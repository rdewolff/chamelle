package rim.cacm;
 
import rim.Indexer;
import java.util.*;
import java.io.*;


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
	static TreeMap<String, HashMap<Integer, Double>> indexInverse2 = 
		new TreeMap<String, HashMap<Integer, Double>>();
	
	//Nombre de documents indexés
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
			//Reinitialisation du fichier d'index
			f = new File("index2.txt");
			f.delete();
			//Reinitialisation du fichier d'index inverse
			f = new File("index_inverse2.txt");
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
	
		Set keys = null;
		double freqMax = 0.0;
		//Table des termes avec leur frequence
		HashMap<String, Integer> elements = new HashMap<String, Integer>();
		
		//Decapitalisation
		content = content.toLowerCase();
		
		//Tokenisation de la ligne passee en parametre tout en enlevant la ponctuation
		String[] tokens = content.split("[\\p{Punct} ]");
		
		//--Creation des indexs--//
		
		//Parcours des termes du document et sotckage de leur frequence
		for (String s: tokens) {
			//Selection des termes qui ne sont pas des stop words
			if(!commonwords.contains(s) && !s.equals("")) {
				elements.put(s, elements.containsKey(s)?elements.get(s)+1:1);
			}
		}
		
		//Recuperation de la liste des termes
		keys = elements.keySet();
		
		//Recherche de la frequence maximale
		for(Object s: keys) {
			if(elements.get(s) > freqMax)
				freqMax = elements.get(s);
		}
		
		//Table pour les termes/fraquences normalisees
		HashMap<String, Double> h = new HashMap<String, Double>();
		//Table pour les termes/frequences absolues
		HashMap<String, Integer> h2 = new HashMap<String, Integer>();
		
		//Recuperation de la liste des termes
		keys = elements.keySet();
		//Construction de la ligne d'index correspondant au document en cours de traitement
		for(Object s: keys)
		{
			//Memorisation des frequences normalisees
			h.put((String)s, (double)elements.get(s)/freqMax);
			//Memorisation des frequences absolues
			h2.put((String)s, elements.get(s));
		}
		
		//Stockage des frequences normalisees
		index.put(id, h);
		//Stockage des frequences
		frequences.put(id, h2);
		
		//La liste des documents avec leur frequence normalisee que 
		//l'on va creer pour les termes
		for(Object s: keys)
		{
			//Si l'index inverse contient deja un terme, il faut rajouter le 
			//terme courant et sa frequence normalisee
			if(indexInverse.containsKey(s))
			{
				indexInverse.get(s).put(id, 
						(double)elements.get((String)s)/freqMax);
			}
			//Sinon, on cree une map avec les valeurs courantes
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
		Set _keys = null;
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
			*Ecriture du fichier d'index frequence normalisee
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
			//Fin de l'ecriture du fichier d'index avec les frequences normalisees
		    os.flush();
	        os.close();
	        
	        //Recuperation de la liste des lignes d'index inverse
			keys = indexInverse.keySet();
			
			//Ouverture du fichier d'index inverse des frequences normalisees
			os = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("index_inverse.txt")));
			
			/*---------------------------------------------------------------
			*Ecriture du fichier d'index inverse de frequences normalisees
			---------------------------------------------------------------*/
			for(Object o: keys)
			{
				ligne = "[" + o + "]{"; //Debut de ligne avec le terme
				_keys = indexInverse.get(o).keySet();
				
				//Liste des document/frequence
				for(Object o2: _keys)
				{
					ligne = ligne + "<" + (Integer)o2 + "," + indexInverse.get(o).get(o2) + ">";
				}

				ligne = ligne + "}\r\n";
				//Ecriture de la ligne
				os.write(ligne);
	   		}
			//Fin de l'ecriture du fichier d'index inverse avec les frequences normalisees
		    os.flush();
	        os.close();
	        
	        //Recuperation de la liste des documents
	        keys = index.keySet();
	        
	        //Ouverture du fichier des tf-idf normalisees
			os = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("index2.txt")));
	        
	        /*--------------------------------------------------
	        *Mise a jour du treemap d'index de fréquences tf-idf
	        --------------------------------------------------*/
			/*--------------------------------------------------
			*Ecriture du fichier d'index de fréquences tf-idf
			--------------------------------------------------*/
			for(Object o: keys) {
				//Le tfIdf max de tous les termes
		        double maxTfIdf = 0.0;
		        //La variable du tf-idf
				double tfidf = 0.0;
		        //Les cles representant les termes dans un document
				_keys = index.get(o).keySet();
				//Table intermediaire pour les termes et leur tf-idf
				HashMap<String, Double> h = new HashMap<String, Double>();
				
				//Calcul des tfidf et de tfidf maximum
				for(Object o2: _keys) {
					//Nombre de documents contenant le terme courant
					int ni = indexInverse.get(o2).keySet().size();
					//TF-IDF
					tfidf = (Math.log((double)frequences.get(o).get(o2)+1.0)/Math.log(2))*
							(Math.log(n/ni)/Math.log(2));
					//Mise à jour du max TF-IDF
					if(tfidf > maxTfIdf) //Mise a jour tfidf Max
						maxTfIdf = tfidf;
					//Stockage du terme avec son TF-IDF
					h.put((String)o2, tfidf);
				}
				
				//Normalisation du tfidf
				for(Object o2: _keys) {
					h.put((String)o2, h.get(o2)/maxTfIdf);
				}
				//Stockage dans l'objet d'index
				index2.put((Integer)o, h);
				
				//Recuperation de la liste des termes du document courant
				_keys = index2.get(o).keySet();
				ligne = "[" + o + "]{"; //Debut de ligne avec l'ID du document
				
				//Liste des termes/frequence
				for(Object o2: _keys) {
					ligne = ligne + "<" + (String)o2 + "," + index2.get(o).get(o2) + ">";
				}
				
				ligne = ligne + "}\r\n";	
				os.write(ligne); //Ecriture de la ligne
	   		}
			//Fin d'ecriture et fermeture du fichier d'index avec les TF-IDF normalisés
		    os.flush();
	        os.close();
			
	        //Recuperation de la liste des termes
	        keys = indexInverse.keySet();
	        
	        //Ouverture du fichier des tf-idf normalisees
			os = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("index_inverse2.txt")));
	        
			/*----------------------------------------------------------
	        *Mise a jour du treemap d'index inverse de fréquences tf-idf
	        ----------------------------------------------------------*/
			/*----------------------------------------------------------
			*Remplissage du fichier d'index inverse de fréquences tf-idf
			----------------------------------------------------------*/
			for(Object s: keys) {
				//Recuperation des documents concernes par le terme courant
				_keys = indexInverse.get(s).keySet();
				//Stockage des documents avec leur TF-IDF normalises contenant le terme courant
				for(Object o: _keys) {
					//Si l'index inverse contient deja un terme, il faut rajouter le 
					//terme courant et sa frequence normalisee
					if(indexInverse2.containsKey(s)) {
						indexInverse2.get(s).put((Integer)o, index2.get(o).get(s));
					}
					//Sinon, on cree une map avec les valeurs courantes
					else {
						HashMap<Integer, Double> h = new HashMap<Integer, Double>();
						h.put((Integer)o, index2.get(o).get(s));
						indexInverse2.put((String)s,h);
					}
				}
				//Recuperation de la liste des documents contenant le terme courant (s)
				_keys = indexInverse2.get(s).keySet();
				ligne = "[" + s + "]{"; //Debut de ligne avec le terme traite
				
				//Liste des terme/frequence
				for(Object o: _keys)
				{
					ligne = ligne + "<" + o + "," + indexInverse2.get(s).get(o) + ">";
				}
				
				ligne = ligne + "}\r\n";	
				os.write(ligne); //Ecriture de la ligne
			}
			//Fin d'ecriture et fermeture du fichier d'index inverse avec TF-IDF normalise
		    os.flush();
	        os.close();
			
			ObjectOutputStream os2 = new ObjectOutputStream(new FileOutputStream("index_object.txt"));
			//Ecriture des deux TreeMaps
			os2.writeObject(index);
			os2.writeObject(indexInverse);
			os2.writeObject(index2);
			os2.writeObject(indexInverse2);
			os2.flush();
			os2.close();
		}
		catch(FileNotFoundException e)
		{System.out.println("Filenfound");}
		catch(IOException e)
		{System.out.println("IOexception2");}
	}
}
