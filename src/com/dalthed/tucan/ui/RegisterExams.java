package com.dalthed.tucan.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.dalthed.tucan.scraper.RegisterExamsScraper;
import com.dalthed.tucan.util.ConfigurationChangeStorage;

public class RegisterExams extends SimpleWebListActivity {
	private String UserName, URLStringtoCall;
	private CookieManager localCookieManager;
	
	private static final String LOG_TAG = "TuCanMobile";
	
	int mode = 0;
	private RegisterExamsScraper scrape;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exams);
		BugSenseHandler.setup(this, "ed5c1682");

		// Webhandling Start
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		URLStringtoCall = getIntent().getExtras().getString("URL");
		UserName = getIntent().getExtras().getString("UserName");
		URL URLtoCall;

		try {
			URLtoCall = new URL(URLStringtoCall);

			localCookieManager = new CookieManager();
			localCookieManager.generateManagerfromHTTPString(URLtoCall.getHost(), CookieHTTPString);
			callResultBrowser = new SimpleSecureBrowser(this);
			if (TucanMobile.DEBUG) {
				callResultBrowser.HTTPS = this.HTTPS;
			}
			RequestObject thisRequest = new RequestObject(URLStringtoCall, localCookieManager,
					RequestObject.METHOD_GET, "");

			callResultBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, e.getMessage());
		}

	}

	public void onPostExecute(AnswerObject result) {
		if (scrape == null) {
			scrape = new RegisterExamsScraper(this, result, URLStringtoCall, localCookieManager);
		} else {
			scrape.setNewAnswer(result);
		}
		try {
			switch (mode) {
			case 0:
				setListAdapter(scrape.scrapeAdapter(mode));
				break;

			case 1:
				mode = scrape.getRegisterdialog();
				break;
			case 3:
				mode = scrape.startRegistration();
				break;

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
		if (scrape.examSelection.get(position) == 2 || scrape.examSelection.get(position) == 3) {
			if (scrape.examSelection.get(position) == 2)
				mode = 1;
			else if (scrape.examSelection.get(position) == 3)
				mode = 1;
			SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(this);
			RequestObject callstatuschange = new RequestObject(TucanMobile.TUCAN_PROT
					+ TucanMobile.TUCAN_HOST + scrape.registerLink.get(position), localCookieManager,
					RequestObject.METHOD_GET, "");
			callOverviewBrowser.execute(callstatuschange);
		}
	}

	@Override
	public ConfigurationChangeStorage saveConfiguration() {
		return null;
	}

	@Override
	public void retainConfiguration(ConfigurationChangeStorage conf) {
	}

}
