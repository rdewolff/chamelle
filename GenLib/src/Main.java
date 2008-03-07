/**
 * Point d'entree de la recherche de placement de centres medicaux sur une carte
 * 
 * @author simon
 */
public class Main {
    public static void main(String[] args) {
       CarteBin carte = new CarteBin(20, 0.02, 2, 600000, 2, 2);
       Thread t = new Thread(carte);
       t.start();
    }
}
