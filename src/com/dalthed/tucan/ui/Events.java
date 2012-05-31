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
import android.os.Debug;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.dalthed.tucan.scraper.EventsScraper;

public class Events extends SimpleWebListActivity {

	private CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
	private String URLStringtoCall;
	private int mode = 0;
	private EventsScraper scrape;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState, true, 2);
		setContentView(R.layout.events);
		BugSenseHandler.setup(this, "ed5c1682");
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		URLStringtoCall = getIntent().getExtras().getString("URL");
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
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.events);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(this);
		RequestObject thisRequest;
		Log.i(LOG_TAG, "Modus:" + mode);
		if (mode == 0) {
			// Modus overview
			switch (position) {
			case 0:
				// Klick auf Module
				mode = 10;
				thisRequest = new RequestObject(TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
						+ scrape.eventLinks.get(0), localCookieManager, RequestObject.METHOD_GET, "");
				callOverviewBrowser.execute(thisRequest);
				break;
			case 1:
				// Klick auf Veranstaltungen
				mode = 1;
				thisRequest = new RequestObject(TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
						+ scrape.eventLinks.get(1), localCookieManager, RequestObject.METHOD_GET, "");
				callOverviewBrowser.execute(thisRequest);
				break;
			case 2:
				// Klick auf Anmeldung
				mode = 2;
				thisRequest = new RequestObject(TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
						+ scrape.eventLinks.get(2), localCookieManager, RequestObject.METHOD_GET, "");
				callOverviewBrowser.execute(thisRequest);
				break;
			}

		} else if (mode == 1) {
			Intent StartSingleEventIntent = new Intent(Events.this, FragmentSingleEvent.class);
			StartSingleEventIntent.putExtra(TucanMobile.EXTRA_URL, TucanMobile.TUCAN_PROT
					+ TucanMobile.TUCAN_HOST + scrape.eventLink.get(position));
			StartSingleEventIntent.putExtra(TucanMobile.EXTRA_COOKIE,
					localCookieManager.getCookieHTTPString(TucanMobile.TUCAN_HOST));
			// StartSingleEventIntent.putExtra("UserName", UserName);
			startActivity(StartSingleEventIntent);
		} else if (mode == 2) {

			thisRequest = new RequestObject(TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
					+ scrape.applyLink.get(position), localCookieManager, RequestObject.METHOD_GET, "");
			callOverviewBrowser.execute(thisRequest);
		} else if (mode == 10) {
			Intent StartModuleIntent = new Intent(Events.this, Module.class);
			StartModuleIntent.putExtra(TucanMobile.EXTRA_URL, TucanMobile.TUCAN_PROT
					+ TucanMobile.TUCAN_HOST + scrape.eventLink.get(position));
			StartModuleIntent.putExtra(TucanMobile.EXTRA_COOKIE,
					localCookieManager.getCookieHTTPString(TucanMobile.TUCAN_HOST));
			startActivity(StartModuleIntent);
		}
	}

	
	public void onPostExecute(AnswerObject result) {
		
		//Start Tracing zur geschwindigkeitsberschleunigung
		
		
		// HTML Parsen
		if(scrape==null){
			scrape = new EventsScraper(this, result);
		}
		else {
			scrape.setNewAnswer(result);
		}
		
		try {
			setListAdapter(scrape.scrapeAdapter(mode));
		} catch (LostSessionException e) {
			Intent BackToLoginIntent = new Intent(this, TuCanMobileActivity.class);
			BackToLoginIntent.putExtra("lostSession", true);
			startActivity(BackToLoginIntent);
		}
		
		//Stop tracing
		
		if(mode==0){
			
			fsh.setSubtitle(getResources().getText(R.string.events_subtitle));
		}
		else if (mode==1){
			fsh.setSubtitle(getResources().getText(R.string.events_subtitle_events));
		}
		else if (mode==2){
			fsh.setSubtitle(getResources().getText(R.string.events_subtitle_register));
		}
		else if(mode==10){
			fsh.setSubtitle(getResources().getText(R.string.events_subtitle_modules));
		}
		if(mode!=0){
			Spinner semesterSpinner = (Spinner) findViewById(R.id.exam_semester_spinner);
			semesterSpinner.setVisibility(View.VISIBLE);
			semesterSpinner.setAdapter(scrape.spinnerAdapter());
			semesterSpinner.setSelection(scrape.SemesterOptionSelected);
			semesterSpinner.setOnItemSelectedListener(new OnItemSelectedListener());
		}
		

	}

	public class OnItemSelectedListener implements
			android.widget.AdapterView.OnItemSelectedListener {
		int hitcount = 0;

		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (hitcount == 0) {

			} else {
				if (mode == 10) {
					RequestObject thisRequest = new RequestObject(TucanMobile.TUCAN_PROT
							+ TucanMobile.TUCAN_HOST + scrape.eventLinks.get(0) + "-N"
							+ scrape.SemesterOptionValue.get(position), localCookieManager,
							RequestObject.METHOD_GET, "");
					SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(Events.this);
					callOverviewBrowser.execute(thisRequest);
				} else if (mode == 1) {
					RequestObject thisRequest = new RequestObject(TucanMobile.TUCAN_PROT
							+ TucanMobile.TUCAN_HOST + scrape.eventLinks.get(1) + "-N"
							+ scrape.SemesterOptionValue.get(position), localCookieManager,
							RequestObject.METHOD_GET, "");
					SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(Events.this);
					callOverviewBrowser.execute(thisRequest);
				}
			}
			hitcount++;
		}

		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && mode != 0) {

			@SuppressWarnings("unchecked")
			ArrayList<String> eventNameBuffer = (ArrayList<String>) scrape.eventNames.clone();
			ArrayAdapter<String> ListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
					eventNameBuffer);
			// Log.i(LOG_TAG,"Exam Names hat: "+examNames.size()+" Elemente");
			setListAdapter(ListAdapter);
			mode = 0;
			Spinner semesterSpinner = (Spinner) findViewById(R.id.exam_semester_spinner);
			semesterSpinner.setVisibility(View.GONE);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

}
