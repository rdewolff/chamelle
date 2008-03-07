
import java.io.*;
import java.util.*;

/**
 * Classe representant une carte formee de points geographiques ponderes 
 * (habitants) dans laquelle il faudra trouver un certain nombre de centres 
 * d'urgence ainsi que les placer de maniere a minimiser les couts, sachant 
 * qu'un centre a un certain cout et que les habitants se deplacent vers le 
 * centre le plus proche avec un certain cout.
 * 
 * Les donnees sont presentes dans un fichier qui fournira le nombre d'habitants
 * par point, le cout d'un centre, et les coordonnees de chaque point.
 * 
 * @author Simon Hintermann & Romain de Wolff
 */
public class CarteBin extends GenLib implements Runnable {

   /** Le nombre de genes binaires d'un individu */
   protected int tailleIndiv;
   /** La taille de la population lue dans le fichier de donnees */
   protected static int sizePop;
   /** Le cout d'un centre medical */
   protected static int centerCost;
   /** Le tableau des points de la carte contenant leur coord. et leur poids */
   protected static int[][] tabPoids;
   /** La matrice des distances entre chaque point */
   protected static int[][] matDist;
   /** Le tableau des centres d'un chromosome, utilise pour le calcul du fitness */
   protected static int[] tabCenters;
   /** Le tableau de tri pour la methode du rang: fitness, index */
   protected double[][] tabRankParents; 
   /** Le tableau de tri pour la methode de la selection par elitisme */
   protected double[][] tabRankAll;
   /** Le tableau de tri pour la methode de la strategie d'evolution: fitness */
   protected double[][] tabRankChilds; 

   static {
      
      /** Le tampon de lecture du fichier */
      BufferedReader br = null;

      // Ouverture du fichier
      try {
         br = new BufferedReader(new FileReader("in.txt"));
      } catch (FileNotFoundException e) {
         System.out.println("Erreur lors de l'ouverture du fichier");
      }

      /** La chaine de caracteres de lecture ligne par ligne */
      String ligne = "";
      int cpt = 0;

      // Lecture du fichier de donnees
      try {
         ligne = br.readLine();
         /** Les elements de la ligne traitee */
         String[] tokens = ligne.split(" ");
         // Recuperation du nombre de points et du cout des centres
         CarteBin.sizePop = Integer.valueOf(tokens[0]);
         CarteBin.centerCost = Integer.valueOf(tokens[1]);
         // Initialisation de la taille du tableau des points
         CarteBin.tabPoids = new int[CarteBin.sizePop][3];
         // Initialisation de la taille du tableau des distances
         CarteBin.matDist = new int[CarteBin.sizePop][CarteBin.sizePop];
         // Initialisation du tableau des centres
         CarteBin.tabCenters = new int[CarteBin.sizePop];
         // Boucle d'enregistrement des point et de leur poids
         while ((ligne = br.readLine()) != null) {
            tokens = ligne.split(" ");
            CarteBin.tabPoids[cpt][0] = Integer.valueOf(tokens[0]);
            CarteBin.tabPoids[cpt][1] = Integer.valueOf(tokens[1]);
            CarteBin.tabPoids[cpt][2] = Integer.valueOf(tokens[2]);
            cpt++;
         }
      } catch (IOException e) {
         System.out.println("Erreur lors de la lecture du fichier");
      } catch (NumberFormatException e) {
         System.out.println("Erreur de decodage d'un entier");
      }

      // Boucle de calcul des distances entre les points
      for (int i = 0; i < CarteBin.sizePop; i++) {
         for (int j = 0; j < CarteBin.sizePop; j++) {
            if (i != j) {
               int x1 = CarteBin.tabPoids[i][0];
               int y1 = CarteBin.tabPoids[i][1];
               int x2 = CarteBin.tabPoids[j][0];
               int y2 = CarteBin.tabPoids[j][1];
               CarteBin.matDist[i][j] = (int) Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
               CarteBin.matDist[j][i] = CarteBin.matDist[i][j];
            } else {
               CarteBin.matDist[i][j] = 0;
            }
         }
      }
   }

