//=============================================================================
// Name        : MOB.cpp
// Author      : Romain de Wolff & Simon Hintermann
// Version     : 0.1
// Description : Programme permettant de trouver un chemin pour un probleme de
// 				  voyageur de commerce a l'aide d'une methode constructive basee
//				     sur une colonie de fourmis.
//=============================================================================

#include <iostream>
#include <fstream>
#include <math.h>
#include <time.h>
#include <stdlib.h>

using namespace std;

// parametres
const int alpha = 1; // Puissance des traces
const int beta  = 4; // Puissance de la distance
const int max_iter = 500; // Maximum de vagues
const int q = 100; // Constante pour la mise a jour des traces locales
const float rho = 0.999;  // Constante d'évaporation, entre 0 et 1
const int nbFourmis = 100; // Nombre de fourmis
const float t0 = 0.1; // Constante de valeur par defaut des traces

// le fichier source
char* fichier_source;
// Fichier de statistiques
const char fichier_stats[] = "stats.out";

// Point d'entree du programme des fourmis
int main(int argc, char *argv[]) {

   // Detection d'un nom de fichier passe en ligne de commande
   if(argc == 2)
      fichier_source = argv[1];
   else {
      char file[] = "BERLIN52.TSP";
      fichier_source = file;
   }
   
   srand(time(NULL));
   cout << "Lancement de la simulation" << endl;
   cout << " 1 - lecture des données" << endl;
    
   // ouvre le fichier contenant les données
   ifstream data(fichier_source);
   ofstream stats(fichier_stats);
    
   // les deux tableaux contenant les valeurs
	float *coord_x;
	float *coord_y;
   float min_x = INT_MAX;
   float max_x = 0.0;
   float min_y = INT_MAX;
   float max_y = 0.0;

	// variable pour la lecture des données du fichier source
	string tmp = "";
	
	// Dimension de l'ensemble des points a traiter
	int dimension;
    
   // element temporaire pour l'insertion      
   float e;
    
   // si les données ont été correctement chargées
   if (data) {
      while(tmp.find("DIMENSION") || tmp.find("DIMENSION:")) {data >> tmp;}
      if(tmp == "DIMENSION") data >> tmp;
      data >> dimension;
      cout << "dimension: " << dimension << endl;
      while(tmp.find("NODE_COORD_SECTION")) {data >> tmp;}
      
      // Les tableaux des coordonnees
		coord_x = new float[dimension];
		coord_y = new float[dimension];
      
      // boucle et enregistre toute les valeurs dans des tableaux
      for (int i=0; i<dimension; i++) {
         data >> tmp; // ignorer la premiere valeur
         data >> e;
			coord_x[i] = e;
         if(e < min_x) min_x = e;
         if(e > max_x) max_x = e;
         data >> e;
			coord_y[i] = e;
         if(e < min_y) min_y = e;
         if(e > max_y) max_y = e;
      }

   } else {
		printf("Erreur de chargement des donnees");
		return 1;
	}
    
   // ------------------
   cout << " 2 - calcul des distances" << endl;

   // Matrices utilisees par les fourmis
   float distance[dimension][dimension];
   float r[dimension][dimension];
   float traces[dimension][dimension];

   // initialisation des matrices à 0
   for(int i = 0; i < dimension; i++) {
      for(int j = 0; j < dimension; j++) {
         distance[i][j] = 0;
         traces[i][j] = t0;
      }
   }
   
   // calcul des distances 
   for(int i = 0; i < dimension; i++) {
      for(int j = 0; j < dimension; j++) {
			if(i!=j) {
		      // calcul la distance en utilisant pythagore
		      distance[i][j] = sqrt(pow(coord_x[i] - coord_x[j], 2) + pow(coord_y[i] - coord_y[j], 2));
			} else { // Diagonale
				distance[i][j] = 0.0;
			}
      }
   }
    
	// Pour stocker les solutions
	int solutions[max_iter][dimension];
	float solutionsQuality[max_iter];
   // Le tableau de selection aleatoire du chemin en fonction de leur score
	float moveQuality[dimension-1][1];
   // Le chemin d'une fourmi
	int chemin[dimension];
	int temp;
   // Longueur d'un chemin
	float L = 0; 
    // La valeur aleatoire pour construire un chemin
	int randomNew;
    
   cout << " 3 - execution - iteration" << endl;
    
   // demarrage des iterations
   for(int n = 0; n < max_iter; n++) {
		
      // reset R a 0
      for (int i = 0; i < dimension; i++)
         for (int j = 0; j < dimension; j++)
            r[i][j] = 0.0;
    
      // pour chaque fourmi
      for (int k = 1; k <= nbFourmis; k++) {

	      // Reset des villes visitees
		   for ( int i = 0; i < dimension; i++)
		      chemin[i] = i;

         L = 0.0; 
               
	      // choix d'une ville au hasard (0..dimension)
         randomNew = (int)((double)rand()/((double)RAND_MAX+1)*dimension);
		   temp = chemin[randomNew];
		   chemin[randomNew] = chemin[0];
		   chemin[0] = temp;

		   // tant que toute les autres villes n'ont pas ete visitees
         for (int j=1; j<dimension; j++) {

		      // Calcul des 1/distanceij * traceij en tableau de roulette pour le random
		      for(int l=0; l<dimension-j; l++) {
			      if(l == 0) { // Premiere valeur
				      moveQuality[l][0] = pow(1/distance[chemin[j-1]][chemin[l+j]], beta) * 
									           pow(traces[chemin[j-1]][chemin[l+j]], alpha);
			      } else { 
				      moveQuality[l][0] = 
					      moveQuality[l-1][0] + 
					      (pow(1/distance[chemin[j-1]][chemin[l+j]], beta) * 
					      pow(traces[chemin[j-1]][chemin[l+j]], alpha));
			      }
		      }
                   
			   // Recherche de la prochaine position aleatoire
			   double random = ((double)rand()/double((double)RAND_MAX+1.0) * 
								   (moveQuality[dimension-j-1][0]));
			   for(int i=0; i<dimension-j; i++) {
				   if(random < moveQuality[i][0]) {
					   randomNew = i+j;
					   break;
				   }
			      randomNew = i+j;
			   }
	
			   // Calcul de la distance parcourue par le chemin construit
			   L += distance[chemin[j-1]][chemin[randomNew]];
			   // stockage de la ville retenue
			   temp = chemin[randomNew];
			   chemin[randomNew] = chemin[j];
			   chemin[j] = temp;

         }
         
         // Mise a jour de la longueur du chemin
	      L += distance[chemin[0]][chemin[dimension-1]];

         // Recherche locale 2-opt
         bool ok = false;
         while(!ok) { // Boucle des itérations de la méthode d'optimisation
            ok = true;
			   for(int a=0; a<dimension-3; a++) {
				   for(int b=a+2; b<dimension-1; b++) {
                  float L2 = L - distance[chemin[a]][chemin[a+1]] - distance[chemin[b]][chemin[b+1]];
                  L2 += distance[chemin[a]][chemin[b]] + distance[chemin[a+1]][chemin[b+1]];
					   
					   // Si la nouvelle distance est plus courte, il va falloir inverser de sens le chemin entre b et c
					   if(L2 < L) {
						   L = L2;
                     // On va choisir de changer de sens le chemin le plus court
                     if(b-a < dimension / 2) {
						      for(int e=0; e<abs(a-b)/2; e++) { // Boucle d'echange des villes d'une demi-boucle (changement de sens)
							      int temp = chemin[b-e];
							      chemin[b-e] = chemin[a+1+e];
							      chemin[a+1+e] = temp;
						      }
                     } else { 
                        for(int e=0; e<(dimension-b+a)/2; e++) { // Boucle d'echange des villes d'une demi-boucle (changement de sens)
							      int temp = chemin[a-e<0?dimension+(a-e):a-e];
							      chemin[a-e<0?dimension+(a-e):a-e] = chemin[(b+1+e)%dimension];
							      chemin[(b+1+e)%dimension] = temp;
						      }
                     }
                     ok = false; // On pourra refaire la boucle pour etre sur qu'aucune autre amelioration n'est possible
					   }
				   }
			   }
         }

			// Mise a jour des traces du chemin
			for(int i=0; i<dimension; i++) {
				r[chemin[i]][chemin[(i+1)==dimension?0:i+1]] += q/L;
			}
			
			// Stockage de la longueur du chemin de la solution
			solutionsQuality[n] = L;
      }
      // Pour Calc (graphiques)
      stats << L << endl;
		
		// Adaptation de la matrice des traces en prenant en compte l'evaporation
      for(int i=0; i<dimension; i++) {
		   for(int j=0; j<dimension; j++) {
			   traces[i][j] *= rho;
		   }
	   }
      // Addition des deux matrices
	   for(int i=0; i<dimension; i++) {
		   for(int j=0; j<dimension; j++) {
			   traces[i][j] += r[i][j];
		   }
	   }
		for(int i=0; i<dimension; i++) {
			solutions[n][i] = chemin[i];
		}
   }
    
   // affiche les solutions trouvées, sous forme de liste
   cout << "Nombre de solutions trouvées : " << max_iter << endl;
   for ( int i = 0; i<max_iter; i++ ) {
		cout << i << ":";
		for ( int j = 0; j < dimension; j++ ) {
         cout << solutions[i][j] << "-";
      }
      cout << endl;
		cout << solutionsQuality[i] << endl;
   }

   // Recherche de la meilleure solution
   float min = INT_MAX;
   int indexOfBest = 0;
   for(int i=0; i<max_iter; i++){
      if(solutionsQuality[i] < min) {
         min = solutionsQuality[i];
         indexOfBest = i;
      }
   }

   cout << endl << "Meilleure solution: " << solutionsQuality[indexOfBest] << endl;

   cout << "Creation d'un fichier postscript" << endl;
    
   // creation d'un fichier PostScript pour visualiser les solutions
   ofstream ps("visual.ps");
   float zoom_x = 100;
   float zoom_y = 60;
    
   ps << "%!PS-Adobe-2.0 EPSF-2.0;" << endl;
   ps << "1 setlinewidth" << endl;
   ps << "0 0 0 setrgbcolor" << endl; // couleur
   ps << "gsave" << endl;
   
   // Dessin de la meilleure tournee
   ps << coord_x[solutions[indexOfBest][0]]/max_x*333 << " " << coord_y[solutions[indexOfBest][0]]/max_y*535 << " moveto" << endl;
   for ( int j = 1; j < dimension; j++ ) {
      ps << coord_x[solutions[indexOfBest][j]]/max_x*333 << " " << coord_y[solutions[indexOfBest][j]]/max_y*535 << " lineto" << endl;
   }
   ps << coord_x[solutions[indexOfBest][0]]/max_x*333 << " " << coord_y[solutions[indexOfBest][0]]/max_y*535 << " lineto" << endl;
   ps << "stroke" << endl;
   ps << "0.0 0.0 0.0 setrgbcolor" << endl; // TODO COLORS
   ps << "0 0 moveto" << endl;
   ps << "grestore" << endl;
   
   ps << "%%EOF" << endl;
    
   cout << "Fin du programme" << endl;
	
	// Fermeture des fichiers et liberation de la memoire
   ps.close();
   data.close();
   stats.close();
	delete[] coord_x;
	delete[] coord_y;
    
	return 0;
}
