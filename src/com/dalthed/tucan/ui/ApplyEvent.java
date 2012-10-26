package com.dalthed.tucan.ui;

import android.os.Bundle;

import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.util.ConfigurationChangeStorage;

public class ApplyEvent extends SimpleWebListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		BugSenseHandler.setup(this, "ed5c1682");
		String CookieHTTPString = getIntent().getExtras().getString("Cookie");
		URLStringtoCall = getIntent().getExtras().getString("URL");
		URL URLtoCall;

		try {
			URLtoCall = new URL(URLStringtoCall);
			localCookieManager = new CookieManager();
			localCookieManager.generateManagerfromHTTPString(
					URLtoCall.getHost(), CookieHTTPString);
			callResultBrowser = new SimpleSecureBrowser(this);
			RequestObject thisRequest = new RequestObject(URLStringtoCall,
					localCookieManager, RequestObject.METHOD_GET, "");

			callResultBrowser.execute(thisRequest);
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, e.getMessage());
		}*/
	}

	public void onPostExecute(AnswerObject result) {
	}

	@Override
	public ConfigurationChangeStorage saveConfiguration() {
		return null;
	}

	@Override
	public void retainConfiguration(ConfigurationChangeStorage conf) {
	}

	
	
	

}
