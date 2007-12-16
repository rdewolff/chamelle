package com.softtechdesign.ga.examples;

import com.softtechdesign.ga.*;
import java.io.* ;
import java.util.*;
import java.io.File;
import jxl.*;
import java.util.ArrayList;

/*
 * Classe permettant de lire une feuille Excel formattee cotentant les contraintes
 * pour générer un horaire avec un algorithme génétique
 */
public class GAHoraire extends GAStringsSeq
{
	static int nbIndividus;   	// Les employes concernes par l'horaire
	static int nbParSoir; 	// Nombre d'employes par soir
	static int nbJours = 14;  	// Nombre de jours au total
	static int[][] placesND;  	// Le tableau des jours non disponibles par employe
	static int[][] placesT;	  	// Le tableau des jours voulus par employe
	static ArrayList<String>individus;	// Les noms des employes
	static ArrayList<String> possibleGenes = new ArrayList<String>(); // Tableau des combinaisons possibles d'employes par soir
	static String[] possibleGenes2; // Tableau des combinaisons possibles d'employes par soir
	static double[] scoreJours = {1.0,1.0,1.0,0.5,0.75,0.0,1.0}; // Tableau des scores des jours
	static double[][] refSoirs;	// Tableau du nombre de jours voulus par employe
	static int numero = 0;
	int numID;
	
	/**
	 * Fonction recursive generant les combinaisons possibles d'employes d'apres:
	 * 
	 * nb: 		nombre d'employes par jour
	 * index: 	parametre interne, doit etre 0 au premier appel
	 * temp: 	parametre interne, doit etre un new ArrayList<String>() a l'appel
	 * liste: 	la liste des employes
	 * res: 	la variable qui recevra les possibilites generees
	 */
	static void liste(int nb, int index, ArrayList<String> temp, ArrayList<String> liste, ArrayList<String> res) {
		ArrayList<String> temp2;
		
		//On boucle sur les employes
		for(int i=index; i<liste.size();i++) {
			temp2 = (ArrayList<String>)temp.clone();
			temp2.add(liste.get(i));
			
			if (temp2.size() < nb) //Tant que l'on est pas au dernier niveau
				liste(nb, i+1,temp2, liste, res);
			else //Si l'on se trouve au dernier niveau de recursion
				res.add( temp2.clone().toString());
		}
	}

