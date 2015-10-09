package fr.product.greg.pleiadmini;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by greg on 05/06/15.
 */
public class MenuCours extends SousMenu{

    public TextView textViewMsgGeneral;
    public ListView listCour;
    public Button btnDownloadPlanning;
    ProgressDialog progress;

    public String[][] listCours;
    public String[][] listSeance;
    //String

    ExpandableListView listViewSeance;
    TextView textViewMsg; //?
    Button btnRetour;
    MyExpandableAdapatater listAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String[]>> listDataChild;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_cours);

        declareVariable();

        chargeParam();

        //Si
         if(lienSeance!=null){
            chargeEcranCoursDetail(lienSeance);
        }else {
            chargeListCours();

            afficheListCours();

            manageEventCoursGeneral(this);
       }
    }

    public void declareVariable(){
        textViewMsgGeneral =(TextView)findViewById(R.id.textViewMenuCOurs) ;
        listCour=(ListView)findViewById(R.id.listViewMenuCours);
        btnDownloadPlanning=(Button)findViewById(R.id.btnDownloadPlanningCours);
        progress=new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    public void chargeParam(){
        String cookie=getParent().getIntent().getStringExtra("cookie1");
        if(cookie!=null){
            mesRessources.setCookieSecondaire(cookie);
        }
        String[] listLibSeance=getParent().getIntent().getStringArrayExtra("listLibSeance");
        String[] listLienSeance=getParent().getIntent().getStringArrayExtra("listLienSeance");

        if(listLibSeance!=null){

            String [][] listSeance=new String[listLibSeance.length][2];
            //Ici je peuple manuellement la liste des seance
            for(int a=0;a<listLibSeance.length;a++){
                listSeance[a][0]=listLibSeance[a];
                listSeance[a][1]=listLienSeance[a];
            }
            mesRessources.setListSeance(listSeance);
        }
    }

    public void chargeListCours(){
        //le 2 passé en parametre correspond a une demande de renvoie de listCours general
        AsyncTask<Void, Void, Ressource> maTache = new DownloadRessource(this,2,mesRessources.getCookies()).
                execute();
        try {
            mesRessources=maTache.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void afficheListCours(){

        int nbCour=mesRessources.getListCours().length;

        final ArrayList<String> list = new ArrayList<String>();
        for (int numCours = 0; numCours < nbCour; numCours++) {
         //   Lien lien=new Lien(mesRessources.getListCours()[numCours][1],mesRessources.getListCours()[numCours][0]);
            list.add(mesRessources.getListCours()[numCours][1].trim());
            //print(mesRessources.getListCours()[numCours][1]);
        }
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this,R.layout.textview_menu_cours,list); //Placer le txtview qui va abriter l objet list

        textViewMsgGeneral.setText(nbCour + " UE(s) enregistré(s)");
        listCour.setAdapter(adapter);
        listCours=mesRessources.getListCours();
    }

    public void manageEventCoursGeneral(  final Activity activity){
        listCour.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        //         setContentView(R.layout.activity_chargement);
                        progress.show();

                       /* String lienDeLaSeance = mesRessources.getListCours()[position][0];
                        afficheDetailCours(position);
                        chargeEcranCoursDetail(lienDeLaSeance);
                        */

                      /*  setContentView(R.layout.activity_chargement);*/
                        String lienDeLaSeance = mesRessources.getListCours()[position][0];
                        //  Log.d("lienpage", lienDeLaSeance);
                        Intent ecranChargement = new Intent(activity, PageChargement.class);
                        ecranChargement.putExtra("demande", "menuCoursDetail");
                        ecranChargement.putExtra("lienDeLaSeance", lienDeLaSeance);
                        ecranChargement.putExtra("cookie", mesRessources.getCookies().toString());
                        ecranChargement.putExtra("path", EspacePerso.getDownloadPath() );
                        //   progress.setMessage("Chargement ");
                        //    progress.show();
                        startActivity(ecranChargement);
                    }
                });
        btnDownloadPlanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDownloadPlanning();
            }
        });



    }

    public void valideTelechargementPlannning(){

        final boolean retour = true;
        new AlertDialog.Builder(MenuCours.this)
                .setTitle("Ouverture navigateur")
                .setMessage("Le planning va s'ouvrir dans le navigateur Internet et l'application ne sera plus actif " +
                        "   Voulez vous continuer?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        telechargePlanning();
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

    public void telechargePlanning( ){
        //IL faut effectuer la tache lourde de cette maniere
        AsyncTask<Void, Void, Ressource> maTache = new DownloadRessource(this,8, mesRessources.getCookies()).
                execute();
        Map<String, String> ancienCookie= mesRessources.getCookies();
        try {
            mesRessources = maTache.get();
            browserSystem(mesRessources.getLienDocu());
            mesRessources.setCookies(ancienCookie);

        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void getDownloadPlanning(){
        new Thread(new Runnable() {
            @Override
            public void run()
            {

                // do the thing that takes a long time
               // telechargePlanning();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //   progress.show();
                        valideTelechargementPlannning();
                    }
                });
            }
        }).start();
    }

    public void browserSystem(String lien ){
        String url=lien;
        Intent i = new Intent(Intent.ACTION_VIEW);
        Uri u = Uri.parse(url);
        i.setData(u);
        startActivity(i);
    }

    public void defaultViewer(String pathPdf) throws ActivityNotFoundException {
        File file = new File(pathPdf);

        if (file.exists()) {
            Uri path = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/pdf");
// optionnel a toi de voir quel flag tu souhaites
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
    }

    //Dans le cas de l'affichage d'une seance précise
    public void afficheListeSeance(Ressource mesRessource1) throws ExecutionException, InterruptedException {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<>();

        //Je boucle sur le nombre de seance

        for(int i=0;i<mesRessource1.getListSeance().length;i++){
            listDataHeader.add(mesRessource1.getListSeance()[i][1]);
            List<String[]> listLien = new ArrayList<String[]>();
            String[] repFictif=new String[2];
            repFictif[0]="Un libéllé fictif";
            repFictif[1]="http://lien.fictif.com";
            listLien.add(repFictif);
            listDataChild.put(listDataHeader.get(i), listLien);

        }
    }

    public void manageEventCoursDetail(final Activity activity){
        //Pour telecharger le document
        listViewSeance.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                //setContentView(R.layout.activity_chargement);
              //  progress.show();

                String[] monTexte = listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition);

                //Ici il faut ajouter l affichage d'un bouton download


               // browserSystem();
              //  Log.d("cookie:", mesRessources.getCookieSecondaire().toString());
             AsyncTask<Void, Void, Ressource> maTache = new DownloadRessource(
                     activity,
                     5,
                     mesRessources.getCookieSecondaire(),
                     monTexte[0]).execute();
                try {
                    Ressource r = maTache.get();
                 //   progress.setMessage("DOWNLOAD OK à l adresse suivante " +  Environment.getExternalStorageDirectory().getPath()+"/Download/t.pdf");
                 //   defaultViewer( Environment.getExternalStorageDirectory().getPath()+"/Download/nato1968.pdf");

                    String fileName=r.getNameLastFileDownload();

                    String path= EspacePerso.getDownloadPath() +fileName;

                     createNotification("document enregistré : "+path,path);


                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        //POur afficher les docuements de la seance
        listViewSeance.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                AsyncTask<Void, Void,Ressource> downloadLienPdf =
                        new DownloadRessource(activity,
                                4,
                                mesRessources.getCookieSecondaire(),
                                mesRessources.getListSeance()[groupPosition][0]).
                                execute();
                try {
                    Ressource res = downloadLienPdf.get();
                    List<String[]> listPdf = new ArrayList<String[]>();

                  //  Log.d("cookie listlien", mesRessources.getCookies() + "");
                 //   Log.d("lien listlien", mesRessources.getListSeance()[groupPosition][0]);
                    //Je boucle sur le nombre de pdf dans la seance
                    for (int o = 0; o < res.getListLienPdf().length; o++) {
                       // Log.d("var", res.getListLienPdf()[o][0]);
                        listPdf.add(res.getListLienPdf()[o]);
                        //listPdf.toArray()
                    }
                   // setContentView(R.layout.activity_menu_cours);
                    //declare
                    listDataChild.put(listDataHeader.get(groupPosition), listPdf); //ajout de l'enfant a la position
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                Intent ecranCours = new Intent(getApplicationContext(), MenuPrincipal.class);
                ecranCours.putExtra("cookie", mesRessources.getCookies().toString());
                startActivity(ecranCours);
            }
        });
    }

    public void chargeEcranCoursDetail(String lienDeLaSeance){
        setContentView(R.layout.activity_menu_cours_detail);
        listViewSeance =(ExpandableListView)findViewById(R.id.listViewMenuCoursDetail);
        textViewMsg =(TextView)findViewById(R.id.textViewMenuCoursDetail);
        btnRetour=(Button)findViewById(R.id.btnRetour);
        progress=new ProgressDialog(this);

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<>();
        listAdapter=new MyExpandableAdapatater(this,listDataHeader,listDataChild);
        listViewSeance.setAdapter(listAdapter);

            textViewMsg.setText("il y a   : " + mesRessources.getListSeance().length + " séances pour ce cours");
            try {
                afficheListeSeance(mesRessources);
                listAdapter=new MyExpandableAdapatater(this,listDataHeader,listDataChild);
                listViewSeance.setAdapter(listAdapter);
                manageEventCoursDetail(this);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    private final void createNotification(String desc,String docPath){
        //Récupération du notification Manager
        final NotificationManager notificationManager;
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        String notificationTitle="DOWNLOAD OK";
        //Création de la notification avec spécification de l'icône de la notification et le texte qui apparait à la création de la notification
        final Notification notification;
      //  notification = new Notification( ,"Download OK", ""+System.currentTimeMillis());
         notification = new Notification(R.drawable.notification, notificationTitle, System.currentTimeMillis());

        Intent intent =new Intent(this,OpenFile.class);

        intent.putExtra("url",docPath);

        //Définition de la redirection au moment du clic sur la notification. Dans notre cas la notification redirige vers notre application
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Récupération du titre et description de la notification
        final String notificationTitle1 ="Document téléchargé";

        final String notificationDesc = desc;

        //Notification & Vibration
        notification.setLatestEventInfo(this, notificationTitle1, notificationDesc, pendingIntent);
        notification.vibrate = new long[] {0,200,100,200,100,200};

        notificationManager.notify(2, notification);
    }




    @Override
    public void onBackPressed() {

        Intent ecranCours = new Intent(getApplicationContext(), MenuPrincipal.class);
        ecranCours.putExtra("cookie", mesRessources.getCookies().toString());
        ecranCours.putExtra("path", EspacePerso.getDownloadPath());

        startActivity(ecranCours);
    }
}