   /**
    * Constructeur
    * 
    * @param taillePop La taille de la population de l'AG
    * @param mutationProb La probabilite qu'un gene mute
    * @param crossoverType Le type de croisement utilise
    * @param nbGen Le nombre de generations a evoluer
    * @param typeOfNextGenSelection Le type de selection des individus qui 
    * seront gardes lors de la generation suivante.<br>
    * 0: Generationnel
    * 1: Strategie d'evolution
    * 2: Stationnaire
    * 3: Elitiste
    */
   CarteBin(int taillePop,
       double mutationProb,
       int crossoverType,
       int nbGen,
       int typeOfParentSelection,
       int typeOfNextGenSelection) {

      // Initialisation des variables
      this.taillePop = taillePop;
      this.mutationProb = mutationProb;
      this.crossoverType = crossoverType;
      this.typeOfNextGenSelection = typeOfNextGenSelection;
      this.typeOfParentSelection = typeOfParentSelection;
      
      this.tailleIndiv = CarteBin.sizePop;
      this.nbGen = nbGen;
      this.chromosomes = new ChromBin[taillePop];
      this.tabSelection = new double[taillePop];
      this.tabRankParents = new double[taillePop][2];

      // Definition de la taille du tableau des enfants qui seront generes lors 
      // de chaque passage a une autre generation
      switch (typeOfNextGenSelection) {
         // Generationnel 
         case 0:
            this.childs = new ChromBin[taillePop];
            this.nbParents = (int) Math.ceil(taillePop * 0.5);
            break;
         // Strategie d'evolution
         case 1:
            this.childs = new ChromBin[(taillePop / 10) % 2 == 0 ? 
               1 : taillePop / 10];
            break;
         // Stationnaire
         case 2:
            this.childs = 
                new ChromBin[(int) Math.round(this.taillePop / 20.0) == 0 ? 1 : 
                   (int) Math.round(this.taillePop / 20.0)];
            this.nbParents = (int) Math.ceil(taillePop * 0.3);
            break;
         // Elitiste
         case 3:
            this.childs = new ChromBin[(taillePop / 20) % 2 == 0 ? 
               1 : taillePop / 20];
            break;
         default:
            System.out.println("Erreur dans le parametre du type de " +
                "generation suivante (0, 1, 2, ou 3)");
            System.exit(1);
      }
      
      // Initialisation du tableau des rangs de tous les individus a la taille 
      // du nombre de parents plus le nombre d'enfants
      this.tabRankAll = new double[taillePop+this.childs.length][3];
      this.tabRankChilds = new double[this.childs.length][2];
      
      // Si le nombre de parents est inferieur a 2, il faut le limiter a 2
      if(this.nbParents < 2)
         this.nbParents = 2;
      // Creation des chromosomes binaires avec la bonne taille
      for (int i = 0; i < this.chromosomes.length; i++) {
         this.chromosomes[i] = new ChromBin(tailleIndiv);
      }
   }
   
