/**
 *	This file is part of TuCan Mobile.
 *
 *	TuCan Mobile is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	TuCan Mobile is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with TuCan Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dalthed.tucan.acraload;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Intent;
import android.os.Bundle;
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
import com.dalthed.tucan.util.ConfigurationChangeStorage;

public class LoadAcraResults extends SimpleWebListActivity {
	private CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
	private final String URLStringtoCall = "http://daniel-thiem.de/ACRA/export.php";
	private ArrayList<String> classes,ids,urls;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acra);
		
		URL URLtoCall;
		
		try {
			URLtoCall = new URL(URLStringtoCall);
			localCookieManager = new CookieManager();
			localCookieManager.inputCookie("daniel-thiem.de", "canView", "16ede40c878aee38d0882b3a6b2642c0ae76dafb");
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
		if(target==null)
			try {
				target=Class.forName("com.dalthed.tucan.scraper."+classes.get(position));
			} catch (ClassNotFoundException e) {
				Log.i(LOG_TAG,"com.dalthed.tucan.scraper."+classes.get(position) + " not found");
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


	@Override
	public ConfigurationChangeStorage saveConfiguration() {
		return null;
	}


	@Override
	public void retainConfiguration(ConfigurationChangeStorage conf) {
	}
}
