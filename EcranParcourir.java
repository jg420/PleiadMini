package fr.product.greg.pleiadmini;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;

/**
 * Created by greg on 14/08/15.
 */
public class EcranParcourir extends ActivityGroup {

    Button btnValid,btnRetour;
    ListActivity listActivity;
    TabHost tab;
    SharedPreferences.Editor editSetting;
    SharedPreferences settings;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parcourir);

        declareVariableGeneral();

        tab.setup(this.getLocalActivityManager());

        TabHost.TabSpec tab1 = tab.newTabSpec("tab1");

        tab1.setIndicator("Systeme de stockage");
        tab1.setContent(new Intent(this, FileChooser.class));

        tab.addTab(tab1);

        manageEvent(this);


    }
    public void validePath(){
        EspacePerso.setDownloadPath( EspacePerso.getLastClickPath()+"/");   //lastclickPath est modifié pre alblement
        editSetting.putString("path", EspacePerso.getDownloadPath());
        editSetting.commit();
    }

    public void manageEvent(final Activity myActivity){
        btnValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validePath();
                Toast.makeText(myActivity, "emplacement choisi: " + EspacePerso.getDownloadPath(), Toast.LENGTH_SHORT)
                        .show();
                // onBackPressed();
               /* Intent ecranCours = new Intent(getApplicationContext(), MenuPrincipal.class);
                ecranCours.putExtra("currentTab", "Divers");
               startActivity(ecranCours);*/
                Intent returnIntent = new Intent();
                returnIntent.putExtra("path", EspacePerso.getDownloadPath());
                setResult(RESULT_OK, returnIntent);
                finish();

            }
        });
        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*
                Intent ecranCours = new Intent(getApplicationContext(), MenuPrincipal.class);
                ecranCours.putExtra("currentTab", "Divers");
                startActivity(ecranCours);
                */
                onBackPressed();
            }
        });
    }

    public void onBackPressed() {


        super.onBackPressed();  // optional depending on your needs
    }

    public void declareVariableGeneral(){
        tab=(TabHost) findViewById(R.id.tabHost3);
        btnValid=(Button)findViewById(R.id.btnValideDownloadPath);
        btnRetour=(Button)findViewById(R.id.btnRetour);
        settings=getPreferences(0);
        editSetting=settings.edit();

    }

}
