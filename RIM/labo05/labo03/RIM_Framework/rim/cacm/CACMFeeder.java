package rim.cacm;
 
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;

import rim.Feeder;
import rim.Indexer;

/**
 * A feeder for the CACM collection.
 * @author Florian Poulin <i>(florian.poulin at heig-vd.ch)</i>
 */
public class CACMFeeder implements Feeder {

	/* (non-Javadoc)
	 * @see rim.Feeder#parseCollection(java.net.URI, rim.Indexer)
	 */
	public int parseCollection(URI collection, Indexer indexer) {

		// open file stream
		BufferedReader br = null;
		try {
			br = new BufferedReader (new FileReader(collection.toString()));
		} catch (FileNotFoundException e) {
			System.err.println("Unable to read from '"+collection+"' : " + e);
			return 0;
		}
		
		// containers for the document parts
		int docID = 0;
		String docContent = "";
		
		// some usefull variables
		boolean firstDoc = true;
		boolean dotRead  = false;
		char currentTopic = 0;
		String readLine = null;
		int count = 0;
		
		// parse collection
		try {
			while ((readLine = br.readLine()) != null) {
				if (dotRead = (readLine.length() > 0 && readLine.charAt(0) == '.')) {
					
					// keep the topic letter
					currentTopic = readLine.charAt(1);
					
					// identifier : the previous document must be indexed
					if (currentTopic == 'I') {
						
						// don't index the first one
						if (firstDoc) {
							firstDoc = false;
						} else {
							indexer.index(docID, docContent);
							count++;
						}
						
						// reinit the containers for the document to come
						docID = Integer.parseInt(readLine.substring(2).trim());
						docContent  = new String();
					}
				}
				
				// add read line to the current topic (if title or summary)
				if (!dotRead && currentTopic == 'T' || currentTopic == 'W')
					docContent += " " + readLine.trim();
			}
			br.close();
			
		} catch (NumberFormatException e) {
			System.err.println("malformed document id : " + e);
			System.exit(0);
		} catch (IOException e) {
			System.err.println("oups : " + e);
			e.printStackTrace();
			System.exit(0);
		}
		
		// index the last document
		indexer.index(docID, docContent);
		count++;

		return count;
	}
}
