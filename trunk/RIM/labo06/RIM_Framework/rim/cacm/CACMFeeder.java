package rim.cacm;

import static rim.util.Constants.DEFAULT_MASK;
import static rim.util.Constants.HASH_ALGO;
import static rim.util.URIPreparer.urlToPrepare;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import rim.Feeder;
import rim.Indexer;
import rim.util.Constants;
import rim.util.Hash;
import rim.util.WebParser;
import rim.util.WebParser.ParsedData;

/**
 * A feeder for the CACM collection.
 * @author Florian Poulin <i>(florian.poulin at heig-vd.ch)</i>
 */
public class CACMFeeder implements Feeder {
	// Sous domaines
	HashMap<String, Integer> subDomains;
	
	// Page de base
	LeafPage root;
	
	/**
	 * Constructeur
	 */
	public CACMFeeder() {
		subDomains = new HashMap<String, Integer>();
	}
	
	/* (non-Javadoc)
	 * @see rim.Feeder#parseCollection(java.net.URI, rim.Indexer)
	 */
	public int parseCollection(String uri, Indexer indexer) {
		// Queue des URI en cours de parcours
		Queue<LeafPage> uriCollection = new LinkedList<LeafPage>();
		
		// Pages visitées (hash des pages)
		TreeSet<String> pagesVisited = new TreeSet<String>();
		
		// Liste des URI visitées (hash des URI)
		TreeSet<String> urisVerified = new TreeSet<String>();
		
		// Masque pour éviter de parcourir tout le web
		Matcher uriMatcher = DEFAULT_MASK.matcher(uri);
		
		// Comptage des URI indexées
		int count = 0;
		
		// L'URI initiale ne respecte pas le masque
		if (!uriMatcher.find())
			return 0;
		
		root = new LeafPage(uri);
		
		// Initialisation du spider
		uriCollection.add(root);
		
		// Parcours du web
		while (!uriCollection.isEmpty()) {
			// Récupération d'une URI
			LeafPage currentURI = uriCollection.poll();
			
			System.out.println("Current : " + currentURI.uri() + " / Nb rest : " 
				+ uriCollection.size() + " / Nb. Scanned : " + count);
			
			// Page parcourue
			ParsedData page;
			
			try {
				// Récupération de la page
				page = WebParser.parseURL(new URL(currentURI.uri()));
				
				// Comptabilise au besoin la page pour le sous domaine
				subDomain(currentURI);
				
				// Mémorise le statut
				currentURI.status(page.getStatusCode());

				// Vérification statut et contenu
				if (page.getStatusCode() == 200 &&
					page.getPageContent() != null) {
					
					// Hachage du contenu de la page
					String hash = hashContent(page.getPageContent()); 
					
					// Vérifie si la page à deja été visitée
					if (!pagesVisited.contains(hash)) {
						// Indexation de la page
						indexer.index(currentURI.uri(), page.getPageContent());
					
						// Un fichier de plus d'indexé
						count++;
						
						// Ajout du hash à la liste des visités
						pagesVisited.add(hash);
						
						// Récupération des URIs de la page courante
						Set<String> uris = page.getPageHrefs();
						
						// Parcours des URIs
						for (String nextURI : uris) {
							// URI invalide ou à écarter
							if (nextURI == null || 
								Constants.STOP_URIS.matcher(nextURI).find())
								continue;

							// Préparation de l'URI à visiter
							nextURI = urlToPrepare(currentURI.uri(), nextURI);
							
							if (nextURI == null)
								continue;
							
							// Vérification de la validité de l'URI par
							// rapport au masque de contrainte
							uriMatcher = DEFAULT_MASK.matcher(nextURI);
							if (!uriMatcher.find())
								continue;
							
							// Hachage de l'URI courante du doc courant
							String uriHash = hashContent(nextURI); 
							
							// Evite de reparcourir une même URI plusieurs fois
							if(!urisVerified.contains(uriHash)) {
								urisVerified.add(uriHash);
								uriCollection.add(
									new LeafPage(nextURI, currentURI));
							}
						}
					}
				}
			}
			
			// Impossible d'accéder à la page
			catch (IOException ioe) {
				page = null;
			}
			
			// Page innaccessible
			catch (NullPointerException npe) {
				page = null;
			}
		}

		// Finalisation de l'indexation
		indexer.finalizeIndexation();
		
		return count;
	}
	
