package rim.util; 

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Permet de hasher des contenus
 * 
 * update() met � jour le contenu � hacher
 * calculate() met fin au contenu et calcule le hash
 * @author Y. Bersier (ancien projet), J. Schmid & L. Pr�vost
 */
public class Hash {
	// M�canisme de hachage
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
	 * @param b Tableau de bytes � ajouter
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
	 * Ajoute la chaine de caract�res voulue
	 * pour le calcul futur du hash
	 * @param str Chaine � ajouter
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
	 * R�cup�re le nom de l'algorithme utilis�
	 * @return Algorithme utilis�
	 */
	public String algorithm() {
		return md.getAlgorithm();
	}
	
	/**
	 * Retourne une cha�ne de caract�res compos�e
	 * de chiffres hexad�cimaux
	 * @return Cha�ne de caract�res correspondante au hash
	 */
	public String toString() {
		// Chiffres hexad�cimaux
		final String digits = "0123456789ABCDEF";

		if (digest != null) {
			// Cha�ne de caract�res convertie
			StringBuffer sb = new StringBuffer();
	
			// Pour chaque byte du hash, 
			// le convertit en texte et l'ajoute � la cha�ne
			for (byte b : digest)
				sb.append(
					(digits.charAt((b >> 4) & 0x0F) + "" + 
					 digits.charAt(b & 0x0F)));
	
			// Retourne la cha�ne de caract�res
			return sb.toString();
		}
		else
			return null;
	}

	/**
	 * Compare l'�galit� entre deux hashs
	 * @param s Hash sous forme de texte � comparer
	 * @return true si les deux hashs sont �gaux, false sinon
	 */
	public boolean equals(String s) {
		return toString().equals(s);
	}

	/**
	 * Compare l'�galit� entre deux hashs
	 * @param h Hash � comparer
	 * @return true si les deux hashs sont �gaux, false sinon
	 */
	public boolean equals(Hash h) {
		return md.getAlgorithm().equalsIgnoreCase(h.md.getAlgorithm()) && 
			equals(h.toString());
	}
}
