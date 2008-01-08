package rim;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import rim.cacm.CACMIndexer;

public class WebSpider {

	// members
	private String startPage;
	private LinkedList<String> linkToVisit = new LinkedList<String>();
	private LinkedList<String> visitedUrls = new LinkedList<String>();

	/** 
	 * Add the links from a String list to the local queue of link to visit 
	 * @param textArray
	 */
	private void saveLinks( String currentLocation, String textArray ) {

		System.out.println("CURRENT LOCATION MOTHERFUCKER : " + currentLocation);
		
		// current location : remove extra information, keep last folder
		// if contains more than just "http://"
		if (currentLocation.lastIndexOf("/") > "http://".length())
			currentLocation = currentLocation.substring(0, currentLocation.lastIndexOf("/"));
		
		// remove first and last braket ( [ and ] ) returned by the webparser
		textArray = textArray.substring(1, textArray.length()-1);
		
		// put the url in an array
		String url[] = textArray.split(",");

		// to store the temp url
		String urlFound = null; // full URL
		String host = null; // name of the host only

		// count
		int badLinks=0;

		// loop through all the links
		for (String u : url) {

			// remove extra spaces
			u = u.trim();

			// if the link contains more than a caracater and does
			// not contain the string "null"
			if (u.length() > 0 && u.indexOf("null") != 0) {
				
				// reconstruct the URL if it's a relative link
				if (u.charAt(0) == '/' || u.charAt(0) == '#') {
					urlFound = currentLocation + u;
				} else if ( u.indexOf("http://") != -1) {
					urlFound = u;
				} else if ( u.indexOf("javascript:") == 0 || u.indexOf("mailto:") == 0){
					// ignore javascript
					urlFound = startPage;
				} else { // normal link, just add the host name
					urlFound = currentLocation + "/" + u;
				}
				
				// debug, display found url
				//System.out.print(" - " + urlFound);

				// store just the host name
				if (urlFound != null && urlFound.length() > 0) {
					try {
						host = (new URL(urlFound).getHost().toString());
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}

				// check the link : we don't add an already visited page or
				// already in the collection or out of the domain
				if (!linkToVisit.contains(urlFound)) {
					if (!visitedUrls.contains(urlFound)) {
						if (host != null && host.contains(".")) {
							if (getDomain(host).equals((String)getDomain(startPage))) {
								// add the found url in the queue of link to visit
								linkToVisit.addLast(urlFound);
							} //else { System.out.println("Error1"); badLinks++;}
						} //else { System.out.println("Error2"); badLinks++;}
					} //else { System.out.println("Error3");badLinks++;}
				} //else { System.out.println("Error4"); badLinks++;}

				//System.out.println(urlFound + " : " + getDomain(host) + " # " + getDomain(startPage));
			}
		}

		// debug
		System.out.println("\nNumber of links (array): " + url.length + " (not considered: " + badLinks + ")");
	}

	/**
	 * Returns the domain name from a host name
	 * Ex: http://age.heig-vd.ch will return heig-vd.ch
	 * 
	 * Note: only a host name will work. It must NOT contains pages or folder
	 * after it's name.
	 * 
	 * @param  The url you want to get the domain
	 * @return The domain name of the given url
	 */
	private String getDomain(String url) {
		String domain = null;
		String dotCom = null;
		if (url != null && url.length() > 0) {
			domain = url.substring(0, url.lastIndexOf("."));
			domain = domain.substring(domain.lastIndexOf(".")+1, domain.length());
			dotCom = url.substring(url.lastIndexOf("."), url.length());

		}
		// debug
		//System.out.println("URL: " + url + " -- Domain: " + domain + dotCom);
		return domain + dotCom;
	}


	/**
	 * Returns the domain name of the URL givent in parameter
	 * @param url
	 * @return
	 */
	private String removeHttpWww( String url ) {		
		if (url.indexOf("www.") != -1) {
			return url.substring(url.indexOf("www.")+4, url.length());
		} else {
			return url.substring(url.indexOf("http://")+7, url.length());
		}
	}

	/**
	 * Save the hash of the page in a list
	 * @param ls
	 * @param pageContent
	 */
	private void saveHashPage( LinkedList<Integer> ls, String pageContent) {
		ls.add(pageContent.hashCode());
	}

	/**
	 * Check that the hash of a give page doesn't already exist in the given list
	 * @param ls
	 * @param pageContent
	 * @return
	 */
	private boolean isHashPageKnown( LinkedList<Integer> ls, String pageContent ) {
		return ls.contains(pageContent.hashCode());
	}

	/**
	 * Increment the counter of the given domain in the given HashMap. If the 
	 * domain is not yet in the list, will be initialized at 1.
	 * @param ls
	 * @param domain
	 */
	private void saveDomain( HashMap<String, Integer> ls, String domain ) {
		if (ls.containsKey(domain)) {
			// add one element in this domain
			ls.put(domain, ls.get(domain)+1);
		} else {
			// init the counter for this domain at 1 as it's the first
			ls.put(domain, 1); 
		}
	}

	/**
	 * Crawl the startpage given (we can call that "spidering")
	 * @param startPage
	 */
	public WebSpider( String startPage ) {

		this.startPage = startPage;

		linkToVisit.add(startPage); // add the root url

		try {

			// variables

			// store the hash of the visited pages
			LinkedList<Integer> hashVisitedPages = new LinkedList<Integer>(); 
			// store the sub domains and number of pages visited
			HashMap<String, Integer> subDomainCounter = new HashMap<String, Integer>();

			int visitedLinks = 0;
			int invalidLinks = 0;
			int fatalError = 0;
			int otherThanHTML = 0;
			Integer error404 = 0;
			WebParser.ParsedData pd;

			String visitedUrl;
			URL Url;
			Set<String> set = null;
			
			CACMIndexer index = new CACMIndexer();
			
			// links iteration

			// iterate through all the links in the queue
			while (linkToVisit.size() > 0) {

				// we use it as a queue (cf. RIM course algorithm)
				visitedUrl = linkToVisit.getFirst();

				System.out.print("*** Trying " + visitedUrl);

				// check if the link is valid to avoid error
				if (visitedUrl != null && visitedUrl.length() > 0 && visitedUrl.indexOf("http://") == 0) {

					// make an URL from the url's String
					Url = new URL(visitedUrl);

					// open the page with the web parser (this might take time)
					pd = WebParser.parseURL(Url);

					if (pd != null) {

						// store a counter for the domain of this page
						saveDomain(subDomainCounter, Url.getHost());

						// valid page, we count it
						visitedLinks++;
						System.out.println( " OK");
 
						// test if opened link is valid
						if (pd.getStatusCode() == 200) {

							// check if it's an HTML page
							if (pd.getContentType().equals("text/html")) {

								// if the page has not yet been visited
								if (!isHashPageKnown(hashVisitedPages, pd.getPageContent())) {

									// add the links of the visited page (if any)
									if (pd.getPageHrefs() != null && pd.getPageHrefs().toString().trim().length() > 0) {
										saveLinks(visitedUrl, pd.getPageHrefs().toString());
									}
									
									// store the hash of the current page beeing visited (if possible)
									if (pd.getPageContent() != null) {
										saveHashPage(hashVisitedPages, pd.getPageContent());
									}

									// index it's content
									index.index(visitedUrl, pd.getPageContent());

								} else {
									System.out.println("*** Page already visited!! ***");
								}

							} else {
								// it's not an HTML page, we just count it
								otherThanHTML++;
							}

						} else if ( pd.getStatusCode() == 404) {

							error404++;

						} else {

							// error on page
							System.out.println( visitedUrl + " is NOT accessible (" + invalidLinks++ + ") (1)");
						}

					} else {
						fatalError++;
						System.out.println("Server does not exist!!");
						fatalError++;
						System.out.println("Server does not exist!!");
					}
					
					// store the page as beeing visited
					visitedUrls.add(visitedUrl);

				} else {
					System.out.println( visitedUrl + " is NOT accessible (" + invalidLinks++ + ") (2)");
				}

				// remove the page we just visited from the queue
				linkToVisit.removeFirst();

				// display
				System.out.println("*** Global stats ***");
				System.out.println("Nombre de liens restant a visiter : " + linkToVisit.size());

				set = subDomainCounter.keySet();
				System.out.println("Domaines ("+set.size()+") : ");
				for (String s : set) {
					System.out.println("- " + s + " (" + subDomainCounter.get(s) + ")");
				}

				System.out.println("TOTAL Visited links: " + visitedLinks);
				System.out.println("Not HTML page: " + otherThanHTML);
				System.out.println("Invalid links: " + invalidLinks);
				System.out.println("Not found (404): " + error404);
				System.out.println("Site down: " + fatalError);


			}
			
			// finish the indexation
			index.finalizeIndexation();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// start crawling
		new WebSpider("http://www.habanasalsa.ch");

		// finished
		System.out.println("----------");
	}

}
