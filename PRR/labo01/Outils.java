
public class Outils {

	/**
	 * Affichage de la matrice passee en parametre (2D)
	 */
	public static void afficheMatrice(int [][] tableau) {
		for (short i=0; i<tableau.length; i++) {
			for (short j=0; j<tableau.length; j++) {
				System.out.print(tableau[i][j] + " ");
			}
			System.out.print('\n');
		}
	}
	
	/**
	 * Converti un entier 32 bits en une chaine d'octets.
	 * 
	 * param in l'entier a convertir; 
	 * param out tableau d'octets, il doit y en avoir au moins 4 octets de libres 
	 * param offset decalage depuis le debut du tableau out
	 * Valeur de retour: nombre d'octets inseres. Toujours 4.
	 */
	public static int intToBytes(int in, byte[] out, int offset) {
		out[0 + offset] = (byte) ((in >>> 24) & 0xFF);
		out[1 + offset] = (byte) ((in >>> 16) & 0xFF);
		out[2 + offset] = (byte) ((in >>> 8) & 0xFF);
		out[3 + offset] = (byte) ((in >>> 0) & 0xFF);
		return 4;
	}

	/**
	 * Converti les 4 premiers octets une chaine d'octets depuis un decalage en
	 * un entier 32 bits.
	 * 
	 * param in suite d'octets formant un entier; 
	 * param offset decalage depuis le debut du tableau 
	 * in ou se trouve l'entier a convertir
	 * Valeur de retour: entier converti
	 */
	public static int bytesToInt(byte[] in, int offset) {
		return (((int) in[offset] << 24) & 0xFF000000)
				| (((int) in[1 + offset] << 16) & 0x00FF0000)
				| (((int) in[2 + offset] << 8) & 0x0000FF00)
				| ((int) in[3 + offset] & 0x000000FF);
	}
}
