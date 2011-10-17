package com.dalthed.tucan.Connection;

import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class RequestObject {
	private URL RequestURL;
	private CookieManager RequestCookies;
	private String RequestMethod;
	private String RequestpostData;
	
	final public static String METHOD_GET="GET";
	final public static String METHOD_POST="POST";
	
	private static final String LOG_TAG = "TuCanMobile";
	
	public RequestObject(String RequestString,CookieManager RequestCookiemanager,String method,String postdata)  {
		try {
			this.RequestURL=new URL(RequestString);
			Log.i(LOG_TAG,"Hier haste die URL:"+RequestString);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG,"Malfomed URL");
		}
		this.RequestCookies=RequestCookiemanager;
		if(method==METHOD_GET){
			this.RequestMethod=METHOD_GET;
		}
		else if(method==METHOD_POST){
			this.RequestMethod=METHOD_POST;
		}
		else {
			this.RequestMethod=METHOD_GET;
		}
		this.RequestpostData=postdata;
	}
	
	public RequestObject(String RequestString,String method,String postdata) {
		this(RequestString,new CookieManager(),method,postdata);
	}
	
	public URL getmyURL() {
		Log.i(LOG_TAG,"geturlHier haste die URL:"+this.RequestURL.getHost());
		return this.RequestURL;		
	}
	
	public void setCookieManager(CookieManager newManager) {
		this.RequestCookies=newManager;
	}
	
	public CookieManager getCookies() {
		return this.RequestCookies;
	}
	
	public String getPostData() {
		return this.RequestpostData;
	}
	
	public String getMethod() {
		return this.RequestMethod;
	}
	
}
