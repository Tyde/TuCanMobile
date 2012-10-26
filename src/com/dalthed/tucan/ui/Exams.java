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
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
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
import com.dalthed.tucan.scraper.BasicScraper;
import com.dalthed.tucan.scraper.ExamsScraper;
import com.dalthed.tucan.scraper.MessagesScraper;
import com.dalthed.tucan.util.ConfigurationChangeStorage;

public class Exams extends SimpleWebListActivity {
	private String UserName;
	
	private static final String LOG_TAG = "TuCanMobile";
	private int mode = 0;

	private ArrayList<String> examNameBuffer;

	private String URLStringtoCall;

	private ExamsScraper scrape;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState, true, 3);
		setContentView(R.layout.exams);
		BugSenseHandler.setup(this, "ed5c1682");

		// Webhandling Start
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
		// Webhandling End
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.exams);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		if (scrape!= null && mode == 0) {
			SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(this);
			RequestObject thisRequest;
			switch (position) {
			case 0:
				mode = 10;
				thisRequest = new RequestObject(TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
						+ scrape.examLinks.get(0), scrape.getCookieManager(), RequestObject.METHOD_GET, "");
				callOverviewBrowser.execute(thisRequest);
				break;
			case 1:
				mode = 1;
				Log.i(LOG_TAG, "Exam Names hat: " + scrape.examNames.size() + " Elemente");

				thisRequest = new RequestObject(TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
						+ scrape.examLinks.get(1), scrape.getCookieManager(), RequestObject.METHOD_GET, "");
				callOverviewBrowser.execute(thisRequest);
				Log.i(LOG_TAG, "Exam Names hat: " + scrape.examNames.size() + " Elemente");
				break;
			case 2:
				mode = 2;
				Log.i(LOG_TAG, "Exam Names hat: " + scrape.examNames.size() + " Elemente");
				thisRequest = new RequestObject(TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
						+ scrape.examLinks.get(2), scrape.getCookieManager(), RequestObject.METHOD_GET, "");
				callOverviewBrowser.execute(thisRequest);
				Log.i(LOG_TAG, "Exam Names hat: " + scrape.examNames.size() + " Elemente");
				break;
			case 3:
				mode = 3;
				thisRequest = new RequestObject(TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
						+ scrape.examLinks.get(3), scrape.getCookieManager(), RequestObject.METHOD_GET, "");
				callOverviewBrowser.execute(thisRequest);
				break;
			case 4:
				Intent callRegisterExams = new Intent(this, RegisterExams.class);
				callRegisterExams.putExtra("URL", scrape.examLinks.get(4));
				callRegisterExams.putExtra("Cookie",
						scrape.getCookieManager().getCookieHTTPString(TucanMobile.TUCAN_HOST));
				callRegisterExams.putExtra("UserName", UserName);
				startActivity(callRegisterExams);
				// TODO: Call RegisterEvents
			default:
				break;
			}
		}

	}

	public class OnItemSelectedListener implements
			android.widget.AdapterView.OnItemSelectedListener {
		int hitcount = 0;

		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (hitcount == 0) {

			} else {

				if (mode == 1) {
					RequestObject thisRequest = new RequestObject(TucanMobile.TUCAN_PROT
							+ TucanMobile.TUCAN_HOST + scrape.examLinks.get(1) + "-N"
							+ scrape.SemesterOptionValue.get(position), scrape.getCookieManager(),
							RequestObject.METHOD_GET, "");
					SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(Exams.this);
					callOverviewBrowser.execute(thisRequest);
				} else if (mode == 2) {
					RequestObject thisRequest = new RequestObject(TucanMobile.TUCAN_PROT
							+ TucanMobile.TUCAN_HOST + scrape.examLinks.get(2) + "-N"
							+ scrape.SemesterOptionValue.get(position), scrape.getCookieManager(),
							RequestObject.METHOD_GET, "");
					SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(Exams.this);
					callOverviewBrowser.execute(thisRequest);
				} else if (mode == 10) {
					RequestObject thisRequest = new RequestObject(TucanMobile.TUCAN_PROT
							+ TucanMobile.TUCAN_HOST + scrape.examLinks.get(0) + "-N"
							+ scrape.SemesterOptionValue.get(position), scrape.getCookieManager(),
							RequestObject.METHOD_GET, "");
					SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(Exams.this);
					callOverviewBrowser.execute(thisRequest);
				}

			}
			hitcount++;

		}

		public void onNothingSelected(AdapterView<?> parent) {
			// Passiert einfach mal nicht!!

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && mode != 0) {

			examNameBuffer = (ArrayList<String>) scrape.examNames.clone();
			ListAdapter ListAdapter = new ArrayAdapter<String>(this, R.layout.menu_row,
					R.id.main_menu_row_textField, examNameBuffer);
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

	class ModuleAdapter extends ArrayAdapter<String> {

		ArrayList<String> resultGrade, resultDate;

		public ModuleAdapter(ArrayList<String> resultName, ArrayList<String> resultGrade,
				ArrayList<String> resultDate) {
			super(Exams.this, R.layout.row_vv_events, R.id.row_vv_veranst, resultName);
			this.resultDate = resultDate;
			this.resultGrade = resultGrade;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			TextView TypeTextView = (TextView) row.findViewById(R.id.row_vv_type);
			TextView DozentTextView = (TextView) row.findViewById(R.id.row_vv_dozent);

			TypeTextView.setText(resultGrade.get(position));
			DozentTextView.setText(resultDate.get(position));

			return row;
		}

	}

	public void onPostExecute(AnswerObject result) {
		if (scrape == null) {
			scrape = new ExamsScraper(this, result);
		} else {
			scrape.setNewAnswer(result);
		}

		try {
			setListAdapter(scrape.scrapeAdapter(mode));
		} catch (LostSessionException e) {
			Intent BackToLoginIntent = new Intent(this, TuCanMobileActivity.class);
			BackToLoginIntent.putExtra("lostSession", true);
			startActivity(BackToLoginIntent);
		}

		setSpinner();

	}

	/**
	 * 
	 */
	private void setSpinner() {
		if (mode == 10 || mode == 1 || mode == 2) {

			Spinner semesterSpinner = (Spinner) findViewById(R.id.exam_semester_spinner);
			semesterSpinner.setVisibility(View.VISIBLE);
			semesterSpinner.setAdapter(scrape.spinnerAdapter());
			semesterSpinner.setSelection(scrape.SemesterOptionSelected);
			semesterSpinner.setOnItemSelectedListener(new OnItemSelectedListener());
		}
	}

	@Override
	public ConfigurationChangeStorage saveConfiguration() {
		ConfigurationChangeStorage cStore = new ConfigurationChangeStorage();
		cStore.adapters.add(getListAdapter());
		cStore.addScraper(scrape);
		cStore.mode = mode;
		return cStore;
	}

	@Override
	public void retainConfiguration(ConfigurationChangeStorage conf) {
		setListAdapter(conf.adapters.get(0));
		BasicScraper retainedScraper = conf.getScraper(0, this);
		if (retainedScraper instanceof ExamsScraper) {
			scrape = (ExamsScraper) retainedScraper;
		}
		mode = conf.mode;
		setSpinner();
	}

}
