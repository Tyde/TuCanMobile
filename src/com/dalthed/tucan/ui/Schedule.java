package com.dalthed.tucan.ui;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.bugsense.trace.BugSenseHandler;
import com.dalthed.tucan.R;
import com.dalthed.tucan.TuCanMobileActivity;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;
import com.dalthed.tucan.scraper.BasicScraper;
import com.dalthed.tucan.scraper.ScheduleScraper;
import com.dalthed.tucan.util.ConfigurationChangeStorage;

public class Schedule extends SimpleWebListActivity {

	private static final String LOG_TAG = "TuCanMobile";
	private String URLStringtoCall;
	ScheduleScraper scrape = null;
	private ListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState, true, 1);
		setContentView(R.layout.schedule);
		BugSenseHandler.setup(this, "ed5c1682");

		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		URLStringtoCall = getIntent().getExtras().getString("URL");
		URL URLtoCall;
		if (!restoreResultBrowser()) {
			try {
				URLtoCall = new URL(URLStringtoCall);
				CookieManager localCookieManager = new CookieManager();
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
		setContentView(R.layout.schedule);
	}

	public void onPostExecute(AnswerObject result) {
		if (scrape == null) {
			scrape = new ScheduleScraper(this, result);
		} else {
			scrape.setNewAnswer(result);
		}
		try {
			adapter = scrape.scrapeAdapter(0);
			if (adapter != null) {
				setListAdapter(adapter);
			}
		} catch (LostSessionException e) {
			Intent BackToLoginIntent = new Intent(this, TuCanMobileActivity.class);
			BackToLoginIntent.putExtra("lostSession", true);
			startActivity(BackToLoginIntent);
		} catch (TucanDownException e) {
			TucanMobile.alertOnTucanDown(this, e.getMessage());
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (scrape != null) {
			Intent singleEventIntent = new Intent(Schedule.this, FragmentSingleEvent.class);
			singleEventIntent.putExtra("PREPLink", true);
			singleEventIntent.putExtra("URL", TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
					+ scrape.eventLink.get(position));
			singleEventIntent.putExtra("Cookie",
					scrape.getCookieManager().getCookieHTTPString(TucanMobile.TUCAN_HOST));
			startActivity(singleEventIntent);
		}
	}

	@Override
	public ConfigurationChangeStorage saveConfiguration() {
		ConfigurationChangeStorage cStore = new ConfigurationChangeStorage();
		cStore.adapters.add(adapter);
		cStore.addScraper(scrape);
		return cStore;
	}

	@Override
	public void retainConfiguration(ConfigurationChangeStorage conf) {
		BasicScraper retainedScraper = conf.getScraper(0, this);
		if (retainedScraper instanceof ScheduleScraper) {
			scrape = (ScheduleScraper) retainedScraper;
		}
		if (conf.adapters.get(0) != null) {
			setListAdapter(conf.adapters.get(0));
		}
	}

}
