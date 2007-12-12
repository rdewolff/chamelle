import java.io.Serializable;

public class Host implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String host; // adresse 
	private String nomAcces; // nom d'acces

	public Host(String host, String nom) {
		this.host = host;
		this.nomAcces = nom;
	}
	
	public String getHost() {
		return this.host;
	}
	
	public String getNomAcces() {
		return this.nomAcces;
	}
}