   /**
    * Methode de tri du tableau des rangs des chromosomes (pourrie)
    */
   private void sortByRank(int mode) {
      // Si on veut classer tous les individus
      if(mode == 0) {
         for(int i=0; i<this.taillePop; i++) {
            this.tabRankAll[i][0] = this.chromosomes[i].fitness;
            this.tabRankAll[i][1] = i;
            this.tabRankAll[i][2] = 0;
         }
         for(int i=0; i<this.childs.length; i++) {
            this.tabRankAll[i+this.taillePop][0] = getFitness(this.childs[i], -1);
            this.tabRankAll[i+this.taillePop][1] = i;
            this.tabRankAll[i][2] = 1;
         }
         for(int i=0; i<tabRankAll.length; i++) {
            for(int j=i+1; j<tabRankAll.length; j++) {
               if(tabRankAll[j][0] > tabRankAll[i][0]) {
                  double temp = tabRankAll[j][0];
                  double temp2 = tabRankAll[j][1];
                  double temp3 = tabRankAll[j][2];
                  tabRankAll[j][0] = tabRankAll[i][0];
                  tabRankAll[j][1] = tabRankAll[i][1];
                  tabRankAll[j][2] = tabRankAll[i][2];
                  tabRankAll[i][0] = temp;
                  tabRankAll[i][1] = temp2;
                  tabRankAll[i][2] = temp3;
               }
            }
         }
      } else if(mode == 1) {
         for(int i=0; i<taillePop; i++) {
            tabRankParents[i][0] = this.chromosomes[i].fitness;
            tabRankParents[i][1] = (double)i;
         }
         for(int i=0; i<tabRankParents.length; i++) {
            for(int j=i+1; j<tabRankParents.length; j++) {
               if(tabRankParents[j][0] > tabRankParents[i][0]) {
                  double temp = tabRankParents[j][0];
                  double temp2 = tabRankParents[j][1];
                  tabRankParents[j][0] = tabRankParents[i][0];
                  tabRankParents[j][1] = tabRankParents[i][1];
                  tabRankParents[i][0] = temp;
                  tabRankParents[i][1] = temp2;
               }
            }
         }
      } else if(mode == 2) {
         for(int i=0; i<this.childs.length; i++) {
            tabRankChilds[i][0] = getFitness(this.childs[i], -1);
            tabRankChilds[i][1] = (double)i;
         }
         for(int i=0; i<tabRankChilds.length; i++) {
            for(int j=i+1; j<tabRankChilds.length; j++) {
               if(tabRankChilds[j][0] > tabRankChilds[i][0]) {
                  double temp = tabRankChilds[j][0];
                  double temp2 = tabRankChilds[j][1];
                  tabRankChilds[j][0] = tabRankChilds[i][0];
                  tabRankChilds[j][1] = tabRankChilds[i][1];
                  tabRankChilds[i][0] = temp;
                  tabRankChilds[i][1] = temp2;
               }
            }
         }
      } else {
         System.out.println("Mode pas pris en compte pour le tri par rang des " +
             "individus (0, 1, ou 2)");
      }
   }
   
   @Override
   protected void initPopulation() {
      // Boucle d'initialisation des individus et calcul des fitness
      for (int i = 0; i < this.chromosomes.length; i++) {
         this.chromosomes[i].init();
         this.chromosomes[i].fitness = getFitness(this.chromosomes[i], i);
      }
      
   }

   @Override
   protected void doMutations() {
      // Boucle d'execution des mutations
      for (int i = 0; i < this.childs.length; i++) {
         for (int j = 0; j < this.childs[i].tailleChrom; j++) {
            if (Math.random() * 1.0 < this.mutationProb) {
               ((ChromBin) this.childs[i]).tabGenes[j] =
                   !((ChromBin) this.childs[i]).tabGenes[j];
            }
         }
      }
   }

   @Override
   protected void doOnePointCrossover(Chromosome c1, Chromosome c2) {
      /** Index de la separation choisi de maniere aleatoire */
      int separation = (int) (Math.random() * this.chromosomes[0].tailleChrom - 2) + 1;

      // Boucle d'execution du croisement des deux chromosomes
      for (int i = 0; i < separation; i++) {
         // On echange les genes avant la separation
         boolean temp = ((ChromBin) c1).tabGenes[i];
         ((ChromBin) c1).tabGenes[i] = ((ChromBin) c2).tabGenes[i];
         ((ChromBin) c2).tabGenes[i] = temp;
      }
   }

   @Override
   protected void doTwoPointCrossover(Chromosome c1, Chromosome c2) {

      /** Borne inferieure du croisement */
      int separation = (int) (Math.random() * (this.chromosomes[0].tailleChrom - 3)) + 1;
      /** Borne superieure du croisement */
      int separation2 = (int) (Math.random() * (this.chromosomes[0].tailleChrom - separation - 2)) + 1;
      separation2 = separation + separation2 + 1;

      // Boucle d'execution du croisement des deux chromosomes
      for (int i = separation; i < separation2; i++) {
         // On echange les genes apres la separation
         boolean temp = ((ChromBin) c1).tabGenes[i];
         ((ChromBin) c1).tabGenes[i] = ((ChromBin) c2).tabGenes[i];
         ((ChromBin) c2).tabGenes[i] = temp;
      }
   }

