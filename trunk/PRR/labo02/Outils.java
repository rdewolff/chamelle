/**
 * Outils pour afficher des matrices et pour gerer des conversions bytes-int
 * 
 * Note: methodes de gestion des conversions fournies par m. Evequoz
 * 
 * @author Romain de Wolff
 * @author Simon Hintermann
 * @date 26 octobre 2007
 */
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
	
	
	
}
