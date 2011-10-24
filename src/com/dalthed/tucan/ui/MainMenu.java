package com.dalthed.tucan.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.BrowseMethods;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;

public class MainMenu extends Activity {

	CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
	private String menu_link_vv = "";
	private String menu_link_ex = "";
	private String UserName = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);

		// Webhandling Start
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		String lastCalledURLString = getIntent().getExtras().getString("URL");
		URL lastCalledURL;
		try {
			lastCalledURL = new URL(lastCalledURLString);
			localCookieManager = new CookieManager();
			localCookieManager.generateManagerfromHTTPString(
					lastCalledURL.getHost(), CookieHTTPString);
			SecureBrowser callOverviewBrowser = new SecureBrowser();
			RequestObject thisRequest = new RequestObject(lastCalledURLString,
					localCookieManager, RequestObject.METHOD_GET, "");

			callOverviewBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG, e.getMessage());
		}
		// Webhandling End

		ListView MenuList = (ListView) findViewById(R.id.mm_menuList);

		MenuList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getResources()
						.getStringArray(R.array.mainmenu_options)));
		MenuList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView arg0, View arg1, int position,
					long arg3) {
				switch (position) {
				case 0:
					Intent StartVVIntent = new Intent(MainMenu.this, VV.class);
					StartVVIntent.putExtra("URL", menu_link_vv);
					StartVVIntent.putExtra("Cookie", localCookieManager
							.getCookieHTTPString(TucanMobile.TUCAN_HOST));
					StartVVIntent.putExtra("UserName", UserName);
					startActivity(StartVVIntent);
					// Vorlesungsverzeichnis
					break;
				case 1:
					// Stundenplan
					break;
				case 2:
					// Veranstaltungen
					break;
				case 3:
					Intent StartExamIntent = new Intent(MainMenu.this, Exams.class);
					StartExamIntent.putExtra("URL", menu_link_ex);
					StartExamIntent.putExtra("Cookie", localCookieManager
							.getCookieHTTPString(TucanMobile.TUCAN_HOST));
					StartExamIntent.putExtra("UserName", UserName);
					startActivity(StartExamIntent);
					// Prüfungen
					break;
				case 4:
					// Nachrichten
					break;
				}
			}
		});

	}

	public class SecureBrowser extends
			AsyncTask<RequestObject, Integer, AnswerObject> {
		ProgressDialog dialog;
		ProgressBar mm_pbar;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog = ProgressDialog.show(MainMenu.this, "", "Lade...", true);
			mm_pbar = (ProgressBar) findViewById(R.id.mm_progress);
			mm_pbar.setVisibility(View.VISIBLE);
		}

		@Override
		protected AnswerObject doInBackground(RequestObject... requestInfo) {
			AnswerObject answer = new AnswerObject("", "", null, null);
			RequestObject significantRequest = requestInfo[0];
			BrowseMethods Browser = new BrowseMethods();
			answer = Browser.browse(significantRequest);
			return answer;
		}

		@Override
		protected void onPostExecute(AnswerObject result) {
			dialog.setMessage("Berechne...");
			mm_pbar.setVisibility(View.GONE);

			// HTML auslesen
			Document doc = Jsoup.parse(result.getHTML());
			// Tabelle mit den Terminen finden und Durchlaufen
			Element EventTable = doc.select("table.nb").first();
			String[] Events;
			String[] Times;
			if (EventTable == null
					|| EventTable.attr("summary") == "Eingegangene Nachrichten") {
				Events = new String[1];
				Times = new String[1];
				Events[0] = "Keine Heutigen Veranstaltungen";
				Times[0] = "";
			} else {
				Elements EventRows = EventTable.select("tr.tbdata");
				Iterator<Element> RowIt = EventRows.iterator();
				Events = new String[EventRows.size()];
				Times = new String[EventRows.size()];
				int i = 0;
				while (RowIt.hasNext()) {
					Element currentElement = (Element) RowIt.next();
					String EventString = currentElement
							.select("td[headers=Name]").select("a").first()
							.text();
					String EventTimeString = currentElement
							.select("td[headers=von]").select("a").first()
							.text();
					String EventTimeEndString = currentElement
							.select("td[headers=bis]").select("a").first()
							.text();
					Times[i] = EventTimeString + "-" + EventTimeEndString;
					Events[i] = EventString;
					i++;
				}
			}

			UserName = doc.select("span#loginDataName").text().split(":")[1];
			TextView usertextview = (TextView) findViewById(R.id.mm_username);
			URL lcURL = null;
			try {
				lcURL = new URL(result.getLastCalledURL());
			} catch (MalformedURLException e) {
				Log.e(LOG_TAG, "Malformed URL");
			}

			menu_link_vv = lcURL.getProtocol() + "://" + lcURL.getHost()
					+ doc.select("li[title=VV]").select("a").attr("href");
			menu_link_ex = lcURL.getProtocol()
					+ "://"
					+ lcURL.getHost()
					+ doc.select("li[title=Prüfungen]").select("a")
							.attr("href");
			Toast.makeText(MainMenu.this, menu_link_ex, Toast.LENGTH_LONG)
					.show();

			usertextview.setText(UserName);
			ListView EventList = (ListView) findViewById(R.id.mm_eventList);
			EventList.setAdapter(new EventAdapter(Events, Times));
			dialog.dismiss();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			localCookieManager = new CookieManager();
			Toast.makeText(this, "Abgemeldet", Toast.LENGTH_SHORT).show();
		}
		return super.onKeyDown(keyCode, event);
	}

	class EventAdapter extends ArrayAdapter<String> {
		String[] startClock;

		EventAdapter(String[] Events, String[] Times) {
			super(MainMenu.this, R.layout.row, R.id.label, Events);
			this.startClock = Times;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View row = super.getView(position, convertView, parent);
			TextView clockText = (TextView) row.findViewById(R.id.row_time);
			clockText.setText(startClock[position]);
			return row;
		}
	}

}