   @Override
   protected void doUniformCrossover(Chromosome c1, Chromosome c2) {

      /** Index de la separation choisi de maniere aleatoire */
      int separation;

      // Boucle d'execution du croisement des deux chromosomes
      for (int i = 0; i < c1.tailleChrom; i++) {
         // Une chance sur deux d'echanger les genes a l'index i
         separation = (int) (Math.random() * 2);
         // On echange les genes
         if (separation == 0) {
            boolean temp = ((ChromBin) c1).tabGenes[i];
            ((ChromBin) c1).tabGenes[i] = ((ChromBin) c2).tabGenes[i];
            ((ChromBin) c2).tabGenes[i] = temp;
         }
      }
   }

   @Override
   public void evolve() {
      //System.out.println("Avant cross: ");
      for (int i = 0; i < this.taillePop; i++) {
      //System.out.println(this.chromosomes[i].fitness);
      }

      int cpt = 0;
      // Boucle d'evolution generation par generation
      for (int i = 0; i < this.nbGen; i++) {
         doNextGen();
         System.out.println("Generation: " + cpt++ + "Best: " + bestFitness);
         System.out.println("Deviation: " + getAvgDeviation());
      }

      //System.out.println("Apres cross: ");
      for (int i = 0; i < this.taillePop; i++) {
      //System.out.println(this.chromosomes[i].fitness);
      }
   }

   @Override
   protected double getFitness(Chromosome c, int indexOfChrom) {

      /** Le cout du chromosome */
      int cout = 0;
      /** Instance d'un chromosome binaire */
      ChromBin chrom = (ChromBin) c;
      int cpt = 0;

      // Boucle d'enregistrement des centres et d'ajout du cout des centres
      for (int i = 0; i < c.tailleChrom; i++) {
         if (chrom.tabGenes[i]) {
            cout += CarteBin.centerCost;
            CarteBin.tabCenters[cpt] = i;
            cpt++;
         }
      }

      /** La distance minimale pour determiner le centre le plus proche d'un point */
      int min = Integer.MAX_VALUE;

      // Boucle de calcul du cout de deplacement pour chaque point
      for (int i = 0; i < this.tailleIndiv; i++) {
         // Boucle sur chaque centre pour calculer la distance min
         for (int j = 0; j < cpt; j++) {
            if (CarteBin.matDist[CarteBin.tabCenters[j]][i] < min) {
               min = CarteBin.matDist[CarteBin.tabCenters[j]][i];
            }
         }
         cout += (min * CarteBin.tabPoids[i][2]);
         min = Integer.MAX_VALUE;
      }
      
      if(indexOfChrom >= 0) {
         // Mise a jour du meilleur et du pire fitness
         if (cout > this.worstFitness) {
            this.worstFitness = cout;
         }
         if (cout < this.bestFitness) {
            this.bestFitness = cout;
            this.indexOfBestFitness = indexOfChrom;
         }
      }

      // On retourne l'inverse du cout, afin de pouvoir maximiser le fitness.
      return 1.0 / cout;
   }

   @Override
   protected void doNextGen() {
      //doChromFitnessUpdate();
      int[] tab = selectParents();
      // Execution des croisements
      for (int i = 0; i < this.childs.length; i++) {
         int x1 = (int)Math.round(Math.random()*tab.length-0.5);
         int x2;
         while((x2 = (int)Math.round(Math.random()*tab.length-0.5)) == x1);
         ChromBin b1 = (ChromBin) this.chromosomes[x1].copyChrom();
         ChromBin b2 = (ChromBin) this.chromosomes[x2].copyChrom();
         switch (crossoverType) {
            case 0:
               doOnePointCrossover(b1, b2);
               break;
            case 1:
               doTwoPointCrossover(b1, b2);
               break;
            case 2:
               doUniformCrossover(b1, b2);
               break;
            default:
               System.out.println("Mauvais parametre pour le type de " +
                   "croisement (0, 1, ou 2)");
               System.exit(1);
         }
         this.childs[i] = b1;
         i++;
         if (this.childs.length - i > 0) {
            this.childs[i] = b2;
         }
      }
      doMutations();
      selectNextGen(tab);
   }

   /**
    * Point d'entree de la carte binaire
    */
   @Override
   public void run() {
      // Initialisation de la population
      this.initPopulation();
      // Evolution de l'AG
      evolve();
   }
   
