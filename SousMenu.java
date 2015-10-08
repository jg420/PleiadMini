package fr.product.greg.pleiadmini;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import fr.product.greg.pleiadmini.*;

/**
 * Created by greg on 10/06/15.
 */
public class SousMenu extends Activity {

    public Ressource mesRessources=new Ressource();
    public String lienSeance;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        recupereCookieDepuisMenu();


    }

    public void recupereCookieDepuisMenu(){
        String valCookie=null;
        String lien=null;
        try{
            valCookie=getParent().getIntent().getStringExtra("cookie"); //Je recupere le cookie de MenuPrincipal
            lien=getParent().getIntent().getStringExtra("lien");

        }catch (Exception e){

        }
        if(valCookie!=null){
            mesRessources.setCookies((valCookie));
        }
        if(lien!=""){
            lienSeance=lien;
        }
    }






}
