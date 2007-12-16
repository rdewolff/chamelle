package com.softtechdesign.ga.examples;

import com.softtechdesign.ga.*;

/*
    This unit contains GATrigFunc which subclasses my  GA (genetic algorithm)
  class, GAFloat. Given a set of two dimensional data points and the maximum dimension of a
  polynomial, this class finds the polynomial equation which best fits the data points.
*/

public class GACurveFit extends GAFloat
{
    int curveDim;
    double[] curveData;

    void setCurveData(double[] CurveData)
    {
        curveDim = CurveData.length;
        curveData = new double[curveDim];

        for (int i = 0; i < curveDim; i++)
            this.curveData[i] = CurveData[i];
    }

    public GACurveFit(double[] curveData) throws GAException
    {
        super(3, //chromosome dim (number of genes)
              100, //population of chromosomes
              0.7, //crossover probability
              6, //random selection chance % (regardless of fitness)
              3000, //stop after this many generations
              10, //num prelim runs (to build good breeding stock for final--full run)
              20, //max prelim generations
              0.1, //chromosome mutation prob.
              Crossover.ctTwoPoint, //crossover type
              2, //num decimal pts of precision
              //if chrom has 3 genes and 2 decimal place, numbers look like "0.12"
              false, //only consider positive float numbers?
              true); //compute statistics?

        setCurveData(curveData);
    }

    /*
      The polynomial being fitted will look something like this (assuming you are looking for 5th degree polynomial)
      0*(0)^5 + 0*(0)^4 + 0*(0)^3 + 0*(0)^2 + 1*(0)^1 + 2 = 2
      0*(1)^5 + 0*(1)^4 + 0*(1)^3 + 0*(1)^2 + 1*(1)^1 + 2 = 3
      0*(2)^5 + 0*(2)^4 + 0*(2)^3 + 0*(2)^2 + 1*(2)^1 + 2 = 4
      ...
      C1*x^5 + C2*x^4 + C3*x^3 + C4*x^2 + C5*x^1 + C6 = rValue
    */
    protected double getFitness(int iChromIndex)
    {
        double rError = 0;
        double rPower = 0, rValue = 0;

        for (int iCurvePt = 0; iCurvePt < curveDim; iCurvePt++)
        {
            rValue = 0;
            
            //calculate the value of the function plugging in the genes
            //in place of coefficients (C1..C6)
            for (int iGene = 0; iGene < chromosomeDim; iGene++)
            {
                rPower = Math.pow((double)iCurvePt, (double)chromosomeDim - 1 - iGene);
                
                rValue += this.getChromosome(iChromIndex).getGene(iGene) * rPower;
            }

            rError = rError + Math.abs(curveData[iCurvePt] - rValue);
        }

        if (Math.abs(rError) > 1e-12)
            return (1 / rError); //this minimizes error (find smallest error)
        else
            return (1 / 1e-12); //prevents divide by zero error
    }

    public static void main(String[] args)
    {
        /*these points are the curve data (which needs to be fitted by a polynomial equation)
          this sample data should fit an equation like y = X^2 -2X + 1.
           (x=0,y=1) (x=1,y=0) (x=2,y=1) (x=3,y=4) (x=4,y=9) (x=5,y=15) (x=6,y=25) (x=7,y=36)
          I have thrown a little noise into the data to make it harder for the GA to find the
          curve fitting equation
        */
        double[] curveData = { 1.01, 0, 1, 3.98, 9.01, 16.01, 25, 35.99 };

        System.out.println("GACurveFit GA...");
        try
        {
            GACurveFit curveFit = new GACurveFit(curveData);
            Thread threadCurveFit = new Thread(curveFit);
            threadCurveFit.start();
        }
        catch (GAException gae)
        {
            System.out.println(gae.getMessage());
        }
    }

}