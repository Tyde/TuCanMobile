package com.dalthed.tucan.acraload;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dalthed.tucan.R;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.ui.SimpleWebListActivity;

public class LoadAcraResults extends SimpleWebListActivity {
	private CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
	private String URLStringtoCall;
	private ArrayList<String> classes,ids,urls;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acra);
		


		
		URLStringtoCall = "http://daniel-thiem.de/ACRA/export.php";
		URL URLtoCall;
		
		try {
			URLtoCall = new URL(URLStringtoCall);
			localCookieManager = new CookieManager();
			RequestObject thisRequest = new RequestObject(URLStringtoCall,
					localCookieManager, RequestObject.METHOD_GET, "");
			SimpleSecureBrowser callResultBrowser = new SimpleSecureBrowser(
					this);
			callResultBrowser.HTTPS=false;
			callResultBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
	}

	@Override
	public void onPostExecute(AnswerObject result) {
		Document doc = Jsoup.parse(result.getHTML());
		Iterator<Element> divs = doc.select("div").iterator();
		classes= new ArrayList<String>();
		ids = new ArrayList<String>();
		urls = new ArrayList<String>();
		ArrayList<String> text = new ArrayList<String>();
		
		while(divs.hasNext()){
			Element next = divs.next();
			Log.i(LOG_TAG,next.attr("title"));
			classes.add(next.attr("title"));
			ids.add(next.id());
			urls.add(next.text());
			text.add("#"+next.id()+": "+next.attr("title")+":"+next.attr("line"));
		}
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,text);
		setListAdapter(mAdapter);
		

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Class target;
		try {
			target=Class.forName("com.dalthed.tucan."+classes.get(position));
		} catch (ClassNotFoundException e) {
			target=null;
			Log.i(LOG_TAG,"com.dalthed.tucan."+classes.get(position) + " not found");	
		}
		if(target==null)
			try {
				target=Class.forName("com.dalthed.tucan.ui."+classes.get(position));
			} catch (ClassNotFoundException e) {
				Log.i(LOG_TAG,"com.dalthed.tucan.ui."+classes.get(position) + " not found");
				target=null;
			}
		
		
		if(target!=null){
			Intent loadDefectClassIntent = new Intent(this,target);
			
			loadDefectClassIntent.putExtra("URL", urls.get(position));
			loadDefectClassIntent.putExtra("Cookie", "");
			loadDefectClassIntent.putExtra("HTTPS", false);
			loadDefectClassIntent.putExtra("PREPLink", false);
			startActivity(loadDefectClassIntent);
		}
		
	}
	


}
