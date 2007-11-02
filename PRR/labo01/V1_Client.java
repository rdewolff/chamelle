

import java.io.*; 
import java.net.*; 

// essai modif Eclipse - RDW - Pologne

class V1_Client { 

	public static void main(String argv[]) throws Exception { 

		while(true){
			try {
				String host = argv[0];
				int port = (int)Integer.decode( argv[1] ); 
				String phrase; 
				String phraseModifiee;
				BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
				Socket clientSocket = new Socket(host, port); // connection en utilisant les parametre passe au lancement du client
				DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); 
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
				phrase = inFromUser.readLine(); 
				outToServer.writeBytes(phrase + '\n'); 
				phraseModifiee = inFromServer.readLine(); 
				System.out.println("DU SERVEUR: " + phraseModifiee);
				clientSocket.close();
				break;
			} catch (IOException e) {
				System.out.println(e); 
			}
		} 
	}
}
