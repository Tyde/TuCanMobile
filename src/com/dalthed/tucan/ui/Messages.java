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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.dalthed.tucan.R;
import com.dalthed.tucan.TuCanMobileActivity;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;

public class Messages extends SimpleWebListActivity {
	private String UserName;
	private CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
	private String  URLStringtoCall;
	private int mode=0;
	private ArrayList<String> MessageLink;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messages);
		BugSenseHandler.setup(this,"ed5c1682");
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		URLStringtoCall = getIntent().getExtras().getString("URL");
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
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.messages);
	}

	class MessageAdapter extends ArrayAdapter<String>{

		ArrayList<String> messageDate,messageAuthor;
		public MessageAdapter(ArrayList<String> messageTitle,ArrayList<String> messageDate,ArrayList<String> messageAuthor) {
			super(Messages.this,R.layout.row_vv_events,R.id.row_vv_veranst,messageTitle);
			this.messageAuthor=messageAuthor;
			this.messageDate=messageDate;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			TextView TypeTextView = (TextView) row.findViewById(R.id.row_vv_type);
			TextView DozentTextView = (TextView) row.findViewById(R.id.row_vv_dozent);
			
			TypeTextView.setText(messageDate.get(position));
			DozentTextView.setText(messageAuthor.get(position));
			
			return row;
		}
		
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent MessageStartIntent = new Intent(Messages.this, SingleMessage.class);
		MessageStartIntent.putExtra("URL", TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST + MessageLink.get(position));
		MessageStartIntent.putExtra("User", UserName);
		MessageStartIntent.putExtra("Cookie", localCookieManager
				.getCookieHTTPString(TucanMobile.TUCAN_HOST));
		startActivity(MessageStartIntent);
	}
	@Override
	public void onPostExecute(AnswerObject result) {
		Document doc = Jsoup.parse(result.getHTML());
		if(doc.select("span.notLoggedText").text().length()>0){
			Intent BackToLoginIntent = new Intent(this,TuCanMobileActivity.class);
			startActivity(BackToLoginIntent);
		}
		else {
			if(mode==0){
				String AllMailLink = TucanMobile.TUCAN_PROT + TucanMobile.TUCAN_HOST + doc.select("div.tbcontrol").select("a").last().attr("href");
				mode=1;
				SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
						this);
				RequestObject thisRequest = new RequestObject(AllMailLink,
						localCookieManager, RequestObject.METHOD_GET, "");
				callOverviewBrowser.execute(thisRequest);
			}
			else {
				Iterator<Element> MessageRows = doc.select("tr.tbdata").iterator();
				ArrayList<String> MessageDate = new ArrayList<String>();
				ArrayList<String> MessageAuthor = new ArrayList<String>();
				ArrayList<String> MessageTitle = new ArrayList<String>();
				MessageLink = new ArrayList<String>();
				
				while(MessageRows.hasNext()){
					Element next = MessageRows.next();
					Elements MessageCols = next.select("td");
					if(MessageCols.size()>0){
						MessageDate.add(MessageCols.get(1).text()+" - "+MessageCols.get(2).text());
						MessageAuthor.add("Von: "+MessageCols.get(3).text());
						MessageTitle.add(MessageCols.get(4).text());
						MessageLink.add(MessageCols.get(4).select("a").attr("href"));
					}			
				}
				MessageAdapter ListAdapter = new MessageAdapter(MessageTitle, MessageDate, MessageAuthor);
				setListAdapter(ListAdapter);
			}
		}
	}

}
