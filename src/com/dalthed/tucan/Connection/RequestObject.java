package com.dalthed.tucan.Connection;

import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

/**
 * Objekt, welches alle notwendigen Informationen f�r einen <code>HTTP</code>/
 * <code>HTTPS</code>-Request enth�lt
 * 
 * @author Tyde
 * 
 */
public class RequestObject {
	private URL RequestURL;
	private CookieManager RequestCookies;
	private String RequestMethod;
	private String RequestpostData;
	/**
	 * HTTP-Methode GET
	 */
	final public static String METHOD_GET = "GET";
	/**
	 * HTTP-Methode POST
	 */
	final public static String METHOD_POST = "POST";

	private static final String LOG_TAG = "TuCanMobile";

	/**
	 * Objekt, welches alle notwendigen Informationen f�r einen
	 * <code>HTTP</code>/<code>HTTPS</code>-Request enth�lt
	 * 
	 * @param RequestString
	 *            URL(als String), welche aufgerufen werden soll
	 * @param RequestCookiemanager
	 *            {@link CookieManager}, welcher die notwendigen Cookies
	 *            enthalten soll
	 * @param method
	 *            Entweder {@link #METHOD_GET} oder {@link #METHOD_POST}
	 * @param postdata
	 *            Daten, welche bei {@link #METHOD_POST} mitgesendert werden
	 *            k�nnen
	 * @author Daniel Thiem
	 */
	public RequestObject(String RequestString, CookieManager RequestCookiemanager, String method,
			String postdata) {
		try {
			this.RequestURL = new URL(RequestString);
			// Log.i(LOG_TAG,"Hier haste die URL:"+RequestString);
		} catch (MalformedURLException e) {

			Log.e(LOG_TAG, "Malfomed URL");
		}
		this.RequestCookies = RequestCookiemanager;
		if (method == METHOD_GET) {
			this.RequestMethod = METHOD_GET;
		} else if (method == METHOD_POST) {
			this.RequestMethod = METHOD_POST;
		} else {
			this.RequestMethod = METHOD_GET;
		}
		this.RequestpostData = postdata;
	}

	/**
	 * Objekt, welches alle notwendigen Informationen f�r einen
	 * <code>HTTP</code>/<code>HTTPS</code>-Request enth�lt. <br>
	 * <br>
	 * Dieser Konstruktor sollte nur aufgerufen werden, falls noch keine Cookies
	 * vorhanden sind.
	 * 
	 * @param RequestString
	 *            URL(als String), welche aufgerufen werden soll
	 * @param method
	 *            Entweder {@link #METHOD_GET} oder {@link #METHOD_POST}
	 * @param postdata
	 *            Daten, welche bei {@link #METHOD_POST} mitgesendert werden
	 *            k�nnen
	 * @author Daniel Thiem
	 */
	public RequestObject(String RequestString, String method, String postdata) {
		this(RequestString, new CookieManager(), method, postdata);
	}

	/**
	 * 
	 * @return aufzurufende URL
	 * @author Daniel Thiem
	 */
	public URL getmyURL() {
		return this.RequestURL;
	}

	/**
	 * 
	 * @param newManager
	 *            neuer {@link CookieManager}
	 * 
	 * @author Daniel Thiem
	 */
	public void setCookieManager(CookieManager newManager) {
		this.RequestCookies = newManager;
	}

	/**
	 * 
	 * @return {@link CookieManager}
	 * @author Daniel Thiem
	 */
	public CookieManager getCookies() {
		return this.RequestCookies;
	}

	/**
	 * 
	 * @return post Data
	 * @author Daniel Thiem
	 */
	public String getPostData() {
		return this.RequestpostData;
	}

	/**
	 * 
	 * @return genutzte Methode
	 * @author Daniel Thiem
	 */
	public String getMethod() {
		return this.RequestMethod;
	}

}
