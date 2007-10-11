package rim;

import rim.cacm.CACMFeeder;
import rim.cacm.CACMIndexer;
import rim.cacm.CACMRetriever;

/**
 * Second laboratory for the RIM course (Multimedia Information Retrieval). This work
 * is proposed by the University of Applied Science of Western Switzerland (HEIG-VD).
 * <br/><br/>
 * The main method must perform the following tasks :
 * <br/><ul>
 * <li>Build an URI pointing to the CACM collection ('cacm.all');</li>
 * <li>Parse and index the CACM collection using a {@link CACMFeeder} instance
 * and a {@link CACMIndexer} instance;</li>
 * <li>Provide a basic search utility using a {@link CACMRetriever} instance.</li>
 * </ul>
 * If you have any question, please ask the assistant or the professor.<br/>
 * Enjoy !
 */
public class Labo2 {
   	
	/**
	 * Main entry point.
	 * @param args console arguments.
	 */
	public static void main (String[] args) {
		
		CACMIndexer i = new CACMIndexer();
		CACMFeeder c = new CACMFeeder();
		c.parseCollection(java.net.URI.create("rim/ressources/cacm.all"), i);
	}
}