   class twoDimComp implements Comparator {
      public int compare(Object arg0, Object arg1) {
         double[] tab1 = (double[])arg0;
         double[] tab2 = (double[])arg1;
         if(tab1[0] < tab2[0])
            return 1;
         else
            return -1;
      }
   }

   @Override
   protected int[] selectParents() {

      /** Le tableau des indexs des parents selectionnes pour les croisements a 
       * renvoyer */
      int[] ret = new int[this.nbParents];
      int cpt = 0;

      // Section de selection des parents suivant la strategie choisie 
      switch (typeOfParentSelection) {
         // Roulette
         case 0:
            for (int i = 0; i < this.childs.length; i++) {
               ret[cpt] += cpt++;
            }
            break;
         // Echantillonnage aleatoire universel
         case 1:
            break;
         // Rang
         case 2:
            sortByRank(1);
            int x;
            // Selection des parents participant a la creation des enfants
            for (int i = 0; i < nbParents; i++) {
               // Calcul du rang
               x = (int) (this.taillePop *
                   (1 - (Math.sqrt(1 - (Math.random() * 1)))) - 0.5);
               ret[cpt] = (int)tabRankParents[x][1];
               cpt++;
            }
            break;
         default:
      }

      return ret;
   }

   @Override
   protected void selectNextGen(int[] indexsOfParents) {
      // Section de selection des individus de la generation suivante d'apres la
      // strategie choisie 
      switch (typeOfNextGenSelection) {
         // Generationnel
         case 0:
            if(this.childs.length > this.taillePop)
               sortByRank(2);
            for(int i=0; i<this.taillePop; i++) {
               if(i != indexOfBestFitness) {
                  this.chromosomes[i] = this.childs[i];
                  this.chromosomes[i].fitness = getFitness(this.chromosomes[i], i);
               }
            }
            break;
         // Strategie d'evolution
         case 1:
            sortByRank(2);
            if(this.typeOfParentSelection != 2)
               sortByRank(1);
            for (int i = 0; i < this.childs.length; i++) {
               if(1.0/getFitness(this.childs[(int)tabRankChilds[i][1]], -1) < this.worstFitness) {
                  this.chromosomes[(int)this.tabRankParents[this.taillePop-(i+1)][1]] = 
                      this.childs[(int)tabRankChilds[i][1]];
                  this.chromosomes[(int)this.tabRankParents[this.taillePop-(i+1)][1]].fitness = 
                      getFitness(this.chromosomes[(int)this.tabRankParents[this.taillePop-(i+1)][1]], 
                      (int)this.tabRankParents[this.taillePop-(i+1)][1]);
               }
            }
            break;
         // Stationnaire
         case 2:
            for (int i = 0; i < this.childs.length; i++) {
               if(1.0/getFitness(this.childs[i], -1) < this.worstFitness) {
                  this.chromosomes[(int)this.tabRankParents[this.taillePop-(i+1)][1]] = this.childs[i];
                  this.chromosomes[(int)this.tabRankParents[this.taillePop-(i+1)][1]].fitness = 
                      getFitness(this.chromosomes[(int)this.tabRankParents[this.taillePop-(i+1)][1]], 
                      (int)this.tabRankParents[this.taillePop-(i+1)][1]);
               }
            }
            break;
         // Elitiste
         case 3:
            sortByRank(0);
            for(int i=0; i<this.chromosomes.length; i++) {
               this.chromosomes[i] = this.tabRankAll[i][2] == 0 ? 
                  this.chromosomes[(int)this.tabRankAll[i][1]] : 
                  this.childs[(int)this.tabRankAll[i][1]];
               this.chromosomes[i].fitness = getFitness(this.chromosomes[i], i);
            }
            break;
         default:
      }
   }

   @Override
   protected double getAvgDeviation() {
      int cpt = 0;
      for (int i = 0; i < this.taillePop; i++) {
         if (i != this.indexOfBestFitness) {
            if (!this.chromosomes[i].equals(this.chromosomes[this.indexOfBestFitness])) {
               cpt++;
            } else {
               //System.exit(1);
            }
         }
      }
      return (double) cpt;
   }
}
