package com.softtechdesign.ga;

import java.math.*;

/**
 * GAInt class models chromosomes as floating point numbers. 
 * 
 * @author Jeff Smith
 */
public abstract class GAInt extends GA
{
    
    /** constrain chromsosomes to positive numbers only? */
    protected boolean positiveNumOnly; 

    /**
     * Returns the chromosome casted as a ChromInt
     * @param index
     * @return ChromInt
     */
    protected ChromInt getChromosome(int index)
    {
        return ((ChromInt)this.chromosomes[index]);
    }

    /**
     * Randomly swap two genes in the chromosome identified by the given index (iChromIndex)
     * @param iChromIndex
     */
    protected void doRandomMutation(int iChromIndex)
    {
        int iGene;
        int rNewGene;

        iGene = getRandom(chromosomeDim);

        rNewGene = ((ChromInt)this.chromosomes[iChromIndex]).genes[iGene];
        if (getRandom(100) > 50)
		  		switch(iGene)
				  {
				  		case 0: rNewGene = rNewGene + (((ChromInt)this.chromosomes[iChromIndex]).genes[iGene]==0?1:0); break;
						case 1: rNewGene = rNewGene + getRandom(20-((ChromInt)this.chromosomes[iChromIndex]).genes[iGene]+1); break;
						case 2: rNewGene = rNewGene + getRandom(200-((ChromInt)this.chromosomes[iChromIndex]).genes[iGene]+1); break;
				  }
        else
            switch(iGene)
				  {
				  		case 0: rNewGene = rNewGene - (((ChromInt)this.chromosomes[iChromIndex]).genes[iGene]==0?0:1); break;
						case 1: rNewGene = rNewGene - getRandom(Math.abs(((ChromInt)this.chromosomes[iChromIndex]).genes[iGene]-20)+1); break;
						case 2: rNewGene = rNewGene - getRandom(Math.abs(((ChromInt)this.chromosomes[iChromIndex]).genes[iGene]-200)+1); break;
				  }

        ((ChromInt)this.chromosomes[iChromIndex]).genes[iGene] = rNewGene;
    }


    /** 
     * Create random chromosomes from the given gene space (floating pt numbers).
     */
    protected void initPopulation()
    {
        for (int i = 0; i < populationDim; i++)
        {
            for (int iGene = 0; iGene < chromosomeDim; iGene++)
            {
                if ((this.positiveNumOnly == true) || (getRandom(100) > 50))
					 {
                    switch(iGene)
						  {
						  		case 0: ((ChromInt)this.chromosomes[i]).genes[iGene] = getRandom(1); break;
								case 1: ((ChromInt)this.chromosomes[i]).genes[iGene] = getRandom(20)+1; break;
								case 2: ((ChromInt)this.chromosomes[i]).genes[iGene] = getRandom(200)+1; break;
						  }
					 }
                else
                    ((ChromInt)this.chromosomes[i]).genes[iGene] = -getRandom(1000) / (1 + getRandom(1000));
            }
            this.chromosomes[i].fitness = getFitness(i);
        }
    }


    /**
     * <pre>
     * Genetically recombine the given chromosomes using a one point crossover technique.
     * For example, if we have:
     *   chromosome A = 1.0, 2.0, 3.0, 4.0
     *   chromosome B = 5.0, 6.0, 7.0, 8.0
     * and we randomly choose the crossover point of 1, the new genes will be:
     *   new chromosome A = 1.0, 6.0, 3.0, 4.0
     *   new chromosome B = 5.0, 2.0, 7.0, 8.0
     * </pre>
     * @param Chrom1
     * @param Chrom2
     */
    protected void doOnePtCrossover(Chromosome Chrom1, Chromosome Chrom2)
    {
        int crossoverPt = getRandom(chromosomeDim);
        swapGene(crossoverPt, (ChromInt)Chrom1, (ChromInt)Chrom2);
    }

