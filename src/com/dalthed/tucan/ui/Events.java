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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class Events extends SimpleWebListActivity {

	private CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
	private String URLStringtoCall;
	private int mode = 0;
	private ArrayList<String> eventLinks, eventNames;
	private ArrayAdapter<String> ListAdapter;
	private ArrayList<String> SemesterOptionName;
	private ArrayList<String> SemesterOptionValue;
	private int SemesterOptionSelected = 0;
	private ArrayList<String> eventLink, applyLink;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events);
		BugSenseHandler.setup(this, "ed5c1682");

		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		URLStringtoCall = getIntent().getExtras().getString("URL");
		URL URLtoCall;

		try {
			URLtoCall = new URL(URLStringtoCall);
			localCookieManager = new CookieManager();
			localCookieManager.generateManagerfromHTTPString(
					URLtoCall.getHost(), CookieHTTPString);
			callResultBrowser = new SimpleSecureBrowser(this);
			RequestObject thisRequest = new RequestObject(URLStringtoCall,
					localCookieManager, RequestObject.METHOD_GET, "");

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
		SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
				this);
		RequestObject thisRequest;
		if (mode == 0) {
			
			switch (position) {
			case 0:
				mode = 10;
				thisRequest = new RequestObject(TucanMobile.TUCAN_PROT
						+ TucanMobile.TUCAN_HOST + eventLinks.get(0),
						localCookieManager, RequestObject.METHOD_GET, "");
				callOverviewBrowser.execute(thisRequest);
				break;
			case 1:
				mode = 1;
				thisRequest = new RequestObject(TucanMobile.TUCAN_PROT
						+ TucanMobile.TUCAN_HOST + eventLinks.get(1),
						localCookieManager, RequestObject.METHOD_GET, "");
				callOverviewBrowser.execute(thisRequest);
				break;
			case 2:
				mode = 2;
				thisRequest = new RequestObject(TucanMobile.TUCAN_PROT
						+ TucanMobile.TUCAN_HOST + eventLinks.get(2),
						localCookieManager, RequestObject.METHOD_GET, "");
				callOverviewBrowser.execute(thisRequest);
				break;
			}

		} else if (mode == 1) {
			Intent StartSingleEventIntent = new Intent(Events.this,
					FragmentSingleEvent.class);
			StartSingleEventIntent.putExtra("URL", TucanMobile.TUCAN_PROT
					+ TucanMobile.TUCAN_HOST + eventLink.get(position));
			StartSingleEventIntent.putExtra("Cookie", localCookieManager
					.getCookieHTTPString(TucanMobile.TUCAN_HOST));
			// StartSingleEventIntent.putExtra("UserName", UserName);
			startActivity(StartSingleEventIntent);
		} else if (mode == 2) {
			
			thisRequest = new RequestObject(TucanMobile.TUCAN_PROT
					+ TucanMobile.TUCAN_HOST + applyLink.get(position),
					localCookieManager, RequestObject.METHOD_GET, "");
			callOverviewBrowser.execute(thisRequest);
		}
	}

	@Override
	public void onPostExecute(AnswerObject result) {
		Document doc = Jsoup.parse(result.getHTML());
		if (doc.select("span.notLoggedText").text().length() > 0) {
			Intent BackToLoginIntent = new Intent(this,
					TuCanMobileActivity.class);
			BackToLoginIntent.putExtra("lostSession", true);
			startActivity(BackToLoginIntent);
		} else {
			sendHTMLatBug(doc.html());
			if (mode == 0) {
				Elements links = doc.select("li#link000273").select("li");

				Iterator<Element> linkIt = links.iterator();
				eventLinks = new ArrayList<String>();
				eventNames = new ArrayList<String>();
				while (linkIt.hasNext()) {
					Element next = linkIt.next();
					String id = next.id();
					if (id.equals("link000275") || id.equals("link000274")
							|| id.equals("link000311")) {

						eventLinks.add(next.select("a").attr("href"));
						eventNames.add(next.select("a").text());
					}
				}
				@SuppressWarnings("unchecked")
				ArrayList<String> eventNameBuffer = (ArrayList<String>) eventNames
						.clone();
				ListAdapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, eventNameBuffer);
				setListAdapter(ListAdapter);
			} else if (mode == 2) {
				if(doc.select("table.tbcoursestatus")==null) {
					Elements ListElements = doc.select("div#contentSpacer_IE")
							.first().select("ul").first().select("li");
					Iterator<Element> ListIterator = ListElements.iterator();
					applyLink 					= new ArrayList<String>();
					ArrayList<String> applyName = new ArrayList<String>();
					while (ListIterator.hasNext()) {
						Element next = ListIterator.next();
						//Log.i(LOG_TAG,next.select("a").attr("href"));
						applyLink.add(next.select("a").attr("href"));
						applyName.add(next.text());
					}
					ListAdapter = new ArrayAdapter<String>(this,
							android.R.layout.simple_list_item_1, applyName);
					setListAdapter(ListAdapter);
				}
				else {
					//TODO : Call importand Intent
				}
				
			} else {
				eventLink = new ArrayList<String>();
				if (mode == 10) {
					ArrayList<String> eventName = new ArrayList<String>();
					ArrayList<String> eventHead = new ArrayList<String>();
					ArrayList<String> eventCredits = new ArrayList<String>();
					Element ModuleOverviewTable = doc.select("div.tb").first();
					Iterator<Element> ExamRowIterator = ModuleOverviewTable
							.select("tbody").first().select("tr").iterator();
					while (ExamRowIterator.hasNext()) {
						Element next = ExamRowIterator.next();
						Elements ExamCols = next.select("td");
						if (ExamCols.size() > 0) {
							eventName.add(ExamCols.get(2).text());
							eventHead.add(ExamCols.get(3).text());
							eventCredits.add(ExamCols.get(4).text());
							eventLink.add(ExamCols.get(2).select("a")
									.attr("href"));
							Log.i(LOG_TAG, "Link"
									+ ExamCols.get(2).select("a").attr("href"));
						}
					}
					ListAdapter.clear();
					ListAdapter = new ModuleAdapter(eventName, eventCredits,
							eventHead);
					setListAdapter(ListAdapter);
				} else if (mode == 1) {
					ArrayList<String> eventName = new ArrayList<String>();
					ArrayList<String> eventHead = new ArrayList<String>();
					ArrayList<String> eventTime = new ArrayList<String>();
					Element ModuleOverviewTable = doc.select("div.tb").first();
					Iterator<Element> ExamRowIterator = ModuleOverviewTable
							.select("tbody").first().select("tr").iterator();
					while (ExamRowIterator.hasNext()) {
						Element next = ExamRowIterator.next();
						Elements ExamCols = next.select("td");
						if (ExamCols.size() > 0) {
							eventName.add(ExamCols.get(2).text());
							eventLink.add(ExamCols.get(2).select("a")
									.attr("href"));
							Log.i(LOG_TAG, "Link"
									+ ExamCols.get(2).select("a").attr("href"));
							eventHead.add(ExamCols.get(3).text());
							eventTime.add(ExamCols.get(4).text());
						}
					}
					ListAdapter.clear();
					ListAdapter = new ModuleAdapter(eventName, eventTime,
							eventHead);
					setListAdapter(ListAdapter);
				}
				SemesterOptionName = new ArrayList<String>();
				SemesterOptionValue = new ArrayList<String>();

				Iterator<Element> SemesterOptionIterator = doc.select("option")
						.iterator();
				int i = 0;
				while (SemesterOptionIterator.hasNext()) {
					Element next = SemesterOptionIterator.next();
					SemesterOptionName.add(next.text());
					SemesterOptionValue.add(next.attr("value"));
					if (next.hasAttr("selected")) {
						Log.i(LOG_TAG, next.text() + " is selected, has val "
								+ i);
						SemesterOptionSelected = i;
					}
					i++;
				}
				ArrayAdapter<String> SpinnerAdapter = new ArrayAdapter<String>(
						this, android.R.layout.simple_spinner_item,
						SemesterOptionName);

				Spinner semesterSpinner = (Spinner) findViewById(R.id.exam_semester_spinner);
				semesterSpinner.setVisibility(View.VISIBLE);
				semesterSpinner.setAdapter(SpinnerAdapter);
				semesterSpinner.setSelection(SemesterOptionSelected);
				semesterSpinner
						.setOnItemSelectedListener(new OnItemSelectedListener());
			}
		}
	}

	class ModuleAdapter extends ArrayAdapter<String> {

		ArrayList<String> moduleCredits, moduleHeads;

		public ModuleAdapter(ArrayList<String> moduleName,
				ArrayList<String> resultGrade, ArrayList<String> resultDate) {
			super(Events.this, R.layout.row_vv_events, R.id.row_vv_veranst,
					moduleName);
			this.moduleHeads = resultDate;
			this.moduleCredits = resultGrade;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			TextView TypeTextView = (TextView) row
					.findViewById(R.id.row_vv_type);
			TextView DozentTextView = (TextView) row
					.findViewById(R.id.row_vv_dozent);

			TypeTextView.setText(moduleCredits.get(position));
			DozentTextView.setText(moduleHeads.get(position));

			return row;
		}

	}

	public class OnItemSelectedListener implements
			android.widget.AdapterView.OnItemSelectedListener {
		int hitcount = 0;

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			if (hitcount == 0) {

			} else {
				if (mode == 10) {
					RequestObject thisRequest = new RequestObject(
							TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
									+ eventLinks.get(0) + "-N"
									+ SemesterOptionValue.get(position),
							localCookieManager, RequestObject.METHOD_GET, "");
					SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
							Events.this);
					callOverviewBrowser.execute(thisRequest);
				} else if (mode == 1) {
					RequestObject thisRequest = new RequestObject(
							TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
									+ eventLinks.get(1) + "-N"
									+ SemesterOptionValue.get(position),
							localCookieManager, RequestObject.METHOD_GET, "");
					SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
							Events.this);
					callOverviewBrowser.execute(thisRequest);
				}
			}
			hitcount++;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0
				&& mode != 0) {

			@SuppressWarnings("unchecked")
			ArrayList<String> eventNameBuffer = (ArrayList<String>) eventNames
					.clone();
			ListAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, eventNameBuffer);
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
