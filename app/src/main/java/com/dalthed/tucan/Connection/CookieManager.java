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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Iterator;


import android.util.Log;

/**
 * Der CookieManager speichert empfangene Cookies ab und gibt sie auch wieder
 * aus
 * 
 * @author Daniel Thiem
 * 
 */
public class CookieManager {

	private Map<String, Map<String, String>> Cookies;
	private static final String LOG_TAG = "TuCanMobile";

	/**
	 * Der CookieManager speichert empfangene Cookies ab und gibt sie auch
	 * wieder aus <br>
	 * <br>
	 * Erzeugt einen leeren {@link CookieManager}
	 */
	public CookieManager() {
		Cookies = new HashMap<String, Map<String, String>>();
	}

	/**
	 * fügt ein neues Cookie hinzu
	 * 
	 * @param domain
	 *            Die Domain, auf welche der Cookie registriert werden soll
	 * @param name
	 *            Cookie-Name
	 * @param value
	 *            Cookie-Wert
	 */
	public void inputCookie(String domain, String name, String value) {
		if (Cookies.get(domain) == null) {
			Map<String, String> DomainMap = new HashMap<String, String>();
			Cookies.put(domain, DomainMap);
			Log.i(LOG_TAG, "Domain in Storage registriert: " + domain);
		}
		Map<String, String> DomainMap = Cookies.get(domain);
		DomainMap.put(name, value);
		Log.i(LOG_TAG, "Cookie in Storage (" + domain + ") aufgenommen: " + name + " = " + value);
	}

	/**
	 * Prüft ob die angegeben Domain schon Cookies gespeichert hat
	 * 
	 * @param domain
	 *            die zu prüfende Domain
	 * @return <code>true</code>, wenn schon ein Cookie unter dieser Domain
	 *         eingespeichert wurde, <code>false</code> anderenfalls
	 */
	public boolean domain_exists(String domain) {
		if (Cookies.get(domain) == null)
			return false;
		else
			return true;
	}

	/**
	 * Gibt die unter der angegebenen URL gespeicherten Cookies als zum Header passenden HTTP-String an.
	 * @param domain Die Domain, von welcher die Cookies abgefragt werden sollen
	 * @return HTTP-Header-Cookie-String
	 */
	public String getCookieHTTPString(String domain) {
		if (!Cookies.containsKey(domain))
			return null;

		String[] HTTPString = new String[Cookies.get(domain).size()];
		Iterator<Entry<String,String>> it = Cookies.get(domain).entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			Entry<String,String> pairs = it.next();
			HTTPString[i] = pairs.getKey() + "=" + pairs.getValue();
			i++;
		}
		if (HTTPString.length == 0)
			return "";

		String glue = ";";
		StringBuilder sb = new StringBuilder();
		for (String s : HTTPString) {
			sb.append(s).append(glue);
		}
		return sb.substring(0, sb.length() - glue.length());

	}
	/**
	 * Speichert die Cookies aus einem HTTP-HEADER-COOKIE String ab
	 * @param host Domain auf welche die Cookies gespeichert werden sollen
	 * @param HTTPString HTTP-Header-Cookie String
	 */
	public void generateManagerfromHTTPString(String host, String HTTPString) {
		if (HTTPString != null) {
			String[] multipleCookies = HTTPString.split(";\\s*");
			for (String ccy : multipleCookies) {
				String[] eachVal = ccy.split("=");
				if (eachVal.length == 2)
					inputCookie(host, eachVal[0], eachVal[1]);
				else
					inputCookie(host, eachVal[0], null);
			}
		}
	}
}
