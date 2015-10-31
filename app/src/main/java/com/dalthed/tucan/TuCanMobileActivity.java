/**
 * This file is part of TuCan Mobile.
 * <p/>
 * TuCan Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * TuCan Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with TuCan Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dalthed.tucan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.BrowseMethods;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.helpers.AuthenticationManager;
import com.dalthed.tucan.ui.ChangeLog;
import com.dalthed.tucan.ui.ImprintActivity;
import com.dalthed.tucan.ui.MainMenu;
import com.dalthed.tucan.ui.ProgressBarDialogFactory;
import com.dalthed.tucan.ui.SimpleWebActivity;
import com.dalthed.tucan.util.ConfigurationChangeStorage;

import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class TuCanMobileActivity extends SimpleWebActivity {
    /**
     * Called when the activity is first created.
     */
    // private HTTPSbrowser mBrowserService;
    private static final String LOG_TAG = "TuCanMobile";
    private EditText usrnameField;
    private EditText pwdField;
    String SessionArgument = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        TextView hint = (TextView) findViewById(R.id.hintForPrivateproject);
        hint.setText(Html.fromHtml(getString(R.string.hintToInofficial)));
        hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startImprint = new Intent(TuCanMobileActivity.this, ImprintActivity.class);
                startActivity(startImprint);
            }
        });
        getSupportActionBar().hide();
        ChangeLog cl = new ChangeLog(this);
        if (cl.firstRun()) {
            cl.getLogDialog().show();
        }

        // this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Get saved login information
        AuthenticationManager.Account acc = AuthenticationManager.getInstance().getAccount();

        String tuid = "";
        String pw = "";

        if (!acc.getTuId().equals("")) {
            tuid = acc.getTuId();
            pw = acc.getPassword();

        }
        // Insert saved login information into EditTexts
        usrnameField = (EditText) findViewById(R.id.login_usrname);
        pwdField = (EditText) findViewById(R.id.login_pw);

        usrnameField.setText(tuid);
        pwdField.setText(pw);
        if (tuid.length() != 0 && pw.length() != 0) {
            ((CheckBox) findViewById(R.id.checkBox1)).setChecked(true);
        }

        // Get old Cookie and Session from Preferences to try using the site
        // without login
        String settCookie = acc.getStoredCookie();
        String settArg = acc.getStoredSession();
        Boolean failedSession = false;
        Boolean loggedout = false;
        // Check if coming back from a lostSessionError and do not try to use
        // old session credentials
        if (getIntent() != null && getIntent().getExtras() != null) {
            failedSession = getIntent().getExtras().getBoolean("lostSession");
            if (getIntent().getExtras().getBoolean("loggedout"))
                loggedout = true;
        }


        // Try to connect to site with old credentials, if they are existent
        if (settCookie != null && !settCookie.equals("") && settArg != null
                && failedSession != true && !loggedout && !cl.firstRun()) {

            // Check if the Login credentials should be safed
            CheckBox remember = (CheckBox) findViewById(R.id.checkBox1);
            remember.setChecked(true);
            // Generate Cookiemanager from string
            CookieManager localCookieManager = new CookieManager();
            localCookieManager.generateManagerfromHTTPString(
                    TucanMobile.TUCAN_HOST, settCookie);
            // Start connection
            SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
                    this);
            RequestObject thisRequest = new RequestObject(
                    "https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi?APPNAME=CampusNet&PRGNAME=MLSSTART&ARGUMENTS="
                            + settArg + ",", localCookieManager,
                    RequestObject.METHOD_GET, "", false);

            callOverviewBrowser.execute(thisRequest);

        } else if (tuid != "" && pw != "" && !cl.firstRun()) {
            // Start Login-procedure
            onClickSendLogin(null);
        }
        // Image gets focus, that the Keyboard is not opening automatically when
        // APP is started..
        ImageView img = (ImageView) findViewById(R.id.imageView1);
        img.requestFocus();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.main);
    }

    /**
     * This method gets called, when the "Login"-Button is pressed, or the login
     * should be executed
     *
     * @param sfNormal Button-View
     * @author Daniel Thiem
     */
    public void onClickSendLogin(final View sfNormal) {

        HTTPSBrowser newBrowser = new HTTPSBrowser();

        try {
            // TuCan leitet 3 mal weiter, damit der Login-Vorgang Abgeschlossen
            // wird
            RequestObject[] thisRequest = new RequestObject[4];
            // Cookie Abholen
            thisRequest[0] = new RequestObject(
                    "https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi?APPNAME=CampusNet&PRGNAME=STARTPAGE_DISPATCH&ARGUMENTS=-N000000000000001",
                    RequestObject.METHOD_GET, "");

            // Login auslesen und senden

            String usrname = usrnameField.getText().toString();
            String pwd = pwdField.getText().toString();

            // vordefinierte Post-Data
            // TODO: Dynamisch <input 's auslesen [evtl]
            String postdata = "usrname="
                    + URLEncoder.encode(usrname, "UTF-8")
                    + "&pass="
                    + URLEncoder.encode(pwd, "UTF-8")
                    + "&APPNAME=CampusNet&PRGNAME=LOGINCHECK&ARGUMENTS=clino%2Cusrname%2Cpass%2Cmenuno%2Cmenu_type%2Cbrowser%2Cplatform&clino=000000000000001&menuno=000344&menu_type=classic&browser=&platform=";
            Log.i(LOG_TAG, postdata);
            // AnmeldeRequest Senden
            thisRequest[1] = new RequestObject(
                    "https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi", new CookieManager(),
                    RequestObject.METHOD_POST, postdata, true);
            // Restliche Requests werden aus der Antwort ausgelesen..

            // Requests abscicken
            newBrowser.execute(thisRequest);
        } catch (Exception e) {
            Log.e(LOG_TAG, "FEHLER: " + e.getMessage());
        }
    }

    public class HTTPSBrowser extends
            AsyncTask<RequestObject, Integer, AnswerObject> {
        ProgressDialog dialog;

        protected void onPreExecute() {
            // ProgressDialog anfertigen und anzeigen
            dialog = ProgressBarDialogFactory.createProgressDialog(
                    TuCanMobileActivity.this,
                    getResources().getString(R.string.ui_login));
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // Fortschritt Berechnen und anzeigen
            int Progress = (int) ((((double) values[0] + 1) / (double) values[1]) * 100);
            Log.i(LOG_TAG, values[0] + " von " + values[1] + " ergibt: "
                    + Progress);
            dialog.setProgress(Progress);
        }

        @Override
        protected AnswerObject doInBackground(RequestObject... requestInfo) {

            AnswerObject answer = new AnswerObject("", "", null, null);
            for (int i = 0; i < requestInfo.length; i++) {
                // requestInfo[i] kann null sein, da das Array mit 2
                // null-Objekten übergeben wird und dort erst
                // später Redirects hinein geschrieben werden
                // TODO: requestInfo in ArrayList umwandeln ?
                if (requestInfo[i] != null) {
                    BrowseMethods Browser = new BrowseMethods();

                    // Requests letztendlich abschicken
                    try {
                        answer = Browser.browse(requestInfo[i]);

                    } catch (Exception e) {
                        TuCanMobileActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TuCanMobileActivity.this, "Keine Internetverbindung", Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                    Log.i(LOG_TAG, "Redirect:" + answer.getRedirectURLString());
                } else {
                    break;
                }
                // Letztes Objekt
                if (i < requestInfo.length - 1) {
                    // Check for HTTP-Redirect
                    if (answer.getRedirectURLString() != ""
                            && requestInfo[i + 1] == null) {
                        Log.i(LOG_TAG,
                                "Insert new Redirect URL in RequestObject:"
                                        + answer.getRedirectURLString());
                        // Add HTTP-Redirect into requestInfo-List
                        requestInfo[i + 1] = new RequestObject("https://"
                                + requestInfo[i].getmyURL().getHost()
                                + answer.getRedirectURLString(), new CookieManager(),
                                RequestObject.METHOD_GET, "", i < (requestInfo.length - 1));

                    }
                    // Forward the Cookies
                    if (requestInfo[i + 1] != null)
                        requestInfo[i + 1].setCookieManager(answer
                                .getCookieManager());
                } else {
                    Log.i(LOG_TAG, "Zu viele Redirects");
                }
                publishProgress(new Integer[]{i, requestInfo.length});

            }

            return answer;
        }

        protected void onPostExecute(AnswerObject result) {

            dialog.setMessage(getResources().getString(R.string.ui_calc));
            // Evaluate result
            ErrorReporter errorReporter = ACRA.getErrorReporter();
            //HTML Daten übergeben, falls es zu einem Fehler kommt
            errorReporter.putCustomData("html", result.getHTML());
            String User = "";
            // Parse result
            Document doc = Jsoup.parse(result.getHTML());
            Element UserSpan = doc.select("span#loginDataName").first();
            // If the maintenance message shows, div#zentrale_spalte exists in
            // the HTML-File
            Elements zentraleSpalte = doc.select("div#zentrale_spalte");
            if (zentraleSpalte != null && zentraleSpalte.first() != null) {
                dialog.dismiss();
                Toast wrongLoginNotif = Toast
                        .makeText(
                                TuCanMobileActivity.this,
                                "TuCan ist in Wartung und deswegen nicht erreichbar...",
                                Toast.LENGTH_LONG);
                wrongLoginNotif.show();
            } else {
                Elements contentSpacer = doc.select("div#contentSpacer_IE");

                if (contentSpacer == null) {
                    dialog.dismiss();
                    Toast error = Toast.makeText(TuCanMobileActivity.this,
                            "Fehler bei der Anmeldung", Toast.LENGTH_LONG);
                    error.show();
                    errorReporter.handleSilentException(new Exception("Fehler bei der Anmeldung"));
                } else if (UserSpan == null) {

                    // If the UserSpan is null, the Login failed
                    dialog.dismiss();
                    Toast wrongLoginNotif = Toast.makeText(
                            TuCanMobileActivity.this, "Login fehlerhaft",
                            Toast.LENGTH_LONG);
                    wrongLoginNotif.show();
                } else {
                    // Login worked
                    try {
                        dialog.dismiss();
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(TuCanMobileActivity.this,
                                "Ich mag es nicht, wenn du mich drehst!",
                                Toast.LENGTH_SHORT).show();
                    }

                    // Get SessionArgument for other purposes in other
                    // activities
                    String lcURLString = result.getLastCalledURL();
                    try {
                        URL lcURL = new URL(lcURLString);
                        SessionArgument = lcURL.getQuery().split("ARGUMENTS=")[1]
                                .split(",")[0];
                    } catch (MalformedURLException e) {
                        // Send Bugreport
                        errorReporter.handleSilentException(e);
                    }
                    // Redeem username
                    User = Jsoup.parse(UserSpan.html().split(":")[1]).text();
                    CheckBox remember = (CheckBox) findViewById(R.id.checkBox1);
                    if (remember.isChecked()) {

                        AuthenticationManager.getInstance().updateAccount(
                                usrnameField.getText().toString(),
                                pwdField.getText().toString(),
                                result.getCookieManager().getCookieHTTPString(TucanMobile.TUCAN_HOST),
                                SessionArgument
                        );
                    }
                    // Start MainMenu
                    final Intent i = new Intent(TuCanMobileActivity.this,
                            MainMenu.class);
                    i.putExtra(
                            TucanMobile.EXTRA_COOKIE,
                            result.getCookieManager().getCookieHTTPString(
                                    "www.tucan.tu-darmstadt.de"));
                    if (result.getHTML().length() < 200000) {

                        i.putExtra("source", result.getHTML());

                    } else {
                        Log.i(LOG_TAG, "No source mode");
                        i.putExtra("noSource", true);
                    }
                    i.putExtra(TucanMobile.EXTRA_USERNAME, User);
                    i.putExtra(TucanMobile.EXTRA_URL, result.getLastCalledURL());
                    startActivity(i);

                }
            }

        }
    }

    public void onRequestisAnswered() {

    }

    /**
     * Is Called, when the Request of the SimpleSecureBrowser has finished This
     * happens, when the Fast-Login method ended
     */
    public void onPostExecute(AnswerObject result) {
        // Parse HTML
        Document doc = Jsoup.parse(result.getHTML());
        try {
            // Get username and check it. If it exists, start MainMenu
            final String[] userSpan = doc.select("span#loginDataName").text()
                    .split(":");
            if (userSpan.length > 1) {
                String UserName = userSpan[1];
                if (!UserName.equals("")) {
                    final Intent i = new Intent(TuCanMobileActivity.this,
                            MainMenu.class);
                    i.putExtra(
                            TucanMobile.EXTRA_COOKIE,
                            result.getCookieManager().getCookieHTTPString(
                                    "www.tucan.tu-darmstadt.de"));
                    i.putExtra("source", result.getHTML());
                    i.putExtra(TucanMobile.EXTRA_URL, result.getLastCalledURL());
                    startActivity(i);
                } else {
                    onClickSendLogin(null);
                }
            } else {

                onClickSendLogin(null);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.i(LOG_TAG, "Fehler: " + e.getMessage());

            onClickSendLogin(null);
        } catch (Exception e) {
            ACRA.getErrorReporter().handleSilentException(e);
            Log.i(LOG_TAG, "Fehler: " + e.getMessage());

            onClickSendLogin(null);
        }

    }

    @Override
    public ConfigurationChangeStorage saveConfiguration() {
        return null;
    }

    @Override
    public void retainConfiguration(ConfigurationChangeStorage conf) {
    }
}
