//============================================================================
// Name        : MOB.cpp
// Author      : Romain de Wolff & Simon Hintermann
// Version     : 0.1
// Description : 
//============================================================================

#include <iostream>
#include <fstream> // travail avec des fichiers
#include <vector>
#include <math.h>
#include <stdlib.h> // random

using namespace std;

// parametres
const int alpha = 1;
const int beta  = 1;
const float rho = 0.5;  // constante d'évaporation, entre 0 et 1

// le fichier source
const char fichier_source[] = "BERLIN52.TSP";

// variable utiles pour la lecture des données du fichier source
char tabString[0][80];
string buffer[5][5];
string tmp;

int dimension;

// programme principal
int main() {
    
    cout << "Lancement de la simulation" << endl;
    
    cout << " 1 - lecture des données" << endl;
    
    // ouvre le fichier contenant les données
    ifstream data(fichier_source);
    
    // les deux vecteurs contenant les valeurs 
    vector<float> node_x(0);
    vector<float> node_y(0);
    
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
        data.getline( tabString[0], 80, '\n' );
        // 4 : dimension
        data >> buffer[3][0];
        data >> dimension;
        // 5 : edge weight type
        data >> buffer[4][0];
        data >> buffer[4][1];
        // ignore, debut coordonnees
        data >> tmp;
        
        // boucle et enregistre toute les valeurs dans un tableau
        for (int i=1; i<=dimension; i++) {
            data >> tmp; // ignore the first value
            data >> e;
            node_x.push_back(e);
            data >> e;
            node_y.push_back(e);
        }

    }
    
    // affichage des valeurs lues
    // cout << buffer[0][0] << buffer[0][1] << endl;
    // cout << buffer[2][0] << tabString[0] << endl;
    // cout << buffer[3][0] << dimension << endl;
    
    // affichage du fichier lu
    // for(int i = 0; i < dimension; ++i) {
    //         printf("val %d \t:\t %f \t %f \n", i, node_x.at(i), node_y.at(i) );
    //     }
    
    // ------------------
    cout << " 2 - calcul des distances " << endl;
    
    // calcul la distance entre les points
    // points : 0 à dimension-1
    // matrice symétrique
    
    //dimension = 10;
    float distance[dimension][dimension];
    float r[dimension][dimension]; // R, utilisé dans chaque iteration
    unsigned int traces[dimension][dimension]; // traces globals
    unsigned int t0 = 0;
    unsigned int max_iter = 5;
    unsigned int m = 10; // nombre de fourmis
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
        // uniquement la moitié de la matrice est calculée car elle est symétrique
        for(int j = i+1; j < dimension; j++) {
            // calcul la distance en utilisant pythagore
            distance[i][j] = 1/sqrt(pow(node_x.at(i) - node_x.at(j), 2) + pow(node_y.at(i) - node_y.at(j), 2));
        }
    }
    
    // affiche la matrice de distance
    cout << "Matrice de distance" << endl;
                    for(int i = 0; i < dimension; i++) {
                        for(int j = 0; j < dimension; j++) {
                            cout << distance[i][j] << "\t ";
                        }
                        cout << endl;
                    }
    
    // stock les solutions
    vector< vector<int> > solutions; // stock les tournées de 0 à max_iter-1
    vector<float> solutionsQuality;
    
    
    cout << " 3 - execution - iteration" << endl;
    
    // demarrage des iterations
    for(unsigned int i = 1; i <= max_iter; i++) {
        
        // reset R à 0
        for (int i = 0; i < dimension; i++)
            for (int j = 0; j < dimension; j++)
                r[i][j] = 0.0;
            
        bool visited[dimension]; // liste des villes visitées
        for ( int i = 0; i < dimension; i++) // initialise  à false
            visited[i] = false;
    
        // pour chaque fourmi
        for (unsigned int k = 1; k <= m; k++) {
            
            // sort by rank 
            
            
            L = 0; // what???
            
            vector<int> vtmp;
                        
            // tant que toute les villes n'ont pas ete visitées
            while ( (int)vtmp.size() <= dimension-1 ) {
                // trouve la ville suivante non-visitée de manière aléatoire
                while (true) {
                    // choix d'une ville au hasard (0..dimension)
                    random = (unsigned)((double)rand()/((double)RAND_MAX+1)*dimension);
                
                    if ( !visited[random] ) {
                        visited[random] = true; // la ville est maintenant visitée
                        vtmp.push_back(random);
                        //solutions.push_back(random); // on l'insere dans la tournée
                        // TODO proportionnel aux traces!
                        break;
                    }
                    
                }
            }
            
            // stock la tournée
            solutions.push_back(vtmp);
            
            break; // buggy without? --> TODO : understand!!!!
        }

    }
    
    // affiche les solutions trouvées, sous forme de liste
    cout << "Nombre de solutions trouvées : " << solutions.size() << endl;
    for ( int i = 0; i<(int)solutions.size(); i++ ) {
        cout << i << ":";
        vector<int> tmp(solutions.at(i).begin(), solutions.at(i).end());
        for ( int j = 0; j < (int)tmp.size(); j++ ) {
            cout << tmp.at(j) << "-";
        }
        cout << endl;
    }

    cout << "Creation d'un fichier postscript pour visualiser les resultats" << endl;
    
    // creation d'un fichier PostScript pour visualiser les solutions
    ofstream ps("visual.ps");
    float zoom_x = 4;
    float zoom_y = 3;
    
    ps << "%!PS-Adobe-2.0 EPSF-2.0;" << endl;
    ps << "1 setlinewidth" << endl;
    //ps << "%%BoundingBox: 0 0 535 333" << endl;
    ps << "0 0 0 setrgbcolor" << endl; // couleur
    ps << "0 0 moveto" << endl;     
    for ( int i = 0; i<(int)solutions.size(); i++ ) {
        vector<int> tmp(solutions.at(i).begin(), solutions.at(i).end());
        for ( int j = 0; j < (int)tmp.size(); j++ ) {
            ps << node_x.at(tmp.at(j))/zoom_x << " " << node_y.at(tmp.at(j))/zoom_y << " lineto" << endl;
        }
        ps << "stroke" << endl;
        ps << "0.0 0.0 " << (sin(i)) << " setrgbcolor" << endl; // TODO COLORS
        ps << "0 0 moveto" << endl;
    }
    
    // ps << "0 0 moveto 500 500 lineto stroke" << endl;
    ps << "%%EOF" << endl;
    
    cout << "Fin du programme" << endl;
    
    data.close();
    return 0;
}
