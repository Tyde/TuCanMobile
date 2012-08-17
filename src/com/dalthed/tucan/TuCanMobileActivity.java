package com.dalthed.tucan;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.acra.ErrorReporter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.BrowseMethods;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.preferences.MainPreferences;
import com.dalthed.tucan.ui.MainMenu;
import com.dalthed.tucan.ui.ProgressBarDialogFactory;
import com.dalthed.tucan.ui.SimpleWebActivity;

public class TuCanMobileActivity extends SimpleWebActivity {
	/** Called when the activity is first created. */
	// private HTTPSbrowser mBrowserService;
	private static final String LOG_TAG = "TuCanMobile";
	private EditText usrnameField;
	private EditText pwdField;
	String SessionArgument = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if (TucanMobile.DEBUG == false) {
			BugSenseHandler.setup(this, "ed5c1682");
		}
		// this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Get saved login information
		final SharedPreferences altPrefs = getSharedPreferences("LOGIN", MODE_PRIVATE);
		String alttuid = altPrefs.getString("tuid", "");
		String altpw = altPrefs.getString("pw", "");

		String tuid = "";
		String pw = "";

		if (!alttuid.equals("")) {
			tuid = alttuid;
			pw = altpw;

		}
		// Insert saved login information into EditTexts
		usrnameField = (EditText) findViewById(R.id.login_usrname);
		pwdField = (EditText) findViewById(R.id.login_pw);

		usrnameField.setText(tuid);
		pwdField.setText(pw);

