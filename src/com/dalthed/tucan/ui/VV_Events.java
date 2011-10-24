package com.dalthed.tucan.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.BrowseMethods;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;


import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class VV_Events extends ListActivity {

	CookieManager localCookieManager;
	String UserName = "";
	private static final String LOG_TAG = "TuCanMobile";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vv_events);
		
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
	}
	
	class EventOverviewAdapter extends ArrayAdapter<String>{

		String[] EventType,EventDozent;
		public EventOverviewAdapter(String[] EventNames,String[] EventType,String[] EventDozent) {
			super(VV_Events.this,R.layout.row_vv_events,R.id.row_vv_veranst,EventNames);
			this.EventDozent=EventDozent;
			this.EventType=EventType;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			TextView TypeTextView = (TextView) row.findViewById(R.id.row_vv_type);
			TextView DozentTextView = (TextView) row.findViewById(R.id.row_vv_dozent);

			TypeTextView.setText(EventType[position]);
			DozentTextView.setText(EventDozent[position]);
			
			return row;
		}
		
	}
	public class SecureBrowser extends AsyncTask<RequestObject, Integer, AnswerObject> {
    	ProgressDialog dialog;
    	//ProgressBar mm_pbar;
    	@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog = ProgressDialog.show(VV_Events.this,"","Lade...",true);
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
			Log.i(LOG_TAG,"HTML zum parsen bereit");
			Document doc = Jsoup.parse(result.getHTML());
			Elements tbdata=doc.select("tr.tbdata");
			String[] Eventnames=new String[tbdata.size()];
			String[] Eventdozent=new String[tbdata.size()];
			String[] Eventtype=new String[tbdata.size()];
			int i=0;
			if(tbdata.size()>0){
				Iterator<Element> EventListIterator = tbdata.iterator();
				while(EventListIterator.hasNext()){
					Element nextElement = EventListIterator.next();
					Elements rows = nextElement.select("td");
					Element leftcolumn = rows.get(1);
					Element rightcolumn = rows.get(3);
					Eventnames[i]=leftcolumn.select("a").text();
					List<Node> importantnotes = leftcolumn.childNodes();
					Iterator<Node> imnit = importantnotes.iterator();
					while(imnit.hasNext()){
						Log.i(LOG_TAG,imnit.next().outerHtml());
					}
					Eventdozent[i]=importantnotes.get(3).toString();
					Eventtype[i]=rightcolumn.text();
					Log.i(LOG_TAG,Eventtype[i]);
					i++;
				}
			}
			//EventAdapter TableAdapter = new EventAdapter(VV_Events.this, Eventnames, Eventtype, Eventdozent);
			ArrayAdapter TableAdapter = new EventOverviewAdapter(Eventnames, Eventtype, Eventdozent);
			callsetListAdapter(TableAdapter);
			dialog.dismiss();
		}
	}
	public void callsetListAdapter(ArrayAdapter ElementsAdapter){
		
		setListAdapter(ElementsAdapter);	
	}
	
}
