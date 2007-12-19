
import java.io.*;
import java.net.*;

/**
 * 
 * Auteurs: Simon Hintermann & Romain de Wolff
 * 
 * But: Cette classe permet de gerer une exclusion mutuelle d'une variable
 * globale accessible dans un contexte reparti avec l'algorithme de Lamport.
 * Le nombre d'entites pouvant modifier la variable ou la lire est fixe une
 * fois pour toutes (au lancement de cette classe, en parametre).
 *
 */
public class VarManager {

    static UserListener u = null;
    static int nbUsers = 0;

    /**
     * 
     * Classe runnable permettant de faire l'interface entre la tache utilisateur
     * et le gestionnaire de la variable.
     * 
     * Cette tache va attendre des demandes sur une socket locale de communication
     * entre UserTask et VarManager. Cette socket permet de recevoir les demandes
     * de consultation et de modification. Une fois la tache traitée, la reponse est
     * renvoyee à la tache utilisateur sur la meme socket. Dans les deux cas, on
     * renverra la valeur de la variable globale.
     * 
     */
    static class UserListener implements Runnable {

        // Valeur du port a utiliser pour la communication locale
        private int port;
        // La variable distribuee entre les sites (locale)
        private int variableLocale;

        UserListener(int port) {
            this.port = port;
        }

        public void run() {

            ServerSocket welcomeSocket = null;
            Socket connectionSocket = null;

            while(true) {
                // Communication avec le(s) gestionnaire(s) de variable(s)
                try {
                    welcomeSocket = new ServerSocket(port);
                    connectionSocket = welcomeSocket.accept();
                    System.out.println("Client " + 1 + " connecte.");

                    System.out.println("Debut de la transmission...");
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

                    System.out.println(inFromClient.readLine());
                } catch (Exception e) {
                    System.out.println("Erreur lors de la connexion au gestionnaire...");
                }
            }
        }
    }

    static class LamportRequest implements Runnable {

        private int responsePort;

        LamportRequest(int port) {
            responsePort = port;
        }

        public void run() {
            ;
        }
    }
    
    /**
     * Point d'entree de la classe VarManager.
     * 
     * C'est depuis ici que l'on va lancer la tache de communication avec la
     * tache utilisateur et celle chargee de gerer l'exclusion mutuelle
     */
    public static void main(String[] args) {
        
        try {
            InetAddress p2pGroup = InetAddress.getByName("228.11.12.13");
            MulticastSocket socket = new MulticastSocket(10555);
        }
        catch(UnknownHostException e) {
            System.out.println("Erreur lors de la definition de l'adresse de groupe p2p");
        }
        catch(IOException e2) {
            System.out.println("Erreur lors de l'initiation de la connexion au reseau");
        }
        

        // Si l'application n'a pas ete lancee avec deux parametres (int), elle 
        // quitte
        try {
            // Creation de la tache de communication locale, pour les commandes
            // envoyees par UserTask
            u = new UserListener((int) Integer.decode(args[0]));
        // Recuperation du nombre de sites partageant la variable
        //nbUsers = (int)Integer.decode(args[1]);
        } catch (Exception e) {
            System.out.println("Use: VarManager <port local>");
            System.exit(1);
        }

        // Lancement de la tache de communication locale
        Thread t = new Thread(u);
        t.start();
    }
}
