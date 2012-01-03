package com.dalthed.tucan.ui;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;

public class Module extends SimpleWebListActivity {
	private CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
	private String URLStringtoCall;
	private ArrayList<String> eventLinks;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.module);
		BugSenseHandler.setup(this, "ed5c1682");
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		URLStringtoCall = getIntent().getExtras().getString("URL");
		localCookieManager = new CookieManager();
		if(CookieHTTPString!=null){
			
			localCookieManager.generateManagerfromHTTPString(
					TucanMobile.TUCAN_HOST, CookieHTTPString);
		}
		else {
			
		}
		
		if(URLStringtoCall.equals("HTML")){
			String HTML = getIntent().getExtras().getString("HTML");
			AnswerObject result = new AnswerObject(HTML, "", localCookieManager, "");
			onPostExecute(result);
		}
		else {
			callResultBrowser = new SimpleSecureBrowser(this);
			RequestObject thisRequest = new RequestObject(URLStringtoCall,
					localCookieManager, RequestObject.METHOD_GET, "");

			callResultBrowser.execute(thisRequest);
		}

	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent StartSingleEventIntent = new Intent(Module.this,
				FragmentSingleEvent.class);
		StartSingleEventIntent.putExtra(
				TucanMobile.EXTRA_URL,
				TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST
						+ eventLinks.get(position));
		StartSingleEventIntent.putExtra(TucanMobile.EXTRA_COOKIE,
				localCookieManager
						.getCookieHTTPString(TucanMobile.TUCAN_HOST));
		
		startActivity(StartSingleEventIntent);
	}


	@Override
	public void onPostExecute(AnswerObject result) {
		Document doc = Jsoup.parse(result.getHTML());
		String title = doc.select("h1").text();
		Elements events = doc.select("a[name=eventLink]");
		//System.out.println(events);
		ArrayList<String> eventNames = new ArrayList<String>();
						  eventLinks = new ArrayList<String>();
		if(events.size()%3==0){
			for (int i = 0; i < events.size(); i += 3) {
				eventNames.add(events.get(i).text()+" "+events.get(i+1).text()+" "+events.get(i+2).text());
				eventLinks.add(events.get(i).attr("href"));
				System.out.println(events.get(i).attr("href"));
			}
		}
		TextView titleTextView = (TextView) findViewById(R.id.module_title);
		titleTextView.setText(title);
		ArrayAdapter<String> simpleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1	,eventNames);
		setListAdapter(simpleAdapter);
		
		
	}

}
