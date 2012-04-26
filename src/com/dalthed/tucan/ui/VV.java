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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bugsense.trace.BugSenseHandler;
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
	boolean haslinkstoclick = false;
	private static final String LOG_TAG = "TuCanMobile";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vv);
		BugSenseHandler.setup(this,"ed5c1682");
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
			callResultBrowser = new SimpleSecureBrowser(
					this);
			RequestObject thisRequest = new RequestObject(URLStringtoCall,
					localCookieManager, RequestObject.METHOD_GET, "");

			callResultBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		// Webhandling End

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Webhandling Start
		if(haslinkstoclick==true){
			SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(this);
			RequestObject thisRequest = new RequestObject(TucanMobile.TUCAN_PROT
					+ TucanMobile.TUCAN_HOST + Listlinks[position],
					localCookieManager, RequestObject.METHOD_GET, "");

			callOverviewBrowser.execute(thisRequest);
		}
		

		// Webhandling End
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.vv);
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
			BackToLoginIntent.putExtra("lostSession", true);
			startActivity(BackToLoginIntent);
		}
		else {
			Elements tbdata = doc.select("tr.tbdata");
			sendHTMLatBug(doc.html());
			
			/*
			 * Crash for Bug Reporting
			 */
			if(TucanMobile.CRASH){
				String crasher = "";
				crasher.charAt(48);
			}
			
			
			if (tbdata.size() > 0) {
				Log.i(LOG_TAG, "In Event-Table angekomen");
				Intent EventStartIntent = new Intent(VV.this, VV_Events.class);
				EventStartIntent.putExtra("URL", result.getLastCalledURL());
				EventStartIntent.putExtra("User", UserName);
				EventStartIntent.putExtra("Cookie", result.getCookieManager()
						.getCookieHTTPString(TucanMobile.TUCAN_HOST));
				startActivity(EventStartIntent);
	
			} else {
				if(doc.select("div.tbdata").size()>0){
					ArrayList<String> noEvents = new ArrayList<String>();
					noEvents.add("Es wurden keine Veranstaltungen gefunden.");
					callsetListAdapter(noEvents);
					haslinkstoclick=false;
				}
				else {
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
					haslinkstoclick=true;
					callsetListAdapter(AllListElementStrings);
				}
				
			}
		}
	}

}
