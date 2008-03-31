//============================================================================
// Name        : MOB.cpp
// Author      : Romain de Wolff & Simon Hintermann
// Version     : 0.1
// Description : 
//============================================================================

#include <iostream>
#include <fstream> // travail avec des fichiers
#include <math.h>
#include <stdlib.h> // random

using namespace std;

// parametres
const int alpha = 1;
const int beta  = 1;
unsigned int max_iter = 1;
const float rho = 0.5;  // constante d'évaporation, entre 0 et 1

// le fichier source
const char fichier_source[] = "BERLIN52.TSP";

// variable utiles pour la lecture des données du fichier source
string buffer[5][5];
string tmp;

int dimension;

float getLength(int* tab, float** dists) {
	float ret = 0.0;
	for(int i=0; i<dimension; i++) {
		ret += dists[tab[i]][tab[(i+1)%dimension]];
	}
	return ret;
}

// programme principal
int main() {
    
    cout << "Lancement de la simulation" << endl;
    
    cout << " 1 - lecture des données" << endl;
    
    // ouvre le fichier contenant les données
    ifstream data(fichier_source);
    
    // les deux tableaux contenant les valeurs
	float *coord_x;
	float *coord_y;
    
    // temp element for insertion in vector      
    float e;
    
    // si les données ont été correctement chargées
    if (data) {
        // première ligne : NAME
        data >> buffer[0][0];
        data >> buffer[0][1];
        // 2 : TYPE
        data >> buffer[1][0];
        data >> buffer[1][1];
        // 3 : COMMENT, lis toute la ligne
        data >> buffer[2][0];
		getline( data, tmp );
        // 4 : dimension
        data >> buffer[3][0];
        data >> dimension;
        // 5 : edge weight type
        data >> buffer[4][0];
        data >> buffer[4][1];
        // ignore, debut coordonnees
        data >> tmp;
        
		coord_x = new float[dimension];
		coord_y = new float[dimension];

        // boucle et enregistre toute les valeurs dans un tableau
        for (int i=1; i<=dimension; i++) {
            data >> tmp; // ignore the first value
            data >> e;
            //node_x.push_back(e);
			coord_x[i] = e;
            data >> e;
            //node_y.push_back(e);
			coord_y[i] = e;
        }

    } else {
		printf("Erreur de chargement des donnees");
		return 1;
	}
    
    // affichage des valeurs lues
    // cout << buffer[0][0] << buffer[0][1] << endl;
    // cout << buffer[2][0] << tabString[0] << endl;
    // cout << buffer[3][0] << dimension << endl;
    
    // affichage du fichier lu
    // for(int i = 0; i < dimension; ++i) {
    //         printf("val %d \t:\t %f \t %f \n", i, coord_x[i], coord_y[i] );
    //     }
    
    // ------------------
    cout << " 2 - calcul des distances " << endl;
    
    // calcul la distance entre les points
    // points : 0 à dimension-1
    // matrice symétrique
    
    //dimension = 10;
	float **distance = new float*[dimension];
	for(int i=0; i<dimension; i++) {
		distance[i] = new float[dimension];
	}
    float r[dimension][dimension]; // R, utilisé dans chaque iteration
    int traces[dimension][dimension]; // traces 
	bool visited[dimension]; // liste des villes visitées
    int t0 = 0;
    int nbFourmis = 10; // nombre de fourmis
    int L = 0;
    int random;

    
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
		        distance[i][j] = 1/sqrt(pow(coord_x[i] - coord_x[j], 2) + pow(coord_y[i] - coord_y[j], 2));
			} else { // Diagonale
				distance[i][j] = 0;
			}
        }
    }
    
    // affiche la matrice de distance
    //cout << "Matrice de distance" << endl;
    //                for(int i = 0; i < dimension; i++) {
    //                    for(int j = 0; j < dimension; j++) {
    //                        cout << distance[i][j] << "\t ";
    //                    }
    //                    cout << endl;
    //                }
    
	// Pour stocker les solutions
	int solutions[max_iter][dimension];
	float solutionsQuality[max_iter];
    
    cout << " 3 - execution - iteration" << endl;
    
    // demarrage des iterations
    for(int n = 0; n < max_iter; n++) {
        
        // reset R à 0
        for (int i = 0; i < dimension; i++)
            for (int j = 0; j < dimension; j++)
                r[i][j] = 0.0;
            
        // Reset des villes visitees
        for ( int i = 0; i < dimension; i++)
            visited[i] = false;
    
        // pour chaque fourmi
        for (unsigned int k = 1; k <= nbFourmis; k++) {
            
            // sort by rank
            L = 0; // what???
                        
            // tant que toute les villes n'ont pas ete visitées
            for (int j=0; j<dimension; j++) {
                // trouve la ville suivante non-visitée de manière aléatoire
                while (true) {
                    // choix d'une ville au hasard (0..dimension)
                    random = (unsigned)((double)rand()/((double)RAND_MAX+1)*dimension);
                
                    if ( !visited[random] ) {
                        visited[random] = true; // la ville est maintenant visitée
                        
						solutions[n][j] = random;

                        // TODO proportionnel aux traces!
                        break;
                    }
                    
                }
            }
            
            break; // buggy without? --> TODO : understand!!!!
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
		cout << getLength(solutions[i], distance) << endl;
    }

    cout << "Creation d'un fichier postscript pour visualiser les resultats" << endl;
    
    // creation d'un fichier PostScript pour visualiser les solutions
    ofstream ps("visual.ps");
    float zoom_x = 4;
    float zoom_y = 2;
    
    ps << "%!PS-Adobe-2.0 EPSF-2.0;" << endl;
    ps << "1 setlinewidth" << endl;
    //ps << "%%BoundingBox: 0 0 535 333" << endl;
    ps << "0 0 0 setrgbcolor" << endl; // couleur
    ps << "0 0 moveto" << endl;     
    for ( int i = 0; i<max_iter; i++ ) {
        for ( int j = 0; j < dimension; j++ ) {
            ps << coord_x[solutions[i][j]]/zoom_x << " " << coord_y[solutions[i][j]]/zoom_y << " lineto" << endl;
        }
        ps << "stroke" << endl;
        ps << "0.0 0.0 " << (sin(i)) << " setrgbcolor" << endl; // TODO COLORS
        ps << "0 0 moveto" << endl;
    }
    
    // ps << "0 0 moveto 500 500 lineto stroke" << endl;
    ps << "%%EOF" << endl;
    
    cout << "Fin du programme" << endl;
	
	// Fermeture des fichiers et liberation de la memoire
    ps.close();
    data.close();
	delete[] coord_x;
	delete[] coord_y;
	delete[] distance;
    
	return 0;
}
