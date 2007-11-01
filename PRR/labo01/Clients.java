import java.net.*;

public class Clients {

	static int nbClient = 0;
	
	private int id;
	private InetAddress adr;
	private int port;
	
	public Clients (int id, InetAddress adr, int port) {
		this.id = id;
		this.adr = adr;
		this.port = port;
	}
	
	public InetAddress getAddress() {
		return this.adr;
	}
	
	public int getPort() {
		return this.port;
	}
}
