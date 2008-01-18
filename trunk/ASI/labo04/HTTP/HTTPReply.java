package HTTP;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.LinkedList;

/**
 * Representation et gestion d'une reponse HTTP.
 * @author Cyril Maulini
 *
 */
class HTTPReply {
	// Page par defaut
	final static private String directoryIndex = "index.html";
	// Repertoire contenant les documents web
	final static private String documentRoot = 
		new File("./www").toURI().normalize().getPath();
	// Flag de fin de ligne
	final static private String CRLF = "\r\n";
	// La taille (approximative) de l'entete Content-Length
	final static private long contentLengthLength = 30;

	// La ligne de status
	private String statusLine;
	// La collection de tous les entetes
	private LinkedList<String> headerLine;
	// Le corps de la reponse (si pas un fichier)
	private String entityBody;
	// Le corps de la reponse (si un fichier)
	File file;
	
	/**
	 * Construire une reponse vide.
	 *
	 */
	HTTPReply() {
		headerLine = new LinkedList<String>();
	}
	
	/**
	 * Generer la reponse a une requete.
	 * @param request La requete de la reponse.
	 */
	void generateFromRequest(HTTPRequest request) {
		// Si requete mal formulee ou version mal inscrite
		if(!request.goodRequestFormat() || !request.getVersion().startsWith("HTTP/")) {
			build400();
		}
		
		// Autrement, numero de version ok
		else {
			// Extraction de la methode
			String method = request.getMethod();
			
			// Si methode GET
			if(method == HTTPRequest.method.GET) {
				// Si le site de l'heig-vd
				if(request.getURI().equals("/heig-vd.html")) {
					build301("http://www.heig-vd.ch");
					return;					
				}
				
				// Extraire l'URI demandee
				file = new File(documentRoot + request.getURI());
				
				// Normaliser l'URI pour ne plus avoir de ".." eventuel
				String document = file.toURI().normalize().getPath();
				
				// Si le document demande ne se trouve pas dans le documentRoot
				if (document.length() < documentRoot.length() ||
						!document.startsWith(documentRoot)) {
					build403(file.getPath());
					// fichier non autorise, l'oublier :)
					file = null;
					return;
				}
							
				// Si un repertoire, ajouter la page par defaut
				if (file.isDirectory()) {
					file = new File(file, "/" + directoryIndex);
				}
				
				// Si le fichier existe
				if(file.exists()) {
					// Construction de la reponse
					statusLine = "HTTP/1.0 200 OK";
					headerLine.add("Content-Type: " + contentType(file.getPath()));
					addContentLength(file.length());
				}
				
				// Le fichier n'existe pas, generer le code d'erreur 404
				else {
					build404(file.getPath().substring(documentRoot.length()));
				}
			}
		
			 // Si methode HEAD ou POST, erreur 501
			else if(method == HTTPRequest.method.HEAD || method == HTTPRequest.method.POST) {
				build501(method);
			}
			
			// Autrement, methode inconnue (erreur 400)
			else {
				build400();
			}
		}	
	}
	
	/**
	 * Envoyer la reponse dans un flux.
	 * @param os Le flux dans lequel mettre la reponse.
	 * @throws Exception
	 */
	public void sendTo(OutputStream os) throws Exception {				
		// Envoyer la ligne de status
		os.write(statusLine.getBytes());
		os.write(CRLF.getBytes());
		
		// Envoyer tous les entetes
		for(String header : headerLine) {
			os.write(header.getBytes());
			os.write(CRLF.getBytes());
		}		
		
		// Signaler la fin des entetes
		os.write(CRLF.getBytes());

		// Si le fichier demande existe, l'envoyer
		if (file != null && file.exists() && file.isFile()) {
			// Ouvrir le flux sur le fichier a envoyer
			FileInputStream fis = new FileInputStream(file.getAbsolutePath());
			
			// Envoyer le fichier
			sendBytes(fis, os);
			
			// Fermer le flux sur le fichier
			fis.close();
		}
		
		// Si pas de fichier, le corps est dans entityBody
		else {
			os.write(entityBody.getBytes());
		}
		
		// Envoyer tous les entetes au client
		for(String header : headerLine) {
			os.write(header.getBytes());
			os.write(CRLF.getBytes());
		}
	}
	
	/**
	 * Construire un 301 MOVED.
	 * @param location L'URI de la redirection.
	 */
	void build301(String location) {
		statusLine = "HTTP/1.0 301 MOVED";
		headerLine.add("Location: "+ location);
		entityBody = "";
		addContentLength(entityBody.getBytes().length);
	}
	
