package fr.product.greg.pleiadmini;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class EcranConnection extends Activity {


    Button btnValid,btnQuitter;
    ProgressDialog progress;
    TextView textView;
    EditText vpwd,vlogin;
    Boolean notLoggedDetected;
    int layout;

    EcranConnection(int xmlPath){

        layout=xmlPath;

    }
    public EcranConnection(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        prepareEspacePerso();

        super.onCreate(savedInstanceState);

        setContentView(EspacePerso.getLayoutLoggin());

        declareVariableGeneral();

        adapteVisuel();

        manageEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {


       super.onBackPressed();  // optional depending on your needs
   }

    public void prepareEspacePerso(){


        /*------------------------------------------------------------------------------------------------------------------*/

        //Modele
        EspacePerso.setModel(new ModeleMetierPleiad());     //A ce niveau le model ne sert  qu a fournir la fonction login

        //Vue
        EspacePerso.setLayoutLoggin(R.layout.activity_connexion);
        EspacePerso.setLayoutLoading(R.layout.activity_chargement);
        EspacePerso.setLayoutMenu(R.layout.activity_menu);

        /*------------------------------------------------------------------------------------------------------------------*/




    }

    public void adapteVisuel(){

        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        int userLoged=getIntent().getIntExtra("authent",0);

        if(userLoged==1){
            notLoggedDetected =true;
        }else {
            notLoggedDetected =false;}


        if(notLoggedDetected){

            textView.setText("Veuillez re-essayer !!!!");
        }
    }

    public void declareVariableGeneral(){
        progress=new ProgressDialog(this);
        vlogin =(EditText)findViewById(R.id.editTextLogin);
        vpwd =(EditText)findViewById(R.id.editTextMdp);
        textView=(TextView)findViewById(R.id.textEtatConnexion);
        btnValid=(Button)findViewById(R.id.btnValidLogin);
        btnQuitter=(Button)findViewById(R.id.btnQuitter);
    }

    public void prepareFichierSysteme(){
        //essaiCreationFichier();

    }

    public boolean essaiCreationFichier() throws IOException {
        File myFile = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Pleiadmini","init"); //on déclare notre futur fichier

        File myDir = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Pleiadmini"); //pour créer le repertoire dans lequel on va mettre notre fichier
        Boolean success=true;
        if (!myDir.exists()) {
            success = myDir.mkdir(); //On crée le répertoire (s'il n'existe pas!!)
        }
        if (success){

            String data= "Ce que je veux ecrire dans mon fichier \r\n";

            FileOutputStream output = new FileOutputStream(myFile,true); //le true est pour écrire en fin de fichier, et non l'écraser
            output.write(data.getBytes());

            return true;
        }
        else {return false;}
    }

    public void affichePageChargement(){

        Intent ecranChargement=new Intent(this,PageChargement.class);
        ecranChargement.putExtra("login", vlogin.getText().toString());
        ecranChargement.putExtra("mdp",vpwd.getText().toString());
        ecranChargement.putExtra("demande","menuCours");
        setContentView(EspacePerso.getLayoutLoading());
        startActivity(ecranChargement);

    }

    public void manageEvent(){
        btnValid.setOnClickListener(
                  new View.OnClickListener() {
           @Override
            public void onClick(View v) {
                progress.setMessage("Chargement");
                progress.show();

               EspacePerso.login(getApplicationContext(),vlogin.getText().toString(),vpwd.getText().toString());

            }

        }

        );
        btnQuitter.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              System.exit(0);
                                          }
                                      }

        );

    }



}
