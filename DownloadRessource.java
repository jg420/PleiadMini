package fr.product.greg.pleiadmini;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.util.Map;

/**
 * Created by greg on 04/06/15.
 */
public class DownloadRessource extends AsyncTask<Void,Void, Ressource> {

    private Ressource mesRessourcesRetour=new Ressource();
    private ModeleMetierPleiad monCompteModeleMetierPleiad;
    private String login,mdp;
    private int nDemande;   //Correspond a la demande
    private String lienPageEncours;
    private Activity activity;

    private  ProgressDialog progress;

    protected DownloadRessource(Activity activity,int nDemande,String login, String mdp){
        this.nDemande=nDemande;
        this.monCompteModeleMetierPleiad =new ModeleMetierPleiad(login,mdp);
        this.login=login;
        this.mdp=mdp;
        this.progress=new ProgressDialog(activity);
        this.activity=activity;
    }

    protected DownloadRessource(Activity activity,int nDemande,Map<String, String> cookie){
        this.nDemande=nDemande;
        this.monCompteModeleMetierPleiad =new ModeleMetierPleiad(cookie);
//re        this.progress=new ProgressDialog(activity);
      //  progress.show();
        this.activity=activity;
       //  print("dans download cookie : "+cookie);
    }

    protected DownloadRessource(Activity activity,int nDemande,Map<String, String> cookie,String lienPageEncours){
        this.nDemande=nDemande;
        this.monCompteModeleMetierPleiad =new ModeleMetierPleiad(cookie);
         this.lienPageEncours=lienPageEncours;
       this.progress=new ProgressDialog(activity);
         this.activity=activity;
       //  progress.show();
    }

    protected void onPreExecute() {

        super.onPreExecute();

    }

    protected Ressource doInBackground(Void... params) {


         switch (nDemande){

             case 1:    //Demander une connexion login
                 monCompteModeleMetierPleiad.connectLogin(login, mdp);
                 if (monCompteModeleMetierPleiad.isConnected()){
                     mesRessourcesRetour.setConnected(true);

                 }else {
                     mesRessourcesRetour.setConnected(false);

                 }
                 break;

             case 2:    //Demande d'affiche des UEs pour un compte précis
                 mesRessourcesRetour.setListCours(monCompteModeleMetierPleiad.getListUe());
                 break;

             case 3:    //Demande d'affichage des séance d'une UE précise
                 mesRessourcesRetour.setListSeance(monCompteModeleMetierPleiad.getListSeance(lienPageEncours));
                 break;

             case 4:    //Demande d'affichage des documents d'une seance précise
                // String[][] list=monCompteModeleMetierPleiad.getListLienDoc(lienPageEncours);
                // Log.d("list",(monCompteModeleMetierPleiad.getListLienDoc(lienPageEncours))[0][0]);
                 mesRessourcesRetour.setListLienPdf(monCompteModeleMetierPleiad.getListLienDoc(lienPageEncours));
                 break;

             case 5:    //Demande de telecharge d'un document précis
                 String path= EspacePerso.getDownloadPath();
                 String lien = monCompteModeleMetierPleiad.getLienDocSeanceUe(lienPageEncours);

                 if(lien!=null){
                     monCompteModeleMetierPleiad.download(lien, path);
                     mesRessourcesRetour.setNameLastFileDownload(monCompteModeleMetierPleiad.getLastFileName());
                 }else{

                     // a voir plus tard
                 }

               //  print("DOWNLOAD OK à l adresse suivante " +path+ " t.pdf");
                 break;

             case 6:    //Demande du lien du planning des examens
                 mesRessourcesRetour.setLienDocu(monCompteModeleMetierPleiad.getRessourceCommune(2));
                 break;

             case 7:    //Demande du lien des resultats des examens
                 mesRessourcesRetour.setLienDocu(monCompteModeleMetierPleiad.getRessourceCommune(3));
                 break;

             case 8:    //Demande du lien du planning des cours
                 mesRessourcesRetour.setLienDocu(monCompteModeleMetierPleiad.getRessourceCommune(1));
                 break;
         }

         mesRessourcesRetour.setCookies(monCompteModeleMetierPleiad.getCookie());

         return mesRessourcesRetour;
    }

    protected void onPostExecute(Ressource result) {
        super.onPreExecute();
    }

    public void print(String msg){
        System.out.println(msg);
    }


}
