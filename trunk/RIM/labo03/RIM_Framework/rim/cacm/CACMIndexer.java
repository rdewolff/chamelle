package rim.cacm;

import rim.Indexer;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * An indexer for the CACM collection.
 */
public class CACMIndexer implements Indexer//, Comparator<String>
{	
	//HashMap contenant les stop words a eliminer des documents
	static HashMap<String, String> commonwords = new HashMap<String, String>();
	//Les deux Maps triees servant de memoire d'indexage
	static TreeMap<Integer, Object[][]> index = new TreeMap<Integer, Object[][]>();
	static TreeMap<String, Object[][]> indexInverse = new TreeMap<String, Object[][]>();
	//Variable dernier ID indexer
	static int lastId = 0;
	
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
				commonwords.put(mot,"");
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
		{System.out.println("Probl�me lors de la mise en m�moire des stop words...");}
	}
	
	/* (non-Javadoc)
	 * @see rim.Indexer#index(java.lang.Integer, java.lang.String)
	 */
	public void index(Integer id, String content){
	
		String tok = null;
		String phrase = null;
		Set keys = null;
		//Table des termes avec leur frequence
		HashMap<String, Integer> elements = new HashMap<String, Integer>();
		
		//Decapitalisation
		content = content.toLowerCase();
		//Suppression de la ponctuation
		content = content.replace("(","");
		content = content.replace(")","");
		content = content.replace(".","");
		content = content.replace(",","");
		content = content.replace(";","");
		content = content.replace(":","");
		content = content.replace("'s","");
		content = content.replace("\"","");
		
		//Tokenisation de la ligne passee en parametre
		StringTokenizer tokens = new StringTokenizer(content);
		
		//--Creation des indexs--//
		
		//Parcours des termes du document et sotckage de leur frequence
		while (tokens.hasMoreTokens())
		{
			tok = tokens.nextToken();
			//Selection des termes qui ne sont pas des stop words
			if(!commonwords.containsKey(tok))
			{
				elements.put(tok, elements.containsKey(tok)?elements.get(tok)+1:1);
			}
		}
		
		//Recuperation de la liste des termes
		keys = elements.keySet();
		//La liste des termes avec leur frequence que l'on va creer
		Object[][] list = new Object[keys.size()][2];
		int cpt = 0;
		
		//Construction de la ligne d'index correspondant au document en cours de traitement
		for(Object s: keys)
		{
			list[cpt][0] = s;
			list[cpt][1] = elements.get((String)s);
			cpt++;
		}
		//Stockage dans la variable statique
		index.put(id, list);
		
		//La liste des documents avec leur frequence que l'on va creer pour les termes
		for(Object s: keys)
		{
			//Si l'index inverse contient deja un terme, il faut recuperer son tableau
			//et y appondre le terme courant
			if(indexInverse.containsKey(s))
			{
				//Longueur du tableau existant dans l'index inverse
				int length = indexInverse.get((String)s).length;
				//Declaration du nouveau tableau
				Object[][] list2 = new Object[length+1][2];
				// Recuperation de l'ancien tableau
				Object[][] listRef = indexInverse.get((String)s);
				
				//Construction avec les anciennes valeurs
				for(int i=0; i<length; i++)
				{
					list2[i][0] = listRef[i][0];
					list2[i][1] = listRef[i][1];
				}
				//Ajout des nouvelles valeurs
				list2[length][0] = id;
				list2[length][1] = elements.get((String)s);
				//Remplacement de la ligne de l'index inverse
				indexInverse.put((String)s,list2);
			}
			//Sinon, on cree un tableau avec les valeurs courantes
			else
			{
				Object[][] list2 = new Object[1][2];
				list2[0][0] = id;
				list2[0][1] = elements.get((String)s);
				indexInverse.put((String)s,list2);
			}
		}
	}
	
	/**
	* Methode permettant de sauver les variables statiques d'indexs dans
	* leurs fichiers respectifs: "index.txt" et "index_inverse.txt"
	* Sauve aussi les deux indexs sous forme d'objet dans "index_object.txt"
	*/
	public void save()
	{
		Set keys = null;
		Set keys2 = null;
		String ligne = null;
		try
		{
			//Recuperation de la liste des lignes d'index
			keys = index.keySet();
			//Ouverture du fichier
			BufferedWriter os = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("index.txt")));
			
			//Remplissage du fichier d'index
			for(Object o: keys)
			{
				Object[][] tableau = new Object[index.get((Integer)o).length][2];
				tableau = index.get(o);
				ligne = "[" + o + "]{"; //Debut de ligne avec l'ID du document
				//Liste des terme/frequence
				for(int i=0; i<index.get((Integer)o).length; i++)
					ligne = ligne + "<" + tableau[i][0] + "," + tableau[i][1] + ">";
				ligne = ligne + "}\r\n";	
				os.write(ligne); //Ecriture de la ligne
	   		}
		    os.flush();
	        os.close();
			
			//Recuperation de la liste des lignes d'index
			keys2 = indexInverse.keySet();
			//Ouverture du fichier
			BufferedWriter os2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("index_inverse.txt")));
			
			//Remplissage du fichier d'index inverse
			for(Object o: keys2)
			{
				Object[][] tableau = new Object[indexInverse.get((String)o).length][2];
				tableau = indexInverse.get(o);
				ligne = "[" + o + "]{"; //Debut de ligne avec le terme
				//Liste des document/frequence
				for(int i=0; i<indexInverse.get((String)o).length; i++)
					ligne = ligne + "<" + tableau[i][0] + "," + tableau[i][1] + ">";
				ligne = ligne + "}\r\n";
				//Ecriture de la ligne
				os2.write(ligne);
	   		}
		    os2.flush();
	        os2.close();
			
			ObjectOutputStream os3 = new ObjectOutputStream(new FileOutputStream("index_object.txt"));
			//Ecriture des deux TreeMaps
			os3.writeObject(index);
			os3.writeObject(indexInverse);
			os3.flush();
			os3.close();
		}
		catch(FileNotFoundException e)
		{System.out.println("Filenfound");}
		catch(IOException e)
		{System.out.println("IOexception2");}
	}
	
	public void finalizeIndexation () {};
}
