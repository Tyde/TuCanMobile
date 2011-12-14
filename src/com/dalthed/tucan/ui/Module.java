package com.dalthed.tucan.ui;

import java.net.URL;

import android.os.Bundle;

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
	private int mode = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events);
		BugSenseHandler.setup(this, "ed5c1682");
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		URLStringtoCall = getIntent().getExtras().getString("URL");
		
		
		localCookieManager = new CookieManager();
		localCookieManager.generateManagerfromHTTPString(
				TucanMobile.TUCAN_HOST, CookieHTTPString);
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
	public void onPostExecute(AnswerObject result) {
		
	}

}
