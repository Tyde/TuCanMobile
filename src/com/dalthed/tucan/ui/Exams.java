package com.dalthed.tucan.ui;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.dalthed.tucan.R;

import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;

public class Exams extends SimpleWebListActivity {
	private String UserName;
	private CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exams);
		
		//Webhandling Start
        String CookieHTTPString = getIntent().getExtras().getString("Cookie");
        String URLStringtoCall = getIntent().getExtras().getString("URL");
        UserName = getIntent().getExtras().getString("UserName");
        URL URLtoCall;
        
		try {
			URLtoCall = new URL(URLStringtoCall);
			localCookieManager=new CookieManager();
	        localCookieManager.generateManagerfromHTTPString(URLtoCall.getHost(), CookieHTTPString);
	        SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(this);
	        RequestObject thisRequest = new RequestObject(URLStringtoCall,localCookieManager, RequestObject.METHOD_GET, "");
	        
	        callOverviewBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG, e.getMessage());
		}
		//Webhandling End
	}
	
	
	
	public void onPostExecute(AnswerObject result) {
		System.out.println(result.getHTML());
		String myHTML = result.getHTML();
		Document doc = Jsoup.parse(result.getHTML());
		Elements links=doc.select("li#link000280").select("li");
		Iterator<Element> linkIt = links.iterator();
		ArrayList<String> examLinks = new ArrayList<String>();
		ArrayList<String> examNames = new ArrayList<String>();
		while(linkIt.hasNext()){
			Element next=linkIt.next();
			String id=next.id();
			
			if(id.equals("link000318") || id.equals("link000316")){
				String link= next.select("a").attr("href");
				String name= next.select("a").text();
				examLinks.add(link);
				examNames.add(name);
			}
			else if(id.equals("link000323")) {
				Iterator<Element> subLinks = next.select("li.depth_3").iterator();
				while (subLinks.hasNext()) {
					Element subnext=subLinks.next();
					String link= subnext.select("a").attr("href");
					String name= subnext.select("a").text();
					examLinks.add(link);
					examNames.add(name);
				}
			}
			
			//Log.i(LOG_TAG,next.toString()+"Hakki");
		}
		ArrayAdapter<String> ListAdapter=new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				examNames);
		setListAdapter(ListAdapter);
	}
	
	

}