	/**
	 * Gestion des statistiques des sous
	 * domaines et du nombre de documents
	 * par sous domaines
	 * @param lp URI à analyser
	 */
	private void subDomain(LeafPage lp) {
		String sub = lp.uri().replace("http://", "");
		
		if (sub.contains("/"))
			sub = sub.substring(0, sub.indexOf("/"));
		
		if (subDomains.containsKey(sub))
			subDomains.put(sub, subDomains.get(sub) + 1);
		else 
			subDomains.put(sub, 1);
	}
	
	/**
	 * Récupère les sous-domaines visités
	 * @return Sous domaines visités
	 */
	public HashMap<String, Integer> subDomains() {
		return subDomains;
	}
	
	/**
	 * Récupère la racine de l'arbre de navigation
	 * qui permet de connaitre les pages inatteignables
	 * @return Racine de l'arbre de navigation
	 */
	public LeafPage root() {
		return root;
	}
	
	/**
	 * Effectue le hachage d'un contenu
	 * @param content Contenu à hacher
	 * @return Hash obtenu
	 */
	private String hashContent(String content) {
		try {
			Hash temp = new Hash(HASH_ALGO);
		
			temp.update(content);
			temp.calculate();
			return temp.toString();
		}
		catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
	/**
	 * Gestion d'un arbre de navigation
	 * @author J. Schmid & L. Prévost
	 */
	public class LeafPage implements Comparable<LeafPage> {
		// Uri du noeud
		private String uri;
		
		// Statut
		private int status;
		
		// Enfants du noeud
		private LinkedList<LeafPage> childs;
		
		/**
		 * Constructeur
		 * @param uri URI du noeud courant
		 */
		public LeafPage(String uri) {
			this.uri = uri;
			childs = new LinkedList<LeafPage>();
		}

		/**
		 * Constructeur
		 * @param uri URI du noeud courant
		 * @param parent Parent du noeud courant
		 */
		public LeafPage(String uri, LeafPage parent) {
			this(uri);
			
			if (parent != null)
				parent.addChild(this);
		}
		
		/**
		 * Ajoute un enfant au noeud
		 * courant
		 * @param lp Enfant du noeud courant
		 */
		public void addChild(LeafPage lp) {
			childs.add(lp);
		}
		
		/**
		 * Détermine le statut du noeud courant
		 * @param status Statut
		 */
		public void status(int status) {
			this.status = status;
		}
		
		/**
		 * URI courante
		 * @return URI
		 */
		public String uri() {
			return uri;
		}
		
		/**
		 * Affiche le noeud parent et toutes ses pages
		 * enfants qui correspondent au code voulu
		 * d'une racine précise et toute sa sous arborescence
		 * @return Nombre de liens morts
		 */
		public int showInvalids(int status) {
			int rValue = 0;
			
			StringBuffer toShow = new StringBuffer();
			
			// Parcours des pages enfants
			for (LeafPage lp : childs) {
				if (lp.status == status) {
					toShow.append("|-" + lp.uri + "\n");
					rValue++;
				}

				// Récursions, stop sur liste vide
				rValue += lp.showInvalids(status); 
			}
			
			// Ajout de la page parent si au 
			// moins un enfant est inatteignable
			if (toShow.length() > 0)
				System.out.println(uri + "\n" + toShow);
			
			return rValue;
		}
		
		@Override
		public int compareTo(LeafPage arg0) {
			return uri.compareTo(arg0.uri);
		}
	}
}
