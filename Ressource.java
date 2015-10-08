package fr.product.greg.pleiadmini;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by greg on 04/06/15.
 */
public class Ressource{

    private Map<String, String> cookies=null;
    private Map<String, String> cookieSecondaire=null;
    private String[][] listCours;
    private String[][] listSeance;
    private String[][] listLienPdf;
    private String lienDocu;
    private boolean reponseDerniereOperation ;
    private boolean connected ;
    private String nameLastFileDownload;

    public String getNameLastFileDownload() {
        return nameLastFileDownload;
    }

    public void setNameLastFileDownload(String nameLastFileDownload) {
        this.nameLastFileDownload = nameLastFileDownload;
    }





    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean getConnected() {
        return connected;
    }


    public void setCookieSecondaire(Map<String, String> cookieSecondaire) {
        this.cookieSecondaire = cookieSecondaire;
    }

    public Map<String, String> getCookieSecondaire() {
        return cookieSecondaire;
    }

    public boolean getReponseDerniereOperation() {
        return reponseDerniereOperation;
    }

    public void setReponseDerniereOperation(boolean reponseDerniereOperation) {
        this.reponseDerniereOperation = reponseDerniereOperation;
    }

    public void setLienDocu(String lienDocu) {
        this.lienDocu = lienDocu;
    }

    public String getLienDocu() {
        return lienDocu;
    }

    public Map<String, String> getCookies(){
        return this.cookies;
    }

    public void setCookies(Map<String, String> cookies){this.cookies=cookies;}

    public void setListCours(String[][] list){
        this.listCours=list;
    }

    public String[][] getListCours(){
        return  listCours;
    }

    public String[][] getListSeance(){
       return this.listSeance;
    }

    public void setListSeance(String[][] list){
        this.listSeance=list;
    }

    public String[][] getListLienPdf(){return this.listLienPdf;}

    public void setListLienPdf(String[][] lien){this.listLienPdf=lien;}


    public void setCookies(String sCookie){
        setCookies(getCookieAssemble(sCookie));
    }

    public void setCookieSecondaire(String sCookie){
        setCookieSecondaire(getCookieAssemble(sCookie));
    }


    /**
     * A REVOIR
     *
     * */
    public Map<String, String> getCookieAssemble(String cookie){
        Map<String, String> cookieAssemble=null;
        if(cookie!=null){
            String valC=cookie.substring(1, cookie.length() - 1).trim();    //Il y a des {} a enlever en db et fn et des espace
            // print("le cookie d'origine : "+valC);
            cookieAssemble=new HashMap<>();
            int nombreKey=stringOccur(valC,"=");
            int[] posKey=new int[nombreKey]; //position de fin de chaque key

            String[][] listParam=new String [nombreKey][2]; //nParam ,2clé par param, 2 valeur par clé

            for(int i=0;i<nombreKey;i++){
                //position du 1er = de valC
                posKey[i]=valC.indexOf("=");

                //la cle vaut tout ce qui est avant le =
                //sans les espaces et les ,
                String valCle= valC.substring(0,posKey[i]).replaceAll(" ","").replaceAll(",","");

                //Valc vaut tout ce qui est apres le
                valC=valC.substring(posKey[i]+1,valC.length()); //+1 car il ya le = en debut de chaine
                String value;

                //Pour le dernier param
                if(i<nombreKey-1){
                    value=valC.substring(0,valC.indexOf(","));
                    valC=valC.substring(valC.indexOf(","));

                }else{
                    value = valC;
                }
                cookieAssemble.put(valCle,value);
            }
        }

        return cookieAssemble;
    }
    /**
     * Renvoie le nombre d'occurrences de la sous-chaine de caractères spécifiée dans la chaine de caractères spécifiée
     * @param text chaine de caractères initiale
     * @param string sous-chaine de caractères dont le nombre d'occurrences doit etre compté
     * @return le nombre d'occurrences du pattern spécifié dans la chaine de caractères spécifiée
     */
    public static final int stringOccur(String text, String string) {
        return regexOccur(text, Pattern.quote(string));
    }

    /**
     * Renvoie le nombre d'occurrences du pattern spécifié dans la chaine de caractères spécifiée
     * @param text chaine de caractères initiale
     * @param regex expression régulière dont le nombre d'occurrences doit etre compté
     * @return le nombre d'occurrences du pattern spécifié dans la chaine de caractères spécifiée
     */
    public static final int regexOccur(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        int occur = 0;
        while(matcher.find()) {
            occur ++;
        }
        return occur;
    }






}
