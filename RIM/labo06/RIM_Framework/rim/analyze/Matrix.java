
package rim.analyze;

import java.util.Vector;

/**
 * This class is here to make operation on matrix. It allows to do various 
 * operation such as :
 *    - transpose a matrix
 *    - multiply two matrixes
 *    - multiply a matrix and a vector 
 * 
 * These must be the same dimension of course or it will make an exception.
 * 
 * @author  Romain de Wolff
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
   
   public static Vector<Double> multiply(AdjacencyMatrix m, Vector<Double> ac) {
      // temp vector
      Vector<Double> result = new Vector<Double>(m.size());
      
      // init
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
         // retourne le resultat
         return result; 
      
      } catch (Exception e) {
         
         System.out.println("Error while trying to multiply vector and matrix!");
         System.out.println(e);
         return result;
         
      }
   }
   
   
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
}
