package com.dalthed.tucan.ui;


import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.dalthed.tucan.R;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;

public class SingleMessage extends SimpleWebActivity {
	
	private CookieManager localCookieManager;
	private static final String LOG_TAG = "TuCanMobile";
	private String  URLStringtoCall;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singlemessage);
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		URLStringtoCall = getIntent().getExtras().getString("URL");
		URL URLtoCall;

		try {
			URLtoCall = new URL(URLStringtoCall);
			localCookieManager = new CookieManager();
			localCookieManager.generateManagerfromHTTPString(
					URLtoCall.getHost(), CookieHTTPString);
			SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
					this);
			RequestObject thisRequest = new RequestObject(URLStringtoCall,
					localCookieManager, RequestObject.METHOD_GET, "");

			callOverviewBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
	}

	@Override
	public void onPostExecute(AnswerObject result) {
		Document doc = Jsoup.parse(result.getHTML());
		Elements TableRows = doc.select("table.tb").select("tr");
		TextView authorTextView = (TextView) findViewById(R.id.message_Author);
		TextView dateTextView = (TextView) findViewById(R.id.message_Date);
		TextView titleTextView = (TextView) findViewById(R.id.message_title);
		TextView textTextView = (TextView) findViewById(R.id.message_text);
		
		authorTextView	.setText(TableRows.get(2).select("td").get(1).text());
		dateTextView	.setText(TableRows.get(3).select("td").get(1).text());
		titleTextView	.setText(TableRows.get(4).select("td").get(1).text());
		textTextView	.setText(TableRows.get(5).select("td").get(1).html().replaceAll("<br />", "\n"));
		

	}

}
