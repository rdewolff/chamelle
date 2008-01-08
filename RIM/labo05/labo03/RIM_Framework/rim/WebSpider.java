package rim;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class WebSpider {

	// members
	String startPage;
	LinkedList<String> linkToVisit = new LinkedList<String>();
	LinkedList<String> visitedUrls = new LinkedList<String>();

	/** 
	 * Add the links from a String list to the local queue of link to visit 
	 * @param textArray
	 */
	private void getLinks( String textArray ) {

		// remove first and last braket ( [ and ] )
		textArray = textArray.substring(1, textArray.length()-1);

		// put the url in an array
		String url[] = textArray.split(",");

		// debug
		System.out.println("Number of links (array): " + url.length);

		// to store the temp url
		String urlFound = null;
		String host = null;

		// count
		int i=1;

		// loop through all the links
		for (String u : url) {

			// remove extra spaces
			u = u.trim();

			if (u.length() > 0) {
				// reconstruct the URL if it's a relative link
				if (u.charAt(0) == '/' || u.charAt(0) == '#') {
					urlFound = startPage + u;
				} else if ( u.indexOf("http://") != -1) {
					urlFound = u;
				} 
			}

			// debug, display found url
			// System.out.print("["+(i++)+"] " + urlFound );

			// store just the host name
			if (urlFound != null && urlFound.length() > 3) {
				try {
					host = (new URL(urlFound).toString());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}

			// check the link : we don't add an already visited page or
			// already in the collection or out of the domain
			if (!linkToVisit.contains(urlFound) && 
					!visitedUrls.contains(urlFound) && 
					host != null && 
					host.indexOf(removeHttpWww(startPage)) != -1 ) {
				// add the found url in the queue of link to visit
				linkToVisit.addLast(urlFound);
			}

		}
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

	public WebSpider( String startPage ) {

		this.startPage = startPage;

		linkToVisit.add(startPage); // add the root url

		try {

			// variables

			// store the hash of the visited pages
			LinkedList<Integer> hashVisitedPages = new LinkedList<Integer>(); 
			// store the sub domains and number of pages visited
			HashMap<String, Integer> subDomainCounter = new HashMap<String, Integer>();
			int validLinks = 0;
			int invalidLinks = 0;
			int fatalError = 0;
			Integer error404 = 0;
			WebParser.ParsedData pd;

			// iterate through all the links (in the domain) 
			String visitedUrl;
			URL Url;
			Set<String> set = null;

			while (linkToVisit.size() > 0) {

				// we use it as a queue (cf. RIM course algorithm)
				visitedUrl = linkToVisit.getFirst();

				System.out.println("Trying *** " + visitedUrl + " ***");

				// check if the link is valid to avoid error
				if (visitedUrl != null && visitedUrl.length() > 0 && visitedUrl.indexOf("http://") == 0) {

					// make an URL from the url's String
					Url = new URL(visitedUrl);

					// open the page with the web parser (this might take time)
					pd = WebParser.parseURL(Url);

					if (pd != null) {

						// store a counter for the domain of this page
						saveDomain(subDomainCounter, Url.getHost());

						// test if link is valid
						if (pd.getStatusCode() == 200) {

							// check if it's an HTML page
							if (pd.getContentType().equals("text/html")) {

								// page valide ---
								System.out.println( "*** Page " + visitedUrl + " is accessible (" + validLinks++ + ")");

								// if the page has not yet been visited
								if (!isHashPageKnown(hashVisitedPages, pd.getPageContent())) {

									// add the links of the visited page (if any)
									if (pd.getPageHrefs() != null && pd.getPageHrefs().toString().trim().length() > 0) {
										getLinks(pd.getPageHrefs().toString());
									}

									// store the hash of the current page beeing visited (if possible)
									if (pd.getPageContent() != null) {
										saveHashPage(hashVisitedPages, pd.getPageContent());
									}

									// index it's content
									// TODO with the other tools (CACM...)

								} else {
									System.out.println("****************************** PAGE ALREADY VISITED!");
								}
								// if not an HTML document, we just count it
							} else {
								// TODO
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
					}
					// store the page as beeing visited
					visitedUrls.add(visitedUrl);

				} else {
					System.out.println( visitedUrl + " is NOT accessible (" + invalidLinks++ + ") (2)");
				}

				// affichages --------------------------------------------------
				System.out.println("Page en cours : " + visitedUrl );

				linkToVisit.removeFirst();
				System.out.println("Nombre de liens restant a visiter : " + linkToVisit.size());

				set = subDomainCounter.keySet();
				System.out.println("Domaines ("+set.size()+") : ");
				for (String s : set) {
					System.out.println("dom : " + s + " (" + subDomainCounter.get(s) + ")");
				}

				// display the counter
				System.out.println("Nombre de liens valide(s) : " + validLinks);
				System.out.println("Nombre de liens invalide(s) : " + invalidLinks);
				System.out.println("Page not found (404 error) : " + error404);
				System.out.println("Site dead : " + fatalError);

			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// start crawling
		WebSpider ws = new WebSpider("http://www.heig-vd.ch");

		// finished
		System.out.println("----------");
		System.out.println("EOF");
	}

}
