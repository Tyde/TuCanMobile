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

public class RegisterExams extends SimpleWebListActivity {
	private String UserName, URLStringtoCall;
	private CookieManager localCookieManager;
	private Boolean justGetimportant = true;
	private static final String LOG_TAG = "TuCanMobile";
	ArrayList<Boolean> eventisModule = new ArrayList<Boolean>();
	ArrayList<String> eventName, examDate, registerLink;
	ArrayList<Integer> examSelection;
	String postString;
	int mode = 0;

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
			localCookieManager.generateManagerfromHTTPString(
					URLtoCall.getHost(), CookieHTTPString);
			callResultBrowser = new SimpleSecureBrowser(this);
			if (TucanMobile.DEBUG) {
				callResultBrowser.HTTPS = this.HTTPS;
			}
			RequestObject thisRequest = new RequestObject(URLStringtoCall,
					localCookieManager, RequestObject.METHOD_GET, "");

			callResultBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, e.getMessage());
		}

	}


	public void onPostExecute(AnswerObject result) {

		Document doc = Jsoup.parse(result.getHTML());

		sendHTMLatBug(result.getHTML());
		if (doc.select("span.notLoggedText").text().length() > 0) {
			Intent BackToLoginIntent = new Intent(this,
					TuCanMobileActivity.class);
			BackToLoginIntent.putExtra("lostSession", true);
			startActivity(BackToLoginIntent);
		} else {
			if (mode == 0) {
				Element significantTable = doc.select("table.nb")
						.select("tbody").first();

				Iterator<Element> rows = significantTable.select("tr")
						.iterator();
				eventisModule = new ArrayList<Boolean>();
				eventName = new ArrayList<String>();
				examDate = new ArrayList<String>();
				registerLink = new ArrayList<String>();
				examSelection = new ArrayList<Integer>();
				while (rows.hasNext()) {
					try {
					
					Element next = rows.next();

					if (next.hasClass("level02")) {
						eventisModule.add(true);
						eventName.add(next.select("td").get(1).text());
						examDate.add("");
						examSelection.add(-1);
						registerLink.add("");
					} else if (next.hasClass("level03")) {
						// NEU
						eventisModule.add(true);
						eventName.add(next.select("td").get(0).text());
						examDate.add("");
						examSelection.add(-1);
						registerLink.add("");
					}

					else {
						eventisModule.add(false);
						Elements cols = next.select("td");
						eventName.add(cols.get(2).text());
						examDate.add(cols.get(3).text());
						// Wenn keine Anmeldung/Abmeldung möglich ist
						if (cols.get(4).select("a").isEmpty()) {
							if (cols.get(4).text().equals("Ausgewählt"))
								examSelection.add(1);
							else
								examSelection.add(0);

							registerLink.add("");
						} else {
							// Anmeldung/Abmeldung möglich
							if (cols.get(4).select("a").text()
									.equals("Anmelden"))
								examSelection.add(2);
							else
								examSelection.add(3);
							registerLink.add(cols.get(4).select("a")
									.attr("href"));
						}

					}
					
					}
					
					catch(IndexOutOfBoundsException e) {
						Log.e(LOG_TAG, "Index out of Bounds");
						ErrorReporter.getInstance().handleSilentException(e);
					}
					// System.out.println();
				}
				if (justGetimportant) {
					// TODO: Only get a selection

				}
				RegisterExamAdapter nextAdapter = new RegisterExamAdapter(
						eventisModule, eventName, examDate, examSelection);
				setListAdapter(nextAdapter);
			} else if (mode == 1) {
				mode = 3;
				Element form = doc.select("form[name=registrationdetailsform]")
						.first();
				Elements cols = form.select("table.tb750").first().select("tr")
						.last().select("td");
				Iterator<Element> iterateForms = form.select("input")
						.iterator();
				ArrayList<String> formName = new ArrayList<String>();
				ArrayList<String> formValue = new ArrayList<String>();
				postString = "";
				int ct = 0;
				while (iterateForms.hasNext()) {
					Element next = iterateForms.next();
					formName.add(next.attr("name"));
					formValue.add(next.attr("value"));
					if (ct > 0) {
						postString += "&";
					}
					ct++;
					postString += next.attr("name") + "=" + next.attr("value");
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(
						"An/Abmelden zu:" + cols.get(1).text() + "\n"
								+ cols.get(6).text()).setCancelable(true)
						.setPositiveButton("Ja", MyYesClickListener)
						.setNegativeButton("Nein", null);
				AlertDialog alert = builder.create();
				alert.show();
			} else if (mode == 3) {
				Element form = doc.select("form[name=registrationdetailsform]")
						.first();
				String resultText = form.select("span.note").first().text();
				Toast.makeText(this, resultText, Toast.LENGTH_LONG).show();
				mode = 0;
				callResultBrowser = new SimpleSecureBrowser(this);
				RequestObject thisRequest = new RequestObject(URLStringtoCall,
						localCookieManager, RequestObject.METHOD_GET, "");

				callResultBrowser.execute(thisRequest);
			}
		}

	}

	DialogInterface.OnClickListener MyYesClickListener = new DialogInterface.OnClickListener() {

		public void onClick(DialogInterface dialog, int which) {
			SimpleSecureBrowser finalizeRegister = new SimpleSecureBrowser(
					RegisterExams.this);
			RequestObject callstatuschange = new RequestObject(
					TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
							+ "/scripts/mgrqcgi", localCookieManager,
					RequestObject.METHOD_POST, postString);
			finalizeRegister.execute(callstatuschange);

		}
	};

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (examSelection.get(position) == 2
				|| examSelection.get(position) == 3) {
			if (examSelection.get(position) == 2)
				mode = 1;
			else if (examSelection.get(position) == 3)
				mode = 1;
			SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
					this);
			RequestObject callstatuschange = new RequestObject(
					TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
							+ registerLink.get(position), localCookieManager,
					RequestObject.METHOD_GET, "");
			callOverviewBrowser.execute(callstatuschange);
		}
	}

	class RegisterExamAdapter extends ArrayAdapter<String> {
		ArrayList<Boolean> eventisModule;
		ArrayList<String> eventName;
		ArrayList<Integer> examSelection;

		public RegisterExamAdapter(ArrayList<Boolean> eventisModule,
				ArrayList<String> eventName, ArrayList<String> examDate,
				ArrayList<Integer> examSelection) {
			super(RegisterExams.this, R.layout.registerexam_row,
					R.id.registerexam_row_date, examDate);
			this.eventisModule = eventisModule;
			this.eventName = eventName;
			this.examSelection = examSelection;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			LinearLayout isModuleLayout = (LinearLayout) row
					.findViewById(R.id.registerexam_row_ismodule);
			LinearLayout isEventLayout = (LinearLayout) row
					.findViewById(R.id.registerexam_row_isevent);

			Log.i(LOG_TAG, "Calling " + position);

			if (eventisModule.get(position)) {
				isModuleLayout.setVisibility(View.VISIBLE);
				isEventLayout.setVisibility(View.GONE);
				TextView ModuleNameView = (TextView) row
						.findViewById(R.id.registerexam_row_module_name);

				ModuleNameView.setText(eventName.get(position));
			} else {
				isModuleLayout.setVisibility(View.GONE);
				isEventLayout.setVisibility(View.VISIBLE);
				TextView eventNameView = (TextView) row
						.findViewById(R.id.registerexam_row_examName);
				TextView eventSelectionView = (TextView) row
						.findViewById(R.id.registerexam_row_selection);
				eventNameView.setText(eventName.get(position));
				String selectionstring = "";
				switch (examSelection.get(position)) {
				case 0:
					eventSelectionView.setTextColor(Color.BLACK);
					selectionstring = "Abgelaufen";
					break;
				case 1:
					eventSelectionView.setTextColor(Color.BLACK);
					selectionstring = "Ausgewählt";
					break;
				case 2:
					eventSelectionView.setTextColor(getResources().getColor(R.color.tucan_green));

					selectionstring = "Anmelden";
					break;
				case 3:
					eventSelectionView.setTextColor(getResources().getColor(R.color.register_deregister_red));
					selectionstring = "Abmelden";
					break;
				default:
					break;

				}

				eventSelectionView.setText(selectionstring);
			}

			return row;
		}

	}
}
