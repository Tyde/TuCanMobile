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
import android.renderscript.Script;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import com.dalthed.tucan.scraper.MessagesScraper;
import com.dalthed.tucan.scraper.RegisterExamsScraper;
import com.dalthed.tucan.util.ConfigurationChangeStorage;

public class Messages extends SimpleWebListActivity {
	private String UserName;
	
	private static final String LOG_TAG = "TuCanMobile";
	private String URLStringtoCall;
	private MessagesScraper scrape;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, true, 4);
		setContentView(R.layout.messages);
		BugSenseHandler.setup(this, "ed5c1682");
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		URLStringtoCall = getIntent().getExtras().getString("URL");
		UserName = getIntent().getExtras().getString("UserName");
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
		setContentView(R.layout.messages);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (scrape != null && scrape.messageLink.size() > position) {
			Intent MessageStartIntent = new Intent(Messages.this, SingleMessage.class);
			MessageStartIntent.putExtra("URL", TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
					+ scrape.messageLink.get(position));
			MessageStartIntent.putExtra("User", UserName);
			MessageStartIntent.putExtra("Cookie",
					scrape.getCookieManager().getCookieHTTPString(TucanMobile.TUCAN_HOST));
			startActivity(MessageStartIntent);
		}
	}

	public void onPostExecute(AnswerObject result) {
		if (scrape == null) {
			scrape = new MessagesScraper(this, result);
		} else {
			scrape.setNewAnswer(result);
		}
		try {
			setListAdapter(scrape.scrapeAdapter(0));
		} catch (LostSessionException e) {
			Intent BackToLoginIntent = new Intent(this, TuCanMobileActivity.class);
			BackToLoginIntent.putExtra("lostSession", true);
			startActivity(BackToLoginIntent);
		} catch (TucanDownException e) {
			TucanMobile.alertOnTucanDown(this, e.getMessage());
		}

	}

	@Override
	public ConfigurationChangeStorage saveConfiguration() {
		ConfigurationChangeStorage cStore = new ConfigurationChangeStorage();
		cStore.adapters.add(getListAdapter());
		cStore.addScraper(scrape);
		return cStore;
	}

	@Override
	public void retainConfiguration(ConfigurationChangeStorage conf) {
		setListAdapter(conf.adapters.get(0));
		BasicScraper retainedScraper = conf.getScraper(0, this);
		if (retainedScraper instanceof MessagesScraper) {
			scrape = (MessagesScraper) retainedScraper;
		}
	}

}
