package fr.product.greg.pleiadmini;

import android.app.ActivityGroup;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by greg on 05/06/15.
 */
public class MenuPrincipal extends ActivityGroup {

    TabHost tabHost;
    Switch swtLogOff;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            setContentView(EspacePerso.getLayoutMenu());
            swtLogOff=(Switch)findViewById(R.id.swtLogOff);
            tabHost=(TabHost)findViewById(R.id.tabHost);

            genereTab();
            manageEvent();

            String currentTab="";
            currentTab = getIntent().getStringExtra("currentTab");
         /*   Toast.makeText(this, "currentTab: " +currentTab, Toast.LENGTH_SHORT)
                    .show();*/
                switch(currentTab){
                    case "Cours":
                        tabHost.setCurrentTab(1);
                        break;
                    case "Examen":
                        tabHost.setCurrentTab(2);
                        break;
                    case "Divers":
                        tabHost.setCurrentTab(2);
                        break;
                }
        }catch (Exception e){
        //    currentTab=null;
        }
        try{
            String path=getIntent().getStringExtra("path");
            if(path!=null){
                EspacePerso.setDownloadPath(path);
            }
        }catch (Exception e){
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                //String result=data.getStringExtra("path");
              /*  Toast.makeText(this, "Result  :" + EspacePerso.getModel() , Toast.LENGTH_SHORT)
                        .show();*/
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void genereTab() {

         tabHost.setup(this.getLocalActivityManager());

         TabHost.TabSpec tab1 = tabHost.newTabSpec("tab1");
         TabHost.TabSpec tab2 = tabHost.newTabSpec("tab2");
         TabHost.TabSpec tab3 = tabHost.newTabSpec("tab3");


         tab1.setIndicator("Cours");
         tab1.setContent(new Intent(this, MenuCours.class));

         tab2.setIndicator("Examen");
         tab2.setContent(new Intent(this, MenuExamen.class));

         tab3.setIndicator("Divers");
         tab3.setContent(new Intent(this, MenuDivers.class));

         tabHost.addTab(tab1);
         tabHost.addTab(tab2);
         tabHost.addTab(tab3);

         tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
             @Override
             public void onTabChanged(String tabId) {

                 for (int i = 0; i < tabHost.getTabWidget().getTabCount(); i++) {
                     tabHost.getTabWidget().getChildTabViewAt(i).setBackgroundColor(Color.RED);
                 }

                 tabHost.getCurrentTabView().setBackgroundColor(Color.WHITE);
             }

         });
         tabHost.setOnClickListener(new TabHost.OnClickListener(){

             @Override
             public void onClick(View v) {

             }
         });

    }

    public void setDefautContent(int i){
        //set
        tabHost.setCurrentTab(i);
    }

    public void manageEvent(){
        swtLogOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ecranConnexion =new Intent(getApplicationContext(),EcranConnection.class);
                startActivity(ecranConnexion);
            }
        });
        tabHost.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

            }
        });
    }
}
