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


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class VV_Events extends SimpleWebListActivity {

	CookieManager localCookieManager;
	String UserName = "";
	private static final String LOG_TAG = "TuCanMobile";
	private String[] Eventlink;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vv_events);
		BugSenseHandler.setup(this,"ed5c1682");
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		String URLStringtoCall = getIntent().getExtras().getString("URL");
		UserName = getIntent().getExtras().getString("UserName");
		URL URLtoCall;
		try {
			URLtoCall = new URL(URLStringtoCall);
			localCookieManager = new CookieManager();
			localCookieManager.generateManagerfromHTTPString(
					URLtoCall.getHost(), CookieHTTPString);
			callResultBrowser = new SimpleSecureBrowser(
					this);
			RequestObject thisRequest = new RequestObject(URLStringtoCall,
					localCookieManager, RequestObject.METHOD_GET, "");

			callResultBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG, e.getMessage());
		}
	}

	class EventOverviewAdapter extends ArrayAdapter<String> {

		String[] EventType, EventDozent;

		public EventOverviewAdapter(String[] EventNames, String[] EventType,
				String[] EventDozent) {
			super(VV_Events.this, R.layout.row_vv_events, R.id.row_vv_veranst,
					EventNames);
			this.EventDozent = EventDozent;
			this.EventType = EventType;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			TextView TypeTextView = (TextView) row
					.findViewById(R.id.row_vv_type);
			TextView DozentTextView = (TextView) row
					.findViewById(R.id.row_vv_dozent);

			TypeTextView.setText(EventType[position]);
			DozentTextView.setText(EventDozent[position]);

			return row;
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent StartSingleEventIntent = new Intent(VV_Events.this, SingleEvent.class);
		StartSingleEventIntent.putExtra("URL", TucanMobile.TUCAN_PROT+TucanMobile.TUCAN_HOST+Eventlink[position]);
		StartSingleEventIntent.putExtra("Cookie", localCookieManager
				.getCookieHTTPString(TucanMobile.TUCAN_HOST));
		//StartSingleEventIntent.putExtra("UserName", UserName);
		startActivity(StartSingleEventIntent);
	}

	public void callsetListAdapter(ArrayAdapter<String> ElementsAdapter) {

		setListAdapter(ElementsAdapter);
	}

	@Override
	public void onPostExecute(AnswerObject result) {
		Log.i(LOG_TAG, "HTML zum parsen bereit");
		Document doc = Jsoup.parse(result.getHTML());
		if(doc.select("span.notLoggedText").text().length()>0){
			Intent BackToLoginIntent = new Intent(this,TuCanMobileActivity.class);
			startActivity(BackToLoginIntent);
		}
		else {
			Elements tbdata = doc.select("tr.tbdata");
			sendHTMLatBug(tbdata.html());
			String[] Eventnames = new String[tbdata.size()];
			String[] Eventdozent = new String[tbdata.size()];
			String[] Eventtype = new String[tbdata.size()];
			Eventlink = new String[tbdata.size()];
			int i = 0;
			if (tbdata.size() > 0) {
				Iterator<Element> EventListIterator = tbdata.iterator();
				while (EventListIterator.hasNext()) {
					Element nextElement = EventListIterator.next();
					Elements rows = nextElement.select("td");
					Element leftcolumn = rows.get(1);
					Element rightcolumn = rows.get(3);
					Eventlink[i] = leftcolumn.select("a").attr("href");
					Eventnames[i] = leftcolumn.select("a").text();
					List<Node> importantnotes = leftcolumn.childNodes();
					Iterator<Node> imnit = importantnotes.iterator();
					while (imnit.hasNext()) {
						Log.i(LOG_TAG, imnit.next().outerHtml());
					}
					Eventdozent[i] = importantnotes.get(3).toString();
					Eventtype[i] = rightcolumn.text();
					Log.i(LOG_TAG, Eventtype[i]);
					i++;
				}
			}
			// EventAdapter TableAdapter = new EventAdapter(VV_Events.this,
			// Eventnames, Eventtype, Eventdozent);
			ArrayAdapter<String> TableAdapter = new EventOverviewAdapter(Eventnames,
					Eventtype, Eventdozent);
			callsetListAdapter(TableAdapter);
		}
		

	}

}
