package com.dalthed.tucan.scraper;

import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.BrowserAnswerReciever;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;
import com.dalthed.tucan.ui.VV;
import com.dalthed.tucan.ui.VV_Events;

public class VVScraper extends BasicScraper implements OnItemClickListener {

	private String userName;
	private CookieManager cookieManager;
	public boolean haslinkstoclick;
	public String[] Listlinks;
	public Boolean hasBothCategoryAndEvents = false;
	

	public VVScraper(Context context, AnswerObject result, String userName) {
		super(context, result);
		this.userName = userName;
		
		this.cookieManager = result.getCookieManager();
	}

	@Override
	public ListAdapter scrapeAdapter(int mode) throws LostSessionException,TucanDownException {
		if (checkForLostSeesion()) {
			Elements tbdata = doc.select("tr.tbdata");

			ArrayAdapter<String> listAdapter = null;

			hasBothCategoryAndEvents = false;
			if (tbdata.size() > 0) {
				Element ulElement = doc.select("ul#auditRegistration_list").first();
				if (ulElement != null) {
					Elements liList = ulElement.select("li");
					if (liList.size() > 0) {
						// Sowohl Kategorieliste, als auch einzelne
						// Veranstaltungen vorhanden.
						hasBothCategoryAndEvents = true;
						if (mode == 1) {
							return getVVCategory();
						}

					} else {
						startEventOverview();
					}
				} else {
					startEventOverview();
				}

			} else {
				if (doc.select("div.tbdata").size() > 0) {
					ArrayList<String> noEvents = new ArrayList<String>();
					noEvents.add("Es wurden keine Veranstaltungen gefunden.");
					listAdapter = convertArrayListToArrayAdapter(noEvents);
					haslinkstoclick = false;
				} else {
					listAdapter = getVVCategory();
				}

			}
			return listAdapter;
		}
		return null;

	}

	/**
	 * @return
	 */
	private ArrayAdapter<String> getVVCategory() {
		ArrayAdapter<String> listAdapter;
		Elements ulList = doc.select("ul#auditRegistration_list").first().select("li");
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
		haslinkstoclick = true;
		listAdapter = convertArrayListToArrayAdapter(AllListElementStrings);
		return listAdapter;
	}

	private ArrayAdapter<String> convertArrayListToArrayAdapter(ArrayList<String> list) {
		ArrayAdapter<String> listAdapter;
		listAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list);
		return listAdapter;
	}

	/**
	 * Starts a EventOverview Activity
	 */
	private void startEventOverview() {
		Log.i(LOG_TAG, "In Event-Table angekomen");
		Intent EventStartIntent = new Intent(context, VV_Events.class);
		EventStartIntent.putExtra("URL", lastCalledUrl);
		EventStartIntent.putExtra("User", userName);
		EventStartIntent.putExtra("Cookie",
				cookieManager.getCookieHTTPString(TucanMobile.TUCAN_HOST));
		context.startActivity(EventStartIntent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(this.haslinkstoclick==true && context instanceof BrowserAnswerReciever){
			SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser((BrowserAnswerReciever) context);
			RequestObject thisRequest = new RequestObject(TucanMobile.TUCAN_PROT
					+ TucanMobile.TUCAN_HOST + this.Listlinks[position],
					cookieManager, RequestObject.METHOD_GET, "");

			callOverviewBrowser.execute(thisRequest);
		}
	}
	
	public String getlastCalledURL(){
		return lastCalledUrl;
	}

	

}
