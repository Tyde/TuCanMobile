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

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TuCanMobileActivity;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;

import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;

public class VV extends SimpleWebListActivity {

	CookieManager localCookieManager;
	String UserName = "";
	String[] Listlinks;
	String myHTML;
	ArrayAdapter<String> ListAdapter = null;
	private static final String LOG_TAG = "TuCanMobile";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vv);

		// Webhandling Start
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		String URLStringtoCall = getIntent().getExtras().getString("URL");
		UserName = getIntent().getExtras().getString("UserName");
		URL URLtoCall;
		try {
			URLtoCall = new URL(URLStringtoCall);
			localCookieManager = new CookieManager();
			localCookieManager.generateManagerfromHTTPString(
					URLtoCall.getHost(), CookieHTTPString);
			SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
					this);
			RequestObject thisRequest = new RequestObject(URLStringtoCall,
					localCookieManager, RequestObject.METHOD_GET, "");

			callOverviewBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		// Webhandling End

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Webhandling Start
		SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(this);
		RequestObject thisRequest = new RequestObject(TucanMobile.TUCAN_PROT
				+ TucanMobile.TUCAN_HOST + Listlinks[position],
				localCookieManager, RequestObject.METHOD_GET, "");

		callOverviewBrowser.execute(thisRequest);

		// Webhandling End
	}

	public void callsetListAdapter(ArrayList<String> Elements) {
		if (ListAdapter != null)
			ListAdapter.clear();
		ListAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Elements);
		setListAdapter(ListAdapter);
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
			if (tbdata.size() > 0) {
				Log.i(LOG_TAG, "In Event-Table angekomen");
				Intent EventStartIntent = new Intent(VV.this, VV_Events.class);
				EventStartIntent.putExtra("URL", result.getLastCalledURL());
				EventStartIntent.putExtra("User", UserName);
				EventStartIntent.putExtra("Cookie", result.getCookieManager()
						.getCookieHTTPString(TucanMobile.TUCAN_HOST));
				startActivity(EventStartIntent);
	
			} else {
				Elements ulList = doc.select("ul#auditRegistration_list").first()
						.select("li");
				Iterator<Element> ListIterator = ulList.iterator();
				ArrayList<String> AllListElementStrings = new ArrayList<String>();
				Listlinks = new String[ulList.size()];
				Log.i(LOG_TAG, "Größe: " + ulList.size());
				int i = 0;
				while (ListIterator.hasNext()) {
					Element next = ListIterator.next();
					AllListElementStrings.add(next.select("a").text());
					Listlinks[i] = next.select("a").attr("href");
					Log.i(LOG_TAG, "Bin bei " + i);
					i++;
				}
				callsetListAdapter(AllListElementStrings);
			}
		}
	}

}
