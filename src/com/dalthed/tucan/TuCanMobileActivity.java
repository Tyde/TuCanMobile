package com.dalthed.tucan;


import java.net.MalformedURLException;
import java.net.URL;

import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.BrowseMethods;
import com.dalthed.tucan.Connection.CookieManager;

import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.ui.MainMenu;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;




public class TuCanMobileActivity extends Activity {
    /** Called when the activity is first created. */
    //private HTTPSbrowser mBrowserService;
	private static final String LOG_TAG = "TuCanMobile";
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    public void onClickSendLogin (final View sfNormal) {
    	HTTPSBrowser newBrowser = new HTTPSBrowser();
    	
    	
    	try {
			RequestObject[] thisRequest = new RequestObject[4];
			thisRequest[0] = new RequestObject("https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi?APPNAME=CampusNet&PRGNAME=STARTPAGE_DISPATCH&ARGUMENTS=-N000000000000001", RequestObject.METHOD_GET, "");
			String postdata= "usrname=se68kado&pass=326435&APPNAME=CampusNet&PRGNAME=LOGINCHECK&ARGUMENTS=clino%2Cusrname%2Cpass%2Cmenuno%2Cpersno%2Cbrowser%2Cplatform&clino=000000000000001&menuno=000344&persno=00000000&browser=&platform=";
			thisRequest[1] = new RequestObject("https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi", RequestObject.METHOD_POST, postdata);
			
    		
			//RequestObject otherRequest=new RequestObject("https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi", RequestObject.METHOD_POST, postdata);
			//Log.i(LOG_TAG, "This is Request 0 "+ otherRequest.getmyURL().getHost() );
			newBrowser.execute(thisRequest);
			
			
			//Toast notifyall = Toast.makeText(TucanMobile.getAppContext(), newBrowser.get().getHTML(), Toast.LENGTH_SHORT);
			//notifyall.show();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG,"FEHLER");
			//Toast notifyall = Toast.makeText(TucanMobile.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT);
			//notifyall.show();
		}
    	
		
		/*
    	final Intent i = new Intent(this,MainMenu.class);
    	startActivity(i);*/
    	
    }
    
    public class HTTPSBrowser extends AsyncTask<RequestObject, Integer, AnswerObject>  {
    	ProgressDialog dialog;
    	protected void onPreExecute() {
             dialog = ProgressDialog.show(TuCanMobileActivity.this,"","Anmelden...",true);
        }
    	
    	@Override
    	protected AnswerObject doInBackground(RequestObject... requestInfo) {
    		AnswerObject answer = new AnswerObject("", "", new CookieManager());
    		for(int i = 0;i<requestInfo.length;i++){
    			
    			if(requestInfo[i]!= null){
    				BrowseMethods Browser=new BrowseMethods();
    				Log.i(LOG_TAG, "Browse with RequestObject:" + i);
    				answer=Browser.browse(requestInfo[0]);  
    				
    			}
    			else{
    				break;
    			}
    			if(i<requestInfo.length)
    			{
	    			if(answer.getRedirectURLString()!=""){
	    				Log.i(LOG_TAG, "Insert new Redirect URL in RequestObject:" + answer.getRedirectURLString());
						requestInfo[i+1]=new RequestObject("https://"+requestInfo[i].getmyURL().getHost()+answer.getRedirectURLString(), RequestObject.METHOD_GET, "");
	    			}
	    			requestInfo[i+1].setCookieManager(answer.getCookieManager());
    			}
    			else{
    				Log.e(LOG_TAG,"Zu viele Redirects");
    			}
    		}
    		
    		return  answer;
    	}
    	
    	
    	protected void onPostExecute(AnswerObject result) {
    		dialog.dismiss();
    		TextView answertextv = (TextView) findViewById(R.id.textView2);
    		answertextv.setText(result.getHTML());
    		Toast notifyall = Toast.makeText(getApplicationContext(), result.getHTML()+"DONE", Toast.LENGTH_SHORT);
    		notifyall.show();
    	}
    }
    
    public void onRequestisAnswered(){
    	
    }
}