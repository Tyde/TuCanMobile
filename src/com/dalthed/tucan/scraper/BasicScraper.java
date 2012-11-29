package com.dalthed.tucan.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;
import android.widget.ListAdapter;

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
	 * Gibt den genutzten CookieManager zur�ck
	 * @return genutzter CookieManager
	 */
	public CookieManager getCookieManager() {
		return localCookieManager;
	}
	/**
	 * Bei einem Configurationchange wird die Activity neu gestartet und somit ist 
	 * ein neuer Context von n�ten
	 * @param context Context der Activity
	 */
	public void renewContext(Context context) {
		this.context = context;
	}
	/**
	 * Hin und wieder muss eine neue Antwort des Browsers �bergeben werden
	 * @param result Antwort des Browsers
	 */
	public void setNewAnswer (AnswerObject result){
		this.lastCalledUrl = result.getLastCalledURL();
		doc= Jsoup.parse(result.getHTML());
	}
	/**
	 * Gibt entsprechenden ListAdapter nach dem auswerten der Daten zur�ck
	 * @param mode evtl genutzt, falls eine Activity mehrere Ansichten unterst�tzt
	 * @return der Adapter f�r die Liste
	 * @throws LostSessionException
	 */
	abstract public ListAdapter scrapeAdapter(int mode) throws LostSessionException,TucanDownException;
	/**
	 * Pr�ft ob die Session schon abgelaufen ist und wirft eine Exception falls dies passiert
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
	

}
