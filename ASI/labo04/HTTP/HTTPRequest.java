package HTTP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * Representation et gestion d'une requete HTTP.
 * @author Cyril Maulini
 *
 */
class HTTPRequest {
	/**
	 * Definition de toutes les methodes du protocole HTTP.
	 * @author Cyril Maulini
	 *
	 */
	final static class method {
		final static String GET    = "GET";
		final static String HEAD   = "HEAD";
		final static String POST   = "POST";
		final static String UNKNOW = "UNKNOW";
	}
	
	// La ligne de requete
	private String requestLine;
	// La collection de tous les entetes
	private LinkedList<String> headerLine;
	// QQCH pour le corps
	
	/**
	 * Construire une requete vide.
	 *
	 */
	HTTPRequest() {
		headerLine = new LinkedList<String>();
	}
	
	/**
	 * Lire une requete dans un flux.
	 * @param is Le flux dans lequel se trouve la requete HTTP.
	 * @throws Exception
	 */
	void readFromStream(InputStream is) throws Exception {
		// Fixer un filtre sur le flux entrant
		BufferedReader br = new BufferedReader (
				new InputStreamReader(is));
		
		// Extraire la ligne d'entete
		requestLine = br.readLine();
		
		// Extraire tous les entetes
		String str;
		while ((str = br.readLine()).length() > 0) {
			headerLine.add(str);
		}
		
		// QQCH pour le corps
	}
	
	/**
	 * Verifier que la ligne de requete comporte bien
	 * les trois informations.
	 * @return Vrai si 3 informations sont dans la ligne
	 * de requetes.
	 */
	boolean goodRequestFormat() {
		return requestLine.split(" ").length == 3;
	}
	/**
	 * Obtenir la methode de la ligne de requete.
	 * @return La methode invoquee.
	 */
	String getMethod() {
		// Extraction du nom de la methode
		String methodName = requestLine.split(" ")[0];
	
		// Recherche de la bonne methode
		if (methodName.equals(method.GET))
			return method.GET;
		
		else if (methodName.equals(method.HEAD))
			return method.HEAD;
		
		else if (methodName.equals(method.POST))
			return method.POST;
		
		else
			return method.UNKNOW;
	}
	
	/**
	 * Obtenir l'URI de la ligne de requete.
	 * @return L'URI demandee.
	 */
	String getURI() {
		// Extraction de l'URI
		return requestLine.split(" ")[1];
	}
	
	/**
	 * Obtenir la version HTTP de la ligne de requete.
	 * @return La version du protocole HTTP.
	 */
	String getVersion() {
		//Extreation de la version
		return requestLine.split(" ")[2];
	}
	
	/**
	 * Obtenir le contenu de la requete HTTP.
	 */
	public String toString() {
		// La ligne de requete
		String str = requestLine + '\n';
		
		// Tous les entetes
		for(String header : headerLine) {
			str += header + '\n';
		}
		
		// QQCH pour le corps
		
		return str;
	}

}
