import java.util.*;

/**
 * Classe Representant un algorithme genetique (AG) generique, avec les methodes
 * dont il aura besoin dans tous les cas.
 * 
 * @author Simon Hintermann & Romain de Wolff
 */
public abstract class GenLib {
   /** Le nombre d'individus de l'algorithme genetique */
   protected int taillePop;
   /** La probabilite pour un gene de muter */
   protected double mutationProb;
   /**
    * Le type de croisement applique<br>
    * 0: one-point<br>
    * 1: two-points<br>
    * 2: uniform
    */
   protected int crossoverType;
   /** Le nombre de generations a operer avant d'arreter l'AG */
   protected int nbGen;
   /** Le tableau des individus de l'algorithme genetique */
   protected Chromosome[] chromosomes;
   /** Le tableau des enfants entre deux generations */
   protected Chromosome[] childs;
   /** 
    * L'index de l'individu possedant le meilleur fitness.<br>
    * 
    * Cet index est utilisable lors de la selection des individus qui feront
    * partie de la prochaine generation. Il est mis a jour lors de 
    * l'initialisation de la population et la creation des enfants.
    */
   protected int indexOfBestFitness;
   /** La valeur du meilleur fitness*/
   protected double bestFitness = Double.MAX_VALUE;
   /** La valeur du pire fitness (pour normaliser) */
   protected double worstFitness = 0;
   /** Le type de changement de generation */
   protected int typeOfNextGenSelection;
   /** Le type de selection des parents pour les croisements et mutations */
   protected int typeOfParentSelection;
   /** Le nombre de parents a recuperer pour les croisements et mutations */
   protected int nbParents;
   /** Le tableau utilise pour la selection des parents: index du chromosome, 
    * indice dans la structure (wheel, echantillonnage) */
   protected double[] tabSelection;
   
   /**
    * Methode permettant d'initialiser la population de l'AG
    */
   protected abstract void initPopulation();
   
   /**
    * Methode executant les mutations sur chaque individu
    */
   protected abstract void doMutations();
   
   /**
    * Methode permettant de faire un croisement en un point de deux chromosomes
    * 
    * @param c1
    * @param c2
    */
   protected abstract void doOnePointCrossover(Chromosome c1, Chromosome c2);
   
   /**
    * Methode permettant de faire un croisement en 2 point de deux chromosomes
    * 
    * @param c1
    * @param c2
    */
   protected abstract void doTwoPointCrossover(Chromosome c1, Chromosome c2);
   
   /**
    * Methode permettant de faire un croisement uniforme de deux chromosomes.<br>
    * 
    * Le croisement est fait avec une chance sur deux pour chaque gene
    * 
    * @param c1
    * @param c2
    */
   protected abstract void doUniformCrossover(Chromosome c1, Chromosome c2);
   
   /**
    * Methode permettant de lancer l'evolution de l'AG
    */
   public synchronized void evolve() {};
   
   /**
    * Methode permettant de calculer le fitness d'un individu en le passant en
    * parametre
    * 
    * @param c L'individu a tester
    * @param indexOfChrom L'index du chromosome, au cas ou il faudrait se 
    * souvenir de son index (si c'est le meilleur)
    * @return Le fitness obtenu par l'individu
    */
   protected abstract double getFitness(Chromosome c, int indexOfChrom); 
   
   /**
    * Methode permettant de calculer la generation suivante en selectionnant des
    * parents qui vont faire des croisements, operer les mutations, puis de 
    * selectionner lesquels des parents et des enfants ainsi concernes feront 
    * partie de la prochaine generation.
    */
   protected abstract void doNextGen();
   
   /**
    * Methode permettant de selectionner des parents pour operer les croisements
    * et les mutations et generer des enfants pour la nouvelle generation.<br><br>
    * 
    * Le pourcentage doit etre superieur a 50%.
    * 
    * @return 
    */
   protected abstract int[] selectParents();
   
   /**
    * Methode permettant de selectionner les individus qui feront partie de la 
    * prochaine generation a l'aide d'une methode de selection.
    * 
    * @param indexsOfParents Les indexs des parents qui feront partie de la 
    * selection. Si ce parametre est nul, on considere tous les parents.
    */
   protected abstract void selectNextGen(int[]indexsOfParents);
   
   /**
    * Methode permettant de normaliser les fitness et d'attribuer un rang a 
    * chaque chromosome en fonction de dson fitness.
    */
   protected void doChromFitnessUpdate() {
      
      /** Liste de tri des fitness */
      TreeMap<Double, Integer> s = new TreeMap<Double, Integer>();
      // Calcul des fitness normalises
//      for(int i=0; i<this.taillePop; i++) {
//         this.chromosomes[i].fitness = (this.worstFitness - 
//             this.chromosomes[i].fitness)/this.bestFitness;
//         s.put(this.chromosomes[i].fitness, i);
//      }
      
      if(this.typeOfParentSelection != 2) {
         for(int i=0; i<this.taillePop; i++) {
         this.tabSelection[i] = (i == 0 ? 
               this.chromosomes[i].fitness : 
               this.tabSelection[i - 1] +
                this.chromosomes[i].fitness);
         }
      }
   }
   
   /** 
    * Methode permettant d'obtenir le nombre de chromosomes differents du 
    * meilleur.
    * 
    * @return Le nombre de chromosomes differents du meilleur
    */
   protected abstract double getAvgDeviation();
}
