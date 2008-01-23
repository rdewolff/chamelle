package rim.analyze;

import java.util.HashMap;
import java.util.Vector;
import java.util.Map.Entry;

/**
 * Laboratoire de RIM Numero 6 : Analyse des Hyperliens
 * 
 * Nous allons lire le fichier contenant un graphe et le mettre sous forme de
 * matrice a l'aide de la class <i>GraphFileReader</i>. 
 * 
 * Puis nous allons utiliser la classe <i>LinkAnalysis</i> afind de calculer
 * le couplage bibliographique ainsi que la co-citation.
 * 
 * Puis nous allons calculer le Hub, l'Autorite et le PageRank de notre matrice
 * en effectuant 5 iterations.
 * 
 * @author Romain de Wolff
 * @author Simon Hintermann
 */
public class Labo {

	final static int 	NBRITERATIONS = 5;
	final static String CHEMIN_GRAPHE  = "/Users/rdewolff/Documents/HEIG-VD/eclipse/" +
	"svnChamelle/RIM/labo06/" +
	"RIM_Framework/rim/analyze/graphe-full.txt";
	
	public static void main( String args[] ) {
		
		System.out.println("------------------------------------------------");
		System.out.println("- RIM - Laboratoire 6                          -");
		System.out.println("- Auteurs : Romain de Wolff & Simon Hintermann -");
		System.out.println("------------------------------------------------");

		GraphFileReader gr = null;

		// ouverture du fichier contenant le graphe
		gr = new GraphFileReader(CHEMIN_GRAPHE);

		// liste des noeuds (utilis
		HashMap<String, Integer> nodes = gr.getNodeMapping();
		
		// matrice initiale
		AdjacencyMatrix m = gr.getAdjacencyMatrix();
		
		AdjacencyMatrix BC, CC = null;
		BC = LinkAnalysis.calculateBC(gr.getAdjacencyMatrix());
		CC = LinkAnalysis.calculateCC(gr.getAdjacencyMatrix());

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
			tmp = LinkAnalysis.calculateHc(m, ac); // Hubs
			ac = LinkAnalysis.calculateAc(m, hc);  // Authority
			hc = tmp;
			pr = LinkAnalysis.calculatePRc(m, pr); // PageRank
		}

		// affiche les resultats
		
		// Couplage bibliographique, position 8 et 9 dans le tableau (en fonction du fichier de graphe)
		System.out.println("BC 25 <-> 22 : " + BC.get(8,9));
		// Co-Citation, position 1 et 7 dans le tableau
		System.out.println("CC 20 <->  2 : " + CC.get(1,7));
		
		System.out.println("------------------------------------------------");
		System.out.println("- Node -  Hubs   - Authority - PageRank        -");
		System.out.println("------------------------------------------------");
		int i;
		for (Entry<String, Integer> e : nodes.entrySet()) {
			i = e.getValue();
			System.out.printf(" %4d    %1.5f   %1.5f     %1.5f\n",
					Integer.parseInt(e.getKey()), hc.get(i), ac.get(i), pr.get(i));
			
		}
	}
}
