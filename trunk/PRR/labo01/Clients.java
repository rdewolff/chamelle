import java.net.*;

/**
 * Classe qui permet de stocker les informations sur les clients connecté
 * 
 * @author Romain de Wolff
 * @author Simon Hintermann
 */

public class Clients {

	static int nbClient = 0;
	
	private int id;
	private InetAddress adr;
	private int port;
	
	/**
	 * Constructeur 
	 * @param id	le numero du client
	 * @param adr	l'adresse internet du client
	 * @param port	le port du client
	 */
	public Clients (int id, InetAddress adr, int port) {
		this.nbClient++; // inutilisé dans cette version
		this.id = id;
		this.adr = adr;
		this.port = port;
	}
	
	/**
	 * Retourne l'adresse du client
	 * @return	l'adresse du client 
	 */
	public InetAddress getAddress() {
		return this.adr;
	}
	
	/**
	 * Retourne le port du client 
	 * @return	le port du client
	 */
	public int getPort() {
		return this.port;
	}
}
