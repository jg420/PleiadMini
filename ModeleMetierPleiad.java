
package fr.product.greg.pleiadmini;

import android.os.Environment;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Un compte pleiad correspond à l'access auditeur sur l'Environnement Numérique de Travail
 * fourni à l'inscription au CNAM (idf.pleiad.net)
 *
 * Cette classe permet via une authentification de récuperer un certains nombre d'informations
 * accessible sur le portail web.
 *
 * Les informations fournis se limitent simplement à l'affichage de :
 *  - la liste des UEs
 *  - la listes des séances et/ou documents associé à une UE
 *  - le planning des cours
 *  - le planning des examens
 *  - les dernieres resultat d'examens enregistrés
 *
 *  Pour les 3 derniers élèments se seront enfaite simplement le lien internet pour telecharger
 *  directement les document en format *.pdf
 */
public class ModeleMetierPleiad {

    private Map<String, String> cookies;

    // A DEPLACER DANS LE  MECANISME DE SHAREDFILE DU SDK
    // Sert a manipuler le nom du dernier fichier telecharger
    private String lastFileName;

    public String getLastFileName() {
        return lastFileName;
    }

    public void setLastFileName(String lastFileName) {
        this.lastFileName = lastFileName;
    }
    /////////////////////////////////////////////////////////////////////

    public ModeleMetierPleiad(String login, String pwdl){
        connectLogin(login, pwdl);
    }

    public ModeleMetierPleiad(){

    }

    public ModeleMetierPleiad(Map<String, String> cookies){
        setCookies(cookies);
    }

    /**
     * Sert à donner la valeur d'un cookie au compte pleiad pour l'authentification
     * @param cookies Cookie en format map
     */
    public void setCookies(Map<String, String> cookies){
        this.cookies=cookies;
    }

    /**
     *  Renvoie la valeur du cookie
     *  @param
     * @return le cookie utilisé actuellement
     */
    public Map<String, String> getCookie(){
        return cookies;

    }

    /**
     *  Renvoie l'ensemble des séances enregistrées par l'enseignant
     * @param
     * @return String[][] dim2 lien http, dim2 libellé
     *
     */
    public String[][] getListUe(){
        Document doc2=null;
        try {
            Connection.Response rep = Jsoup.connect("https://lecnam.net/enseignement_index/getListeEnseignement").
                    data("strPnis","idf.pleiad.net"). //Recuperer depuis l analyser reseau mais devrait être dynamique
                    method(Connection.Method.POST).
                    cookies(cookies).
                    execute();
            doc2=rep.parse();
            //Dump d'un doc2
            /*
            *
            <a href="http://idf.pleiad.net/controle.php?Hdj28vp1gRYnU686TuTiXnicAloUb4repmtQ35LiDHgj8HFCSfVXrCz+kEjBPLGZjTZ6dJ04s4Dr+bYXZUttlfiwsVRPW6PPjyOjjc2K26MjwgweCRBoO5NIEsFtCzaxvhDo6g==">EME102 : Management et organisation des entreprises CO (6 ECTS) [2014-2015, semestre 2]</a>
            <a href="http://idf.pleiad.net/controle.php?i67C/f7N6DwSK8oGvk8ebYK8ejCO7Ujm1RJaA6B+fQjie3zKbRVV1TF9ZOLok9qt4SFykzHZjbjJ0X6M8fehKxinIQfJ/7Jq9yUORktfaFt5nvs5kN2lhv4IyVwm+DRPjBiBAw==">GLG105 : Génie logiciel CO (6 ECTS) [2014-2015, semestre 1]</a>
            <a href="http://idf.pleiad.net/controle.php?tOF8BxColX0WGmWQEl8wNzH+X5c84kXE2xHaphOvB9oBqX8dhWXBpln+wf0MhfmWwif0Zn3XLJsg3Sn3m0RJBcsP+dKwi/EW2jdzDIkarNvp/F4yn/Edf/jb5mIKKiEISKyoag==">NSY103 : Linux : principes et programmation CO (6 ECTS) [2014-2015, semestre 2]</a>
            <a href="http://idf.pleiad.net/controle.php?OWqMItWKIVxzqWEwK32OagasflEG+Orha9Wwhc0JtiSlI8B8f9WP8aG15kHZQnceSAlMfbu04w53V0PQt66UtqyzARhQL5ZaSx/uC2pqwcIRec3xJOnlsZy6LKKlCPatZvMQSA==">RCP101 : Recherche opérationnelle et aide à la décision CO (6 ECTS) [2014-2015, semestre 1]</a>
             * */

        } catch (Exception e) {
            e.printStackTrace();
        }
        //print(doc2.select("div [class=panel-body] a"));
        return getFinalListUe(doc2.select("div [class=panel-body] a"));
    }


