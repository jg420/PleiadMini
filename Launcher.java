package fr.product.greg.pleiadmini;

/**
 * Created by greg on 28/05/15.
 */

/*
* Objectif: Avoir un environnement d exploitation
*          pour tester la classe métiers
* */
public class Launcher {

    public static void main(String[]args ){

        String login="";
        String mdp="";
         ModeleMetierPleiad monCompteModeleMetierPleiad =new ModeleMetierPleiad(login,mdp);

        /*
        *   Fonctionnalité ModeleMetierPleiad a tester :
        *
        *   -   Afficher les cours (et les seance de ce cours) de l'auditeur
        *   -   Afficher/Telecharger tout les pdf pour un cours donnés
        *   -   Afficher/Telecharger le planning des cours
        *   -   Afficher/Telecharger le planning des examens
        *   -   Afficher/Telecharger les notes des examens
        *
        * */

        String nomDuCours,lienDuCours;
        String[][] listCours,listSeance ;
        int numCours;

        listCours= monCompteModeleMetierPleiad.getListUe();

        numCours=1;                             //correspond au cours
        nomDuCours=listCours[numCours-1][1];     //deuxieme dim val 1 correspond a un libellé ,0 correspond au lien
        lienDuCours=listCours[numCours-1][0];

        listSeance= monCompteModeleMetierPleiad.getListSeance(lienDuCours);  //[2]-> cours n°2 , [0]-> le lien et non pas [1]->le libelle

        print("Il y a   " + listSeance.length + " seance(s) pour le cours  " + nomDuCours);

        try {
            int numSeance = 3;
            String[] lienSeance = listSeance[numSeance - 1]; //commence a 0
            //print("La seance n°"+numSeance+" du cours "+nomDuCours+" est "+lienSeance[0]);

            String[] listPdf = monCompteModeleMetierPleiad.getListLienDoc(lienSeance[0])[3]; //[] defini le document a dl
            print("Apres getListLienDoc : "+ monCompteModeleMetierPleiad.getCookie());
            String lien0 = listPdf[0];
            print("Le lien de la 1er page  de la seance " + numSeance + " du cours " + nomDuCours + " est " + lien0);
            //monCompteModeleMetierPleiad.download(lien0);

            String lienPdf = monCompteModeleMetierPleiad.getLienDocSeanceUe(lien0);

            print("Le lien du 1er pdf " + lienPdf);

            //monCompteModeleMetierPleiad.setCookies(cnamCookie);
            monCompteModeleMetierPleiad.download(lienPdf, "/home/greg/");

            String pl = monCompteModeleMetierPleiad.getRessourceCommune(2);  //1 -> Planning cours, 2->Planning exam  3->note exam
            // print("Le planning de cette année  est la : "+pl);/**/

            // monCompteModeleMetierPleiad.download(pl);
        }catch (Exception e){
            print("Exception detecté");
        }
    }

    static void print(String msg){
        System.out.println(msg);
    }

}
