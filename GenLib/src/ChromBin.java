import java.util.*;

/**
 * Classe representant un chromosome binaire de longueur fixee a la creation.
 * 
 * @author Simon Hintermann & Romain de Wolff
 */
public class ChromBin extends Chromosome {

   /** Le tableau permettant de representer les genes du chromosome */
   protected boolean[] tabGenes;
   
   /**
    * Constructeur d'un chromosome binaire
    * 
    * @param tailleChrom Le nombre de genes par individu
    */
   ChromBin(int tailleChrom) {
      tabGenes = new boolean[tailleChrom];
      this.tailleChrom = tailleChrom;
   }
   
   /**
    * Methode permettant de remplacer le tableau des genes du chromosome
    * 
    * @param tab Le tableau des genes qui va remplacer l'ancien
    */
   protected void setGenes(boolean[] tab) {
      this.tabGenes = tab;
   }
   
   @Override
   protected void init() {
      // Boucle d'initialisation des genes
      for(int i=0; i<this.tabGenes.length; i++) {
         tabGenes[i] = (((int) (Math.random() * 2)) == 0?true:false);
      }
   }
   
   @Override
   protected boolean equals(Chromosome c) {
      // Test d'appartenance a la classe ChromBin
      if (c.getClass() == ChromBin.class) {
         // Test de la longueur du tableau des genes
         if (this.tabGenes.length == ((ChromBin)c).tabGenes.length) {
            // Boucle de test d'egalite des genes
            for (int i = 0; i < tabGenes.length; i++) {
               if(this.tabGenes[i] != ((ChromBin)c).tabGenes[i])
                  return false;
            }
            return true;
         }
         else
            return false;
      }
      else
         return false;
   }

   @Override
   protected String chromToString() {
      
      /** La chaine de caracteeres a renvoyer */
      String ret = "";
      
      // Construction de la chaine de caracteres
      for(int i=0; i<this.tailleChrom; i++) {
         ret += this.tabGenes[i]?"1":"0";
      }
      
      return ret;
   }

   @Override
   protected Chromosome copyChrom() {
      
      /** Le chromosome a renvoyer */
      ChromBin ret = new ChromBin(this.tailleChrom);
      /** Le tableau des  */
      boolean[] tab = new boolean[tailleChrom];
      
      // Creation de la copie du tableau de l'individu
      for(int i=0; i<tailleChrom; i++) {
         tab[i] = this.tabGenes[i];
      }
      
      // Copie du tableau au chromosome a renvoyer
      ret.setGenes(tab);
      return ret;
   }
}
