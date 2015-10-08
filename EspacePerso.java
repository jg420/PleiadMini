package fr.product.greg.pleiadmini;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.View;

/**
 * Created by greg on 11/08/15.
 */
public class EspacePerso{


    private static String downloadPath= Environment.getExternalStorageDirectory().getPath()+"/Download/" ;
    private static String lastClickPath="defaultPath";
    private static int layoutLoggin;
    private static int layoutLoading;
    private static int layoutMenu;

    public static int getLayoutMenu() {
        return layoutMenu;
    }

    public static void setLayoutMenu(int layoutMenu) {
        EspacePerso.layoutMenu = layoutMenu;
    }

    public static ModeleMetierPleiad getModel() {
        return model;
    }

    public static void setModel(ModeleMetierPleiad model) {
        EspacePerso.model = model;
    }

    private static ModeleMetierPleiad model;

    public static String getDownloadPath() {
        //this.
        return downloadPath;
    }

    public static void setDownloadPath(String downloadPath) {
        EspacePerso.downloadPath = downloadPath;
    }

    public static String getLastClickPath() {
        return lastClickPath;
    }

    public static void setLastClickPath(String lastClickPath) {
        EspacePerso.lastClickPath = lastClickPath;
    }

    public static int getLayoutLoggin() {
        return layoutLoggin;
    }

    public static void setLayoutLoggin(int layoutLoggin) {
        EspacePerso.layoutLoggin = layoutLoggin;
    }

    public static int getLayoutLoading() {
        return layoutLoading;
    }

    public static void setLayoutLoading(int layoutLoading) {
        EspacePerso.layoutLoading = layoutLoading;
    }

    public static void login(Context a,String login, String pwd) {
        Intent ecranChargement=new Intent(a,PageChargement.class);
        ecranChargement.putExtra("login",login);
        ecranChargement.putExtra("mdp",pwd);
        ecranChargement.putExtra("demande", "menuCours");
        ecranChargement.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //a.setContentView(EspacePerso.getLayoutLoading());
        a.startActivity(ecranChargement);

    }
    //public static void
    //public static void get


}
