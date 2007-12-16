package com.softtechdesign.ga.examples;

import com.softtechdesign.ga.*;

public class GATrigFunc extends GAString
{
    public GATrigFunc() throws GAException
    {
        super(  7, //number of genes in chromosome
                50, //population of N chromosomes
                0.7, //crossover probability
                10, //random selection chance % (regardless of fitness)
                150, //stop after N generations
                0, //num prelim runs
                20, //max prelim generations
                0.003, //chromosome mutation prob.
                6, //number of decimal places in chrom (0 means treat chrom as integer)
                //if chrom has 7 genes and 6 decimal place, numbers look like "0.123456"
                "0123456789", //gene space (possible gene values)
                Crossover.ctTwoPoint, //crossover type
                true); //compute statistics?
    }

    protected double getFitness(int iChromIndex)
    {
        double value = chromStrToFloat(this.getChromosome(iChromIndex).getGenesAsStr(), this.chromDecPts);
        value = Math.sin(value) + Math.cos(value);
        return (value);
    }

    public static void main(String[] args)
    {
        System.out.println("GATrigFunc GA...");
        try
        {
            GATrigFunc trigFunc = new GATrigFunc();
            Thread threadTrigFunc = new Thread(trigFunc);
            threadTrigFunc.start();
        }
        catch (GAException gae)
        {
            System.out.println(gae.getMessage());
        }
    }

}