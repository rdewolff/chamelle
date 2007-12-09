import java.rmi.RemoteException;

	

public class Test {

	String[] adrClients = null;
	
	public void inscription( int id, String adr) {
		String[] temp = new String[adrClients.length];
		temp = adrClients;
		adrClients = new String[adrClients.length + 1];
		adrClients = temp;
		adrClients[adrClients.length+1] = adr;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("TEST");
		
		inscription(1, "cham");
		
	}

}
