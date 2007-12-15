package com.softtechdesign.ga.examples;

import com.softtechdesign.ga.*;

/*
*/

public class GAMaze extends GAStringsSeq
{
    static String[] possibleGenes = { "right", "forward", "left" };
    static String[] solution =
        { "forward", "right", "forward", "forward", "forward", "left", "forward", "forward", "forward" };

    public GAMaze() throws GAException
    {
        super(  9, //size of chromosome
                200, //population has N chromosomes
                0.7, //crossover probability
                10, //random selection chance % (regardless of fitness)
                200, //max generations
                0, //num prelim runs (to build good breeding stock for final/full run)
                20, //max generations per prelim run
                0.06, //chromosome mutation prob.
                0, //number of decimal places in chrom
                possibleGenes, //gene space (possible gene values)
                Crossover.ctTwoPoint, //crossover type
                true); //compute statisitics?
    }

    protected double getFitness(int iChromIndex)
    {
        ChromStrings chromosome = (ChromStrings)this.getChromosome(iChromIndex);
        double fitness = 0;

        for (int i = 0; i < chromosomeDim; i++)
        {
            if (chromosome.getGene(i).equals(solution[i]))
                fitness += 1;
        }

        return (fitness);
    }

    public static void main(String[] args)
    {
        System.out.println("Maze GA...");
        try
        {
            GAMaze gaMaze = new GAMaze();
            Thread threadMaze = new Thread(gaMaze);
            threadMaze.start();
        }
        catch (GAException gae)
        {
            System.out.println(gae.getMessage());
        }
    }

}