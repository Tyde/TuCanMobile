package com.dalthed.tucan.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.content.res.Configuration;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.bugsense.trace.BugSenseHandler;
import com.dalthed.tucan.R;
import com.dalthed.tucan.TuCanMobileActivity;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;

import com.dalthed.tucan.Connection.BrowserAnswerReciever;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.adapters.MergedAdapter;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.scraper.VVEventsScraper;
import com.dalthed.tucan.scraper.VVScraper;

public class VV extends SimpleWebListActivity {

	CookieManager localCookieManager;
	String UserName = "";

	String myHTML;

	private static final String LOG_TAG = "TuCanMobile";
	private VVScraper scrape;
	private VVEventsScraper evScrape;
	private ListAdapter categoryAdapter;
	private ListAdapter eventAdapter;
	private ArrayList<String> previousURLs = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState, true, 0);
		setContentView(R.layout.vv);
		BugSenseHandler.setup(this, "ed5c1682");
		// Webhandling Start
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		String URLStringtoCall = getIntent().getExtras().getString("URL");
		UserName = getIntent().getExtras().getString("UserName");
		URL URLtoCall;
		try {
			URLtoCall = new URL(URLStringtoCall);
			localCookieManager = new CookieManager();
			localCookieManager.generateManagerfromHTTPString(URLtoCall.getHost(), CookieHTTPString);
			callResultBrowser = new SimpleSecureBrowser(this);
			RequestObject thisRequest = new RequestObject(URLStringtoCall, localCookieManager,
					RequestObject.METHOD_GET, "");

			callResultBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		// Webhandling End

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Webhandling Start

		if (scrape != null && !scrape.hasBothCategoryAndEvents) {
			previousURLs.add(scrape.getlastCalledURL());
			scrape.onItemClick(l, v, position, id);
		} else if (scrape != null && evScrape != null && scrape.hasBothCategoryAndEvents) {
			if (position < categoryAdapter.getCount()) {
				previousURLs.add(scrape.getlastCalledURL());
				scrape.onItemClick(l, v, position, id);
			} else {
				evScrape.onItemClick(l, v, position, id);
			}

		}

		// Webhandling End
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.vv);
	}

	public void callsetListAdapter(ArrayList<String> Elements) {

	}

	@Override
	public void onBackPressed() {
		if (previousURLs != null && previousURLs.size() > 0) {
			SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(this);
			RequestObject thisRequest = new RequestObject(
					previousURLs.get(previousURLs.size() - 1), localCookieManager,
					RequestObject.METHOD_GET, "");
			previousURLs.remove(previousURLs.size() - 1);

			callOverviewBrowser.execute(thisRequest);
			return;
		}
		super.onBackPressed();
	}

	public void onPostExecute(AnswerObject result) {

		scrape = new VVScraper(this, result, UserName);
		evScrape = null;
		try {
			ListAdapter adapter = scrape.scrapeAdapter(0);

			if (adapter != null) {
				setListAdapter(scrape.scrapeAdapter(0));
			} else if (scrape.hasBothCategoryAndEvents) {
				evScrape = new VVEventsScraper(this, result);
				categoryAdapter = scrape.scrapeAdapter(1);
				eventAdapter = evScrape.scrapeAdapter(0);

				MergedAdapter mergedAdapter = new MergedAdapter(categoryAdapter, eventAdapter);
				setListAdapter(mergedAdapter);
			}

		} catch (LostSessionException e) {
			// Im falle einer verlorenen Session -> zurück zum login
			Intent BackToLoginIntent = new Intent(this, TuCanMobileActivity.class);
			BackToLoginIntent.putExtra("lostSession", true);
			startActivity(BackToLoginIntent);
		}
	}

}
