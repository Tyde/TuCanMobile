package com.dalthed.tucan.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.exceptions.LostSessionException;

import android.content.Context;
import android.widget.Adapter;

public abstract class BasicScraper {
	static final String LOG_TAG = "TuCanMobile";
	protected Document doc;
	protected Context context;
	
	public BasicScraper(Context context,AnswerObject result) {
		this.context = context;
		doc= Jsoup.parse(result.getHTML());
	}
	
	abstract public Adapter scrapeAdapter(int mode) throws LostSessionException;
	

}
