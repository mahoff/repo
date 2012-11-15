/**
 ** Das Programm verbindet sich mit der Stadtbücherei Wuppertal
 ** und ruft die Login-Seite auf. Dann übergibt es die Login-Parameter
 ** und ruft dann die erste Seite der Ausleihen auf.
 **
 ** TODO:
 ** - speichern des HTML-Ergebnises als Datei
 ** - versenden der HTML-Ergebnises per Mail
 ** - parsen des HTML-Ergebnisses und Ablegen als XML-Dokument
 ** - parsen des HTML-Ergebnisses und Ablegen als Datensätze in einer Datenbank
 ** - verängern der Ausleihen, die heute oder morgen auslaufen
 ** - versenden der Ausleihliste per Mail über das Formular "UserAccountForm"
 **     methodToCall:mail
 **     CSId:8607N15Sbdb2dc279d31cdce5151f85706ef9eb31f1d4914
 **     email:mh.martin.hoffmann%40googlemail.com
 **     subject:o
 ** - Recherchen nach Büchern und versenden/speichern/parsen wie oben
 ** 
 **/

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

// import org.apache.http.cookie.Cookie; // aus ClientCustomContext.java
// import org.apache.http.impl.client.BasicCookieStore; wenn z.B. List<Cookie> cookies_StartAct
// verwendet wird.

public class QuickStart {
    public QuickStart() {
        super();
    }

