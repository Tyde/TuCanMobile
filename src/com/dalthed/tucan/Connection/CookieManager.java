package com.dalthed.tucan.Connection;

import java.util.HashMap;
import java.util.Map;

import java.util.Iterator;

import android.util.Log;


public class CookieManager {

	private Map<String,Map<String,String>> Cookies;
	private static final String LOG_TAG = "TuCanMobile";
	
	public CookieManager(){
		Cookies = new HashMap<String,Map<String,String>>();
	}
	
	public void inputCookie(String domain,String name,String value){
		if(Cookies.get(domain)==null) {
			Map <String,String> DomainMap = new HashMap<String,String>();
			Cookies.put(domain, DomainMap);
			Log.i(LOG_TAG,"Domain in Storage registriert: "+domain);
		}
		Map <String,String> DomainMap = Cookies.get(domain);
		DomainMap.put(name, value);
		Log.i(LOG_TAG,"Cookie in Storage ("+domain+") aufgenommen: "+name+ " = " + value);
	}
	
	public boolean domain_exists(String domain){
		if(Cookies.get(domain)==null)
			return false;
		else 
			return true;
	}
	
	@SuppressWarnings("rawtypes")
	public String getCookieHTTPString(String domain) {
		String[] HTTPString = new String[Cookies.get(domain).size()];
		Iterator it= Cookies.get(domain).entrySet().iterator();
		int i=0;
		while (it.hasNext()){
			Map.Entry pairs = (Map.Entry) it.next();
			HTTPString[i]=pairs.getKey() + "=" + pairs.getValue();
			i++;
		}
		if (HTTPString.length == 0)
			return "";
		
		String glue = ";";
		StringBuilder sb = new StringBuilder();
		for (String s : HTTPString) {
			sb.append(s).append(glue);
		}
		return sb.substring(0, sb.length() - glue.length());
		
	}
}