    /**
     *  Renvoie la liste des seance en fonction de la page du cours
     * @param  lienPageUe lien http pour accéder à une UE
     * @return la liste (dim1 val http,dim2 libelé) des seances associé à une UE
     */
    public String[][] getListSeance(String lienPageUe){

        String[][] rep=null;
        Connection.Response res=connectionParticuliere(lienPageUe);
        Document d;
        try {
            d=res.parse();
            rep= getFinalListLien(d.select("div [id=bloc_seances] h5[class=seance] a"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rep;
    }

    /**
     *  Renvoie la liste des document d'un cours en fonction de son lien
     * @param  lienPageSeance lien http pour accéder à une séance précise
     * @return la liste (dim1 val http,dim2 libelé) des documents associé à une séance
     */
    public String[][] getListLienDoc(String lienPageSeance){
        String[][] listPdf=null;
        Document doc2;
        Connection.Response res=simpleGet(lienPageSeance);
        try {
            doc2=res.parse();
            listPdf= getFinalListLien(doc2.select("ul[class=rightSeqList] li[class=suivi_seance] a")); //recupere le lien de chaque ressource
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listPdf;
    }

    /**
     * Renvoie le lien du document associé à une page contenant un document
     * @param lienDoc le lien http d'une page contenant un document (pdf, doc,ect ...)
     * @return le lien http du document précis
     */
    public String getLienDocSeanceUe(String lienDoc){
        String  listPdf=null;
        Document doc2;
        Connection.Response res=simpleGet(lienDoc);

        try {
            doc2=res.parse();
            Elements e=doc2.select("a[id=telecharger]");
            listPdf= e.attr("abs:href"); //recupere le lien de chaque ressource
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listPdf;

    }


    /**
     * A NE PAS UTILISER
     * @param lien
     * @return
     */
    public boolean download(String lien, String path){
       //print("Lien : "+lien);
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {

            String lien2="https://lecnam.net/";
            String cookie=getCookie().toString();

            cookie=cookie.substring(1, cookie.length() - 1); //pour enlever les { }
            cookie=cookie.replace(",",";"); //Jsoup vers HttpURLConnection

            URL url = new URL(lien);
            connection=(HttpURLConnection) url.openConnection();

            //Preparation de la requette HTTP avec ces parametre headers
            connection.setRequestProperty("Cookie", cookie);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.3) Gecko/20100401");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Cache-control", "max-age=0");
            connection.connect();

            //Construction du filename
            List txtBrut1=connection.getHeaderFields().get("Content-Disposition");
            List txtBrut2 =connection.getHeaderFields().get("ETag");

            String name=getFileName(txtBrut1,txtBrut2);
            input = connection.getInputStream();
            output = new FileOutputStream(path+name);
            lastFileName=name;


            byte data[] = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) {
             //print("ECRITURE");
                output.write(data, 0, count);
            }
        } catch (Exception e) {
           // print("exception : "+e.getMessage());
            return false;
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
                //print("Exception");
            }
            if (connection != null)
                connection.disconnect();
            return true;
        }
    }


    public String getFileName( List l1,List l2){
        String name="file.pdf";
        List filename0 =l1;
        if(filename0!=null){
            name=filename0.toString();
            name=name.substring(name.indexOf("filename="));
            name=name.substring(10,name.length()-2);
        }
        List filename1 =l2;
        if(filename1!=null){
            name=filename1.toString();
            name=name.substring(4,name.length()-4)+".pdf";
        }

        return name;
    }
    /**
    *Pour l'instant j'utilise un lien statique pour effectuer l'appel
    * mais on peut imaginer un tri sur une selection
    * Ressource utilisé
    * Planning cours= http://idf.pleiad.net/Fr/ent-21.html     -> numRessource =1
    * Planning examen= http://idf.pleiad.net/Fr/ent-22.html     -> numRessource =2
    * Resultat examen= http://idf.pleiad.net/Fr/ent-24.html     -> numRessource =3
    *
    * @param numRessource numéro correspondant à une demande précis
    * @return le lien http du planning des cours, des examens , ou le resultat des examens
    *
    **/
    public String getRessourceCommune(int numRessource){

        String lienRessource=null;
        String adrsPage=null;
        Connection.Response r=null;
         recupuCookieForRessource();
        switch (numRessource){

            case 1:
                adrsPage="http://idf.pleiad.net/Fr/ent-21.html";
                r=simpleGet(adrsPage);
                try {
                    String[] param=getAUthentParamServeurStockage(r.parse().select("div [id=content]"));
                    lienRessource=getPlanningCours(param[0], param[1], param[2]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                adrsPage= "http://idf.pleiad.net/Fr/ent-22.html";
                r=simpleGet(adrsPage);
                try {
                    String[] param=getAUthentParamServeurStockage(r.parse().select("div [id=content]"));
                    lienRessource=getPlanningExam(param[0], param[1], param[2]);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case 3:
                adrsPage= "http://idf.pleiad.net/Fr/ent-24.html";
                r=simpleGet(adrsPage);
                try {
                    String[] param=getAUthentParamServeurStockage(r.parse().select("div [id=content]"));
                    lienRessource=getNoteExam(param[0], param[1], param[2]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

        return lienRessource;
    }

    /**
     *  Renvoie une connexion speciale neccessaire a l'authenfication
     * @param lienPageUe lien http de l'ues
     * @return  connexion indispensable pour consulter les séances des UEs
     * Utile a getListSeance
     */
    public Connection.Response connectionParticuliere(String lienPageUe){

        Connection.Response r=null;
        try {
            String adrs = lienPageUe;

            /////////////////////debut 1er appel
            r = Jsoup.connect(adrs).
                    method(Connection.Method.GET).
                    cookies(cookies).
                    timeout(200000).
                    header("Accept-Encoding", "gzip, deflate")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .followRedirects(false) //Autoriser la redirection ne permet pas d'atteindre la bonne page :(
                    .execute();
            cookies = r.cookies();  //Tres important car le cookie change de valeur apres l appel de la requete controle.php?
            ///////////fin 1er sequence

            /////////// debut 2eme sequence
            r=Jsoup.connect(r.url().toString()).
                    method(Connection.Method.GET).
                    cookies(cookies).
                    timeout(20000).
                    // followRedirects(false).
                            execute();
            ///////////fin 2eme sequence

        }catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }


    /**
     * Renvoie une connexion specialement adapté pour recuperer les plannings et resultats
     * @param lien lien http d'une page (cas d'utilisation : ent21.php)
     * @return connexion necéssaire pour recuperer les plannings et resultats
     */
    public Connection.Response simpleGet(String lien ){

        Connection.Response response=null;
        try {
            response=Jsoup.connect(lien).
                    timeout(500000).                 //A importer depuis un fichier exterieur
                    method(Connection.Method.GET).
                    cookies(cookies).
                    execute();
      //      print("sortie : "+response.parse().text());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }


    /**
     * Placer les élèments dans un tableau [][]
     * @param e element recuperer via un res.select()
     * @return un tableau contenant les elements passé en parametres
     * utile a getListSeance pour la presentation des donnees
     */
    public String[][] getFinalListUe(Elements e){
        String[][] retour=new String[e.size()][2]; //2dimension liblien , url
        int i=0;
        for(Element element:e){
            retour[i][0]=element.attr("abs:href");
            // System.out.print(retour[i][0]+"---");
            retour[i][1]=element.html();
            //System.out.print(retour[i][1]+"---");
            i++;
        }
        return retour;
    }


    /**
     * DOUBLON
     *  Renvoie un tableau simple de lien de documents
     *
     * @param   e  element recuperer via un res.select()
     * @return  un tableau contenant les elements passé en parametres
     *
     * utile a getListSeance
     */
    public String[][] getFinalListLien(Elements e){
        String[][] finalList=new String[e.size()][2];
        int i=0;
        for(Element element:e){
            finalList[i][0]=element.attr("abs:href");
            finalList[i][1]=element.text();
            i++;
        }
        return finalList;
    }

    /**
     *  Renvoie le lien du planning
     * @param   valComptId,valCodeAuditeur
     * @return Renvoie la valeur du lien http du planning des cours
     */
    public String getPlanningCours(String lien,String valComptId,String valCodeAuditeur ){
        String retour=null;
        String eventTarget="m$c$LinkButtonImprTableauPDF";      //Recupéré via l analyseur réseau de mozilla ;)
        try {
       //     print("Lien appelé : "+lien+"---");
            Connection.Response res=Jsoup.connect(lien).
                    data("compte_id", valComptId).
                    data("code_auditeur", valCodeAuditeur).
                    timeout(90000).
                    method(Connection.Method.POST).
                    execute();
            String[] dataHeader = getParamAspNetHidden(res.parse().select("form[id=aspnetForm] input"),eventTarget);
            Connection.Response res1=Jsoup.connect(lien).
                    timeout(90000).
                    data("__EVENTTARGET", dataHeader[0]).
                    data("__EVENTARGUMENT", dataHeader[1]).
                    data("__LASTFOCUS","").
                    data("__VIEWSTATE", dataHeader[3]).
                    data("__VIEWSTATEGENERATOR", dataHeader[4]).
                    data("__EVENTVALIDATION", dataHeader[5]).
                    data("m$SM", dataHeader[7]).
                    data("__ASYNCPOST", dataHeader[8]).
                    data("m$c$DropDownListTableauAnnsco", dataHeader[6]).
                    userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").
                    method(Connection.Method.POST).
                    followRedirects(true).
                    cookies(res.cookies()).
                    execute();
            String host="http://iscople.gescicca.net";      //A recuperer dynamiquement
            //      print("le lien du planning est : "+host+ getLienForBrower(res1.parse().body().toString()));
            retour=host+ getLienForBrower(res1.parse().body().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //download(retour);
        return retour;
    }


    /**
     *
     * @param
     * @return
     */
    public String getPlanningExam(String valAction,String valComptId,String valCodeAuditeur){
       // print("lien : "+valAction);
        String retour=null;
        String eventTarget="m$c$LinkButtonImpressionPDF";      //Recupéré via l analyseur réseau de mozilla ;)
   //     print("Lien appelé : "+valAction+"---");
        try {

            Connection.Response res=Jsoup.connect(valAction).
                    data("compte_id", valComptId).
                    data("code_auditeur", valCodeAuditeur).
                    method(Connection.Method.POST).
                    timeout(90000).
                    execute();
            String[] dataHeader = getParamAspNetHidden(res.parse().select("form[id=aspnetForm] input"),eventTarget);
            Connection.Response res1=Jsoup.connect(valAction).
                    data("__EVENTTARGET", dataHeader[0]).
                    data("__EVENTARGUMENT", dataHeader[1]).
                    data("__LASTFOCUS","").
                    data("__VIEWSTATE", dataHeader[3]).
                    data("__VIEWSTATEGENERATOR", dataHeader[4]).
                    data("__EVENTVALIDATION", dataHeader[5]).
                    data("m$SM", dataHeader[7]).
                    data("__ASYNCPOST", dataHeader[8]).
                    data("m$c$DropDownListExamensAnnsco", dataHeader[6]).
                    userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").
                    method(Connection.Method.POST).
                    followRedirects(true).
                    cookies(res.cookies()).
                    execute();
            String host="http://iscople.gescicca.net";      //A recuperer dynamiquement
            //      print("le lien du planning est : "+host+ getLienForBrower(res1.parse().body().toString()));
            retour=host+ getLienForBrower(res1.parse().body().toString() );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retour;
    }


    /**
     *
     * @param
     * @return
     */
    public String getNoteExam(String valAction,String valComptId,String valCodeAuditeur){
        String retour=null;
        String eventTarget="m$c$LinkButtonImpressionPDF";      //Recupéré via l analyseur réseau de mozilla ;)
        try {
            // print(valAction);
            Connection.Response res=Jsoup.connect(valAction).
                    data("compte_id", valComptId).
                    data("code_auditeur", valCodeAuditeur).
                    method(Connection.Method.POST).
                    timeout(90000).
                    execute();
            String[] dataHeader = getParamAspNetHidden(res.parse().select("form[id=aspnetForm] input"),eventTarget);
            Connection.Response res1=Jsoup.connect(valAction).
                    data("__EVENTTARGET", dataHeader[0]).
                    data("__EVENTARGUMENT", dataHeader[1]).
                    data("__LASTFOCUS","").
                    data("__VIEWSTATE", dataHeader[3]).
                    data("__VIEWSTATEGENERATOR", dataHeader[4]).
                    data("__EVENTVALIDATION", dataHeader[5]).
                    data("m$SM", dataHeader[7]).
                    data("__ASYNCPOST", dataHeader[8]).
                    data("m$c$HiddenFieldFiltre", "").
                    userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").
                    method(Connection.Method.POST).
                    followRedirects(true).
                    cookies(res.cookies()).
                    timeout(90000).
                    execute();
            String host="http://iscople.gescicca.net";      //A recuperer dynamiquement
            //      print("le lien du planning est : "+host+ getLienForBrower(res1.parse().body().toString()));
            retour=host+ getLienForBrower(res1.parse().body().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retour;
    }


    /**
     *
     * Renvoie les  parametre pour la demande de ressource pdf
     * @param   e des élèments recupéré depuis select()
     * @return  une liste contenant les informations de parametrage
     */
    public String[] getAUthentParamServeurStockage(Elements e){
        String[] finalParam=new String[3];

        String addresse=e.select("form[id=params_iframe]").attr("action");
        String valCompteId=e.select("input[name=compte_id]").val();
        String valCodeAuditeur=e.select("input[name=code_auditeur]").val();

        finalParam[0]=addresse;
        finalParam[1]=valCompteId;
        finalParam[2]=valCodeAuditeur;

        return finalParam;
    }


    /**
     *
     * @param eventTarget corresondant enfaite à la demande précise (planning cours/examen, resultat exam)
     * @return
     */
    public String[] getParamAspNetHidden(Elements e,String eventTarget){
        String[] rep=new String[9];

        rep[0]=e.select("input[id=__EVENTTARGET]").val();
        rep[0]=eventTarget;
        rep[1]=e.select("input[id=__EVENTARGUMENT]").val();
        rep[2]=e.select("input[id=__LASTFOCUS]").val();
        rep[3]=e.select("input[id=__VIEWSTATE]").val();
        rep[4]=e.select("input[id=__VIEWSTATEGENERATOR]").val();
        rep[5]=e.select("input[id=__EVENTVALIDATION]").val();
        rep[6]="2014";                                                  //INFORMATION SUR L'ANNEE A RENDRE INTERACTIF
        rep[7]="m$UpdatePanel1|m$c$LinkButtonImprTableauPDF";
        rep[8]="true";

        return  rep;

    }

    /**
     * Renvoie le lien http du pdf soit du planning des cours, des examens , ou le resultat des examen
     *
     * @param chaine la chaine "brute"
     * @return
     */
    public String getLienForBrower(String chaine){

        String chainePurgee;
        String paramDebutLien;
        String paramFinLien;

        paramDebutLien="/_documents/pdf/";
        paramFinLien="pdf',";

        int debutLien=chaine.lastIndexOf(paramDebutLien);//print(""+debutLien);
        int finLien=chaine.indexOf(paramFinLien);//print(""+finLien);

        chainePurgee=chaine.substring(debutLien,finLien+3);

        return chainePurgee;

    }


    /**
     * Permet de se connecter au portail
     * @param login login, mdp mot de passe
     * @return
     */
    public void  connectLogin(String login,String mdp){
        Map<String,String> cookiesLeCnam;
        Map<String,String> cookiesStsCnam;
        try {
            //1er Requette envoyé sur l'adresse par defaut
            Connection.Response rep = Jsoup.connect("https://lecnam.net/index.php").
                    method(Connection.Method.GET).
                    timeout(900000).
                    execute();

            cookiesLeCnam = rep.cookies(); //Sauvegarde du cookie entre chaque bond
            print("cookieCNAM1 : "+cookiesLeCnam);


            //2eme Requette envoyé vers cette page (qui peut être definie depuis la valeur de setLocation pour une réponse302)
            Connection.Response rep1 = Jsoup.connect("https://sts.lecnam.net/idp/Authn/UserPassword").
                    data("j_username", login).
                    data("j_password", mdp).
                    method(Connection.Method.POST).
                    timeout(900000).
                    cookies(cookiesLeCnam).
                    execute();

            cookiesStsCnam = rep1.cookies(); //Sauvegarde du cookie entre chaque bond
            print("cookieStSCnam : "+ cookiesLeCnam);
            //print("location : "+rep1.headers());
            Document doc=rep1.parse();
            Elements form=doc.select("form");
            //Recuperation des attributs contenu dans la balise form qui vont etre envoyé ;)
            String valueHostRelaySAML=form.attr("action");
            String valueRelayState =form.select("input[name=RelayState]").attr("value").toString();
            String valueSAMLResponsee =form.select("input[name=SAMLResponse]").attr("value").toString();

            Connection.Response relaySAMLResponse=Jsoup.connect("https://lecnam.net/Shibboleth.sso/SAML2/POST").
                    data("SAMLResponse", valueSAMLResponsee).
                    data("RelayState", valueRelayState).
                    method(Connection.Method.POST).
                    cookies(cookiesLeCnam).
                    execute();
            cookiesLeCnam=relaySAMLResponse.cookies();
            print("cookieCnam2 : "+cookiesLeCnam);



            cookies=cookiesLeCnam;
            //print("cookieCnam 3 : "+host.parse());

            if(!(getListUe()[0][1].contains("Mot de passe oublié ?"))){
          //  if((relaySAMLResponse.parse().select("a [href=/authentification_deconnexion]")).toString()!=null){
               print("SUCCESS AUTHENT ");
                //cookies = rep.cookies();
            }else {
                 print("ECHEC AUTHENT");
                cookies=null;
            }
        } catch (Exception e) {
         //       print("ERROR");
            e.printStackTrace();
        }
    }


    /**
     *
     * @param
     * @return
     */
    public boolean isConnected(){
        if(cookies!=null){
            return true;
        }else return false;
    }


    /**
     *
     * @param
     * @return
     */
    public void recupuCookieForRessource(){

        //barbare comme methode : je demande au programme la liste de
        // seance du 1er Cours inscrit
        // , mais au moins je suis sur que le cookie est a jour
        getListSeance(getListUe()[0][0]);

        //print("cookie"+getCookie());
    }


    /**
     *
     * @param
     * @return
     */
    public void disconnect(){
        if(cookies!=null){
            simpleGet("https://lecnam.net/authentification_deconnexion");

        }
        cookies=null;
    }


    /**
     *
     * @param
     * @return
     */
    public void print(String msg){
        System.out.println(msg);
    }


    public void setCookies(String sCookie){
        setCookies(getCookieAssemble(sCookie));
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