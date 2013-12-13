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

package com.dalthed.tucan.scraper;

import org.acra.ACRA;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Context;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.dalthed.tucan.R;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;
import com.dalthed.tucan.ui.SimpleWebListActivity;

public abstract class BasicScraper {
	static final String LOG_TAG = "TuCanMobile";
	protected Document doc;
	protected Context context;
	protected String lastCalledUrl;
	protected CookieManager localCookieManager;
	
	/**
	 * Konstruktor bekommt Activity-Context und ein Answerobject zur verarbeitung
	 * @param context Activity-Context
	 * @param result AnswerObject der Anfarge
	 */
	public BasicScraper(Context context,AnswerObject result) {
		this.context = context;
		this.lastCalledUrl = result.getLastCalledURL();
		this.localCookieManager = result.getCookieManager();
		doc= Jsoup.parse(result.getHTML());
	}
	/**
	 * Gibt den genutzten CookieManager zurück
	 * @return genutzter CookieManager
	 */
	public CookieManager getCookieManager() {
		return localCookieManager;
	}
	/**
	 * Bei einem Configurationchange wird die Activity neu gestartet und somit ist 
	 * ein neuer Context von nten
	 * @param context Context der Activity
	 */
	public void renewContext(Context context) {
		this.context = context;
	}
	/**
	 * Hin und wieder muss eine neue Antwort des Browsers übergeben werden
	 * @param result Antwort des Browsers
	 */
	public void setNewAnswer (AnswerObject result){
		this.lastCalledUrl = result.getLastCalledURL();
		doc= Jsoup.parse(result.getHTML());
	}
	/**
	 * Gibt entsprechenden ListAdapter nach dem auswerten der Daten zurück
	 * @param mode evtl genutzt, falls eine Activity mehrere Ansichten unterstützt
	 * @return der Adapter für die Liste
	 * @throws LostSessionException
	 */
	abstract public ListAdapter scrapeAdapter(int mode) throws LostSessionException,TucanDownException;
	/**
	 * Prüft ob die Session schon abgelaufen ist und wirft eine Exception falls dies passiert
	 * @return true, wenn Session noch aktiv
	 * @throws LostSessionException
	 */
	protected Boolean checkForLostSeesion() throws LostSessionException,TucanDownException {
		
		SimpleWebListActivity.sendHTMLatBug(doc.html());
		
		if (doc.select("span.notLoggedText").text().length() > 0 || doc.select("form#cn_loginForm").size()>0) {
			throw new LostSessionException();
		} else if(doc.select("div#pageContainer").first()==null || doc.select("div#pageContainer").text().equals("")){
			throw new TucanDownException(doc.text());
		} else {
			return true;
		}
	}
	
	protected void reportUnexpectedBehaviour(Exception e) {
		Toast.makeText(context, context.getResources().getString(R.string.site_opt_unknown), Toast.LENGTH_SHORT).show();
		
		ACRA.getErrorReporter().handleSilentException(e);
	}
	

}