	/**
	 * Construire un 400 BAD REQUEST.
	 *
	 */
	void build400() {
		statusLine = "HTTP/1.0 400 BAD REQUEST";
		headerLine.add("Content-Type: text/html");
		entityBody = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">" +
				"<html><head>" +
				"<title>400 Bad Request</title>" +
				"</head><body>" +
				"<h1>Bad Request</h1>" +
				"<p>Your browser sent a request that this server could not understand.<p>" +
				"</body></html>";
		addContentLength(entityBody.getBytes().length);
	}
	
	/**
	 * Construire un 403 FORBIDDEN.
	 * @param pathForbidden L'URI non autorise.
	 */
	void build403(String pathForbidden) {
		statusLine = "HTTP/1.0 403 FORBIDDEN";
		headerLine.add("Content-Type: text/html");
		entityBody = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">" +
				"<html><body>" +
				"<title>403 Forbidden</title>" +
				"</head><body>" +
				"<H1>Forbidden</H1>" +
				"You don't have permission to access " + pathForbidden + " on this server.<P>" +
				"</body></html>";
		addContentLength(entityBody.getBytes().length);
	}
	
	/**
	 * Construire un 404 NOT FOUND
	 * @param pathNotFound L'URI inexistante.
	 */
	void build404(String pathNotFound) {
		statusLine = "HTTP/1.0 404 NOT FOUND";
		headerLine.add("Content-Type: text/html");
		entityBody = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"> " +
				"<html><head>" +
				"<title>404 Not Found</title>" +
				"</head><body>" +
				"<h1>Not Found</h1>" +
				"<p>The requested URL " + pathNotFound + " was not found on this server.</p>" +
				"</body></html>";
		addContentLength(entityBody.getBytes().length);
	}
	
	/**
	 * Construire un 501 NOT IMPLEMENTED
	 * @param methodNotImplemented La methode non implementee.
	 */
	void build501(String methodNotImplemented) {
		statusLine = "HTTP/1.0 501 NOT IMPLEMENTED";
		headerLine.add("Content-Type: text/html");
		entityBody = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"> " +
		"<html><head>" +
		"<title>501 NOT IMPLEMENTED</title>" +
		"</head><body>" +
		"<h1>NOT IMPLEMENTED</h1>" +
		"<p>The requested method " + methodNotImplemented + " was not implemented on this server.</p>" +
		"</body></html>";
		addContentLength(entityBody.getBytes().length);
	}
	
	/**
	 * Ajouter l'entete Content-Length en prenant en compte la presence
	 * de tous les entetes dans le corps.
	 * @param bodyLength : La taille (en byte) du corps.
	 */
	private void addContentLength(long bodyLength) {
		// Compter la longueur du corps
		long length = bodyLength;
		
		// Compter la longueur de tous les entetes
		for(String header : headerLine) {
			length += (header + CRLF).getBytes().length;
		}
		
		// Ajouter l'entete Content-Length
		headerLine.add("Content-Length: " + length + contentLengthLength);
	}
	
	/**
	 * Definir le Content-Type d'un fichier en fonction de son extension.
	 * @param fileName Le chemin du fichier.
	 * @return L'entete du Content-Type correspondant au fichier.
	 */
	private static String contentType(String fileName) {
		// Resoudre le probleme maj/min
		fileName = fileName.toUpperCase();
		
		// Si c'est un fichier html
		if(fileName.endsWith(".HTM") || fileName.endsWith(".HTML")) {
			return "text/html";
		}
		
		else if(fileName.endsWith(".JPEG") || fileName.endsWith(".JPG")) {
			return "image/jpeg";
		}
			
		else if(fileName.endsWith(".GIF")) {
			return "image/gif";
		}
			
		else if(fileName.endsWith(".CSS")) {
			return "text/css";
		}
		
		// Si c'est un fichier text
		else if(fileName.endsWith(".TXT") || fileName.endsWith(".JAVA")) {
			return "text/plain";
		}
		
		// Dans tous les autres cas
		else
			return "application/octet-stream";
	}
	
	/**
	 * Envoyer un fichier dans un flux.
	 * @param fis La reference sur le fichier.
	 * @param os La reference sur le flux.
	 * @throws Exception
	 */
	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
		// Le tampon de 1KB pour faire transiter les octets du fichier dans le flux
		byte[] buffer = new byte[1024];
		int bytes = 0;
		
		// Copier le fichier dans le flux sortant de la socket
		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
	}
	
	/**
	 * Obtenir la ligne de staut + entetes de la reponse HTTP. 
	 */
	public String toString() {
		// La ligne de status
		String str = statusLine + '\n';
		
		// Tous les entetes
		for (String header : headerLine) {
			str += header + '\n';
		}
		
		// La fin des entetes
		str += '\n';
		
		return str;
	}

}
