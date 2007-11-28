/**
 * Configuration des clients-serveurs calculant des matrices
 * 
 * @author Romain de Wolff
 * @author Simon Hintermann
 *
 */
public abstract class Config {

	static final int 	N 				= 3;
	static final String HOST 			= "localhost";
	static final int 	PORT 			= 6000;
	static final int 	PORT_MULTICAST 	= 6001;
	static final int 	PORT_UDP 		= 6002;
	static final String GROUPE 			= "228.5.6.7";
	static final int 	TAILLE_INT 		= 4;  // taille d'un entier en byte
	static final int 	TAILLE_CHAR	    = 2;

}
