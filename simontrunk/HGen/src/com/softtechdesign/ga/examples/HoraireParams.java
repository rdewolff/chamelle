package com.softtechdesign.ga.examples;

public class HoraireParams
{
	static int nbIndividus = 6;
	static int nbParSoir   = 2;
	static int nbJours = 14;
	static int[][] placesND;
	static int[][] placesT;
	static String[] individus = {"Sim","Pam","Rom","Yan","Gab","Pio"};
	static String[] possibleGenes;
	static double[] scoreJours = {1.0,1.0,1.0,0.5,0.75,0.0,1.0};
	static int[] nbSoirs = new int[nbIndividus];
	protected void setNbIndiv(int nb)
	{
		this.nbIndividus = nb;
	}
}