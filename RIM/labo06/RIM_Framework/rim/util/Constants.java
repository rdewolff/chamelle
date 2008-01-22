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
	public static final String COMMON_WORDS_FILE = "ressources/french_common_words";
	
	// URI de depart pour le spider
	public static final String INITIAL_URI = "http://www.heig-vd.ch";
	
	// Masque a forcer pour ne pas telecharger le web
	public static final Pattern DEFAULT_MASK = 
		Pattern.compile("^http://([a-zA-Z0-9_-]{1,}\\.){1,}heig-vd\\.ch.*");
	
	// Debut d'URI interidite
	public static final Pattern STOP_URIS = 
		Pattern.compile(".*(mailto|javascript|file):.*", Pattern.CASE_INSENSITIVE);
	
	// Temps d'attente pour les connexions URL
	public static final int TIMEOUT = 6000;
}
