/**
 *	This file is part of TuCan Mobile.
 *
 *	TuCan Mobile is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	TuCan Mobile is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with TuCan Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dalthed.tucan.Connection;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;

import org.acra.ACRA;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import android.os.Build;
import android.util.Log;

import com.dalthed.tucan.TucanMobile;

/**
 * BrowseMethods implementiert wichtige Methoden, um Webseiten angepasst zu
 * Laden
 *
 * @author Tyde
 */
public class BrowseMethods {
    private HttpURLConnection httpConnection;
    private CookieManager myCookies;
    public boolean iwantthereader = false;
    public InputStream in;
    public InputStreamReader isr;
    private static final String LOG_TAG = "TuCanMobile";
    public boolean HTTPS = true;

    /**
     * setImportantHeaders setzt Header für den HTTP-Request
     *
     * @param RequestMethod RequestObject.METHOD_POST oder RequestObject.METHOD_GET
     * @param domain        wichtig für CookieHandler
     */
    private void setImportantHeaders(String RequestMethod, String domain) {
        try {
            // GET oder POST setzen
            httpConnection.setRequestMethod(RequestMethod);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        // Browser-Header: Currently Chrome 22
        httpConnection
                .setRequestProperty(
                        "User-Agent",
                        "TuCan.Mobile Android App");
        httpConnection.setInstanceFollowRedirects(false);
        // Cookies setzen:
        if (myCookies != null && myCookies.domain_exists(domain)) {
            if (TucanMobile.DEBUG) {
                Log.i(LOG_TAG, "Cookie gesetzt:" + myCookies.getCookieHTTPString(domain));
            }
            httpConnection.setRequestProperty("Cookie", myCookies.getCookieHTTPString(domain));
        }
    }

    /**
     * HauptMethode der BrowseMethods: lädt die Seite und gibt sie als
     * AnswerObject wieder zurück
     *
     * @param requestInfo RequestObject mit allen Wichtigen Informationen zum Request
     * @return AnswerObject der Seite
     */
    public AnswerObject browse(RequestObject requestInfo) throws ConnectException, UnknownHostException {
        String redirectURL = "";
        String alllines = "";
        if (Build.VERSION.SDK_INT < 9) {
            // Notwendig, da sonst die Verbindung bei älteren Systemen nicht
            // klappt
            System.setProperty("http.keepAlive", "false");
        }
        try {
            // Informationen aus RequestObject auslesen
            URL realURL = requestInfo.getmyURL();
            if (realURL != null) {
                myCookies = requestInfo.getCookies();
                String RequestMethod = requestInfo.getMethod();
                String postdata = requestInfo.getPostData();

                if (HTTPS) {
                    httpConnection = (HttpsURLConnection) realURL.openConnection();
                } else {
                    httpConnection = (HttpURLConnection) realURL.openConnection();
                }
                if (TucanMobile.DEBUG) {
                    Log.i(LOG_TAG, "Started Connection with: " + realURL.toString());
                }
                if (RequestMethod.equals("POST")) {
                    // Output bei POST-Übertragung aktivierten
                    httpConnection.setDoOutput(true);
                }
                // Ursprünglichen Request setzen
                setImportantHeaders(RequestMethod, realURL.getHost());

                if (RequestMethod.equals("POST")) {
                    // Post-Daten senden
                    OutputStreamWriter out = new OutputStreamWriter(
                            httpConnection.getOutputStream());
                    out.write(postdata);
                    out.close();
                }



                if (!iwantthereader) {
                    // Antwort auslesen
                    in = httpConnection.getInputStream();
                    isr = new InputStreamReader(in, "UTF-8");
                    BufferedReader bin = new BufferedReader(isr, 8 * 1024);

                    // Header auslesen
                    for (int n = 0; ; n++) {
                        String headerValue = httpConnection.getHeaderField(n);
                        String headerName = httpConnection.getHeaderFieldKey(n);
                        if (headerValue == null && headerName == null) {

                            break;
                        }

                        // Cookies auslesen
                        if ("Set-Cookie".equalsIgnoreCase(headerName)) {
                            if (TucanMobile.DEBUG) {
                                Log.i(LOG_TAG, "Lese Cookies aus");
                            }
                            String[] multipleCookies = headerValue.split(";\\s*");
                            for (String ccy : multipleCookies) {
                                String[] eachVal = ccy.split("=");
                                if (eachVal.length == 2)
                                    myCookies
                                            .inputCookie(realURL.getHost(), eachVal[0], eachVal[1]);
                                else
                                    myCookies.inputCookie(realURL.getHost(), eachVal[0], null);
                            }
                        }
                        // Eventuellen redirect auslesen und speichern
                        if ("refresh".equalsIgnoreCase(headerName)) {
                            String[] getredirectURL = headerValue.split("URL=");
                            if (getredirectURL.length > 1)
                                redirectURL = getredirectURL[1];
                        }
                        if ("location".equalsIgnoreCase(headerName)) {
                            redirectURL = headerValue;
                        }

                    }
                    int contentlength = httpConnection.getContentLength();
                    if (TucanMobile.DEBUG) {
                        Log.i(LOG_TAG, contentlength + "...");
                    }


                    // Server-Antwort auslesen und speichern
                    StringBuilder inputBuilder = new StringBuilder();
                    String inputLine;

                    while ((inputLine = bin.readLine()) != null) {

                        inputBuilder.append(inputLine);

                    }
                    in.close();
                    alllines = inputBuilder.toString();
                    if(requestInfo.isRedirectNecessary() && redirectURL.equals("")) {
                        Document document = Jsoup.parse(alllines);
                        Elements meta = document.select("html head meta");
                        if (meta != null)
                        {
                            System.out.println("Meta found");
                            String lvHttpEquiv = meta.attr("http-equiv");
                            if (lvHttpEquiv != null && lvHttpEquiv.toLowerCase().contains("refresh"))
                            {
                                System.out.println("Refresh found");
                                String lvContent = meta.attr("content");
                                if (lvContent != null)
                                {
                                    System.out.println("lvContent found");
                                    String[] lvContentArray = lvContent.split(";");
                                    System.out.println(lvContent);
                                    if (lvContentArray.length > 1)
                                        if((lvContentArray[1].toLowerCase()).startsWith("url=")){
                                            redirectURL = lvContentArray[1].substring(4);
                                        }

                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            if (e instanceof EOFException) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Verbindung abgebrochen");
            } else if (e instanceof ConnectException) {
                throw ((ConnectException) e);
            } else if (e instanceof UnknownHostException) {
                throw (UnknownHostException) e;
            } else if (!(e instanceof UnknownHostException) && !(e instanceof SSLException)) {
                ACRA.getErrorReporter().handleSilentException(e);
            }


            if (TucanMobile.DEBUG) {
                e.printStackTrace();
            }

        }
        if (requestInfo.getmyURL() != null) {
            return new AnswerObject(alllines, redirectURL, myCookies, requestInfo.getmyURL()
                    .toString());
        }
        return new AnswerObject(alllines, redirectURL, myCookies, "");
    }
}
