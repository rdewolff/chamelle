package rim.util; 

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Permet de hasher des contenus
 * 
 * update() met à jour le contenu à hacher
 * calculate() met fin au contenu et calcule le hash
 * @author Y. Bersier (ancien projet), J. Schmid & L. Prévost
 */
public class Hash {
	// Mécanisme de hachage
	private MessageDigest md;
	
	// Empreinte obtenue
	private byte[] digest = null;
	
	/**
	 * Constructeur
	 * @param algorithm Algorithme choisi
	 */
	public Hash(String algorithm) throws NoSuchAlgorithmException {
		md = MessageDigest.getInstance(algorithm);
	}

	/**
	 * Ajoute les bytes voulus pour 
	 * le calcul du hash
	 * @param b Tableau de bytes à ajouter
	 * @return Vrai si ajout ok.
	 */
	public boolean update(byte[] b, int length) {
		if (digest == null) {
			md.update(b, 0, length);
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Ajoute la chaine de caractères voulue
	 * pour le calcul futur du hash
	 * @param str Chaine à ajouter
	 * @return Vrai si ajout ok.
	 */
	public boolean update(String str) {
		if (str == null)
			return false;
		
		byte[] temp = str.getBytes();
		return update(temp, temp.length);
	}
	
	/**
	 * Calcule l'empreinte du contenu voulu
	 */
	public void calculate() {
		if (digest == null)
			digest = md.digest();
	}
	
	/**
	 * Récupère le nom de l'algorithme utilisé
	 * @return Algorithme utilisé
	 */
	public String algorithm() {
		return md.getAlgorithm();
	}
	
	/**
	 * Retourne une chaîne de caractères composée
	 * de chiffres hexadécimaux
	 * @return Chaîne de caractères correspondante au hash
	 */
	public String toString() {
		// Chiffres hexadécimaux
		final String digits = "0123456789ABCDEF";

		if (digest != null) {
			// Chaîne de caractères convertie
			StringBuffer sb = new StringBuffer();
	
			// Pour chaque byte du hash, 
			// le convertit en texte et l'ajoute à la chaîne
			for (byte b : digest)
				sb.append(
					(digits.charAt((b >> 4) & 0x0F) + "" + 
					 digits.charAt(b & 0x0F)));
	
			// Retourne la chaîne de caractères
			return sb.toString();
		}
		else
			return null;
	}

	/**
	 * Compare l'égalité entre deux hashs
	 * @param s Hash sous forme de texte à comparer
	 * @return true si les deux hashs sont égaux, false sinon
	 */
	public boolean equals(String s) {
		return toString().equals(s);
	}

	/**
	 * Compare l'égalité entre deux hashs
	 * @param h Hash à comparer
	 * @return true si les deux hashs sont égaux, false sinon
	 */
	public boolean equals(Hash h) {
		return md.getAlgorithm().equalsIgnoreCase(h.md.getAlgorithm()) && 
			equals(h.toString());
	}
}
