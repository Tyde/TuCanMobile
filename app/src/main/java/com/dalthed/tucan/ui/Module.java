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

package com.dalthed.tucan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

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
import com.dalthed.tucan.scraper.ModuleScraper;
import com.dalthed.tucan.util.ConfigurationChangeStorage;

public class Module extends SimpleWebListActivity {
	private CookieManager localCookieManager;
	
	private String URLStringtoCall;
	private ModuleScraper scrape;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.module);
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		URLStringtoCall = getIntent().getExtras().getString("URL");
		localCookieManager = new CookieManager();
		if (CookieHTTPString != null) {

			localCookieManager.generateManagerfromHTTPString(TucanMobile.TUCAN_HOST,
					CookieHTTPString);
		} else {

		}
		if (!restoreResultBrowser()) {
			if (URLStringtoCall.equals("HTML")) {
				String HTML = getIntent().getExtras().getString("HTML");
				AnswerObject result = new AnswerObject(HTML, "", localCookieManager, "");
				onPostExecute(result);
			} else {
				callResultBrowser = new SimpleSecureBrowser(this);
				RequestObject thisRequest = new RequestObject(URLStringtoCall, localCookieManager,
						RequestObject.METHOD_GET, "");

				callResultBrowser.execute(thisRequest);
			}
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (scrape != null) {
			Intent StartSingleEventIntent = new Intent(Module.this, FragmentSingleEvent.class);
			StartSingleEventIntent.putExtra(TucanMobile.EXTRA_URL, TucanMobile.TUCAN_PROT
					+ TucanMobile.TUCAN_HOST + scrape.eventLinks.get(position));
			StartSingleEventIntent.putExtra(TucanMobile.EXTRA_COOKIE,
					localCookieManager.getCookieHTTPString(TucanMobile.TUCAN_HOST));

			startActivity(StartSingleEventIntent);
		}
	}

	public void onPostExecute(AnswerObject result) {
		scrape = new ModuleScraper(this, result);

		TextView titleTextView = (TextView) findViewById(R.id.module_title);

		try {
			setListAdapter(scrape.scrapeAdapter(0));
			titleTextView.setText(scrape.title);
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
		TextView titleTextView = (TextView) findViewById(R.id.module_title);
		setListAdapter(conf.adapters.get(0));
		BasicScraper retainedScraper = conf.getScraper(0, this);
		if (retainedScraper != null && retainedScraper instanceof ModuleScraper) {
			scrape = (ModuleScraper) retainedScraper;
			titleTextView.setText(scrape.title);   //title was getting hidden after rotating the device.
		}
	}

}
