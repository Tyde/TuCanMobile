package com.dalthed.tucan;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.BrowseMethods;
import com.dalthed.tucan.Connection.CookieManager;

import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.ui.MainMenu;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
    		//TuCan leitet 3 mal weiter, damit der Login-Vorgang Abgeschlossen wird 
			RequestObject[] thisRequest = new RequestObject[4];
			//Cookie Abholen
			thisRequest[0] = new RequestObject("https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi?APPNAME=CampusNet&PRGNAME=STARTPAGE_DISPATCH&ARGUMENTS=-N000000000000001", RequestObject.METHOD_GET, "");
			
			//Login Senden
			EditText usrnameField 	= 	(EditText) findViewById(R.id.login_usrname);
			EditText pwdField		=	(EditText) findViewById(R.id.login_pw);
			String	usrname	=	usrnameField.getText().toString();
			String 	pwd		=	pwdField.getText().toString();
			
			String postdata= "usrname="+usrname+"&pass="+pwd+"&APPNAME=CampusNet&PRGNAME=LOGINCHECK&ARGUMENTS=clino%2Cusrname%2Cpass%2Cmenuno%2Cpersno%2Cbrowser%2Cplatform&clino=000000000000001&menuno=000344&persno=00000000&browser=&platform=";
			thisRequest[1] = new RequestObject("https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi", RequestObject.METHOD_POST, postdata);
			
    		
			
			newBrowser.execute(thisRequest);
			
			
		} catch (Exception e) {
			Log.e(LOG_TAG,"FEHLER" +e.getMessage());
			//Toast notifyall = Toast.makeText(TucanMobile.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT);
			//notifyall.show();
		}
    	
		
		/*
    	*/
    	
    }
    
    public class HTTPSBrowser extends AsyncTask<RequestObject, Integer, AnswerObject>  {
    	ProgressDialog dialog;
    	protected void onPreExecute() {
             dialog = ProgressDialog.show(TuCanMobileActivity.this,"","Anmelden...",true);
        }
    	
    	@Override
    	protected AnswerObject doInBackground(RequestObject... requestInfo) {
    		AnswerObject answer = new AnswerObject("", "", null,null);
    		for(int i = 0;i<requestInfo.length;i++){
    			
    			if(requestInfo[i]!= null){
    				BrowseMethods Browser=new BrowseMethods();
    				Log.i(LOG_TAG, "Browse with RequestObject:" + i + " URL:"+requestInfo[i].getmyURL().toString());
    				answer=Browser.browse(requestInfo[i]);  
    				
    			}
    			else{
    				break;
    			}
    			if(i<requestInfo.length-1)
    			{
	    			if(answer.getRedirectURLString()!="" && requestInfo[i+1]==null){
	    				Log.i(LOG_TAG, "Insert new Redirect URL in RequestObject:" + answer.getRedirectURLString());
						requestInfo[i+1]=new RequestObject("https://"+requestInfo[i].getmyURL().getHost()+answer.getRedirectURLString()
								, RequestObject.METHOD_GET
								, "");
	    				
	    			}
	    			if(requestInfo[i+1]!= null)
	    				requestInfo[i+1].setCookieManager(answer.getCookieManager());
    			}
    			else{
    				Log.e(LOG_TAG,"Zu viele Redirects");
    			}
    		}
    		
    		return  answer;
    	}
    	
    	
    	protected void onPostExecute(AnswerObject result) {
    		Pattern findUser = Pattern.compile("<span\\s+class=\"loginDataName\"\\s+id=\"loginDataName\"><b>Name<span\\s+class=\"colon\">:</span>\\s+</b>(.*?)</span>");
    		Matcher findUsermatcher = findUser.matcher(result.getHTML());
    		boolean found=false;
    		String User="";
    		while(findUsermatcher.find()){
    			User=findUsermatcher.group(1);
    			Log.i(LOG_TAG,"Match:"+findUsermatcher.group(1));
    			found=true;
    		}
    		if(found==true){
    			dialog.dismiss();
        		final Intent i = new Intent(TuCanMobileActivity.this,MainMenu.class);
        		i.putExtra("Cookie", result.getCookieManager().getCookieHTTPString("www.tucan.tu-darmstadt.de"));
        		i.putExtra("URL", result.getLastCalledURL());
        		startActivity(i);
    		}
    		else{
    			dialog.dismiss();
    			Toast wrongLoginNotif = Toast.makeText(TuCanMobileActivity.this, "Login fehlerhaft", Toast.LENGTH_LONG);
    			wrongLoginNotif.show();
    			
    		}
    		
    		
    		
        	
    		
    	}
    }
    
    public void onRequestisAnswered(){
    	
    }
}