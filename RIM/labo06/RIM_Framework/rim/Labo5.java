package rim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import rim.cacm.CACMFeeder;
import rim.cacm.CACMIndexer;
import rim.cacm.CACMRetriever;
import rim.index.Index;
import rim.index.Index.WeightingType;
import rim.util.Constants;

/**
 * Second laboratory for the RIM course 
 * (Multimedia Information Retrieval). This work
 * is proposed by the University of 
 * Applied Science of Western Switzerland (HEIG-VD).
 * <br/><br/>
 * The main method must perform the following tasks :
 * <br/><ul>
 * <li>Build an URI pointing to the CACM collection ('cacm.all');</li>
 * <li>Parse and index the CACM collection using a {@link CACMFeeder} instance
 * and a {@link CACMIndexer} instance;</li>
 * <li>Provide a basic search utility 
 * using a {@link CACMRetriever} instance.</li>
 * </ul>
 * If you have any question, please ask the assistant or the professor.<br/>
 * Enjoy !
 */
public class Labo5 {
	/**
	 * Affichage du r�sultat d'une requ�te
	 * en "langage naturel"
	 * @param result R�sultat obtenu
	 * @param query Requ�te demand�e
	 * @param max Nombre maximum de r�sultats � afficher, -1 = infini
	 */
	public static void showQueryResult(
		Map<Double, String> result, String query, int max) {
		
		int cpt = 0;
		
		System.out.println("---------------------------------------------");
		System.out.println("Resultat pour la requete : " + query);
		System.out.println("Nombre de documents trouves : " + result.size());
		System.out.println("---------------------------------------------");
		
		for (Entry<Double, String> entry : result.entrySet()) {
			
			if(cpt == max)
				break;
			
			System.out.println("URI : " + entry.getValue() +
				" (cosinus = " + entry.getKey() + ")");
			
			cpt++;
		}
	}
	
	
	/**
	 * Permet de lire le contenu �crit par l'utilisateur
	 * 
	 * @return chaine lue
	 */
	private static String readString(){
		String s = new String();
		
		// Essaie d'ouvrir la console et de lire une ligne
		try{
		    BufferedReader bfr = 
		    	new BufferedReader(new InputStreamReader(System.in));
		    s = bfr.readLine();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return s;
    }
	
	/**
	 * Lit un entier rentr� par l'utilisateur 
	 * Ressaie tant que l'entr�e n'est pas un entier valide.
	 * 
	 * @return entier lu
	 */
	private static int readInt(){
		String s = readString();
		int r;
		
		// Essaie de lire l'entr�e de l'utilisateur
		// Appell r�cursif en cas d'erreur
		try{
		    r = Integer.parseInt(s);
		} catch(NumberFormatException e){
		    System.out.println("Entier invalide, recommencez");
		    return readInt();
		}
		
		return r;
	}
	
	/**
	 * Invite l'utilisateur a entrer un mot et en affiche les
	 * r�sulats de la recherche
	 * 
	 * @param cr Retriever
	 */
	private static void seachTerm(CACMRetriever cr) {
		System.out.print("Entrez un mot a rechercher : ");
		System.out.println(cr.searchTerm(readString()));
	}
	
	/**
	 * Invite l'utilisateur a entrer une uri de document
	 * et affiche les mots-cl� correspondants
	 * @param cr Retriever
	 */
	private static void searchDoc(CACMRetriever cr) {
		System.out.print("Entrez l'uri du document : ");
		System.out.println(cr.searchDocument(readString()));
	}
	
	/**
	 * Invite l'utilisateur a entrer une requ�te et
	 * affiche les r�sultats obtenus
	 * @param cr Retriever
	 */
	private static void searchQuery(CACMRetriever cr) {
		System.out.print("Entrez votre requete (lang. nat.) : ");
		
		// Requ�te
		String query = readString();
		
		// R�sultat
		Map<Double, String> result = cr.executeQuery(query);
		
		// Affichage
		showQueryResult(result, query, -1);
	}
	
	/**
	 * Lecture du fichier des mots communs
	 * @param file Fichier � lire
	 * @return Collection des mots communs
	 */
	private static HashSet<String> readCommonWords(String file) {
		try {
			// Ouverture du fichier des mots communs
			BufferedReader br = 
				new BufferedReader(new FileReader(file));
			
			// Cr�ation de l'arbre contenant les mots � filtrer
			String readLine;
			
			// Cr�ation de la liste
			HashSet<String> cw = new HashSet<String>();
			
			// Construction de l'arbre
			while ((readLine = br.readLine()) != null)
				cw.add(readLine);
			
			return cw;
		}
		catch (FileNotFoundException fnfe) {
			System.out.println("Fichier des mots communs non trouv� !");
		}
		catch (IOException ioe) {
			System.out.println("Erreur lors de la lecture " +
				"du fichier des mots communs !");
		}

		return null;
	}

	/**
	 * Affichage du prompt
	 */
	private static void prompt(String phrase) {
		if (phrase == null)
			phrase = "";
		
		System.out.print("\nJACK> " + phrase);
	}
	
	/**
	 * Affichage du menu
	 * @param retrievers Retrievers utilis�s pour la recherche
	 * @param indexes Index utilis�s �galement pour la recherche
	 */
	private static void menu(CACMRetriever retriever) {
		
		Integer reponseMenu;
		
		// Reprise de la console tant qu'on ne demande pas de quitter
		while(true) {
			System.out.println(
				"====================================================");
			System.out.println();
			System.out.println("Menu : ");
			System.out.println(" 0. Quitter");
			System.out.println(" 1. Rechercher un mot");
			System.out.println(" 2. Rechercher un document");
			System.out.println(" 3. Rechercher documents similaires");
			prompt("");
				
			reponseMenu = readInt();
			
			switch(reponseMenu) {
			case 0:
				prompt("Bye...");
				return;
				
			case 1:
				seachTerm(retriever);
				break;
				
			case 2:
				searchDoc(retriever);
				break;
				
			case 3: 
				searchQuery(retriever);
				break;
				
			default:
				System.out.println("Erreur de saisie.");
			}
		}
	}
	
	/**
	 * Main entry point.
	 * @param args console arguments.
	 */
	public static void main (String[] args) {
		// Chemin du dossier vers les fichiers utilis�s
		String folder = null;
		
		// Variables de test
		String[] queries = new String[] {
			"Offres d'Emploi",
			"Inscriptions et ing�nierie",
			"Horaire TIC trimestre",
			"Travail de dipl�me � l'�tranger",
			"Raclette de no�l"
		};
		
		// V�rification que le dossier o� �crire les
		// r�sultats soit accessible en �criture
		try {
			folder = new File (".").getCanonicalPath() + "\\";
		} catch (IOException e) {
			System.out.println("Le repertoire courant n'est pas accessible " +
				"en ecriture, les fichiers ne pourront pas etre sauvegardes.");
		}
		
		System.out.println("Bienvenue dans ce labo 5 !");
		System.out.println("Le programme se d�roule en 3 parties");
		System.out.println();
		System.out.println("1. Construction de l'index (tf-idf)");
		System.out.println("2. Demonstration automatique");
		System.out.println("3. Essais manuels");
		System.out.println();
		System.out.println("Appuyez sur <enter> pour commencer " +
			"la creation de l'index (tf-idf)");
		readString();
		
		System.out.println("Lecture des mots communs...");

		HashSet<String> cw = readCommonWords(Constants.COMMON_WORDS_FILE);

		// Cr�ation de l'index
		Index index = new Index(WeightingType.tfidf);
		
		// Cr�ation de l'Indexer
		Indexer cacmIndex = new CACMIndexer(index, cw);
		
		// Cr�ation du Feeder
		CACMFeeder cacmFeeder = new CACMFeeder();
		
		// Affichage
		System.out.println("Creation de l'index [" + index.type() + "]...");
			
		// Cr�ation m�me de l'index
		cacmFeeder.parseCollection(Constants.INITIAL_URI, cacmIndex);
			
		// Sauvegarde l'index dans le r�pertoire courant
		System.out.println("Sauvegarde index [" + index.type() + "]...");
		index.save(folder);

		System.out.println("L'index est sauvegarde dans le dossier " + 
			folder);
		System.out.println();

		System.out.println("=======================================");
		System.out.println("= Affichage des statistiques =");
		System.out.println("=======================================");
		System.out.println("Pressez <enter> pour l'affichage\n");
		readString();
		
		System.out.println("Nombre de liens dans l'index : " + 
			index.idxSize());
		System.out.println("Nombre de termes dans l'index : " + 
				index.ridxSize());

		System.out.println("=======================================");
		System.out.println("= Affichage des sous domaines (pages) =");
		System.out.println("=======================================");
		System.out.println("Pressez <enter> pour l'affichage\n");
		readString();
		
		System.out.println("Nombre de sous-domaines : " + 
			cacmFeeder.subDomains().size());
		
		for (Entry<String, Integer> e : cacmFeeder.subDomains().entrySet())
			System.out.println("Sous domaine [" + e.getKey() + "] - " +
				"pages <" + e.getValue() + ">");
			
		System.out.println("=======================================");
		System.out.println("= Affichage des liens morts           =");
		System.out.println("=======================================");
		System.out.println("Pressez <enter> pour l'affichage\n");
		readString();
		
		int nbDeathLinks = cacmFeeder.root().showInvalids(404);
		
		System.out.println("Nb liens morts : " + nbDeathLinks);
			
		System.out.println();
		System.out.println("Appuyez sur <enter> pour " +
			"lancer les test automatiques");
		System.out.println();
		
		readString();
		
		// Recherche
		CACMRetriever retriever = new CACMRetriever(index, cw);

		// Recherches de tests
		for (String s : queries) {
			System.out.println("=========================================");
			System.out.println("10 premiers resultats pour la recherche : \""
				+ s + "\" : ");
			System.out.println("=========================================");

			showQueryResult(retriever.executeQuery(s), s, 10);
		
			System.out.println();
		}
		
		System.out.println();
		System.out.println("Fin des tests automatiques");
		System.out.println("Appuyez sur <enter> pour lancer le menu");
		System.out.println();
		
		readString();

		// D�but de la console permettant de faire d'autre recherches
		System.out.println();
		System.out.println("Ready my Lord !");
		System.out.println();
		
		menu(retriever);
	}
}
