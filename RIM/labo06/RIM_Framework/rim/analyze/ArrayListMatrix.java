package rim.analyze;

import java.util.ArrayList;
import java.util.Vector;


/**
 * Simple implementation of the {@link AdjacencyMatrix} using ArrayLists.
 * @author Florian Poulin <i>(florian.poulin at heig-vd.ch)</i>
 */
public class ArrayListMatrix implements AdjacencyMatrix {

	// For outputs
	private final static String spacerStr = " ";
	private final static String newlineStr = "\n";
	
	/**
	 * Structure holding the content of the matrix
	 */
	private ArrayList<ArrayList<Integer>> content;
	
	/**
	 * Default constructor. Builds a matrix of size 0.
	 */
	public ArrayListMatrix() {
		content = new ArrayList<ArrayList<Integer>>();
	}
	
	/**
	 * Constructor. Initializes the content of the matrix to <code>0</code>.
	 */
	public ArrayListMatrix(int length) {
		
		// allocate memory for length * length integers and initialize to 0
		content = new ArrayList<ArrayList<Integer>>(length);
		for (int i=0; i<length; i++) {
			ArrayList<Integer> tmp = new ArrayList<Integer>(length);
			for (int j=0; j<length; j++)
				tmp.add(j, 0);
			content.add(tmp);
		}
	}

	/* (non-Javadoc)
	 * @see AdjacencyMatrix#getLength()
	 */
	public int size() {
		return content.size();
	}
	
	/* (non-Javadoc)
	 * @see AdjacencyMatrix#get(int, int)
	 */
	public int get(int i, int j) {
		return content.get(i).get(j);
	}

	/* (non-Javadoc)
	 * @see AdjacencyMatrix#set(int, int, boolean)
	 */
	public void set(int i, int j, int edge) {
		content.get(i).set(j, edge);
	}
	
	/* (non-Javadoc)
	 * @see AdjacencyMatrix#addLast()
	 */
	public int addLast() {
		
		// Add new column
		for (ArrayList<Integer> alb : content)
			alb.add(0);
		
		// Allocate and add new row
		ArrayList<Integer> tmp = new ArrayList<Integer>(content.size()+1);
		for (int i=0; i<content.size()+1; i++)
			tmp.add(0);
		content.add(tmp);
		
		return content.size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		String out = new String();
		for (ArrayList<Integer> alb : content) {
			for (Integer isEdge : alb)
				out += isEdge + spacerStr;
			out += newlineStr;
		}
		return out;
	}

	/**
	 * Unit test.
	 * @param args console args.
	 */
	public static void main (String[] args) {
		
		// Declare some stuff
		int size = 10;
		AdjacencyMatrix am = new ArrayListMatrix (size);
		
		// Output
		System.out.println("Empty matrix :");
		System.out.println(am);
		
		// Define some edges
		for (int i=0; i<size; i++)
			am.set(i, i, 1);
		
		// Output
		System.out.println("Diagonal matrix :");
		System.out.println(am);
		
		// Add a new element
		am.addLast();
		
		// Output
		System.out.println("Resized matrix :");
		System.out.println(am);
		
		// Define some edges on newly added element
		am.set(10, 9, 1);
		am.set(1, 10, 1);
      // tests
      am.set(10, 1, 2);
      
      System.out.println("Modified Matrix");
      System.out.println(am);
      
      System.out.println("Matrice Transposee");
      // Effectue les tests de la classe LinkAnalysis
      am = Matrix.transpose(am);
		
		// Output
		System.out.println("Modified matrix :");
		System.out.println(am);
		
		// Create a default matrix
		AdjacencyMatrix def = new ArrayListMatrix ();
		def.addLast();
		def.addLast();
      
      // Create a vector and a matrix and multiply them
      AdjacencyMatrix test = new ArrayListMatrix (3);
      test.set(0, 0, 4);
      test.set(0, 1, 2);
      test.set(0, 2, 0);
      test.set(1, 0, 3);
      test.set(1, 1, 0);
      test.set(1, 2, 2);
      test.set(2, 0, 1);
      test.set(2, 1, 1);
      test.set(2, 2, 0);
      
      Vector<Double> v = new Vector<Double>();
      v.add(3.0);
      v.add(-1.0);
      v.add(-2.0);
     
      System.out.println("Multiply Matrix and Vector :");
      System.out.println(test);
      
      System.out.println("Vector :");
      System.out.println(v);
      
      System.out.println("Result :");
      System.out.println(Matrix.multiply(test, v));
		
      // 
      System.out.println("Multiply 2 Matrix :");
      System.out.println(Matrix.multiply(test,test));
      
		// Output
		System.out.println("Default matrix :");
		System.out.println(def);
	}
}
