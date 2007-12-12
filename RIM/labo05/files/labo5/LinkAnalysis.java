package labo5;

import java.util.Vector;

/**
 * This class provides static methods to make link analysis.
 * @author Florian Poulin <i>(florian.poulin at heig-vd.ch)</i>
 */
public class LinkAnalysis {
	
	/**
	 * Calculates and returns the bibliographic coupling matrix.
	 * @param m Adjacency matrix.
	 * @return Bibliographic coupling matrix.
	 */
	public static AdjacencyMatrix calculateBC (AdjacencyMatrix m) {
		
		AdjacencyMatrix result = new ArrayListMatrix(m.size());
		
		/* complete content of result here */
		
		return result; 
	}
	
	/**
	 * Calculates and returns the co-citation matrix.
	 * @param m Adjacency matrix.
	 * @return Co-citation matrix.
	 */
	public static AdjacencyMatrix calculateCC (AdjacencyMatrix m) {
		
		AdjacencyMatrix result = new ArrayListMatrix(m.size());
		
		/* complete content of result here */
		
		return result; 
	}
	
	/**
	 * Calculates and returns the hub vector.
	 * @param m Adjacency matrix.
	 * @param ac Auhtority vector of the previous step.
	 * @return Hub vector.
	 */
	public static Vector<Double> calculateHc (AdjacencyMatrix m, Vector<Double> ac) {
		
		Vector<Double> result = new Vector<Double>(m.size());
		
		/* complete content of result here */
		
		return result; 
	}
	
	/**
	 * Calculates and returns the authority vector.
	 * @param m Adjacency matrix.
	 * @param hc Hub of the previous step.
	 * @return Authority vector.
	 */
	public static Vector<Double> calculateAc (AdjacencyMatrix m, Vector<Double> hc) {
		
		Vector<Double> result = new Vector<Double>(m.size());
		
		/* complete content of result here */
		
		return result; 
	}
	
	/**
	 * Calculates and returns the pagerank vector.
	 * @param m Adjacency matrix.
	 * @param pr Pagerank vector of the previous step.
	 * @return Pagerank vector.
	 */
	public static Vector<Double> calculatePRc (AdjacencyMatrix m, Vector<Double> pr) {
		
		Vector<Double> result = new Vector<Double>(m.size());
		
		/* complete content of result here */
		
		return result; 
	}
}
