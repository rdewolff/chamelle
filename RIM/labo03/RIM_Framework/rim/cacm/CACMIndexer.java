package rim.cacm;

import rim.Indexer;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * An indexer for the CACM collection.
 */
public class CACMIndexer implements Indexer {
	
	static HashMap commonwords = new HashMap();
	
	static
	{
		String mot;
		try
      {
			// Ouverture du fichier de stop words
			FileInputStream in = new FileInputStream("rim/ressources/common_words");
			
			BufferedReader d = new BufferedReader(new InputStreamReader(in));
			while((mot=d.readLine())!= null)
			{ 	
				commonwords.put(mot,"");
			}
      }
      catch(ClassCastException e)
      {System.out.println("ClassCast");}
      catch(FileNotFoundException e)
	  {System.out.println("Le fichier <common_words> n'existe pas");}
	  catch(IOException e)
	  {System.out.println("Probl�me lors de la mise en memoire des stop words...");}
	}
	
	/* (non-Javadoc)
	 * @see rim.Indexer#index(java.lang.Integer, java.lang.String)
	 */
	public void index(Integer id, String content){
		String tok;
		String phrase;
		HashMap table = new HashMap(100);
		content = content.toLowerCase();
		content = content.replace("(","");
		content = content.replace(")","");
		content = content.replace(".","");
		content = content.replace(",","");
		content = content.replace(";","");
		content = content.replace(":","");
		content = content.replace("'s","");
		content = content.replace("-"," ");
		
		StringTokenizer tokens = new StringTokenizer(content); 
		while (tokens.hasMoreTokens()) 
		{ 
			tok = tokens.nextToken();
			if(!commonwords.containsKey(tok))
			System.out.println(tok); 
		} 
		BufferedReader inFromUser = 
          new BufferedReader(new InputStreamReader(System.in)); 
		 try{phrase = inFromUser.readLine();}
		 catch(IOException e){}
		//System.out.println(content);
	}
	
	public void finalizeIndexation () {};
}
