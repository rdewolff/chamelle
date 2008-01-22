package rim.util; 

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Permet de hasher des contenus
 * 
 * update() met a jour le contenu a hacher
 * calculate() met fin au contenu et calcule le hash
 * @author Y. Bersier (ancien projet), J. Schmid & L. Prevost
 */
public class Hash {
	// Mecanisme de hachage
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
	 * @param b Tableau de bytes a ajouter
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
	 * Ajoute la chaine de caracteres voulue
	 * pour le calcul futur du hash
	 * @param str Chaine a ajouter
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
	 * Recupere le nom de l'algorithme utilise
	 * @return Algorithme utilise
	 */
	public String algorithm() {
		return md.getAlgorithm();
	}
	
	/**
	 * Retourne une chaine de caracteres composee
	 * de chiffres hexadecimaux
	 * @return Chaine de caracteres correspondante au hash
	 */
	public String toString() {
		// Chiffres hexadecimaux
		final String digits = "0123456789ABCDEF";

		if (digest != null) {
			// Chaine de caracteres convertie
			StringBuffer sb = new StringBuffer();
	
			// Pour chaque byte du hash, 
			// le convertit en texte et l'ajoute a la chaine
			for (byte b : digest)
				sb.append(
					(digits.charAt((b >> 4) & 0x0F) + "" + 
					 digits.charAt(b & 0x0F)));
	
			// Retourne la chaine de caracteres
			return sb.toString();
		}
		else
			return null;
	}

	/**
	 * Compare l'egalite entre deux hashs
	 * @param s Hash sous forme de texte a comparer
	 * @return true si les deux hashs sont egaux, false sinon
	 */
	public boolean equals(String s) {
		return toString().equals(s);
	}

	/**
	 * Compare l'egalite entre deux hashs
	 * @param h Hash a comparer
	 * @return true si les deux hashs sont egaux, false sinon
	 */
	public boolean equals(Hash h) {
		return md.getAlgorithm().equalsIgnoreCase(h.md.getAlgorithm()) && 
			equals(h.toString());
	}
}
