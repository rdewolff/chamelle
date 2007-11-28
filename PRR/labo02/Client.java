import java.rmi.*;

public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String site = null;
		if (args.length == 1)
			site = args[0];
		else {
			System.out.println("Usage: ChaineMemoireClient site_serveur"); 
			System.exit(1);
		}
		System.setSecurityManager(new RMISecurityManager());
		MultMatrice serveur = null;
		try {
			serveur = (MultMatrice)Naming.lookup("rmi://" + site + "/MultMatrice");
		} catch(Exception e) {
			System.out.println("Erreur avec la reference du serveur " + e); 
			System.exit(1);
		}
		try {
			Message m = serveur.getParams();
		}catch(Exception e) {
			System.out.println("Erreur lors de la communication " + e);
		}
	}

}
