package com.dalthed.tucan.Connection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;

import org.acra.ErrorReporter;

import android.os.Build;
import android.util.Log;

import com.dalthed.tucan.TucanMobile;

/**
 * BrowseMethods implementiert wichtige Methoden, um Webseiten angepasst zu
 * Laden
 * 
 * @author Tyde
 * 
 */
public class BrowseMethods {
	private HttpURLConnection HTTPConnection;
	private CookieManager myCookies;
	public boolean iwantthereader = false;
	public InputStream in;
	public InputStreamReader isr;
	private static final String LOG_TAG = "TuCanMobile";
	public boolean HTTPS = true;

	/**
	 * setImportantHeaders setzt Header für den HTTP-Request
	 * 
	 * @param RequestMethod
	 *            RequestObject.METHOD_POST oder RequestObject.METHOD_GET
	 * @param domain
	 *            wichtig für CookieHandler
	 */
	private void setImportantHeaders(String RequestMethod, String domain) {
		try {
			// GET oder POST setzen
			HTTPConnection.setRequestMethod(RequestMethod);
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		// Browser-Header: Currently Chrome 22
		HTTPConnection
				.setRequestProperty(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4");
		HTTPConnection.setInstanceFollowRedirects(false);
		// Cookies setzen:
		if (myCookies.domain_exists(domain)) {
			if (TucanMobile.DEBUG) {
				Log.i(LOG_TAG, "Cookie gesetzt:" + myCookies.getCookieHTTPString(domain));
			}
			HTTPConnection.setRequestProperty("Cookie", myCookies.getCookieHTTPString(domain));
		}
	}

	/**
	 * HauptMethode der BrowseMethods: lädt die Seite und gibt sie als
	 * AnswerObject wieder zurück
	 * 
	 * @param requestInfo
	 *            RequestObject mit allen Wichtigen Informationen zum Request
	 * @return AnswerObject der Seite
	 */
	public AnswerObject browse(RequestObject requestInfo) {
		String redirectURL = "";
		String alllines = "";
		if (Integer.parseInt(Build.VERSION.SDK) < 9) {
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

				if (HTTPS == true) {
					HTTPConnection = (HttpsURLConnection) realURL.openConnection();
				} else {
					HTTPConnection = (HttpURLConnection) realURL.openConnection();
				}
				if (TucanMobile.DEBUG) {
					Log.i(LOG_TAG, "Started Connection with: " + realURL.toString());
				}
				if (RequestMethod == "POST") {
					// Output bei POST-Übertragung aktivierten
					HTTPConnection.setDoOutput(true);
				}
				// Ursprünglichen Request setzen
				setImportantHeaders(RequestMethod, realURL.getHost());

				if (RequestMethod == "POST") {
					// Post-Daten senden
					OutputStreamWriter out = new OutputStreamWriter(
							HTTPConnection.getOutputStream());
					out.write(postdata);
					out.close();
				}

				// Antwort auslesen
				in = HTTPConnection.getInputStream();
				isr = new InputStreamReader(in, "ISO8859_1");

				if (iwantthereader == false) {

					BufferedReader bin = new BufferedReader(isr, 8 * 1024);
					// Header auslesen
					for (int n = 0;; n++) {
						String headerValue = HTTPConnection.getHeaderField(n);
						String headerName = HTTPConnection.getHeaderFieldKey(n);
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
							redirectURL = getredirectURL[1];
						}
						if ("location".equalsIgnoreCase(headerName)) {
							redirectURL = headerValue;
						}

					}
					int contentlength = HTTPConnection.getContentLength();
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
				}
			}

		} catch (Exception e) {
			if (!(e instanceof UnknownHostException) && !(e instanceof SSLException)) {
				ErrorReporter.getInstance().handleSilentException(e);
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
