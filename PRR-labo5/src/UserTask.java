import java.io.*; 
import java.net.*; 

/**
 * Auteurs: Simon Hintermann & Romain de Wolff
 * 
 * But: Cette classe fournit une interface avec une variable globale partagee 
 * entre n utilisateurs. Cette interface permet de modifier ou de visualiser 
 * la variable globale
 *
 */
public class UserTask {

    /**
     * 
     * Point d'entree de la classe UserTask
     * 
     * C'est ici que l'utilisateur peut demander a voir la variable, ou a la
     * modifier. Cette classe va communiquer avec la classe VarManager, qui
     * va gerer l'exclusion entre les sites de la variable.
     * 
     */
    public static void main(String[] args) {

        // Le port utilise pour la communication locale
        int port = 0;

        // Si l'application n'a pas été lancée avec un paramètre (int), elle quitte
        try {
            // Recuperation de la valeur du port de communication locale
            port = (int)Integer.decode( args[0] );
        }
        catch(Exception e) {
            System.out.println("Use: UserTask <port local>");
            System.exit(1);
        }

        // Communication avec le gestionnaire de la variable
        try {
            Socket userSocket  = new Socket("localhost", port);
            DataOutputStream outToServer = new DataOutputStream(userSocket.getOutputStream()); 
            BufferedReader inFromServer = 
                    new BufferedReader(new InputStreamReader(userSocket.getInputStream())); 
            String phrase = "GOOO";
            outToServer.writeBytes(phrase + '\n'); 
        }
        catch(Exception e) {
            System.out.println("Erreur lors de la connexion au gestionnaire..." + e);
        }
    }
}
