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

public class Exams extends SimpleWebListActivity {
	private String UserName;
	private CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
	private int mode = 0;

	private ArrayList<String> examLinks, examNames, examNameBuffer;
	private ArrayAdapter<String> ListAdapter;
	private String URLStringtoCall;
	private ArrayList<String> SemesterOptionName;
	private ArrayList<String> SemesterOptionValue;
	private int SemesterOptionSelected = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState,true,3);
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
			localCookieManager.generateManagerfromHTTPString(
					URLtoCall.getHost(), CookieHTTPString);
			callResultBrowser = new SimpleSecureBrowser(this);
			RequestObject thisRequest = new RequestObject(URLStringtoCall,
					localCookieManager, RequestObject.METHOD_GET, "");

			callResultBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, e.getMessage());
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
		if (mode == 0) {
			SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
					this);
			RequestObject thisRequest;
			switch (position) {
			case 0:
				mode = 10;
				thisRequest = new RequestObject(TucanMobile.TUCAN_PROT
						+ TucanMobile.TUCAN_HOST + examLinks.get(0),
						localCookieManager, RequestObject.METHOD_GET, "");
				callOverviewBrowser.execute(thisRequest);
				break;
			case 1:
				mode = 1;
				Log.i(LOG_TAG, "Exam Names hat: " + examNames.size()
						+ " Elemente");

				thisRequest = new RequestObject(TucanMobile.TUCAN_PROT
						+ TucanMobile.TUCAN_HOST + examLinks.get(1),
						localCookieManager, RequestObject.METHOD_GET, "");
				callOverviewBrowser.execute(thisRequest);
				Log.i(LOG_TAG, "Exam Names hat: " + examNames.size()
						+ " Elemente");
				break;
			case 2:
				mode = 2;
				Log.i(LOG_TAG, "Exam Names hat: " + examNames.size()
						+ " Elemente");
				thisRequest = new RequestObject(TucanMobile.TUCAN_PROT
						+ TucanMobile.TUCAN_HOST + examLinks.get(2),
						localCookieManager, RequestObject.METHOD_GET, "");
				callOverviewBrowser.execute(thisRequest);
				Log.i(LOG_TAG, "Exam Names hat: " + examNames.size()
						+ " Elemente");
				break;
			case 3:
				mode = 3;
				thisRequest = new RequestObject(TucanMobile.TUCAN_PROT
						+ TucanMobile.TUCAN_HOST + examLinks.get(3),
						localCookieManager, RequestObject.METHOD_GET, "");
				callOverviewBrowser.execute(thisRequest);
				break;
			case 4:
				Intent callRegisterExams = new Intent(this, RegisterExams.class);
				callRegisterExams.putExtra("URL", examLinks.get(4));
				callRegisterExams.putExtra("Cookie", localCookieManager
						.getCookieHTTPString(TucanMobile.TUCAN_HOST));
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

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			if (hitcount == 0) {

			} else {

				if (mode == 1) {
					RequestObject thisRequest = new RequestObject(
							TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
									+ examLinks.get(1) + "-N"
									+ SemesterOptionValue.get(position),
							localCookieManager, RequestObject.METHOD_GET, "");
					SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
							Exams.this);
					callOverviewBrowser.execute(thisRequest);
				} else if (mode == 2) {
					RequestObject thisRequest = new RequestObject(
							TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
									+ examLinks.get(2) + "-N"
									+ SemesterOptionValue.get(position),
							localCookieManager, RequestObject.METHOD_GET, "");
					SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
							Exams.this);
					callOverviewBrowser.execute(thisRequest);
				} else if (mode == 10) {
					RequestObject thisRequest = new RequestObject(
							TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
									+ examLinks.get(0) + "-N"
									+ SemesterOptionValue.get(position),
							localCookieManager, RequestObject.METHOD_GET, "");
					SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
							Exams.this);
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
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0
				&& mode != 0) {

			examNameBuffer = (ArrayList<String>) examNames.clone();
			ListAdapter = new ArrayAdapter<String>(this, R.layout.menu_row,
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

		public ModuleAdapter(ArrayList<String> resultName,
				ArrayList<String> resultGrade, ArrayList<String> resultDate) {
			super(Exams.this, R.layout.row_vv_events, R.id.row_vv_veranst,
					resultName);
			this.resultDate = resultDate;
			this.resultGrade = resultGrade;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			TextView TypeTextView = (TextView) row
					.findViewById(R.id.row_vv_type);
			TextView DozentTextView = (TextView) row
					.findViewById(R.id.row_vv_dozent);

			TypeTextView.setText(resultGrade.get(position));
			DozentTextView.setText(resultDate.get(position));

			return row;
		}

	}

	@SuppressWarnings("unchecked")
	public void onPostExecute(AnswerObject result) {

		Document doc = Jsoup.parse(result.getHTML());
		sendHTMLatBug(result.getHTML());
		if (result.getHTML().length() < 10
				|| doc.select("span.notLoggedText").text().length() > 0) {
			Intent BackToLoginIntent = new Intent(this,
					TuCanMobileActivity.class);
			BackToLoginIntent.putExtra("lostSession", true);
			startActivity(BackToLoginIntent);
		} else {
			if (mode == 0) {
				Elements links = doc.select("li#link000280").select("li");

				Iterator<Element> linkIt = links.iterator();
				examLinks = new ArrayList<String>();
				examNames = new ArrayList<String>();
				while (linkIt.hasNext()) {
					Element next = linkIt.next();
					String id = next.id();

					if (id.equals("link000318") || id.equals("link000316")) {
						String link = next.select("a").attr("href");
						String name = next.select("a").text();
						examLinks.add(link);
						examNames.add(name);
					} else if (id.equals("link000323")) {
						Iterator<Element> subLinks = next.select("li.depth_3")
								.iterator();
						while (subLinks.hasNext()) {
							Element subnext = subLinks.next();
							String link = subnext.select("a").attr("href");
							String name = subnext.select("a").text();
							examLinks.add(link);
							examNames.add(name);
						}
					}

					// Log.i(LOG_TAG,next.toString()+"Hakki");
				}
				String SessionArgument = result.getLastCalledURL().split(
						"ARGUMENTS=")[1].split(",")[0];
				examLinks
						.add("https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi?APPNAME=CampusNet&PRGNAME=EXAMREGISTRATION&ARGUMENTS="
								+ SessionArgument + ",-N000318,");
				examNames.add("Anmeldung zu Prüfungen");
				examNameBuffer = (ArrayList<String>) examNames.clone();
				ListAdapter = new ArrayAdapter<String>(this, R.layout.menu_row,
						R.id.main_menu_row_textField, examNameBuffer);

				setListAdapter(ListAdapter);
			} else if (mode == 10 || mode == 1 || mode == 2) {

				if (mode == 10) {
					ArrayList<String> ExamName = new ArrayList<String>();
					ArrayList<String> ExamDate = new ArrayList<String>();
					ArrayList<String> ExamState = new ArrayList<String>();
					Element ModuleOverviewTable = doc.select("div.tb").first();
					Iterator<Element> ExamRowIterator = ModuleOverviewTable
							.select("tbody").first().select("tr").iterator();
					while (ExamRowIterator.hasNext()) {
						Element next = ExamRowIterator.next();
						Elements ExamCols = next.select("td");
						if (ExamCols.size() > 1) {
							ExamName.add(ExamCols.get(1).text());
							ExamDate.add(ExamCols.get(3).text());
							ExamState.add(ExamCols.get(4).text());
						}
					}
					ListAdapter.clear();
					ListAdapter = new ModuleAdapter(ExamName, ExamDate,
							ExamState);
					setListAdapter(ListAdapter);
				} else if (mode == 1) {
					ArrayList<String> ResultName = new ArrayList<String>();
					ArrayList<String> ResultGrade = new ArrayList<String>();
					ArrayList<String> ResultCredits = new ArrayList<String>();
					Element ModuleOverviewTable = doc.select("div.tb").first();
					Iterator<Element> ExamResultRowIterator = ModuleOverviewTable
							.select("tbody").first().select("tr").iterator();
					while (ExamResultRowIterator.hasNext()) {
						Element next = ExamResultRowIterator.next();
						Elements ExamResultCols = next.select("td");
						Log.i(LOG_TAG, "Größe Cols:" + ExamResultCols.size());
						if (ExamResultCols.size() > 1) {
							ResultName.add(ExamResultCols.get(1).text());
							ResultCredits.add(ExamResultCols.get(4).text());
							ResultGrade.add(ExamResultCols.get(2).text());
						}

					}
					ListAdapter.clear();
					ListAdapter = new ModuleAdapter(ResultName, ResultGrade,
							ResultCredits);
					setListAdapter(ListAdapter);
					Log.i(LOG_TAG, "Exam Names hat: " + examNames.size()
							+ " Elemente");
				} else if (mode == 2) {
					ArrayList<String> ResultName = new ArrayList<String>();
					ArrayList<String> ResultGrade = new ArrayList<String>();
					ArrayList<String> ResultDate = new ArrayList<String>();
					Iterator<Element> ExamResultRowIterator = doc.select(
							"tr.tbdata").iterator();
					while (ExamResultRowIterator.hasNext()) {
						Element next = ExamResultRowIterator.next();
						Elements ExamResultCols = next.select("td");
						Log.i(LOG_TAG, "Größe Cols:" + ExamResultCols.size());
						ResultName.add(Jsoup
								.parse(ExamResultCols.get(0).html()
										.split("<br />")[0]).text());
						ResultDate.add(ExamResultCols.get(1).text());
						ResultGrade.add(ExamResultCols.get(2).text() + "  "
								+ ExamResultCols.get(3).text());
					}
					ListAdapter.clear();
					ListAdapter = new ModuleAdapter(ResultName, ResultGrade,
							ResultDate);
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
				Log.i(LOG_TAG, "Exam Names hat: " + examNames.size()
						+ " Elemente");

			} else if (mode == 3) {
				ArrayList<String> ResultName = new ArrayList<String>();
				ArrayList<String> ResultGrade = new ArrayList<String>();
				ArrayList<String> ResultCredits = new ArrayList<String>();
				ArrayList<String> ResultCountedCredits = new ArrayList<String>();
				Element ModuleOverviewTable = doc.select("div.tb").first();
				Iterator<Element> ExamResultRowIterator = ModuleOverviewTable
						.select("tbody").first().select("tr").iterator();
				while (ExamResultRowIterator.hasNext()) {
					Element next = ExamResultRowIterator.next();
					Elements ExamResultCols = next.select("td");
					if (ExamResultCols.size() > 1) {
						ResultName.add(ExamResultCols.get(1).text());
						ResultCountedCredits.add(ExamResultCols.get(3).text());
						ResultCredits.add(ExamResultCols.get(4).text());
						ResultGrade.add(ExamResultCols.get(5).text());
					}

				}
				ListAdapter = new AccomplishmentAdapter(ResultName,
						ResultGrade, ResultCredits, ResultCountedCredits);
				setListAdapter(ListAdapter);
			}
		}

	}

	class AccomplishmentAdapter extends ArrayAdapter<String> {
		ArrayList<String> resultName, resultGrade, resultCredits,
				resultCountedCredits;

		public AccomplishmentAdapter(ArrayList<String> resName,
				ArrayList<String> resGrade, ArrayList<String> resCredits,
				ArrayList<String> resCtCredits) {
			super(Exams.this, R.layout.singleevent_row_date,
					R.id.singleevent_row_date_date, resCredits);
			this.resultName = resName;
			this.resultGrade = resGrade;
			this.resultCredits = resCredits;
			this.resultCountedCredits = resCtCredits;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);

			TextView AppTimeView = (TextView) row
					.findViewById(R.id.singleevent_row_date_time);
			TextView AppNumberView = (TextView) row
					.findViewById(R.id.singleevent_row_date_number);
			TextView AppRoomView = (TextView) row
					.findViewById(R.id.singleevent_row_date_room);
			TextView AppInstructorView = (TextView) row
					.findViewById(R.id.singleevent_row_date_instructor);

			AppTimeView.setText(this.resultCountedCredits.get(position));
			AppNumberView.setText("");
			AppRoomView.setText(this.resultName.get(position));
			AppInstructorView.setText(this.resultGrade.get(position));

			return row;
		}

	}

}
