package rim;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class WebSpider {

	String startPage;
	LinkedList<String> linkToVisit = new LinkedList<String>();
	LinkedList<String> visitedUrls = new LinkedList<String>();


	private void addLinkWebParser( String textArray ) {

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

		// loop through the URL
		for (String u : url) {

			// remove extra spaces
			u = u.trim();

			if (u.length() > 0) {
				// reconstruct the URL
				if (u.charAt(0) == '/' || u.charAt(0) == '#') {
					urlFound = startPage + u;
				} else if ( u.indexOf("http://") != -1) {
					urlFound = u;
				} 
			}

			// debug, display found url
			System.out.print("["+(i++)+"] " + urlFound );

			// store just the host name
			if (urlFound != null && urlFound.length() > 3) {
				try {
					host = (new URL(urlFound).toString());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}

			// check link : if we don't add an already visited page or
			// already in the collection or out of the domain
			if (!linkToVisit.contains(urlFound) && !visitedUrls.contains(urlFound) && host != null) {
				// and that the link is in the same domain as the main page
				if (host.indexOf(removeHttpWww(startPage)) != -1 ) {
					// add the found url in the collection
					linkToVisit.addLast(urlFound);
					System.out.println(" (Kept in list)");
				} else {
					System.out.println(" (Out of domain!)");
				}
			} else {
				System.out.println(" (Already in list!)");
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

	private boolean isInDomain( String url, String domain ) {

		return true;
	}

	private void saveHashPage( LinkedList<Integer> ls, String pageContent) {
		ls.add(pageContent.hashCode());
	}

	private void saveDomaine( HashMap<String, Integer> ls, String domain ) {
		Set<String> tmpSet = null;
		if (ls.containsKey(domain)) {
			// add one element in this domain
			ls.put(domain, ls.get(domain)+1);

		} else {
			ls.put(domain, 1); // add one element of this domain
		}
	}

	private boolean isPageAlreadyVisited( LinkedList<Integer> ls, String pageContent ) {
		return ls.contains(pageContent.hashCode());
	}

	public WebSpider( String startPage ) {

		this.startPage = startPage;

		linkToVisit.add(startPage); // add the root url

		try {

			// needed variables
			LinkedList<Integer> hashVisitedPages = new LinkedList<Integer>(); // store the hash of the visited pages
			HashMap<String, Integer> subDomainCounter = new HashMap<String, Integer>(); // store the sub domains and number of pages visited
			int validLinks = 0;
			int invalidLinks = 0;
			Integer errors = 0;
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
				if (visitedUrl != null && visitedUrl.length() > 0 && visitedUrl.indexOf("http://") != -1) {

					// make an URL from the url's String
					Url = new URL(visitedUrl);

					// open the page with the web parser (this might take time)
					pd = WebParser.parseURL(Url);
					
					// store a counter for the domain of this page
					saveDomaine(subDomainCounter, Url.getHost());

					// test if link is valid
					if (pd.getStatusCode() == 200) {

						// check if it's an HTML page
						if (pd.getContentType().equals("text/html")) {
							
							// page valide ---
							System.out.println( "*** Page " + visitedUrl + " is accessible (" + validLinks++ + ")");

							// add the links of the visited page (if any)
							if (pd.getPageHrefs() != null && pd.getPageHrefs().toString().trim().length() > 3) {
								addLinkWebParser(pd.getPageHrefs().toString());
							}

							// store the hash of the current page beeing visited (if possible)
							if (pd.getPageContent() != null) {
								saveHashPage(hashVisitedPages, pd.getPageContent());
							}

							// index it's content
							// TODO with the other tools (CACM...)

							// if not an HTML document, we just count it
						} else {
							// TODO
						}

					} else {

						// error on page
						System.out.println( visitedUrl + " is NOT accessible (" + invalidLinks++ + ")");

					}
					// store the page as beeing visited
					visitedUrls.add(visitedUrl);

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

			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		WebSpider ws = new WebSpider("http://www.heig-vd.ch");

		System.out.println("----------");
	}

}
