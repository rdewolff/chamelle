package rim;

import rim.cacm.CACMFeeder;
import rim.cacm.CACMIndexer;
import rim.cacm.CACMRetriever;
import java.util.*;
import java.io.*;

/**
 * Second laboratory for the RIM course (Multimedia Information Retrieval). This work
 * is proposed by the University of Applied Science of Western Switzerland (HEIG-VD).
 * <br/><br/>
 * The main method must perform the following tasks :
 * <br/><ul>
 * <li>Build an URI pointing to the CACM collection ('cacm.all');</li>
 * <li>Parse and index the CACM collection using a {@link CACMFeeder} instance
 * and a {@link CACMIndexer} instance;</li>
 * <li>Provide a basic search utility using a {@link CACMRetriever} instance.</li>
 * </ul>
 * If you have any question, please ask the assistant or the professor.<br/>
 * Enjoy !
 */
public class Labo2 {
   	
	/**
	 * Main entry point.
	 * @param args console arguments.
	 */
	public static void main (String[] args) {
	
		CACMRetriever r = new CACMRetriever();
		//Tampon de lecture du clavier
		BufferedReader inFromUser = 
          		new BufferedReader(new InputStreamReader(System.in));
		int requete = 0;
		int document = 0;
		String terme = null;
		
		//En-tete du programme
		System.out.println("\n-- Moteur d'indexation, RIM --\n");
		System.out.println("Auteurs: Simon Hintermann et Romain de Wolff\n\n");
		
		try
		{
			while(requete != 4)
			{
				//Choix de l'utilisateur
				System.out.println("Commandes disponibles:");
				System.out.println("1: Rechercher un document");
				System.out.println("2: Rechercher un terme");
				System.out.println("3: Indexer la collection");
				System.out.println("4: Quitter");
				
				//Boucle de saisie
				while(true)
				{
					try
					{
						requete = Integer.valueOf(inFromUser.readLine());
						//Si l'entree au clavier est erronnee, on envoie une erreur
						if(requete != 1 && requete != 2 && requete != 3 && requete != 4)
							throw new NumberFormatException();
						break;
					}
					//En cas d'erreur de format ou de bornes, on signale l'erreur
					catch(NumberFormatException e)
					{System.out.println("Veuillez entrer 1,2,3,ou 4");}
				}
				
				//Si l'utilisateur veut quitter
				if(requete == 4)
				{
					System.out.println("Fin du programme...");
					break;
				}
				//Selection de la requete
				try
				{
					switch(requete)
					{
						//Cherche un document
						case 1: 	System.out.println("Entrez le document recherche:");
									document = Integer.valueOf(inFromUser.readLine());
									System.out.println("Termes trouves dans le document avec leur frequence:");
									Map<String, Integer> t = r.searchDocument(document);
									Set keys = t.keySet();
									for(Object s: keys)
										System.out.println(s + ", " + t.get(s));
									break;
						//Cherche un terme
						case 2: 	System.out.println("Entrez le terme recherche:");
									terme = inFromUser.readLine();
									System.out.println("Documents contenant ce terme avec leur frequence:");
									terme = terme.trim();
									Map<Integer, Integer> t2 = r.searchTerm(terme);
									keys = t2.keySet();
									for(Object s: keys)
										System.out.println(s + ", " + t2.get(s));
									break;
						//Reindexage
						case 3: 	System.out.println("Creation des indexs...");
									CACMIndexer i = new CACMIndexer();
									CACMFeeder c = new CACMFeeder();
									c.parseCollection(java.net.URI.create("rim/ressources/cacm.all"), i);
									i.save();
									break;
					}
				}
				catch (Exception e)
				{System.out.println("Aucun resultat...");}
				
				System.out.println("\n");
			}
		}
		catch(Exception e)
		{System.out.println(e.getMessage());}
	}
}
