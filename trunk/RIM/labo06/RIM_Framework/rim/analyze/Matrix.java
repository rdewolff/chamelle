
package rim.analyze;

import java.util.Vector;

/**
 * This class provide various calculs on matrix. It allows to do various 
 * operation such as :
 *    - transpose a matrix
 *    - multiply a matrix and a vector
 *    - multiply two matrixes
 *    - multiply two vector
 *    - add two vector 
 *    
 * The vector we use containing doubles.
 * 
 * These must be the same dimension or it will cause an exception.
 * 
 * Last modification : 26 janvier 2008
 * 
 * @author  Romain de Wolff & Simon Hintermann
 * @date    15 dec 2007
 */

public class Matrix {

	/**
	 * Calculate the transposed matrix
	 * @param m The matrix to transpose
	 * @return The transposed matrix
	 */
	public static AdjacencyMatrix transpose(AdjacencyMatrix m) {

		// matrice temporaire
		AdjacencyMatrix result = new ArrayListMatrix(m.size());

		// parcours la matrice et insere la transposition dans la matrice
		// temporaire
		for (int i = 0; i < m.size(); i++) {
			for (int j = 0; j < m.size(); j++) {
				result.set(i, j, m.get(j, i));
			}
		}
		// retourne le resultat
		return result; 

	}

	/**
	 * Multiply a matrix with a vector
	 * @param m The matrix
	 * @param ac The vector (double)
	 * @return the resulting vector (double)
	 */
	public static Vector<Double> multiply(AdjacencyMatrix m, Vector<Double> ac) {

		// temp vector
		Vector<Double> result = new Vector<Double>(m.size());

		// init result to 0
		for (int i = 0; i < ac.size(); i++) {
			result.add(0.0);
		}

		try {
			// multiply 
			for (int i = 0; i < m.size(); i++) {
				for (int j = 0; j < m.size(); j++) {
					result.set( i, result.get(i)+(m.get(j, i)*ac.get(j)) );
				}
			}
			// result
			return result; 

		} catch (Exception e) {

			System.out.println("Error while trying to multiply vector and matrix!");
			System.out.println(e);
			return result;

		}
	}
	
	/**
	 * Multiply two matrixes.
	 * @param m1 the first matrix
	 * @param m2 the second matrix
	 * @return the resulting matrix
	 */
	public static AdjacencyMatrix multiply(AdjacencyMatrix m1, AdjacencyMatrix m2) {

		// matrice temporaire
		AdjacencyMatrix result = new ArrayListMatrix(m1.size());

		// size of the matrix
		int size = m1.size();

		// do the calcul 
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				for (int k = 0; k < size; k++) {
					result.set(i, j, result.get(i, j)+m1.get(i, k)*m2.get(k, j));
				}
			}
		}

		// return the resulted matrix
		return result; 

	}

	/**
	 * Multiply two vectors (containing double)
	 * @param v1 the first vector
	 * @param v2 the second vector 
	 * @return the resulting vector
	 */
	public static Vector<Double> multiply(Vector<Double> v1, Vector<Double> v2) {

		Vector<Double> result = new Vector<Double>();

		for (short i=0;i<v1.size();i++) {
			result.add(v1.get(i) * v2.get(i));
		}

		return result;

	}

	/**
	 * Add two vectors
	 * @param v1 the first vector
	 * @param v2 the second vector
	 * @return the resulting vector
	 */
	public static Vector<Double> add(Vector<Double> v1, Vector<Double> v2) {

		Vector<Double> result = new Vector<Double>();

		for (short i=0;i<v1.size();i++) {
			result.add(v1.get(i) + v2.get(i));
		}

		return result;

	}
}
