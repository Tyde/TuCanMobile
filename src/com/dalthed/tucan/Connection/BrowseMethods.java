package com.dalthed.tucan.Connection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.util.Log;

public class BrowseMethods {
	private HttpsURLConnection HTTPConnection;
	private CookieManager myCookies;
	
	private static final String LOG_TAG = "TuCanMobile";
	
	private void setImportantHeaders(String RequestMethod,String domain){
		try {
			HTTPConnection.setRequestMethod(RequestMethod);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HTTPConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
		HTTPConnection.setInstanceFollowRedirects(false);
		if(myCookies.domain_exists(domain)){
			HTTPConnection.setRequestProperty("Cookie", myCookies.getCookieHTTPString(domain));
		}
	}

	public AnswerObject browse(RequestObject requestInfo){
		String redirectURL="";
		String alllines="";
		
		try {
			URL realURL=requestInfo.getmyURL();
			myCookies=requestInfo.getCookies();
			String RequestMethod=requestInfo.getMethod();
			String postdata=requestInfo.getPostData();
			HTTPConnection = (HttpsURLConnection) realURL.openConnection();
			Log.i(LOG_TAG, "Started Connection with"+ realURL.getHost());
			
			
			setImportantHeaders(RequestMethod,realURL.getHost());
			if(RequestMethod=="POST")
			{
				HTTPConnection.setDoOutput(true);
				OutputStreamWriter out = new OutputStreamWriter(HTTPConnection.getOutputStream());
				out.write(postdata);
				out.close();
			}
			
			//HTTPConnection.setChunkedStreamingMode(0);
			
			
			InputStream in = HTTPConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader bin = new BufferedReader(isr);
			for(int n=0;;n++){
				String headerValue = HTTPConnection.getHeaderField(n);
				String headerName = HTTPConnection.getHeaderFieldKey(n);
				if(headerValue==null && headerName==null){
					break;
				}
				Log.i(LOG_TAG,headerName+":"+headerValue);
				
				if("Set-Cookie".equalsIgnoreCase(headerName)){
					Log.i(LOG_TAG,"Lese Cookies aus");
					String[] multipleCookies = headerValue.split(";\\s*");
					for(String ccy : multipleCookies){
						String[] eachVal=ccy.split("=");
						if(eachVal.length==2)
							myCookies.inputCookie(realURL.getHost(),eachVal[0], eachVal[1]);
						else
							myCookies.inputCookie(realURL.getHost(),eachVal[0], null);
					}
				}
				if("refresh".equalsIgnoreCase(headerName)){
					String[] getredirectURL=headerValue.split("URL=");
					redirectURL=getredirectURL[1];
				}
				
				
			}
			int contentlength =HTTPConnection.getContentLength();
			Log.i(LOG_TAG,contentlength+"...");
			
			String inputLine;
			
			while ((inputLine = bin.readLine()) != null){
				//Log.i(LOG_TAG,(alllines.length()/contentlength)+"%");
				alllines+=inputLine;
			}
			in.close();
			Log.i(LOG_TAG,alllines);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AnswerObject(alllines,redirectURL,myCookies);
	}
}
