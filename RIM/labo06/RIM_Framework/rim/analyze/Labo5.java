/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rim.analyze;

/**
 *
 * @author rdewolff
 */
public class Labo5 {

   public static void main( String args[] ) {
      
      System.out.println("** RIM - LABO 5 **");
      
      GraphFileReader gr = null;
      
      String fileGraph = "/Users/rdewolff/Documents/HEIG-VD/eclipse/svnChamelle/RIM/labo06/RIM_Framework/rim/analyze/graphe-full.txt";
      
      gr = new GraphFileReader(fileGraph);
      
      System.out.println("Scanned graph from the file : ");
      
      System.out.println(gr.getAdjacencyMatrix());
      
   }
}
