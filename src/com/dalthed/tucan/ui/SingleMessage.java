package com.dalthed.tucan.ui;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;
import com.dalthed.tucan.R;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;
import com.dalthed.tucan.scraper.SingleMessageScraper;
import com.dalthed.tucan.util.ConfigurationChangeStorage;

public class SingleMessage extends SimpleWebListActivity {

	private CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
	private String URLStringtoCall;
	private SingleMessageScraper scrape;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState, true, 4);
		setContentView(R.layout.singlemessage);
		BugSenseHandler.setup(this, "ed5c1682");
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		URLStringtoCall = getIntent().getExtras().getString("URL");
		URL URLtoCall;
		if (!restoreResultBrowser()) {
			try {
				URLtoCall = new URL(URLStringtoCall);
				localCookieManager = new CookieManager();
				localCookieManager.generateManagerfromHTTPString(URLtoCall.getHost(),
						CookieHTTPString);
				callResultBrowser = new SimpleSecureBrowser(this);
				RequestObject thisRequest = new RequestObject(URLStringtoCall, localCookieManager,
						RequestObject.METHOD_GET, "");

				callResultBrowser.execute(thisRequest);
			} catch (MalformedURLException e) {
				Log.e(LOG_TAG, e.getMessage());
			}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.singlemessage);
	}

	public void onPostExecute(AnswerObject result) {
		scrape = new SingleMessageScraper(this, result);

		try {
			setListAdapter(scrape.scrapeAdapter(0));

		} catch (LostSessionException e) {
			e.printStackTrace();
		} catch (TucanDownException e) {
			e.printStackTrace();
		}

	}

	@Override
	public ConfigurationChangeStorage saveConfiguration() {
		ConfigurationChangeStorage cStore = new ConfigurationChangeStorage();
		cStore.addScraper(scrape);
		cStore.adapters.add(getListAdapter());
		return cStore;
	}

	@Override
	public void retainConfiguration(ConfigurationChangeStorage conf) {
		scrape = (SingleMessageScraper) conf.getScraper(0, this);
		setListAdapter(conf.adapters.get(0));
	}

}
