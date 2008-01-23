package rim.analyze;

import java.util.HashMap;
import java.util.Vector;
import java.util.Map.Entry;

/**
 *
 * @author Romain de Wolff
 */
public class Labo5 {

	final static int NBRITERATIONS = 5;
	
	public static void main( String args[] ) {
		
		
		
		System.out.println("------------------------------------------------");
		System.out.println("- RIM - Laboratoire 6                          -");
		System.out.println("- Auteurs : Romain de Wolff & Simon Hintermann -");
		System.out.println("------------------------------------------------");

		GraphFileReader gr = null;

		String fileGraph = "/Users/rdewolff/Documents/HEIG-VD/eclipse/" +
				"svnChamelle/RIM/labo06/" +
				"RIM_Framework/rim/analyze/graphe-full.txt";

		gr = new GraphFileReader(fileGraph);

		// liste des noeuds
		HashMap<String, Integer> nodes = gr.getNodeMapping();
		
		// matrice initiale
		AdjacencyMatrix m = gr.getAdjacencyMatrix();

		
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
			tmp = LinkAnalysis.calculateHc(m, ac);
			ac = LinkAnalysis.calculateAc(m, hc);
			hc = tmp;
			pr = LinkAnalysis.calculatePRc(m, pr);
		}

		// affiche les resultats
		System.out.println("------------------------------------------------");
		System.out.println("- Node - Hubs    - Authority - PageRank        -");
		System.out.println("------------------------------------------------");
		int i;
		for (Entry<String, Integer> e : nodes.entrySet()) {
			i = e.getValue();
			System.out.printf(" %4d    %1.5f   %1.5f     %1.5f\n",
					Integer.parseInt(e.getKey()), hc.get(i), ac.get(i), pr.get(i));
			
		}	
	}
}
