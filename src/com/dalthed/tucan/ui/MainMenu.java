package com.dalthed.tucan.ui;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dalthed.tucan.R;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.BrowseMethods;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;

public class MainMenu extends ListActivity  {
	
	CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        String CookieHTTPString = getIntent().getExtras().getString("Cookie");
        String lastCalledURLString = getIntent().getExtras().getString("URL");
        URL lastCalledURL;
		try {
			lastCalledURL = new URL(lastCalledURLString);
			localCookieManager=new CookieManager();
	        localCookieManager.generateManagerfromHTTPString(lastCalledURL.getHost(), CookieHTTPString);
	        SecureBrowser callOverviewBrowser = new SecureBrowser();
	        RequestObject thisRequest = new RequestObject(lastCalledURLString,localCookieManager, RequestObject.METHOD_GET, "");
	        
	        callOverviewBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG, e.getMessage());
		}
        
        showMenuElements();
    }
    
    public class SecureBrowser extends AsyncTask<RequestObject, Integer, AnswerObject> {
    	ProgressDialog dialog;
    	
    	@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog = ProgressDialog.show(MainMenu.this,"","Lade...",true);
		}
    	
		@Override
		protected AnswerObject doInBackground(RequestObject... requestInfo) {
			AnswerObject answer = new AnswerObject("", "", null,null);
			RequestObject significantRequest = requestInfo[0];
			BrowseMethods Browser=new BrowseMethods();
			answer=Browser.browse(significantRequest); 
			return answer;
		}


		@Override
		protected void onPostExecute(AnswerObject result) {
			dialog.setMessage("Berechne...");
			
			Document doc = Jsoup.parse(result.getHTML());
			Element EventTable = doc.select("table.nb").first();
			Elements EventRows = EventTable.select("tr.tbdata");
			Iterator<Element> RowIt = EventRows.iterator();
			String[] Events = new String[EventRows.size()];
			int i=0;
			while(RowIt.hasNext()){
				Element currentElement = (Element) RowIt.next();
				String EventString = currentElement.select("td[headers=Name]").select("a").first().text();
				Events[i]=EventString;
				i++;
				Toast.makeText(MainMenu.this, EventString, Toast.LENGTH_LONG).show();
			}
			
		
			dialog.dismiss();
		}
		
		
		
    	
    }
    private void showMenuElements() {
    	
    	final ArrayAdapter<String> ElementAdapter = 
    		new ArrayAdapter<String>(this,
    				android.R.layout.simple_list_item_1
    				, getResources().getStringArray(R.array.mainmenu_options));
    	setListAdapter(ElementAdapter);    	
    	
    }
    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		final Toast hinweis = Toast.makeText(this, "Hallo, du hast "+position+" geklickt.", Toast.LENGTH_LONG);
		hinweis.show();
		switch (position) {
		case 0:
			Intent StartVVIntent = new Intent(this, VV.class);
			startActivity(StartVVIntent);
			//Vorlesungsverzeichnis
			break;
		case 1:
			//Stundenplan
			break;
		case 2:
			//Veranstaltungen
			break;
		case 3: 
			//Prüfungen
			break;
		
		}
	}
    
}
