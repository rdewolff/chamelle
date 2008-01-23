package rim.analyze;

import java.util.Vector;

/**
 * This class provides static methods to make link analysis.
 */


public class LinkAnalysis {
	
	private final static double PAGERANKVALUE = 0.85;

	/**
	 * Calculates and returns the bibliographic coupling matrix.
	 * @param m Adjacency matrix.
	 * @return Bibliographic coupling matrix.
	 */
	public static AdjacencyMatrix calculateBC (AdjacencyMatrix m) {

		return Matrix.multiply(m, Matrix.transpose(m)); 
	}

	/**
	 * Calculates and returns the co-citation matrix.
	 * @param m Adjacency matrix.
	 * @return Co-citation matrix.
	 */
	public static AdjacencyMatrix calculateCC (AdjacencyMatrix m) {

		return Matrix.multiply(Matrix.transpose(m), m);

	}

	/**
	 * Calculates and returns the hub vector.
	 * @param m Adjacency matrix.
	 * @param ac Auhtority vector of the previous step.
	 * @return Hub vector.
	 */
	public static Vector<Double> calculateHc (AdjacencyMatrix m, Vector<Double> ac) {

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
					result.set( i, result.get(i)+(m.get(i, j)*ac.get(j)) );
				}
			}
			
			// normalisation
			double normalize = 0.0;
			
			for (short i=0; i<result.size(); i++) 
				normalize += Math.pow(result.get(i), 2);
			
			normalize = (double)Math.sqrt(normalize);
			
			for (short i=0; i<result.size(); i++)
				result.set(i, result.get(i)/normalize);
				
			// result
			return result; 

		} catch (Exception e) {

			System.out.println("Error while trying to multiply vector and matrix!");
			System.out.println(e);
			return result;

		}

	}

	/**
	 * Calculates and returns the authority vector.
	 * @param m Adjacency matrix.
	 * @param hc Hub of the previous step.
	 * @return Authority vector.
	 */
	public static Vector<Double> calculateAc (AdjacencyMatrix m, Vector<Double> hc) {

		// we use the calculateHc methode, except we transpose the matrice
		// given in parameter
		return calculateHc( Matrix.transpose(m), hc);

	}

	/**
	 * Calculates and returns the pagerank vector.
	 * @param m Adjacency matrix.
	 * @param pr Pagerank vector of the previous step.
	 * @return Pagerank vector.
	 */
	public static Vector<Double> calculatePRc (AdjacencyMatrix m, Vector<Double> pr) {

		// size
		int vectorSize = pr.size();
		
		// store the results
		Vector<Double> vectValPR = new Vector<Double>(vectorSize);
		Vector<Double> E = new Vector<Double>(vectorSize);
		Vector<Double> result = new Vector<Double>(vectorSize);
		
		// put the initial constante value (from the course formula)
		for (short i=0; i<vectorSize; i++) {
			vectValPR.add(PAGERANKVALUE);
			E.add(0.15/(double)vectorSize);
		}
		
		// the calcul is : PR(c) = normalize * ( 0.85 * (Mt * PR(c-1) )  ) 
		// result = Matrix.multiply(vectValPR, Matrix.add(Matrix.multiply(vectValPR, Matrix.multiply(Matrix.transpose(m), pr)), E) );
		result = Matrix.add(Matrix.multiply(vectValPR, Matrix.multiply(m, pr)), E);
		
		// normalize
		Double total = 0.0;
		for (short i=0; i<vectorSize; i++)
			total += result.get(i);
		
		for (short i=0; i<vectorSize; i++)
			result.set(i, result.get(i)/total);
		
		return result;
		
	}
}
