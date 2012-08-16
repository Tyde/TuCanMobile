package com.dalthed.tucan.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.bugsense.trace.BugSenseHandler;
import com.dalthed.tucan.R;
import com.dalthed.tucan.TuCanMobileActivity;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.scraper.VVEventsScraper;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class VV_Events extends SimpleWebListActivity {

	CookieManager localCookieManager;
	String UserName = "";
	private static final String LOG_TAG = "TuCanMobile";
	private VVEventsScraper scrape;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.vv_events);
		BugSenseHandler.setup(this, "ed5c1682");
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		String URLStringtoCall = getIntent().getExtras().getString("URL");
		UserName = getIntent().getExtras().getString("UserName");
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
		setContentView(R.layout.vv_events);
	}

	class EventOverviewAdapter extends ArrayAdapter<String> {

		String[] EventType, EventDozent;

		public EventOverviewAdapter(String[] EventNames, String[] EventType, String[] EventDozent) {
			super(VV_Events.this, R.layout.row_vv_events, R.id.row_vv_veranst, EventNames);
			this.EventDozent = EventDozent;
			this.EventType = EventType;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			TextView TypeTextView = (TextView) row.findViewById(R.id.row_vv_type);
			TextView DozentTextView = (TextView) row.findViewById(R.id.row_vv_dozent);

			TypeTextView.setText(EventType[position]);
			DozentTextView.setText(EventDozent[position]);

			return row;
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
	}

	public void onPostExecute(AnswerObject result) {
		scrape = new VVEventsScraper(this, result);
		ListAdapter adapter;
		try {
			adapter = scrape.scrapeAdapter(0);

			if (adapter != null) {
				setListAdapter(adapter);
			}
		} catch (LostSessionException e) {
			//Im falle einer verlorenen Session -> zurück zum login
			Intent BackToLoginIntent = new Intent(this, TuCanMobileActivity.class);
			BackToLoginIntent.putExtra("lostSession", true);
			startActivity(BackToLoginIntent);
		}

	}

}