    public static void main(String[] args) throws Exception {
        // main objects
        DefaultHttpClient httpclient;
        httpclient = new DefaultHttpClient();
        HttpEntity entity_ShowAct;
        entity_ShowAct = null;
        HttpEntity entity_LoginAct;
        entity_LoginAct = null;
        // http methods
        HttpGet httpGet_StartAct;
        httpGet_StartAct = null;
        HttpGet httpGet_ShowAct;
        httpGet_ShowAct = null;
        HttpResponse resp_StartAct;
        resp_StartAct = null;
        HttpResponse resp_ShowAct;
        resp_ShowAct = null;
        HttpResponse resp_LoginAct;
        resp_LoginAct = null;
        HttpPost httpLoginPost;
        httpLoginPost = null;
        List<NameValuePair> LoginPostVars;
        LoginPostVars = new ArrayList<NameValuePair>();
        // Cookies
        HttpContext lc_CookieTransporter;
        lc_CookieTransporter = new BasicHttpContext();
        CookieStore cs_CookieTransporter;
        cs_CookieTransporter = null;
        //
        String sCookie_JSESSIONID;
        sCookie_JSESSIONID = null;
        String sStartAct;
        sStartAct = "http://webopac.wuppertal.de/webOPACClient.sisis/start.do";
        String sShowActPrefix;
        sShowActPrefix =
                "http://webopac.wuppertal.de/webOPACClient.sisis/userAccount.do;jsessionid=";
        String sShowActPostfix;
        sShowActPostfix = "?methodToCall=show&type=1";
        String sShowActNoCookie;
        sShowActNoCookie =
                "http://webopac.wuppertal.de/webOPACClient.sisis/userAccount.do?methodToCall=show&type=1";
        String sLoginAct;
        sLoginAct = "http://webopac.wuppertal.de/webOPACClient.sisis/login.do";
        /*~*********************************************************************/
        /* Start processing                                               ******/
        /*                                                               *******/
        /*                                                              ********/
        /*~*********************************************************************/
        /**
         ** Aktivität Start
         ** ---------------
         ** 
         **    es wird der locale Http-Kontext mit Cookies befüllt,
         **    damit die nächste Aktivität weiterarbeiten kann.
         **/
        // Bind custom cookie store to the local context
        httpGet_StartAct = new HttpGet(sStartAct);
        lc_CookieTransporter.setAttribute(ClientContext.COOKIE_STORE, cs_CookieTransporter);
        resp_StartAct = httpclient.execute(httpGet_StartAct, lc_CookieTransporter);
        /*
         * ab hier liefert das Cookie USERSESSIONID validen Werte.
         */
        httpGet_StartAct.releaseConnection();
        /**
         ** Aktivität Show
         ** ---------------
         ** es verwendet das Cookie Store aus der Aktivität Start
         **/
        httpGet_ShowAct = new HttpGet(sShowActNoCookie);
        resp_ShowAct = httpclient.execute(httpGet_ShowAct, lc_CookieTransporter);
        try {
            entity_ShowAct = resp_ShowAct.getEntity();
            /*
             * make some usefull things with the response...
             * */
            EntityUtils.consume(entity_ShowAct); // ... ensure it is fully consumed!
        } finally {
            httpGet_ShowAct.releaseConnection();
        }
        /**
         ** Aktivität Login
         **/
        httpLoginPost = new HttpPost(sLoginAct);
        LoginPostVars.add(new BasicNameValuePair("methodToCall", "submit"));
        LoginPostVars.add(new BasicNameValuePair("CSId", get_sCookie_USERSESSIONID(httpclient)));
        LoginPostVars.add(new BasicNameValuePair("username", "********"));
        LoginPostVars.add(new BasicNameValuePair("password", "****"));
        httpLoginPost.setEntity(new UrlEncodedFormEntity(LoginPostVars, Consts.UTF_8));
        resp_LoginAct = httpclient.execute(httpLoginPost);
        try {
            entity_LoginAct = resp_LoginAct.getEntity();
            /*
             * make some usefull things with the response...
             * */
            System.out.println(EntityUtils.toString(entity_LoginAct));
            EntityUtils.consume(entity_LoginAct); // ... and ensure it is fully consumed!
        } finally {
            httpLoginPost.releaseConnection();
            // When HttpClient instance is no longer needed, shut down the connection
            // manager to ensure immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }
    //

    private final static void printResponseHeader(HttpResponse rsp) {
        System.out.println(rsp.getStatusLine());
        Header[] headers = rsp.getAllHeaders();
        for (int i = 0; headers.length > i; i++) {
            String headerRow;
            headerRow = null;
            headerRow = headers[i].toString();
            if (headers[i].toString().contains("Set-Cookie: ")) {
                headerRow.replace("Set-Cookie", "");
                System.out.println("#" + headerRow + "#");
            }
            // System.out.println(headers[i]);
        }
    }
    //

    private final static void printCookies(List<Cookie> lc) {
        if (lc.isEmpty()) {
            System.out.println("None cookies found...");
        } else {
            for (int i = 0; i < lc.size(); i++) {
                System.out.println(lc.get(i).toString());
            }
        }
    }
    //

    private final static String get_sCookie_USERSESSIONID(DefaultHttpClient hc) {
        List<Cookie> lc = hc.getCookieStore().getCookies();
        String sc;
        String pre;
        String post;
        sc = null;
        pre = "[version: 0][name: USERSESSIONID][value: ";
        post =
"][domain: webopac.wuppertal.de][path: /webOPACClient.sisis][expiry: null]";
        for (int i = 0; i < lc.size(); i++) {
            sc = lc.get(i).toString();
            if (sc.contains(pre) && sc.contains(post)) {
                sc = sc.replace(pre, "");
                sc = sc.replace(post, "");
                break;
            }
        }
        return sc;
    }
    //
    /*
    Tipps für getCookies:
    ---------------------
        List<Cookie> cookies_StartAct = cookieStore_StartAct.getCookies();
        List<Cookie> cookies_StartAct = httpclient.getCookieStore().getCookies();

    Tipps für Ausgabe der kompletten Webseite:
    ---------------------
        System.out.println(EntityUtils.toString(entity_LoginAct));

    Tipps für Ausgabe der Statuszeile:
    ---------------------
        System.out.println(resp_LoginAct.getStatusLine());

    Tipps für Ausgabe der HTTP-Header:
    ---------------------
        printResponseHeader(resp_LoginAct);
        printResponseHeader(resp_StartAct);
     **
     */
}


