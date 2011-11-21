package com.dalthed.tucan.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
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

import com.bugsense.trace.BugSenseHandler;
import com.dalthed.tucan.R;
import com.dalthed.tucan.TuCanMobileActivity;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class MainMenu extends SimpleWebActivity {

	CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
	private String menu_link_vv = "";
	private String menu_link_ex = "";
	private String menu_link_msg = "";
	
	private String menu_link_month = "";
	private String UserName = "";
	String SessionArgument="";
	private boolean noeventstoday=false;
	private String[] today_event_links;
	public GoogleAnalyticsTracker mAnalyticsTracker;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		mAnalyticsTracker = GoogleAnalyticsTracker.getInstance();
		mAnalyticsTracker.startNewSession("UA-2729322-5", this);
		BugSenseHandler.setup(this,"ed5c1682");
		// Webhandling Start
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		String lastCalledURLString = getIntent().getExtras().getString("URL");
		String source = getIntent().getExtras().getString("source");
		//Log.i(LOG_TAG,"Qsource);
		URL lastCalledURL;
		if(source==null || source.equals("")){
			try {
				lastCalledURL = new URL(lastCalledURLString);
				localCookieManager = new CookieManager();
				localCookieManager.generateManagerfromHTTPString(
						lastCalledURL.getHost(), CookieHTTPString);
				callResultBrowser = new SimpleSecureBrowser(
						this);
				RequestObject thisRequest = new RequestObject(lastCalledURLString,
						localCookieManager, RequestObject.METHOD_GET, "");

				callResultBrowser.execute(thisRequest);
			} catch (MalformedURLException e) {
				Log.e(LOG_TAG, e.getMessage());
			}
		}
		else {
			localCookieManager = new CookieManager();
			localCookieManager.generateManagerfromHTTPString(
					TucanMobile.TUCAN_HOST, CookieHTTPString);
			onPostExecute(new AnswerObject(source, "", localCookieManager, lastCalledURLString));
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
					Intent StartScheduleIntent = new Intent(MainMenu.this,
							Schedule.class);
					StartScheduleIntent.putExtra("URL", menu_link_month);
					StartScheduleIntent.putExtra("Cookie", localCookieManager
							.getCookieHTTPString(TucanMobile.TUCAN_HOST));
					StartScheduleIntent.putExtra("Session", SessionArgument);
					startActivity(StartScheduleIntent);
					// Stundenplan
					break;
				case 2:
					Intent StartEventIntent = new Intent(MainMenu.this,
							Events.class);
					StartEventIntent.putExtra("URL", menu_link_ex);
					StartEventIntent.putExtra("Cookie", localCookieManager
							.getCookieHTTPString(TucanMobile.TUCAN_HOST));
					StartEventIntent.putExtra("UserName", UserName);
					startActivity(StartEventIntent);
					// Veranstaltungen
					break;
				case 3:
					Intent StartExamIntent = new Intent(MainMenu.this,
							Exams.class);
					StartExamIntent.putExtra("URL", menu_link_ex);
					StartExamIntent.putExtra("Cookie", localCookieManager
							.getCookieHTTPString(TucanMobile.TUCAN_HOST));
					StartExamIntent.putExtra("UserName", UserName);
					startActivity(StartExamIntent);
					// Pr�fungen
					break;
				case 4:
					Intent StartMessageIntent = new Intent(MainMenu.this,
							Messages.class);
					StartMessageIntent.putExtra("URL", menu_link_msg);
					StartMessageIntent.putExtra("Cookie", localCookieManager
							.getCookieHTTPString(TucanMobile.TUCAN_HOST));
					StartMessageIntent.putExtra("UserName", UserName);
					startActivity(StartMessageIntent);
					break;
				}
			}
		});

	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.main_menu);
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
			View row = super.getView(position, convertView, parent);
			TextView clockText = (TextView) row.findViewById(R.id.row_time);
			clockText.setText(startClock[position]);
			return row;
		}
	}

	@Override
	public void onPostExecute(AnswerObject result) {
		// HTML auslesen
		sendHTMLatBug(result.getHTML());
		
	
		
		Document doc = Jsoup.parse(result.getHTML());
		if(doc.select("span.notLoggedText").text().length()>0 || result.getHTML().equals("")){
			Intent BackToLoginIntent = new Intent(this,TuCanMobileActivity.class);
			BackToLoginIntent.putExtra("lostSession", true);
			startActivity(BackToLoginIntent);
		}
		else {
			String lcURLString=result.getLastCalledURL();
			try {
				URL lcURL = new URL (lcURLString);
				SessionArgument = lcURL.getQuery().split("ARGUMENTS=")[1].split(",")[0];
			} catch (MalformedURLException e) {
			
				e.printStackTrace();
			}
			
			// Tabelle mit den Terminen finden und Durchlaufen
			Element EventTable = doc.select("table.nb").first();
			
			String[] Events;
			String[] Times;
			
			if(EventTable==null){
				Events = new String[1];
				Times = new String[1];
				Events[0] = "Keine Heutigen Veranstaltungen";
				Times[0] = "";
				noeventstoday = true;
			}				
			else  {
				
				
				if (EventTable.select("tr.tbdata").first().select("td").size()==5) {
					Events = new String[1];
					Times = new String[1];
					Events[0] = "Keine Heutigen Veranstaltungen";
					Times[0] = "";
					noeventstoday = true;
				} else {

					Elements EventRows = EventTable.select("tr.tbdata");
					Iterator<Element> RowIt = EventRows.iterator();
					Events = new String[EventRows.size()];
					Times = new String[EventRows.size()];
					today_event_links = new String[EventRows.size()];
					int i = 0;
					while (RowIt.hasNext()) {
						Element currentElement = (Element) RowIt.next();
						String EventString = currentElement.select("td[headers=Name]")
								.select("a").first().text();
						today_event_links[i]=currentElement.select("td[headers=Name]")
								.select("a").first().attr("href");
						String EventTimeString = currentElement
								.select("td").get(2).select("a").first().text();
						String EventTimeEndString = currentElement
								.select("td").get(3).select("a").first().text();
						Times[i] = EventTimeString + "-" + EventTimeEndString;
						Events[i] = EventString;
						i++;
					}
					
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
			Elements LinkstoOuterWorld = doc.select("div.tb");
			Element ArchivLink=LinkstoOuterWorld.get(1).select("a").first();

			menu_link_vv = lcURL.getProtocol() + "://" + lcURL.getHost()
					+ doc.select("li#link000326").select("a").attr("href");
			menu_link_ex = lcURL.getProtocol() + "://" + lcURL.getHost()
					+ doc.select("li#link000280").select("a").attr("href");
			menu_link_msg = lcURL.getProtocol() + "://" + lcURL.getHost()
					+ ArchivLink.attr("href");
			/*menu_link_export = lcURL.getProtocol() + "://" + lcURL.getHost()
					+ doc.select("li#link000272").select("a").attr("href");*/
			menu_link_month = lcURL.getProtocol() + "://" + lcURL.getHost()
					+ doc.select("li#link000271").select("a").attr("href");
			if(doc.select("li#link000326").select("a").attr("href").equals("")) {
				Dialog wronglanguageDialog = new AlertDialog.Builder(this)
				.setTitle("")
				.setMessage(R.string.general_not_supported_lang)
				.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.create();
				wronglanguageDialog.show();
				
				
				
			}
			usertextview.setText(UserName);
			ListView EventList = (ListView) findViewById(R.id.mm_eventList);
			EventList.setAdapter(new EventAdapter(Events, Times));
			EventList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if(noeventstoday==false){
						Intent StartSingleEventIntent = new Intent(MainMenu.this, FragmentSingleEvent.class);
						StartSingleEventIntent.putExtra("URL", TucanMobile.TUCAN_PROT+TucanMobile.TUCAN_HOST+today_event_links[position]);
						StartSingleEventIntent.putExtra("Cookie", localCookieManager
								.getCookieHTTPString(TucanMobile.TUCAN_HOST));
						//StartSingleEventIntent.putExtra("UserName", UserName);
						startActivity(StartSingleEventIntent);
					}										
				}
			});
			
		}
		

	}
	
	/*
	public class LocationRequester extends AsyncTask<RequestObject, Integer, AnswerObject>{
		
		ProgressBar progressView;
		@Override
		protected void onPreExecute() {
			progressView = (ProgressBar) findViewById(R.id.mm_progress);
			progressView.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(AnswerObject result) {
			progressView.setVisibility(View.GONE);
			super.onPostExecute(result);
		}

		@Override
		protected AnswerObject doInBackground(RequestObject... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}*/
}
