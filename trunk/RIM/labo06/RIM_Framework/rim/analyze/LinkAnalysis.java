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

		return Matrix.multiply(m, ac);

	}

	/**
	 * Calculates and returns the authority vector.
	 * @param m Adjacency matrix.
	 * @param hc Hub of the previous step.
	 * @return Authority vector.
	 */
	public static Vector<Double> calculateAc (AdjacencyMatrix m, Vector<Double> hc) {

		return Matrix.multiply(Matrix.transpose(m), hc);

	}

	/**
	 * Calculates and returns the pagerank vector.
	 * @param m Adjacency matrix.
	 * @param pr Pagerank vector of the previous step.
	 * @return Pagerank vector.
	 */
	public static Vector<Double> calculatePRc (AdjacencyMatrix m, Vector<Double> pr) {

		// store the results
		Vector<Double> vectValPR = new Vector<Double>(m.size());
		Vector<Double> E = new Vector<Double>(m.size());
		
		// put the initial constante value (from the course formula)
		for (short i=0; i<vectValPR.size(); i++) {
			vectValPR.add(PAGERANKVALUE);
			E.add(0.15/(double)m.size());
		}
		
		// the calcul is : PR(c) = normalize * ( 0.85 * (Mt * PR(c-1) )  ) 
		// (cf course)
		// return Matrix.add(Matrix.multiply(vectValPR, Matrix.multiply(Matrix.transpose(m), pr)), E);
		return Matrix.multiply(Matrix.transpose(m), pr);
		
		// probblem VECTOR * MATRIX

	}
}
