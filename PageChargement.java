package fr.product.greg.pleiadmini;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

/**
 * Created by greg on 19/06/15.
 */
public class PageChargement extends Activity{

    TextView txtview;
    fr.product.greg.pleiadmini.Ressource r;
    ProgressDialog progress;
    String ancienCookie;
    int layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(EspacePerso.getLayoutLoading());

        txtview=(TextView)findViewById(R.id.textViewChargement);
        progress=new ProgressDialog(this);
        r=new Ressource();
        String demande=getIntent().getStringExtra("demande");
        String downloadPath=getIntent().getStringExtra("path");


        switch(demande){
            case "menuCours":
                ///Recup de tout ce qui il y a d utile
                String login=getIntent().getStringExtra("login");
                String mdp=getIntent().getStringExtra("mdp");

                afficheMenuPrincipal(login,mdp ,downloadPath);
                break;
            case "menuCoursDetail":
               //
               // progress.show();
                String lienDeLaSeance=getIntent().getStringExtra("lienDeLaSeance");
                String cookie=getIntent().getStringExtra("cookie");


                ancienCookie=cookie;
                r.setCookies(cookie);
               // Log.d("cookie",r.getCookies().toString());
               afficheDetailCours(lienDeLaSeance,downloadPath);
                break;
        }
    }

    public void afficheMenuPrincipal(String login,String mdp,String path ){

        //  Log.d("login",login);
        // Log.d("mdp",mdp);

       /* Methode classique
       progress.show();
       ModeleMetierPleiad pleiad=new ModeleMetierPleiad(login,mdp);
       if(pleiad.isConnected()){
           progress.setMessage("CONNECTE");
       }else progress.setMessage("NON CONNECTE");
       */

       /* Tache Asynchrone*/
        AsyncTask<Void, Void, Ressource> maTache = new DownloadRessource(this,1,login, mdp ).
                execute();
        String txtStatus="";
        try {
            Intent ecranSuivant=new Intent(getApplicationContext(),MenuPrincipal.class);
            r=maTache.get(); //doInBackground renvoie les resssources
            boolean connected=r.getConnected();

            if(connected){

              //  progress.show();
                //Il faudrait faire passer l'objet ressource
                //mais pour l'instant le cookie suffit
                //et enfaite c'est la valeur toString() du cookie
                // et pas le cookie qui est envoyé
                String cookieRecu=r.getCookies().toString();
                ecranSuivant.putExtra("cookie", cookieRecu);
                ecranSuivant.putExtra("path",path);

            }else {

                ecranSuivant=new Intent(getApplicationContext(),EcranConnection.class);
                txtStatus="Erreur Connection";
                ecranSuivant.putExtra("authent",1);

            }
            txtview.setText(txtStatus);
            startActivity(ecranSuivant);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public void afficheDetailCours( String lienDeLaSeance , String path){
        //String lienDeLaSeance=r.getListCours()[position][0]; //[0] correspond a la valeur du lien
        //Préparation des données pour la prochaine activité et
        //Lancement de l'ecran correspondant à l'activité
        final Intent ecranCours=new Intent(getApplicationContext(),MenuPrincipal.class);

     //  Log.d("lien", lienDeLaSeance);
       Ressource nouvelleRessource=new Ressource();
        AsyncTask<Void, Void, Ressource> maTache = new DownloadRessource(this,3,r.getCookies(),lienDeLaSeance).
                execute();
        try {
            nouvelleRessource=maTache.get();
            int nbSeance=nouvelleRessource.getListSeance().length;

            String[] listLibSeance=new String[nbSeance];
            String[] listLienSeance=new String[nbSeance];
            for(int i=0;i<nbSeance;i++){
                String lienSeance=nouvelleRessource.getListSeance()[i][1];
                String libSeance=nouvelleRessource.getListSeance()[i][0];
                listLienSeance[i] =(lienSeance);
                listLibSeance[i] =(libSeance);
                //Log.d("lienSeance",lienSeance);
            }
            ecranCours.putExtra("listLibSeance",listLibSeance);
            ecranCours.putExtra("listLienSeance",listLienSeance);
            //ecranCours.put
            //  ecranCours.put

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        ecranCours.putExtra("lien",lienDeLaSeance);
        ecranCours.putExtra("cookie1", nouvelleRessource.getCookies().toString());
        ecranCours.putExtra("cookie", ancienCookie);
        ecranCours.putExtra("path", path);
        //Log.d("lien",lienDeLaSeance);
        startActivity(ecranCours);
    }
}