	static {
		int cpt = 0;
		// Recuperation du fichier
		File f = new File("input.xls");
                if(!f.exists())
                    System.out.println("cac");
		// S'il n'existe pas, on va demander les parametres a l'utilisateur
		if(f.exists()) { // S'il existe, on va lire la feuille excel pour recuperer les contraintes
			try {
				Workbook workbook = Workbook.getWorkbook(f);
				Sheet sheet = workbook.getSheet(0);
				Cell c;
				String s;

				cpt = 0;
				// Recuperation du nombre d'individus
				for(int i=0; i<10000; i++) {
					try {
						c = sheet.getCell(0,i+1);
						s = c.getContents();
					}
					catch(Exception e) {
						break;
					}
					if(s == "")
						break;
					cpt++;
				}
				nbIndividus = cpt;

				// Initialisation du tableau des noms des individus
				individus = new ArrayList<String>();

				// Lecture des noms des individus
				for(int i=0; i<nbIndividus; i++) {
					c = sheet.getCell(0,i+1);
					s = c.getContents();
					if(s == null)
						break;
					individus.add(s);
				}

				cpt = 0;
				// Lecture du nombre de jours enregistres
				for(int i=0; i<100; i++) {
					try {
						c = sheet.getCell(i+1,0);
						s = c.getContents();
					}
					catch(Exception e) {
						break;
					}
					if(s == "")
						break;
					cpt++;
				}
				nbJours = cpt;

				// Initialisation du tableau des jours non disponibles 
				placesND = new int[nbIndividus][];
				for(int i=0; i<nbIndividus; i++) {
					placesND[i] = new int[nbJours];
					for(int j=0; j<nbJours; j++)
						placesND[i][j] = 0;
				}

				// Initialisation du tableau des jours voulus
				placesT = new int[nbIndividus][];
				for(int i=0; i<nbIndividus; i++) {
					placesT[i] = new int[nbJours];
					for(int j=0; j<nbJours; j++)
						placesT[i][j] = 0;
				}

				// Lecture des jours non-disponibles et voulus
				for(int i=0; i<nbJours; i++)
					for(int j=0; j<nbIndividus; j++) {
						c = sheet.getCell(i+1,j+1);
						s = c.getContents();
						if(s.equals("nd")) {
							placesND[j][i] = 1;
						}
						if(s.equals("t"))
							placesT[j][i] = 1;
					}

				// Initialisation du tableau du nombre de jours voulus
				refSoirs = new double[nbIndividus] [];
				for(int i=0; i<nbIndividus; i++)
					refSoirs[i] = new double[2];

				// Lecture du nombre de soirs voulus par employe (facultatif)
				for(int i=0; i<nbIndividus; i++)
					for(int j=0; j<2; j++) {
						try {
							c = sheet.getCell(nbJours+1+j,i+1);
							s = c.getContents();
							refSoirs[i][j] = Integer.valueOf(s);
						}
						catch(Exception e) {
							if(j == 1)refSoirs[i][j] = refSoirs[i][j-1];
						}
					}

				//Recuperation du nombre d'employes par soir
				try {
					c = sheet.getCell(0,0);
					s = c.getContents();
					nbParSoir = Integer.valueOf(s);
				}
				catch(Exception e){

				}

				// Si les nombres de soirs voulus n'ont pas ete enregistres,
				// on repartit equitablement les jours a disposition
				//
				// Calcul des nombre de soirs voulus par rapport au total de
				// places a disposition et pour donner la meme fourchette a
				// tout le monde
				if(refSoirs[0][0] == 0)
					for(int i=0; i<nbIndividus; i++) {
						refSoirs[i][0] = Math.floor(nbParSoir*nbJours/nbIndividus);
						refSoirs[i][1] = Math.ceil((double)nbParSoir*(double)nbJours/(double)nbIndividus);
					}

				ArrayList<String> tmp = new ArrayList<String>();
				liste(nbParSoir, 0, tmp, individus, possibleGenes);
				System.out.println("Taille possGenes: "+possibleGenes.size());
				Object[] tab = possibleGenes.toArray();
				possibleGenes2 = new String[possibleGenes.size()];

				cpt = 0;
				for(Object o: tab) {
					possibleGenes2[cpt] = (String)o;
					cpt++;
				}
			}
			catch(IOException e) {
				System.out.println(e.getCause());
			} catch(jxl.JXLException b) {
				System.out.println("BIFF");
			}
		}
	}
	
	/**
	 * Méthode héritée de la classe GA, qui doit implémenter l'interface Observer
	 */
	public void update(Observable o, Object arg)
	{
		o.notify();
	}
	
	/**
	 * Methode permettant de recuperer le tableau des jours non disponibles
	 */
	public int[][] getPlacesND() {
		int[][] ret = new int[placesND.length][];
		for(int i=0; i<placesND.length; i++)
			ret[i] = new int[placesND[i].length];
		for(int i=0; i<placesND.length; i++)
			for(int j=0; j<placesND[i].length; j++)
				ret[i][j] = placesND[i][j];
		return ret;
	}

	/**
	 * Methode permettant de recuperer le tableau des jours voulus
	 */
	public int[][] getPlacesT() {
		int[][] ret = new int[placesT.length][];
		for(int i=0; i<placesT.length; i++)
			ret[i] = new int[placesT[i].length];
		for(int i=0; i<placesT.length; i++)
			for(int j=0; j<placesT[i].length; j++)
				ret[i][j] = placesT[i][j];
		return ret;
	}

	/**
	 * Methode permettant de recuperer les scores des jours de la semaine
	 */
	public double[] getScoreJours() {
		double[] ret = new double[scoreJours.length];
		for(int i=0; i<scoreJours.length; i++)
			ret[i] = scoreJours[i];
		return ret;
	}

	/**
	 * Methode permettant de recuperer le tableau des noms des employes
	 */
	public String[] getIndiv() {
		String[] ret = new String[nbIndividus];
		for(int i=0; i<nbIndividus; i++)
			ret[i] = new String(individus.get(i));
		return ret;
	}

	/**
	 * Methode permettant de recuperer le nombre d'employes
	 */
	public int getNbIndiv() {
		return nbIndividus;
	}

