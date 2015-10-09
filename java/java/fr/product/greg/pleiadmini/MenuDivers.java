package fr.product.greg.pleiadmini;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


//import fr.product.greg.pleiadmini.filechooser.*;


/**
 * Created by greg on 05/06/15.
 */
public class MenuDivers extends SousMenu{
    TextView textView,txtInfo;
    Button btnContact,btnChangeDir;
    SharedPreferences settings;
    SharedPreferences.Editor editSetting;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_divers);

        declareVariableGlobal();
        // textView.setText("UN TEST"); 

        manageEvent();
        //FileChooser f=new FileChooser();
        editSetting.putString("path", EspacePerso.getDownloadPath());
        editSetting.commit();
        txtInfo.setText("Le dossier de telechargement est actuellement "+settings.getString("path", EspacePerso.getDownloadPath()));


    }
    @Override
    public void onBackPressed() {
        Intent ecranCours=new Intent(getApplicationContext(), fr.product.greg.pleiadmini.MenuPrincipal.class);
        ecranCours.putExtra("cookie", mesRessources.getCookies().toString());
        startActivity(ecranCours);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                //String result=data.getStringExtra("path");
               /* Toast.makeText(this, "Result  :" + EspacePerso.downloadPath, Toast.LENGTH_SHORT)
                        .show();*/
                txtInfo.setText("Le dossier de telechargement est maintenant "+ EspacePerso.getDownloadPath());

            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void manageEvent(){

        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                String[] adr = {"pleiad.android@gmail.com"};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, adr);

                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.setType("message/rfc882");
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(Intent.createChooser(emailIntent, "Envoi Mail"));

               /* Intent parcourir;
                parcourir = new Intent(v.getContext(),FileChooser.class);
                startActivity(parcourir);*/
            }
        });
        btnChangeDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ecranParcourir;
                ecranParcourir = new Intent(getApplication(), EcranParcourir.class);
                startActivityForResult(ecranParcourir, 1);
              //  finish();
            }
        });

    }

    public void declareVariableGlobal(){
        textView=(TextView)findViewById(R.id.textViewMenuDivers);
        txtInfo=(TextView)findViewById(R.id.txtInfoParamPath);
        btnContact=(Button)findViewById(R.id.btnContactDev);
        btnChangeDir=(Button)findViewById(R.id.btnChangeDirectory);
        settings=getPreferences(0);
        editSetting=settings.edit();

    }
}
