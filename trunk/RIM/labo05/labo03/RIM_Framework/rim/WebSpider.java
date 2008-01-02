package rim;

import java.io.IOException;
import java.net.URL;

public class WebSpider {

	// start page
	static String startPage = "http://www.heig-vd.ch";
	static boolean stayInDomain = true;
	
	public WebSpider() {
		try {
			WebParser.ParsedData pd = WebParser.parseURL(new URL(startPage));
			System.out.println("Status code  : " + pd.getStatusCode());
			System.out.println("Content type : " + pd.getContentType());
			System.out.println("Page content : " + pd.getPageContent());
			System.out.println("Page hrefs   : " + pd.getPageHrefs());
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
