package rim;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

public class WebSpider {

	// start page
	String startPage = "http://www.heig-vd.ch";
	boolean stayInDomain = true;
	
	LinkedList<String> visitedUrls = new LinkedList<String>();
	
	private LinkedList<String> CreateLinkFromTextArray( String textArray ) {

		// remove first and last braket ( [ and ] )
		textArray = textArray.substring(1, textArray.length()-1);
		
		
		// put the url in an array
		String url[] = textArray.split(",");
		
		// debug
		System.out.println("Number of links (array): " + url.length);
		
		LinkedList<String> newUrl = new LinkedList<String>();
		String urlFound = null;
		
		for (String u : url) {					
			u = u.trim();
			if (u.charAt(0) == '/' || u.charAt(0) == '#') {
				urlFound = startPage + u;
			} else {
				urlFound = u;
			}
			
			// debug, display found url
			System.out.println(urlFound);
			
			// add it to the linkedlist
			newUrl.add(urlFound);
		}
				
		return newUrl;
	}
	
	public WebSpider() {
		
		try {
			
			// start crawling on the main page
			WebParser.ParsedData pd = WebParser.parseURL(new URL(startPage));
			
			System.out.println("Status code  : " + pd.getStatusCode());
			System.out.println("Content type : " + pd.getContentType());
			System.out.println("Page content : " + pd.getPageContent());
			System.out.println("Page hrefs   : " + pd.getPageHrefs());
			
			LinkedList<String> newUrl = CreateLinkFromTextArray(pd.getPageHrefs().toString());
						
			System.out.println("-----------------------------");
			
			// iterate through all the links
			String urlToVisit;
			LinkedList<String> hashVisitedPages;
			LinkedList<String> visitedPages;
			
			while (newUrl.size() > 0) {
				urlToVisit = newUrl.getFirst();
				
				pd = WebParser.parseURL(new URL(urlToVisit));
				if (pd.getStatusCode() == 200) {
					System.out.println( newUrl.getFirst() + " is accessible");
				} else {
					System.out.println( newUrl.getFirst() + " is NOT accessible");
				}
				newUrl.removeFirst(); 
			}
			
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		WebSpider ws = new WebSpider();
		
		
	}

}
