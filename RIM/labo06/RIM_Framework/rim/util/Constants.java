package rim.util;

import java.util.regex.Pattern;

/**
 * Definit les constantes utilisees dans l'application
 * @author J. Schmid & L. Prevost
 */
public class Constants {
	// Algorithme de hachage pour comparaison des contenus
	public static final String HASH_ALGO = "SHA1";

	// Dictionnaire des mots communs
	public static final String COMMON_WORDS_FILE = 
		"/Users/rdewolff/Documents/HEIG-VD/eclipse/svnChamelle/" +
		"RIM/labo06/RIM_Framework/rim/ressources/common_words-fr";
	
	// URI de depart pour le spider
	public static final String INITIAL_URI = "http://www.proyectolatino.ch";
	
	// Masque a forcer pour ne pas telecharger le web
	public static final Pattern DEFAULT_MASK = 
		Pattern.compile("^http://([a-zA-Z0-9_-]{1,}\\.){1,}proyectolatino\\.ch.*");
	
	// Debut d'URI interidite
	public static final Pattern STOP_URIS = 
		Pattern.compile(".*(mailto|javascript|file):.*", Pattern.CASE_INSENSITIVE);
	
	// Temps d'attente pour les connexions URL
	public static final int TIMEOUT = 6000;
}
