/**
 * Classe abstraite Representant le modele des chromosomes
 * 
 * @author Simon Hintermann & Romain de Wolff
 */
public abstract class Chromosome {
   
   /** La taille du chromosome (le nombre de genes) */
   protected int tailleChrom;
   /** Le score du chromosome */
   protected double fitness;
   /** Le rang du chromosome */
   protected double rank; 
   
   /**
    * Methode de test d'egalite
    * 
    * @param c L'autre chromosome a tester
    * @return Le résultat de l'egalite
    */
   protected abstract boolean equals(Chromosome c);
   
   /**
    * Methode permettant de visualiser un chromosome sous forme de chaine de
    * caracteres.
    * 
    * @return La chaine de caracteres representant le chromosome
    */
   protected abstract String chromToString();
   
   /**
    * Methode permettant de renvoyer un instance copiee d'un chromosome.<br>
    * 
    * Cette methode sera utilisee pour dupliquer des parents afin de les traiter
    * et qu'ils deviennent des enfants (croisements, mutations).
    * 
    * @return Instance de copie du chromosome
    */
   protected abstract Chromosome copyChrom();
   
   /**
    * Methode permettant d'initialiser les genes d'un chromosome aleatoirement.
    */
   protected abstract void init();
}
