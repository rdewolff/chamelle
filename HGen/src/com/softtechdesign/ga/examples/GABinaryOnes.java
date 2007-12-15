package com.softtechdesign.ga.examples;

import com.softtechdesign.ga.*;

/*
    GABinaryOnes extends GAString.
  This class solves the trivial problem of finding the bitstring with the largest
  value (namely all ones, e.g., 11111111).
  As a very simple GA, it is easy for beginners to follow.
*/

public class GABinaryOnes extends GAString
{

    /* given the chromosome identified by iChromIndex, calculate and return its fitness */
    protected double getFitness(int iChromIndex)
    {
        String s = this.getChromosome(iChromIndex).getGenesAsStr();
        return (getChromValAsDouble(s));
    }

    public GABinaryOnes() throws GAException
    {
        super(20, //chromosome has 20 chars
              50, //population of N chromosomes
              0.7, //crossover probability (0.7 = 70%)
              5, //random selection chance % (regardless of fitness)
              50, //stop after N generations
              0, //num prelim runs
              10, //max prelim generations
              0.01, //chromosome mutation prob.
              0, //number of decimal places in chrom (0 means treat chrom as integer)
              "01", //gene space (possible gene values '0' or '1')
              Crossover.ctTwoPoint, //crossover type
              true); //compute statistics?
    }

    public static void main(String[] args)
    {
        System.out.println("GABinary ones GA...");
        try
        {
            GABinaryOnes binaryOnes = new GABinaryOnes();
            Thread threadBinaryOnes = new Thread(binaryOnes);
            threadBinaryOnes.start();
        }
        catch (GAException gae)
        {
            System.out.println(gae.getMessage());
        }
    }

}