package fr.product.greg.pleiadmini;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fr.product.greg.pleiadmini.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by greg on 05/06/15.
 */
public class MenuExamen extends SousMenu{
    TextView textView=null;
    Button   btnPlanningExam=null;
    Button btnResultat=null;
    ProgressDialog progressDialog=null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_examen);

        declareVariableGlobal();

        manageEvent(this);


    }

    public void manageEvent(final Activity activity){
        btnPlanningExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valideTelechargement(1,activity);
            }
        });
         btnResultat.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 valideTelechargement(2, activity);
             }

         });

    }

    public void browserSystem(String lien ){
        String url=lien;
        Intent i = new Intent(Intent.ACTION_VIEW);
        Uri u = Uri.parse(url);
        i.setData(u);
        startActivity(i);
    }

    public void afficheAttente(final ProgressDialog p){
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                // do the thing that takes a long time

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        p.show();
                    }
                });
            }
        }).start();
    }

    public void telechargeResultatExamen(Activity act){
        //afficheAttente(progressDialog);
        //IL faut effectuer la tache lourde de cette maniere
        AsyncTask<Void, Void, Ressource> maTache = new DownloadRessource(act, 7, mesRessources.getCookies()).
                execute();
        Map<String, String> ancienCookie = mesRessources.getCookies();
        try {
            mesRessources = maTache.get();
            browserSystem(mesRessources.getLienDocu());
            mesRessources.setCookies(ancienCookie);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void telechargePlanningExamen(Activity activity){
        //afficheAttente(progressDialog);
        //Ancien cookie a rester car le cookie change apres le telechargement
        Map<String, String>  ancienCookie= mesRessources.getCookies();

        //IL faut effectuer la tache lourde de cette maniere
        AsyncTask<Void, Void, Ressource> maTache = new DownloadRessource(activity,
                6,
                mesRessources.getCookies()).
                execute();
        try {
            mesRessources=maTache.get();
            browserSystem(mesRessources.getLienDocu());
            mesRessources.setCookies(ancienCookie);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void valideTelechargement(final int demande,final Activity act){

        final boolean retour = true;
        new AlertDialog.Builder(MenuExamen.this)
                .setTitle("Ouverture navigateur")
                .setMessage("Le planning va s'ouvrir dans le navigateur Internet et l'application ne sera plus actif " +
                        "   Voulez vous continuer?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                      if(demande==1){
                          telechargePlanningExamen(act);
                      }if (demande==2){
                            telechargeResultatExamen(act);
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    public void declareVariableGlobal(){

        textView=(TextView)findViewById(R.id.textViewMenuExamen);
        btnPlanningExam=(Button)findViewById(R.id.btnPlanningExamen);
        btnResultat=(Button)findViewById(R.id.btnResultat);
        progressDialog=new ProgressDialog(this);
    }
    @Override
    public void onBackPressed() {
        Intent ecranCours=new Intent(getApplicationContext(), fr.product.greg.pleiadmini.MenuPrincipal.class);
        ecranCours.putExtra("cookie",mesRessources.getCookies().toString());
        startActivity(ecranCours);
    }
}