    /**
     * swaps the gene values in chromosome objects at given geneIndex
     * @param geneIndex
     * @param chrom1
     * @param chrom2
     */
    protected void swapGene(int geneIndex, ChromInt chrom1, ChromInt chrom2)
    {
        int temp = chrom1.genes[geneIndex];
        chrom1.genes[geneIndex] = chrom2.genes[geneIndex];
        chrom2.genes[geneIndex] = temp;
    }

    /**
     * Genetically recombine the given chromosomes using a two point crossover technique which
     * combines two chromosomes at two random genes (loci), creating two new chromosomes.
     * <pre>
     * For example (crossover pts, 1 and 4):
     *  1 234 567890 --> 1 999 567890
     *  8 999 888888 --> 8 234 888888
     *
     *  For example (crossover pts, 9 and 10):
     *  123456789 0 --> 123456789 0
     *  888888888 9 --> 888888888 9
     * </pre>
     * @param Chrom1
     * @param Chrom2
     */
    protected void doTwoPtCrossover(Chromosome Chrom1, Chromosome Chrom2)
    {
        int crossPt1 = getRandom(chromosomeDim);
        int crossPt2 = getRandom(chromosomeDim);
        
        if (crossPt1 != crossPt2)
        {
            for (int geneIndex=crossPt1; geneIndex < crossPt2; geneIndex++)
                swapGene(geneIndex, (ChromInt)Chrom1, (ChromInt)Chrom2);
        }
    }

    /**
     * Genetically recombine the given chromosomes using a uniform crossover technique. This
     * technique randomly swaps genes from one chromosome to another.
     * <pre>
     * For example, if we have:
     *   chromosome A = 1.0, 2.0, 3.0, 4.0
     *   chromosome B = 5.0, 6.0, 7.0, 8.0
     * our uniform (random) crossover might result in something like:
     *   new gene A = 5.0, 2.0, 3.0, 4.0
     *   new gene B = 1.0, 6.0, 7.0, 8.0
     * if only the first gene in the chromosome was swapped.
     * </pre>
     * @param Chrom1
     * @param Chrom2
     */
    protected void doUniformCrossover(Chromosome Chrom1, Chromosome Chrom2)
    {
        int rGene1, rGene2;
        String sGene1, sGene2;
        String sNewChrom1 = "", sNewChrom2 = "";

        for (int geneIndex = 0; geneIndex < chromosomeDim; geneIndex++)
        {
            if (getRandom(100) > 50)
                swapGene(geneIndex, (ChromInt)Chrom1, (ChromInt)Chrom2);
        }
    }

    /**
     * Initializes the GAInt chromosome
     * @param chromosomeDim
     * @param populationDim
     * @param crossoverProb
     * @param randomSelectionChance
     * @param maxGenerations
     * @param numPrelimRuns
     * @param maxPrelimGenerations
     * @param mutationProb
     * @param crossoverType
     * @param decPtsPrecision
     * @param positiveNumOnly
     * @param computeStatistics
     * @throws GAException
     */
    public GAInt(int chromosomeDim,
                    int populationDim,
                    double crossoverProb,
                    int randomSelectionChance,
                    int maxGenerations,
                    int numPrelimRuns,
                    int maxPrelimGenerations,
                    double mutationProb,
                    int crossoverType,
                    boolean positiveNumOnly,
                    boolean computeStatistics) throws GAException
    {
        super(chromosomeDim,
              populationDim,
              crossoverProb,
              randomSelectionChance,
              maxGenerations,
              numPrelimRuns,
              maxPrelimGenerations,
              mutationProb,
              crossoverType,
              computeStatistics);
				  
        if (chromosomeDim < 1)
            throw new GAException("chromosomeDim must be greater than zero.");

        for (int i = 0; i < populationDim; i++)
        {
            this.chromosomes[i] = new ChromInt(chromosomeDim);
            this.chromNextGen[i] = new ChromInt(chromosomeDim);
            this.prelimChrom[i] = new ChromInt(chromosomeDim);
        }

        this.positiveNumOnly = positiveNumOnly;
        initPopulation();
    }
    
    public static void main(String[] args)
    {
    }
}