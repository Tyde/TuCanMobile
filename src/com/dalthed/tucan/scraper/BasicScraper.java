package com.dalthed.tucan.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.dalthed.tucan.TuCanMobileActivity;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;
import com.dalthed.tucan.ui.SimpleWebListActivity;

import android.content.Context;
import android.content.Intent;
import android.widget.Adapter;
import android.widget.ListAdapter;

public abstract class BasicScraper {
	static final String LOG_TAG = "TuCanMobile";
	protected Document doc;
	protected Context context;
	protected String lastCalledUrl;
	
	/**
	 * Konstruktor bekommt Activity-Context und ein Answerobject zur verarbeitung
	 * @param context Activity-Context
	 * @param result AnswerObject der Anfarge
	 */
	public BasicScraper(Context context,AnswerObject result) {
		this.context = context;
		this.lastCalledUrl = result.getLastCalledURL();
		doc= Jsoup.parse(result.getHTML());
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
		if (doc.select("span.notLoggedText").text().length() > 0) {
			throw new LostSessionException();
		} else if(doc.select("div#pageContainer")==null){
			throw new TucanDownException(doc.text());
		} else {
			return true;
		}
	}
	

}
