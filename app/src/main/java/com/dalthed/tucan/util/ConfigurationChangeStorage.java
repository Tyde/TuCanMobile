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

package com.dalthed.tucan.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;

import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.connection.BrowserAnswerReciever;
import com.dalthed.tucan.connection.SimpleSecureBrowser;
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
					singleBrowser.dialog = null;
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
