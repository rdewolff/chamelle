/**
 * Class qui permet de gerer des host
 * 
 * Chaque host possede une adresse et un nom d'acces
 * 
 * Serializable pour les communication en RMI.
 * 
 * @author Romain de Wolff 
 */


import java.io.Serializable;

public class Host implements Serializable{

	// identifiant de version
	private static final long serialVersionUID = 1L;
	
	// variables
	private String host; // adresse 
	private String nomAcces; // nom d'acces

	/**
	 * Constructeur
	 * @param host	Le nom/adresse ip du serveur
	 * @param nom	Le nom a utiliser pour y acceder
	 */
	public Host(String host, String nom) {
		this.host = host;
		this.nomAcces = nom;
	}
	
	/**
	 * Retourne le nom/adresse ip du serveur
	 * @return le nom ou l'adresse ip du serveur
	 */
	public String getHost() {
		return this.host;
	}
	
	/**
	 * Retourne le nom d'acces du serveur
	 * @return le nom d'acces du serveur
	 */
	public String getNomAcces() {
		return this.nomAcces;
	}
	
	/**
	 * Pour l'affichage (debug)
	 */
	public String toString() {
		return "Host: " + host + "\nNom d'acces: " + nomAcces + "\n";
	}
}
