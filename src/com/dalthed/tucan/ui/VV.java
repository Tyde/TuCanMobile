package com.dalthed.tucan.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.BrowseMethods;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;

public class VV extends ListActivity {

	CookieManager localCookieManager;
	String UserName = "";
	String[] Listlinks;
	ArrayAdapter<String> ListAdapter = null;
	private static final String LOG_TAG = "TuCanMobile";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vv);
		
		//Webhandling Start
        String CookieHTTPString = getIntent().getExtras().getString("Cookie");
        String URLStringtoCall = getIntent().getExtras().getString("URL");
        UserName = getIntent().getExtras().getString("UserName");
        URL URLtoCall;
		try {
			URLtoCall = new URL(URLStringtoCall);
			localCookieManager=new CookieManager();
	        localCookieManager.generateManagerfromHTTPString(URLtoCall.getHost(), CookieHTTPString);
	        SecureBrowser callOverviewBrowser = new SecureBrowser();
	        RequestObject thisRequest = new RequestObject(URLStringtoCall,localCookieManager, RequestObject.METHOD_GET, "");
	        
	        callOverviewBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG, e.getMessage());
		}
		//Webhandling End
		
	}
	
	public class SecureBrowser extends AsyncTask<RequestObject, Integer, AnswerObject> {
    	ProgressDialog dialog;
    	//ProgressBar mm_pbar;
    	@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog = ProgressDialog.show(VV.this,"","Lade...",true);
    		//mm_pbar=(ProgressBar) findViewById(R.id.mm_progress);
    		//mm_pbar.setVisibility(View.VISIBLE);
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
			//mm_pbar.setVisibility(View.GONE);
			
			//HTML auslesen
			Document doc = Jsoup.parse(result.getHTML());
			Elements ulList = doc.select("ul#auditRegistration_list").first().select("li");
			Iterator<Element> ListIterator = ulList.iterator();
			String[] AllListElementStrings = new String[ulList.size()];
			Listlinks = new String[ulList.size()];
			int i=0;
			while(ListIterator.hasNext()){
				AllListElementStrings[i]=ListIterator.next().select("a").text();
				Listlinks[i]=ListIterator.next().select("a").attr("href");
				i++;
			}
			callsetListAdapter(AllListElementStrings);
			
			
			
			dialog.dismiss();
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		//Webhandling Start
        SecureBrowser callOverviewBrowser = new SecureBrowser();
        RequestObject thisRequest = new RequestObject(TucanMobile.TUCAN_PROT+
        		TucanMobile.TUCAN_HOST+Listlinks[position],
        		localCookieManager, 
        		RequestObject.METHOD_GET, "");
	        
	    callOverviewBrowser.execute(thisRequest);
		
		//Webhandling End
	}

	public void callsetListAdapter(String[] Elements){
		if(ListAdapter!=null)
			ListAdapter.clear();
		ListAdapter=new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				Elements);
		setListAdapter(ListAdapter);	
	}

}