		// Get old Cookie and Session from Preferences to try using the site
		// without login
		String settCookie = altPrefs.getString("Cookie", null);
		String settArg = altPrefs.getString("Session", null);
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
				&& failedSession != true && !loggedout) {

			// Check if the Login credentials should be safed
			CheckBox remember = (CheckBox) findViewById(R.id.checkBox1);
			remember.setChecked(true);
			// Generate Cookiemanager from string
			CookieManager localCookieManager = new CookieManager();
			localCookieManager.generateManagerfromHTTPString(TucanMobile.TUCAN_HOST, settCookie);
			// Start connection
			SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(this);
			RequestObject thisRequest = new RequestObject(
					"https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi?APPNAME=CampusNet&PRGNAME=MLSSTART&ARGUMENTS="
							+ settArg + ",", localCookieManager, RequestObject.METHOD_GET, "");

			callOverviewBrowser.execute(thisRequest);

		} else if (tuid != "" && pw != "") {
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
	 * @param sfNormal
	 *            Button-View
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
					+ "&APPNAME=CampusNet&PRGNAME=LOGINCHECK&ARGUMENTS=clino%2Cusrname%2Cpass%2Cmenuno%2Cpersno%2Cbrowser%2Cplatform&clino=000000000000001&menuno=000344&persno=00000000&browser=&platform=";
			Log.i(LOG_TAG, postdata);
			// AnmeldeRequest Senden
			thisRequest[1] = new RequestObject("https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi",
					RequestObject.METHOD_POST, postdata);
			// Restliche Requests werden aus der Antwort ausgelesen..

			// Requests abscicken
			newBrowser.execute(thisRequest);
		} catch (Exception e) {
			Log.e(LOG_TAG, "FEHLER: " + e.getMessage());
		}
	}

	public class HTTPSBrowser extends AsyncTask<RequestObject, Integer, AnswerObject> {
		ProgressDialog dialog;

		protected void onPreExecute() {
			// ProgressDialog anfertigen und anzeigen
			dialog = ProgressBarDialogFactory.createProgressDialog(TuCanMobileActivity.this,
					getResources().getString(R.string.ui_login));
			dialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// Fortschritt Berechnen und anzeigen
			int Progress = (int) ((((double) values[0] + 1) / (double) values[1]) * 100);
			Log.i(LOG_TAG, values[0] + " von " + values[1] + " ergibt: " + Progress);
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
					answer = Browser.browse(requestInfo[i]);
					Log.i(LOG_TAG, "Redirect:" + answer.getRedirectURLString());
				} else {
					break;
				}
				// Letztes Objekt
				if (i < requestInfo.length - 1) {
					// Check for HTTP-Redirect
					if (answer.getRedirectURLString() != "" && requestInfo[i + 1] == null) {
						Log.i(LOG_TAG,
								"Insert new Redirect URL in RequestObject:"
										+ answer.getRedirectURLString());
						// Add HTTP-Redirect into requestInfo-List
						requestInfo[i + 1] = new RequestObject("https://"
								+ requestInfo[i].getmyURL().getHost()
								+ answer.getRedirectURLString(), RequestObject.METHOD_GET, "");

					}
					// Forward the Cookies
					if (requestInfo[i + 1] != null)
						requestInfo[i + 1].setCookieManager(answer.getCookieManager());
				} else {
					Log.i(LOG_TAG, "Zu viele Redirects");
				}
				publishProgress(new Integer[] { i, requestInfo.length });

			}

			return answer;
		}

		protected void onPostExecute(AnswerObject result) {

			dialog.setMessage(getResources().getString(R.string.ui_calc));
			// Evaluate result

			String User = "";
			// Parse result
			Document doc = Jsoup.parse(result.getHTML());
			Element UserSpan = doc.select("span#loginDataName").first();
			// If the maintenance message shows, div#zentrale_spalte exists in
			// the HTML-File
			if (doc.select("div#zentrale_spalte").first() != null) {
				dialog.dismiss();
				Toast wrongLoginNotif = Toast.makeText(TuCanMobileActivity.this,
						"TuCan ist in Wartung und deswegen nicht erreichbar...", Toast.LENGTH_LONG);
				wrongLoginNotif.show();
			} else if (UserSpan == null) {
				// If the UserSpan is null, the Login failed
				dialog.dismiss();
				Toast wrongLoginNotif = Toast.makeText(TuCanMobileActivity.this,
						"Login fehlerhaft", Toast.LENGTH_LONG);
				wrongLoginNotif.show();
			} else {
				// Login worked
				dialog.dismiss();
				// Get SessionArgument for other purposes in other activities
				String lcURLString = result.getLastCalledURL();
				try {
					URL lcURL = new URL(lcURLString);
					SessionArgument = lcURL.getQuery().split("ARGUMENTS=")[1].split(",")[0];
				} catch (MalformedURLException e) {
					// Send Bugreport
					ErrorReporter.getInstance().handleSilentException(e);
				}
				// Redeem username
				User = Jsoup.parse(UserSpan.html().split(":")[1]).text();
				CheckBox remember = (CheckBox) findViewById(R.id.checkBox1);
				if (remember.isChecked()) {

					// Delete sensitive Data in old Preferences
					final SharedPreferences einstellungen = MainPreferences
							.getSettings(TuCanMobileActivity.this);
					SharedPreferences.Editor editor = einstellungen.edit();
					editor.putString("tuid", "");
					editor.putString("pw", "");
					editor.putString("Cookie", "");
					editor.putString("Session", "");
					editor.commit();

					// Save Data in new Preferences
					final SharedPreferences altPrefs = getSharedPreferences("LOGIN", MODE_PRIVATE);
					SharedPreferences.Editor edit = altPrefs.edit();
					edit.putString("tuid", usrnameField.getText().toString());
					edit.putString("pw", pwdField.getText().toString());
					edit.putString("Cookie",
							result.getCookieManager().getCookieHTTPString(TucanMobile.TUCAN_HOST));
					edit.putString("Session", SessionArgument);
					edit.commit();
				}
				// Start MainMenu
				final Intent i = new Intent(TuCanMobileActivity.this, MainMenu.class);
				i.putExtra(TucanMobile.EXTRA_COOKIE,
						result.getCookieManager().getCookieHTTPString("www.tucan.tu-darmstadt.de"));
				i.putExtra("source", result.getHTML());
				i.putExtra(TucanMobile.EXTRA_USERNAME, User);
				i.putExtra(TucanMobile.EXTRA_URL, result.getLastCalledURL());
				startActivity(i);
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
			final String[] userSpan = doc.select("span#loginDataName").text().split(":");
			if (userSpan.length > 1) {
				String UserName = userSpan[1];
				if (!UserName.equals("")) {
					final Intent i = new Intent(TuCanMobileActivity.this, MainMenu.class);
					i.putExtra(TucanMobile.EXTRA_COOKIE, result.getCookieManager()
							.getCookieHTTPString("www.tucan.tu-darmstadt.de"));
					i.putExtra("source", result.getHTML());
					i.putExtra(TucanMobile.EXTRA_URL, result.getLastCalledURL());
					startActivity(i);
				} else {
					Toast.makeText(this, "Schneller Login fehlgeschlagen", Toast.LENGTH_LONG)
							.show();
					onClickSendLogin(null);
				}
			} else {
				Toast.makeText(this, "Schneller Login fehlgeschlagen", Toast.LENGTH_LONG).show();
				onClickSendLogin(null);
			}

		} catch (Exception e) {
			ErrorReporter.getInstance().handleSilentException(e);
			Log.i(LOG_TAG, "Fehler: " + e.getMessage());
			Toast.makeText(this, "Schneller Login fehlgeschlagen", Toast.LENGTH_LONG).show();
			onClickSendLogin(null);
		}

	}
}