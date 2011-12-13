package com.dalthed.tucan.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.dalthed.tucan.R;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;

public class RegisterExams extends SimpleWebListActivity {
	private String UserName, URLStringtoCall;
	private CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
	ArrayList<Boolean> eventisModule = new ArrayList<Boolean>();
	ArrayList<String> eventName, examDate, registerLink;
	ArrayList<Integer> examSelection;

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
			RequestObject thisRequest = new RequestObject(URLStringtoCall,
					localCookieManager, RequestObject.METHOD_GET, "");

			callResultBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, e.getMessage());
		}

	}

	@Override
	public void onPostExecute(AnswerObject result) {

		Document doc = Jsoup.parse(result.getHTML());
		Element significantTable = doc.select("table.nb").select("tbody")
				.first();

		Iterator<Element> rows = significantTable.select("tr").iterator();
		eventisModule = new ArrayList<Boolean>();
		eventName = new ArrayList<String>();
		examDate = new ArrayList<String>();
		registerLink = new ArrayList<String>();
		examSelection = new ArrayList<Integer>();
		while (rows.hasNext()) {
			Element next = rows.next();

			if (next.hasClass("level02")) {
				eventisModule.add(true);
				eventName.add(next.select("td").get(1).text());
				examDate.add("");
				examSelection.add(-1);
				registerLink.add("");
			} else {
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
					if (cols.get(4).select("a").text().equals("Anmelden"))
						examSelection.add(2);
					else
						examSelection.add(3);
					registerLink.add(cols.get(4).select("a").attr("href"));
				}

			}

			// System.out.println();
		}
		RegisterExamAdapter nextAdapter = new RegisterExamAdapter(eventisModule, eventName, examDate, examSelection);
		setListAdapter(nextAdapter);
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
			
			if (eventisModule.get(position)) {
				isEventLayout.setVisibility(View.GONE);
				TextView ModuleNameView = (TextView) row
						.findViewById(R.id.registerexam_row_module_name);
				
				ModuleNameView.setText(eventName.get(position));
			} else {
				isModuleLayout.setVisibility(View.GONE);
				TextView eventNameView = (TextView) row
						.findViewById(R.id.registerexam_row_examName);
				TextView eventSelectionView = (TextView) row
						.findViewById(R.id.registerexam_row_selection);
				eventNameView.setText(eventName.get(position));
				String selectionstring = "";
				switch (examSelection.get(position)) {
				case 0:
					selectionstring = "Abgelaufen";
					break;
				case 1:
					selectionstring = "Ausgewählt";
					break;
				case 2:
					selectionstring = "Anmelden";
					break;
				case 3:
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
