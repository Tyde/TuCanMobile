package com.dalthed.tucan.Connection;

import java.net.MalformedURLException;
import java.net.URL;

public class RequestObject {
	private URL RequestURL;
	private CookieManager RequestCookies;
	private String RequestMethod;
	private String RequestpostData;
	
	final public static String METHOD_GET="GET";
	final public static String METHOD_POST="POST";
	
	
	public RequestObject(String RequestString,CookieManager RequestCookiemanager,String method,String postdata) throws MalformedURLException  {
		this.RequestURL=new URL(RequestString);
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
	
	public RequestObject(String RequestString,String method,String postdata) throws MalformedURLException{
		new RequestObject(RequestString,new CookieManager(),method,postdata);
	}
	
	public URL getURL() {
		return this.RequestURL;		
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