	/**
	 * Constructeur du generateur d'horaire avec les parametres lus
	 * @throws GAException
	 */
	public GAHoraire() throws GAException {
		super(  nbJours, //size of chromosome
				200, //population has N chromosomes
				0.75, //crossover probability
				10, //random selection chance % (regardless of fitness)
				1000, //max generations
				20, //num prelim runs (to build good breeding stock for final/full run)
				20, //max generations per prelim run
				0.1, //chromosome mutation prob.
				0, //number of decimal places in chrom
				possibleGenes2, //gene space (possible gene values)
				Crossover.ctTwoPoint, //crossover type
				true); //compute statisitics?
		numID = numero++;
	}

	/**
	 * Methode pour calculer le score d'un chromosome avec les diverses penalites
	 * possibles
	 */
	protected double getFitness(int iChromIndex)
	{
		// Le chromosome a tester
		ChromStrings chromosome = (ChromStrings)this.getChromosome(iChromIndex);

		// Fitness de depart de 1 (maximum a atteindre)
		double fitness = 1.0;
		// Le nombre de placages de chaque individu
		double nbOccurence[] = new double[nbIndividus];
		// Le score de chaque individu
		double scoreTotal[] = new double[nbIndividus];
		// Initialisation du tableau des placages
		int occ[][] = new int[nbIndividus][];

		for(int i=0; i<nbIndividus; i++) {
			occ[i] = new int[nbJours];
			for(int j=0; j<nbJours; j++)
				occ[i][j] = 0;
		}

		// Calcul du score et du nombre de jours places pour chaque employe
		for (int i = 0; i < chromosomeDim; i++) {
			for(int j=0; j<nbIndividus; j++) {
				if(chromosome.getGene(i).contains(individus.get(j)))
				{nbOccurence[j] += 1;scoreTotal[j] += scoreJours[i%7];
				occ[j][i] = 1;}
			}
		}

		double min = 1000.0;
		double max = 0.0;
		// Penalite calculee pour les scores des jours. Le min et le max des scores
		// sont calcules, et on penalise proportionnellement a l'ecart entre le
		// min et le max. Le but est d'obtenir un score moyen pour tous les employes
		for(int i=0; i<nbIndividus; i++) {
			if(nbOccurence[i]!=0&&scoreTotal[i]/(double)nbOccurence[i]<min)min=scoreTotal[i]/(double)nbOccurence[i];
			if(nbOccurence[i]!=0&&scoreTotal[i]/(double)nbOccurence[i]>max)max=scoreTotal[i]/(double)nbOccurence[i];
			if(nbOccurence[i]==0)min=0;
		}
		if(max-min != 0)
			fitness /= (1+(max-min)/20); // Application de la penalite

		// Penalites pour les jours places qui sont des jours non-disponibles
		for(int i=0; i<nbJours; i++)
			for(int j=0; j<nbIndividus; j++)
				if(occ[j][i]==1 && placesND[j][i]==1)
					fitness/=1.2;
		// Penalites pour les jours voulus qui ne sont pas places
		for(int i=0; i<nbJours; i++)
			for(int j=0; j<nbIndividus; j++)
				if(occ[j][i]==0 && placesT[j][i]==1)
					fitness/=1.2;

		// Penalites proportionnelles au nombre de jours d'ecart entre le total places
		// et le total voulu
		for(int i=0;i<nbIndividus;i++) {
			fitness /= nbOccurence[i]<refSoirs[i][0]?((refSoirs[i][0] - (double)nbOccurence[i])*0.2+1.0):1.0;
			fitness /= nbOccurence[i]>refSoirs[i][1]?(((double)nbOccurence[i] - refSoirs[i][1])*0.2+1.0):1.0;
		}

		// Retour du fitness mis a jour avec les penalites
		return (fitness);
	}
	
	/**
	 * Point d'entree de la generation d'horaire
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Horaire GA...");
		try {
			// Lancement de l'algorithme
			GAHoraire gaHoraire  = new GAHoraire();
			Thread threadHoraire = new Thread(gaHoraire);
			threadHoraire.start();
		}
		catch (Exception gae) {
			System.out.println(gae.getMessage());
		}
	}
}