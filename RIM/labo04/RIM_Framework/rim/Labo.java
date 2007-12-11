package rim;


import rim.cacm.CACMFeeder;
import rim.cacm.CACMIndexer;
import rim.cacm.CACMRetriever;
import java.util.*;
import java.io.*;

/**
 * Fourth laboratory for the RIM course (Multimedia Information Retrieval). This work
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
public class Labo {
   	
	/**
	 * Main entry point.
	 * @param args console arguments.
	 */
	public static void main (String[] args) {
	
		CACMRetriever r = new CACMRetriever();
		//Tampon de lecture du clavier
		BufferedReader inFromUser = 
          		new BufferedReader(new InputStreamReader(System.in));
		int requete = -1; // utilise pour stocker la reponse de l'utilisateur
		String terme = null;
		
		//En-tete du programme
		System.out.println("\n-- Moteur d'indexation, RIM --\n");
		System.out.println("Auteurs: Simon Hintermann et Romain de Wolff\n\n");
		try
		{
			while(requete != 0)
			{
				//Choix de l'utilisateur
				System.out.println("* Commandes disponibles: *");
				System.out.println("* 1: Indexer la collection");
				System.out.println("* 2: Entrer une requete");
				System.out.println("* 0: Quitter");
				
				//Boucle de saisie
				while(true)
				{
					try
					{
						requete = Integer.valueOf(inFromUser.readLine());
						//Si l'entree au clavier est erronnee, on envoie une erreur
						if(requete < 0 || requete > 2)
							throw new NumberFormatException();
						break;
					}
					//En cas d'erreur de format ou de bornes, on signale l'erreur
					catch(NumberFormatException e)
					{
						System.out.println("Veuillez entrer un numero de la liste");
					}
				}
				
				//Si l'utilisateur veut quitter
				if(requete == 0)
				{
					System.out.println("Fin du programme...");
					break;
				}
				//Selection de la requete
				try
				{
					Set<Double> keys;
					
					switch(requete)
					{
						//Reindexage
						case 1: 	System.out.println("Creation des indexs...");
									CACMIndexer i = new CACMIndexer();
									CACMFeeder c = new CACMFeeder();
									c.parseCollection(java.net.URI.create("rim/ressources/cacm.all"), i);
									i.finalizeIndexation();
									break;
									
						// Requete sur la collection indexee avec les frequences normalisees
						case 2: 	System.out.println("Entrer le(s) terme(s) recherche:");
									terme = inFromUser.readLine();
									System.out.println("Documents trouve ainsi " +
											"que similarite par cosinus:");
									// terme = terme.trim();
									Map<Double,Integer> t = r.executeQuery(terme, false);
									System.out.println("Nombre de resultat(s): "+t.size());
									keys = t.keySet();
									for(Object s: keys)
										// affichage des resultats 
										System.out.println("Document no : "+t.get(s)+
												" (cosinus = "+s+")"); 
									break;
					}
				}
				catch (Exception e)
				{e.printStackTrace();
					System.out.println("Aucun resultat...");}
				
				System.out.println("\n");
			}
		}
		catch(Exception e)
		{System.out.println(e.getMessage());}
	}
}
