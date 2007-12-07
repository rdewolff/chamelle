// Fichier: RMIConcurrentClient.java

import java.rmi.*;

public class RMIConcurrentClient
{
   public static void main(String argv[])
   {
      String siteServeur = "sunst51";
      if (argv.length != 1) {
         System.out.println("Usage 1 RMIConcurrentClient {1|2}");
         System.exit(1);
      }
      if (argv[0].charAt(0) != '1' && argv[0].charAt(0) != '2') {
         System.out.println("Usage RMIConcurrentClient {1|2}");
         System.exit(1);
      }
      System.setSecurityManager(new RMISecurityManager());
      String serveurNom = "rmi://" + siteServeur + "/RMIConcurrent";
      RMIConcurrent serveur = null;
      try {
         serveur = (RMIConcurrent)Naming.lookup(serveurNom);
      } catch (Exception e) {
         System.out.println("Erreur de connexion au serveur: " + e);
         System.exit(1);
      } 
      try {
         for (int i = 0; i < 10; i++) {
            if (argv[0].charAt(0) == '1')
               serveur.Acces1();
            else
               serveur.Acces2();
            for (int j = 0; j < 100000; j++);
         }
      } catch (Exception e) {
         System.out.println("Erreur de traitement: " + e);
      } 
   }
}
