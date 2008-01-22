/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rim.analyze;

import java.util.Vector;

/**
 *
 * @author Romain de Wolff
 */
public class Labo5 {

	final static int NBRITERATIONS = 5;
	
	public static void main( String args[] ) {

		/*
		// test des mult *******************************************************
		Vector<Double> test = new Vector<Double>(3);
		Vector<Double> test2 = new Vector<Double>(3);
		test.add(1.0); test.add(3.0); test.add(2.0);
		test2.add(2.0); test2.add(4.0); test2.add(3.0);
		
		System.out.println("Test : " + test );
		System.out.println("Test2: " + test2);
		
		System.out.println("Test + Test2 = " + Matrix.add(test, test2));
		System.out.println("Test + Test2 = " + Matrix.multiply(test, test2));
		// *********************************************************************
		*/
		
		System.out.println("** RIM - LABO 5 **");

		GraphFileReader gr = null;

		String fileGraph = "/Users/rdewolff/Documents/HEIG-VD/eclipse/" +
				"svnChamelle/RIM/labo06/" +
				"RIM_Framework/rim/analyze/graphe-full.txt";

		gr = new GraphFileReader(fileGraph);

		System.out.println("Scanned graph from the file : ");

		// matrice initiale
		AdjacencyMatrix m = gr.getAdjacencyMatrix();

		// affiche de cette matrice representant le graphe
		System.out.println(m);


		AdjacencyMatrix BC, CC = null;
		BC = LinkAnalysis.calculateBC(gr.getAdjacencyMatrix());
		CC = LinkAnalysis.calculateCC(gr.getAdjacencyMatrix());

		// position 8 et 9 dans le tableau (en fonction du fichier de graphe)
		System.out.println("BC 25 <-> 22 : " + BC.get(8,9));
		// position 1 et 7 dans le tableau
		System.out.println("CC 20 <->  2 : " + CC.get(1,7));


		// on effectue 5 iterations
		// les matrices
		Vector<Double> ac  = new Vector<Double>(m.size()); // authorite
		Vector<Double> hc  = new Vector<Double>(m.size()); // hub
		Vector<Double> pr  = new Vector<Double>(m.size()); // page rank
		Vector<Double> tmp = new Vector<Double>(m.size()); // temp values

		// initialise le contenu du vecteur
		Double prInitVal = 1/(double)m.size();
		for (short i=0; i<m.size();i++) {
			ac.add(1.0);
			hc.add(1.0);
			pr.add(prInitVal); 
		}
		
		// effectue les iterations
		for (short j=0; j<NBRITERATIONS; j++) {
			// debug
			System.out.println("j="+j+"\nac="+ac.toString()+"\nhc="+hc.toString()+"\npr="+pr.toString());
			
			tmp = LinkAnalysis.calculateHc(m, ac);
			ac = LinkAnalysis.calculateAc(Matrix.transpose(m), hc);
			hc = tmp;
			pr = LinkAnalysis.calculatePRc(m, pr);
		}

		// affiche les resultats
		System.out.println("Hubs - Authority - PageRank");
		for (short i=0; i<hc.size();i++) {
			System.out.println(hc.get(i).toString() + "   " + ac.get(i) + "   " + pr.get(i));
		}
		
		
		

	}
}
