package rim;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * <b>NOT ROBUST</b> implementation of a basic Web parser. Provides a static method 
 * for parsing the content of a given URL and a nested class to encapsulate the 
 * parsed data (HTTP status code, content type, page content and hrefs).
 * 
 * @author Florian Poulin <i>(florian.poulin at heig-vd.ch)</i>
 * @version December 2007
 */

public class WebParser {
	
	/**
	 * A nested class to encapsulate parsing results (HTTP status code, content type,
	 * page content and hypertext references). Page content and hrefs may not be
	 * defined (null).
	 */
	public static class ParsedData {
		
		// members
		private Integer statusCode;
		private String contentType;
		private String pageContent    = null;
		private Set<String> pageHrefs = null;
		
		// constructor
		public ParsedData (int statusCode,
							  String contentType) {
			
			this.statusCode  = statusCode;
			this.contentType = contentType;
		}
		
		// getters
		public Integer getStatusCode()    { return this.statusCode; }
		public String getContentType()    { return this.contentType; }
		public String getPageContent()    { return this.pageContent; }
		public Set<String> getPageHrefs() { return this.pageHrefs; }
		
		// setters
		void setpageContent(String pageContent)  { this.pageContent = pageContent; }
		void setpageHrefs(Set<String> pageHrefs) { this.pageHrefs = pageHrefs; }
	}
	
	/**
	 * A private nested class to act as parser callback.
	 */
	private static class Callback extends ParserCallback {
		
		// members
		private StringBuffer contentBuffer = new StringBuffer();
		private Set<String> hrefs = new HashSet<String>();
		
		// override the default implementation (4 methods)
		public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
			if (t.equals(HTML.Tag.A))
				hrefs.add((String) a.getAttribute(HTML.Attribute.HREF));
		}
		public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
			contentBuffer.append(' ');
		}
		public void handleText(char[] data, int pos) {
			contentBuffer.append(data);
		}
		public void handleEndTag(HTML.Tag t, int pos) {
			contentBuffer.append(' ');
		}
		
		// public getters
		public String      getContent() { return contentBuffer.toString(); }
		public Set<String> getHrefs()   { return hrefs; }
	}

	/**
	 * Parses the given URL and returns data about the URL encapsulated in a 
	 * {@link ParsedData} instance (status code, content type, and possibly the page
	 * content and hrefs).<br>
	 * The page content and hrefs are only defined if the status code is 200 (meaning
	 * that the page could be returned properly) and the content type is "text/html"
	 * (meaning that the page is actually a Web page, and not a pdf file or 
	 * whatever).<br>
	 * <br>
	 * <i>Notice that this implementation fixes the <b>CharSet problem</b> encoutered
	 * last years ...</i>
	 */
	public static ParsedData parseURL (URL url) 
	throws UnsupportedEncodingException, IOException {
		
		// added "dots" to better follow the progress
		System.out.print(" [.");
		// define parsing utilities
		ParserDelegator parser = new ParserDelegator();
		System.out.print(".");
		Callback callback = new Callback();
		System.out.print(".");

		// open connection
		URLConnection connection = url.openConnection();
		System.out.print(".");
		// connection.setConnectTimeout(5000); // timeout in ms
		try {
		connection.connect();
		} catch (Exception e) {
			return null;
		}
		System.out.print(".");
		
		// get some request headers
		ParsedData pd = new ParsedData(getStatusCode(connection),
				  					   getContentType(connection));
		System.out.print(".");
		
		// do not continue if not an html page
		if (!pd.getContentType().equals("text/html"))
			return pd;
		
		System.out.print(".");
		
		// parse content if a page was returned
		if (getStatusCode(connection) == 200) {
			Reader reader = new InputStreamReader(connection.getInputStream(),
												  getCharset(connection));
			parser.parse(reader, callback, true);
			pd.setpageContent(callback.getContent());
			pd.setpageHrefs(callback.getHrefs());
		}
		System.out.print(".]");
		return pd;
	}
	
	/**
	 * Reads the content-type in the given connection (typically : "utf-8"). If none
	 * can be found, returns "ISO-8859-1" as a default value.
	 */
	private static String getCharset (URLConnection connection) {
		
		String ctString = connection.getContentType();
		if (ctString.indexOf("charset=") == -1)
			return "ISO-8859-1";
		String[] parts = ctString.split(";");
		for(String part : parts) {
			if (part.trim().startsWith("charset="))
				return part.substring(part.indexOf("=")+1,part.length());
		}
		return "ISO-8859-1";
	}
	
	/**
	 * Reads the content-type in the given connection (typically : "text/html").
	 */
	private static String getContentType (URLConnection connection) {
		
		String[] parts = connection.getContentType().split(";");
		return parts[0].trim();
	}
	
	/**
	 * Reads the status code in the given connection (typically : 200, 404).
	 */
	private static Integer getStatusCode (URLConnection connection) {
		
		String headerLine = connection.getHeaderField(0);
		return Integer.parseInt(headerLine.split(" ")[1]);
	}

	/**
	 * Usage example.
	 * @param args
	 */
	public static void main(String[] args) {

		// define some urls
		String[] urls = new String[] {
				"http://www.heig-vd.ch/LinkClick.aspx?link=http%3a%2f%2fwww.24heures.ch&tabid=766&mid=1951",
				"http://tigr.heig-vd.ch/ri/tabid/306/Default.aspx",
				"http://www.heig-vd.ch/Default.aspx?tabid=90",
				"http://www.heig-vd.ch/Portals/0/HEIG-VD/pdf/brochures/brochure2006.pdf",
				"http://www.heig-vd.ch/Default.aspx?tabid=100" };

		// parse urls and print parsed data
		for (int i = 0; i < urls.length; i++) {

			System.out.println("---------------");
			try {
				WebParser.ParsedData pd = WebParser.parseURL(new URL(urls[i]));
				if (pd != null) {
				System.out.println("Status code  : " + pd.getStatusCode());
				System.out.println("Content type : " + pd.getContentType());
				System.out.println("Page content : " + pd.getPageContent());
				System.out.println("Page hrefs   : " + pd.getPageHrefs()); 
				} else {
					System.out.println("Page inacessible!");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
