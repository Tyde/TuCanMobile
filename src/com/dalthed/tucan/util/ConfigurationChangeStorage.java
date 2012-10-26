package com.dalthed.tucan.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;

import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.BrowserAnswerReciever;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.scraper.BasicScraper;

public class ConfigurationChangeStorage {

	private ArrayList<BasicScraper> scrapers;
	private ArrayList<SimpleSecureBrowser> browser;
	public ArrayList<ListAdapter> adapters;

	public int mode;

	public ConfigurationChangeStorage() {
		scrapers = new ArrayList<BasicScraper>();
		adapters = new ArrayList<ListAdapter>();
		browser = new ArrayList<SimpleSecureBrowser>();
	}

	/**
	 * Gibt den Scraper mit angepassten Context zurück
	 * 
	 * @param index
	 * @param context
	 * @return
	 */
	public BasicScraper getScraper(int index, Context context) {
		if (scrapers.size() > index) {
			BasicScraper returnScraper = scrapers.get(index);
			if (returnScraper != null) {

				returnScraper.renewContext(context);
				return returnScraper;
			}
		}
		return null;

	}

	public void addScraper(BasicScraper scrape) {
		scrapers.add(scrape);
	}

	public void addBrowser(List<SimpleSecureBrowser> browser) {
		if (browser != null) {
			this.browser.addAll(browser);
		}
	}

	public void addBrowser(SimpleSecureBrowser browser) {
		if (browser != null) {
			this.browser.add(browser);
		}
	}

	public void dismissDialogs() {
		if(browser!=null){
			for(SimpleSecureBrowser singleBrowser: browser) {
				if(singleBrowser.dialog!=null) {
					singleBrowser.dialog.dismiss();
				}
			}
		}
	}

	public void updateBrowser(BrowserAnswerReciever context) {
		for (SimpleSecureBrowser singleBrowser : browser) {
			singleBrowser.renewContext(context);
			singleBrowser.dialog = null;
			if (!(singleBrowser.getStatus().equals(AsyncTask.Status.FINISHED))) {
				Log.i(TucanMobile.LOG_TAG,
						"Configuration Change at unfinished Browser, show Dialog");

				singleBrowser.showDialog();

			}
		}
	}

}
