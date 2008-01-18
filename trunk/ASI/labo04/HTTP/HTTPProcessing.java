package HTTP;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Traitement d'une requete HTTP dans une thread separee.
 * @author Cyril Maulini
 *
 */
public class HTTPProcessing extends Thread {
	// Gerer l'exclusion mutuelle lors des affichages requetes + reponses
	static private Object mutex = new Object();
	
	// La socket de communication avec le client
	private Socket socket;
	
	// Respectivement, la requete et la reponse
	private HTTPRequest request;
	private HTTPReply   reply;

	/**
	 * Construire un traitement HTTP.
	 * @param s La socket de communication avec le client.
	 */
	public HTTPProcessing(Socket s) {
		request = new HTTPRequest();
		reply   = new HTTPReply();
		
		socket  = s;
	}
	
	/**
	 * Methode executee apres invocation du start(),
	 * qui demarre 	la thread.
	 */
	public void run() {
		try {
			// Traiter la requete HTTP
			processRequest();
		}
		catch (Exception e) {
			synchronized(mutex) {
				System.out.println(e);
			}
		}
	}
	
	/**
	 * Traiter la requete HTTP. Autrement dit, gererer et envoyer
	 * une reponse a la requete du client.
	 * @throws Exception
	 */
	private void processRequest() throws Exception {
		// Obtenir une reference aux flux entrant et sortant de la socket
		InputStream is  = socket.getInputStream();
		OutputStream os = socket.getOutputStream();
		
		// Lire la requete du client
		request.readFromStream(is);
		
		// Interpreter la requete et construire la reponse appropriee
		reply.generateFromRequest(request);

		// Envoyer la reponse au client
		reply.sendTo(os);
				
		// Fermer les flux et la socket
		is.close();
		os.close();
		socket.close();
		
		// Afficher requete + reponse
		synchronized(mutex) {
			System.out.println(request);
			System.out.println(reply);
		}
	}
}
