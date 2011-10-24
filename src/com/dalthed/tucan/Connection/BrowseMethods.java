package com.dalthed.tucan.Connection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ProtocolException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

import android.util.Log;
/**
 * BrowseMethods implementiert wichtige Methoden, um Webseiten angepasst zu Laden
 * @author Tyde
 *
 */
public class BrowseMethods {
	private HttpsURLConnection HTTPConnection;
	private CookieManager myCookies;

	private static final String LOG_TAG = "TuCanMobile";
	/**
	 * setImportantHeaders setzt Header f�r den HTTP-Request
	 * @param RequestMethod RequestObject.METHOD_POST oder RequestObject.METHOD_GET
	 * @param domain wichtig f�r CookieHandler
	 */
	private void setImportantHeaders(String RequestMethod, String domain) {
		try {
			HTTPConnection.setRequestMethod(RequestMethod);
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		HTTPConnection.setRequestProperty("User-Agent",
		 "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:7.0.1) Gecko/20100101 Firefox/7.0.1");
		HTTPConnection.setInstanceFollowRedirects(false);
		if (myCookies.domain_exists(domain)) {
			HTTPConnection.setRequestProperty("Cookie",
					myCookies.getCookieHTTPString(domain));
		}
	}
	/**
	 * HauptMethode der BrowseMethods: l�dt die Seite und gibt sie als AnswerObject wieder zur�ck
	 * @param requestInfo RequestObject mit allen Wichtigen Informationen zum Request
	 * @return AnswerObject der Seite
	 */
	public AnswerObject browse(RequestObject requestInfo) {
		String redirectURL = "";
		String alllines = "";

		try {
			URL realURL = requestInfo.getmyURL();
			myCookies = requestInfo.getCookies();
			String RequestMethod = requestInfo.getMethod();
			String postdata = requestInfo.getPostData();
			HTTPConnection = (HttpsURLConnection) realURL.openConnection();
			Log.i(LOG_TAG, "Started Connection with: " + realURL.toString());
			HTTPConnection.setDoOutput(true);

			setImportantHeaders(RequestMethod, realURL.getHost());
			if (RequestMethod == "POST") {

				OutputStreamWriter out = new OutputStreamWriter(
						HTTPConnection.getOutputStream());
				out.write(postdata);
				out.close();
			}


			GZIPInputStream in = (GZIPInputStream) HTTPConnection
					.getInputStream();
			InputStreamReader isr = new InputStreamReader(in, "ISO8859_1");
			BufferedReader bin = new BufferedReader(isr);

			for (int n = 0;; n++) {
				String headerValue = HTTPConnection.getHeaderField(n);
				String headerName = HTTPConnection.getHeaderFieldKey(n);
				if (headerValue == null && headerName == null) {
					break;
				}

				if ("Set-Cookie".equalsIgnoreCase(headerName)) {
					Log.i(LOG_TAG, "Lese Cookies aus");
					String[] multipleCookies = headerValue.split(";\\s*");
					for (String ccy : multipleCookies) {
						String[] eachVal = ccy.split("=");
						if (eachVal.length == 2)
							myCookies.inputCookie(realURL.getHost(),
									eachVal[0], eachVal[1]);
						else
							myCookies.inputCookie(realURL.getHost(),
									eachVal[0], null);
					}
				}
				if ("refresh".equalsIgnoreCase(headerName)) {
					String[] getredirectURL = headerValue.split("URL=");
					redirectURL = getredirectURL[1];
				}

			}
			int contentlength = HTTPConnection.getContentLength();
			Log.i(LOG_TAG, contentlength + "...");

			StringBuilder inputBuilder = new StringBuilder();
			String inputLine;

			while ((inputLine = bin.readLine()) != null) {
				inputBuilder.append(inputLine);
			}
			in.close();
			alllines = inputBuilder.toString();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return new AnswerObject(alllines, redirectURL, myCookies, requestInfo
				.getmyURL().toString());
	}
}
