// Modifications: Simon Hintermann
// But: pouvoir résoudre un tri de cinq chiffres avec un algorithme génétique


package com.softtechdesign.ga.examples;

	import com.softtechdesign.ga.*;
	import java.util.*;

	/**
	    Traveling salesman problem. A salesman has to travel to N different cities. In what
	  sequence should he visit each city to minimize the total distance traveled. Each city
	  is represented in the chromosome string as a letter (e.g. 'A' or 'B').

	  To simplify the mathematics of the fitness function, this example reduces the coordinate
	  space to one dimension. Each city (or node) has a position given by one coordinate.
	  This model can be extrapolated to 2 or 3 dimensions by giving each city (or node) a 2
	  dimensional (X,Y) or 3 dimensional (X,Y,Z) coordinate and then modifying the distance
	  calculating function accordingly.

	  If a chromosome = 'ABCDEFGHI', then the fitness is evaluated as
	    fitness = Dist(A, B) + Dist(B, C) + Dist(C, D)....+ Dist(S, T)
	  Higher fitness values mean a higher probability that this chromosome will reproduce.

	  The possible combinations (sequences of cities) is N factorial. For 20 cities, the possible
	  combinations = 20! or 2.432902008177e+18.
	  This is an enormous number. If you tried every combination of sequences and could test
	  10,000 of those sequences per second (looking for the minium), it would take your
	  computer 8 million years to randomly come up with the minimum (ideal) sequence.
	*/

	public class GASort extends GAString
	{
		int possibilites[][] = null;
		
	    public GASort() throws GAException
	    {
	        super(  9, //size of chromosome
	                100, //population has N chromosomes
	                0.7, //crossover probability
	                10, //random selection chance % (regardless of fitness)
	                100, //max generations
	                0, //num prelim runs (to build good breeding stock for final/full run)
	                25, //max generations per prelim run
	                0.06, //chromosome mutation prob.
	                100, //number of decimal places in chrom
	                "ABCDEFGHIJ", //gene space (possible gene values)
	                Crossover.ctTwoPoint, //crossover type
	                true); //compute statisitics?
	        
	        setInitialSequence();
	    }
	    
	    // initisalisation du tableau de toutes les possibilités de suites
	    // de cinq chiffres
	    void setInitialSequence()
	    {
	    	 int idx = 0;
	    	 possibilites = new int[120][5];
	         for(int i = 0; i < 5; i++) {
	         	for(int j = 0; j < 5; j++) {
	         		if (i != j) {
	 	            	for(int k = 0; k < 5; k++) {
	 	            		if (k != i && k != j) {
	 		                	for(int l = 0; l < 5; l++) {
	 			            		if (l != i && l != j && l != k) {
	 			                    	for(int m = 0; m < 5; m++) {
	 			    	            		if (m != i && m != j && m != k && m != l) {
	 			    	            			possibilites[idx][0] = i+1;
	 			    	            			possibilites[idx][1] = j+1;
	 			    	            			possibilites[idx][2] = k+1;
	 			    	            			possibilites[idx][3] = l+1;
	 			    	            			possibilites[idx][4] = m+1;
	 			    	            			idx++;
	 			    	            		}
	 			                    	}
	 			            		}
	 		                	}
	 	            		}
	 	            	}
	         		}
	         	}
	         }
	    }
	    
	    // Fonction pour échanger deux chiffres si le premier est plus grand
	    // que le deuxième
	    void change(int a, int b, int[] tab)
	    {
	    	int mem = 0;
    		if(tab[a] > tab[b])
    		{
    			mem = tab[b];
    			tab[b] = tab[a];
    			tab[a] = mem;
    		}
	    }
	    
	    // Fontion renvoyant true si le tableau de cinq chiffres passé
	    // en paramètres est trié
	    boolean tabTrie(int tab[])
	    {
	    	if(tab[0] < tab[1] &&
	    	   tab[1] < tab[2] &&
	    	   tab[2] < tab[3] &&
	    	   tab[3] < tab[4])
	    		return true;
	    	return false;
	    }
	    
	    // copie d'un tableau 
	    int[] getTab(int idx)
	    {
	    	int tab[] = new int[5];
	    	for(int i=0; i<5; i++)
	    		tab[i] = possibilites[idx][i];
	    	return tab;
	    }
	    
	    /** Fitness function for GASalesman now access genes directly through genes[] array
	     * Old benchmark: 29 sec. New benchmark 16 sec.
	     */
	    protected double getFitness(int iChromIndex)
	    {
	        char genes[] = this.getChromosome(iChromIndex).getGenes();
	        int lenChromosome = genes.length;
	        double fitness = 0.0;
	        // si le tableau n'est pas init, on renvoie 0
	        if(possibilites == null)
	        	return 0.0;
	        int tab[];
	        
	        // permutations avec le chromosome
	        for(int p=0; p<possibilites.length; p++)
	        {
	        	tab = getTab(p);
	        	for(int i=0; i<lenChromosome; i++)
		        {
		            switch(genes[i])
		            {
		            case 'A': change(0,1,tab); break;
		            case 'B': change(0,2,tab); break;
		            case 'C': change(0,3,tab); break;
		            case 'D': change(0,4,tab); break;
		            case 'E': change(1,2,tab); break;
		            case 'F': change(1,3,tab); break;
		            case 'G': change(1,4,tab); break;
		            case 'H': change(2,3,tab); break;
		            case 'I': change(2,4,tab); break;
		            case 'J': change(3,4,tab); break;
		            default: ;
		            }
		        }
	        	if(tabTrie(tab)) 
	        		fitness++;
	        }
	        // on renvoie le pourcentage de tableaux pouvant être triés
	        return fitness/120.0;
	    }
	    
	    public static void main(String[] args)
	    {
	        String startTime = new Date().toString();
	        System.out.println("GASort GA..." + startTime);
	        
	        try
	        {
	            GASort sort = new GASort();
	            Thread threadSort = new Thread(sort);
	            threadSort.start();
	        }
	        catch (GAException gae)
	        {
	            System.out.println(gae.getMessage());
	        }
	        
	        System.out.println("Process started at " + startTime + ". Process completed at " +  new Date().toString());
	    }

	}

