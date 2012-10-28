package com.dalthed.tucan.ui;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.dalthed.tucan.R;
import com.dalthed.tucan.TuCanMobileActivity;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.BackgroundBrowserReciever;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleBackgroundBrowser;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;
import com.dalthed.tucan.scraper.BasicScraper;
import com.dalthed.tucan.scraper.MainMenuScraper;
import com.dalthed.tucan.util.ConfigurationChangeStorage;

public class MainMenu extends SimpleWebListActivity implements BackgroundBrowserReciever {
	private Boolean windowFeatureCalled = false;
	CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";

	/**
	 * s HTML Scraper
	 */
	private MainMenuScraper scrape;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.main_menu);
		// sthis.windowFeatureCalled = true;
		acBar.setTitle("Startseite");

		BugSenseHandler.setup(this, "ed5c1682");
		// Webhandling Start
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		String lastCalledURLString = getIntent().getExtras().getString("URL");
		String source = getIntent().getExtras().getString("source");
		// Log.i(LOG_TAG,"Qsource);
		URL lastCalledURL;
		if (!restoreResultBrowser()) {
			if (source == null || source.equals("")) {
				try {
					lastCalledURL = new URL(lastCalledURLString);
					localCookieManager = new CookieManager();
					localCookieManager.generateManagerfromHTTPString(lastCalledURL.getHost(),
							CookieHTTPString);
					callResultBrowser = new SimpleSecureBrowser(this);
					RequestObject thisRequest = new RequestObject(lastCalledURLString,
							localCookieManager, RequestObject.METHOD_GET, "");

					callResultBrowser.execute(thisRequest);
				} catch (MalformedURLException e) {
					Log.e(LOG_TAG, e.getMessage());
				}
			} else {
				localCookieManager = new CookieManager();
				localCookieManager.generateManagerfromHTTPString(TucanMobile.TUCAN_HOST,
						CookieHTTPString);
				onPostExecute(new AnswerObject(source, "", localCookieManager, lastCalledURLString));
			}
		}

		// Webhandling End

		menuListInitialisation();

	}

	/**
	 * 
	 */
	private void menuListInitialisation() {
		ListView menuList = getListView();

		menuList.setDivider(null);

		menuList.setAdapter(new ArrayAdapter<String>(this, R.layout.menu_row,
				R.id.main_menu_row_textField, getResources().getStringArray(
						R.array.mainmenu_options)));

		setMenuListListener(menuList);
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

	public void onPostExecute(AnswerObject result) {
		// Scraper initialisieren
		scrape = new MainMenuScraper(this, result);

		// Adapter für heutige Events erstellen
		ListAdapter todayseventsadapter;
		try {
			// Adapter mittels scraper starten und auf die Liste setzen
			todayseventsadapter = scrape.scrapeAdapter(0);
			ListView EventList = (ListView) findViewById(R.id.mm_eventList);
			EventList.setAdapter(todayseventsadapter);

			// OnClicklistener für Eventliste: Bei klick wird ein intent für die
			// SingleEventAnsicht gestartet
			setEventListListener(EventList);
		} catch (LostSessionException e) {
			// Im falle einer verlorenen Session -> zurück zum login
			Intent BackToLoginIntent = new Intent(this, TuCanMobileActivity.class);
			BackToLoginIntent.putExtra("lostSession", true);
			startActivity(BackToLoginIntent);

		} catch (TucanDownException e) {
			TucanMobile.alertOnTucanDown(this, e.getMessage());
		}

		acBar.setSubtitle(scrape.UserName);

		// Start Location finding
		SimpleBackgroundBrowser simpleBackgroundBrowser = new SimpleBackgroundBrowser(this, acBar);
		simpleBackgroundBrowser.execute(new RequestObject(scrape.load_link_ev_loc, result
				.getCookieManager(), RequestObject.METHOD_GET, ""));
		// User, welche Tucan auf englisch gestellt haben, ausschließen, da
		// sonst fehler auftreten würden
		scrape.checkForRightTucanLanguage(this);
		// Links in den Buffer für die Actionbar schreiben
		scrape.bufferLinks(this, localCookieManager);

	}

	/**
	 * @param EventList
	 */
	private void setEventListListener(ListView EventList) {
		EventList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (scrape.noeventstoday == false) {
					Intent StartSingleEventIntent = new Intent(MainMenu.this,
							FragmentSingleEvent.class);
					StartSingleEventIntent.putExtra("URL", TucanMobile.TUCAN_PROT
							+ TucanMobile.TUCAN_HOST + scrape.today_event_links[position]);
					StartSingleEventIntent.putExtra("Cookie",
							localCookieManager.getCookieHTTPString(TucanMobile.TUCAN_HOST));
					// StartSingleEventIntent.putExtra("UserName",
					// UserName);
					parent.invalidate();
					startActivity(StartSingleEventIntent);

				}
			}
		});
	}

	/**
	 * @param menuList
	 */
	private void setMenuListListener(ListView menuList) {
		menuList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				if (scrape != null) {
					view.invalidate();
					switch (position) {
					case 0:
						Intent StartVVIntent = new Intent(MainMenu.this, VV.class);
						StartVVIntent.putExtra("URL", scrape.menu_link_vv);
						StartVVIntent.putExtra("Cookie",
								localCookieManager.getCookieHTTPString(TucanMobile.TUCAN_HOST));
						StartVVIntent.putExtra("UserName", scrape.UserName);
						startActivity(StartVVIntent);
						// Vorlesungsverzeichnis
						break;
					case 1:
						Intent StartScheduleIntent = new Intent(MainMenu.this, Schedule.class);
						StartScheduleIntent.putExtra("URL", scrape.menu_link_month);
						StartScheduleIntent.putExtra("Cookie",
								localCookieManager.getCookieHTTPString(TucanMobile.TUCAN_HOST));
						StartScheduleIntent.putExtra("Session", scrape.SessionArgument);
						startActivity(StartScheduleIntent);
						// Stundenplan
						break;
					case 2:
						Intent StartEventIntent = new Intent(MainMenu.this, Events.class);
						StartEventIntent.putExtra("URL", scrape.menu_link_ex);
						StartEventIntent.putExtra("Cookie",
								localCookieManager.getCookieHTTPString(TucanMobile.TUCAN_HOST));
						StartEventIntent.putExtra("UserName", scrape.UserName);
						startActivity(StartEventIntent);
						// Veranstaltungen
						break;
					case 3:
						Intent StartExamIntent = new Intent(MainMenu.this, Exams.class);
						StartExamIntent.putExtra("URL", scrape.menu_link_ex);
						StartExamIntent.putExtra("Cookie",
								localCookieManager.getCookieHTTPString(TucanMobile.TUCAN_HOST));
						StartExamIntent.putExtra("UserName", scrape.UserName);
						startActivity(StartExamIntent);
						// Prüfungen
						break;
					case 4:
						Intent StartMessageIntent = new Intent(MainMenu.this, Messages.class);
						StartMessageIntent.putExtra("URL", scrape.menu_link_msg);
						StartMessageIntent.putExtra("Cookie",
								localCookieManager.getCookieHTTPString(TucanMobile.TUCAN_HOST));
						StartMessageIntent.putExtra("UserName", scrape.UserName);
						startActivity(StartMessageIntent);
						break;
					}
				}
			}
		});
	}

	public void onBackgroundBrowserFinalized(AnswerObject result) {
		// chill
	}

	public boolean getwindowFeatureCalled() {
		return this.windowFeatureCalled;
	}

	@Override
	public ConfigurationChangeStorage saveConfiguration() {
		ConfigurationChangeStorage cStore = new ConfigurationChangeStorage();
		ListView menuList = getListView();
		ListView eventList = (ListView) findViewById(R.id.mm_eventList);
		cStore.adapters.add(menuList.getAdapter());
		cStore.adapters.add(eventList.getAdapter());
		cStore.addScraper(scrape);
		return cStore;
	}

	@Override
	public void retainConfiguration(ConfigurationChangeStorage conf) {
		BasicScraper retainedScraper = conf.getScraper(0, this);
		if (retainedScraper instanceof MainMenuScraper) {
			scrape = (MainMenuScraper) retainedScraper;
		}
		ListView eventList = (ListView) findViewById(R.id.mm_eventList);
		eventList.setAdapter(conf.adapters.get(1));
		setEventListListener(eventList);

	}

	/*
	 * public class LocationRequester extends AsyncTask<RequestObject, Integer,
	 * AnswerObject>{
	 * 
	 * ProgressBar progressView;
	 * 
	 * @Override protected void onPreExecute() { progressView = (ProgressBar)
	 * findViewById(R.id.mm_progress); progressView.setVisibility(View.VISIBLE);
	 * super.onPreExecute(); }
	 * 
	 * @Override protected void onPostExecute(AnswerObject result) {
	 * progressView.setVisibility(View.GONE); super.onPostExecute(result); }
	 * 
	 * @Override protected AnswerObject doInBackground(RequestObject... params)
	 * {
	 * 
	 * }
	 */
}
