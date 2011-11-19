package com.dalthed.tucan;



import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.BrowseMethods;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.Connection.RequestObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.preferences.MainPreferences;
import com.dalthed.tucan.ui.MainMenu;
import com.dalthed.tucan.ui.ProgressBarDialogFactory;
import com.dalthed.tucan.ui.SimpleWebActivity;




public class TuCanMobileActivity extends SimpleWebActivity {
    /** Called when the activity is first created. */
    //private HTTPSbrowser mBrowserService;
	private static final String LOG_TAG = "TuCanMobile";
	private EditText usrnameField;
	private EditText pwdField;
	String SessionArgument="";
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        BugSenseHandler.setup(this,"ed5c1682");
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        
        
        final SharedPreferences einstellungen = MainPreferences.getSettings(this);
        String tuid= einstellungen.getString("tuid","");
        String pw = einstellungen.getString("pw", "");
        
        usrnameField 	= 	(EditText) findViewById(R.id.login_usrname);
		pwdField		=	(EditText) findViewById(R.id.login_pw);
		usrnameField.setText(tuid);
		pwdField.setText(pw);
        //https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi?APPNAME=CampusNet&PRGNAME=MLSSTART&ARGUMENTS=
		String settCookie=einstellungen.getString("Cookie", null);
		String settArg=einstellungen.getString("Session", null);
		Boolean failedSession = false;
		if(getIntent()!=null && getIntent().getExtras()!=null){
			failedSession = getIntent().getExtras().getBoolean("lostSession");
		}
		
		if(settCookie!=null && settArg!=null && failedSession!=true){
			//taking the fast Road
			CheckBox remember = (CheckBox) findViewById(R.id.checkBox1);
			remember.setChecked(true);
			Toast.makeText(this, "Versuch: FastLogin", Toast.LENGTH_SHORT);
			CookieManager localCookieManager = new CookieManager();
			localCookieManager.generateManagerfromHTTPString(
					TucanMobile.TUCAN_HOST, settCookie);
			SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(
					this);
			RequestObject thisRequest = new RequestObject("https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi?APPNAME=CampusNet&PRGNAME=MLSSTART&ARGUMENTS="+settArg+",",
					localCookieManager, RequestObject.METHOD_GET, "");

			callOverviewBrowser.execute(thisRequest);
		}
		else if(tuid!="" && pw!=""){
			onClickSendLogin(null);
		}
		ImageView img = (ImageView) findViewById(R.id.imageView1);
		img.requestFocus();
        
    }
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.main);
	}
    public void onClickSendLogin (final View sfNormal) {
    	HTTPSBrowser newBrowser = new HTTPSBrowser();
    	
    	
    	try {
    		//TuCan leitet 3 mal weiter, damit der Login-Vorgang Abgeschlossen wird 
			RequestObject[] thisRequest = new RequestObject[4];
			//Cookie Abholen
			thisRequest[0] = new RequestObject("https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi?APPNAME=CampusNet&PRGNAME=STARTPAGE_DISPATCH&ARGUMENTS=-N000000000000001", RequestObject.METHOD_GET, "");
			
			//Login auslesen und senden
			
			String	usrname	=	usrnameField.getText().toString();
			String 	pwd		=	pwdField.getText().toString();
			
			//vordefinierte Post-Data
			//TODO: Dynamisch <input 's auslesen [evtl]
			String postdata= "usrname="+URLEncoder.encode(usrname,"UTF-8")+"&pass="+URLEncoder.encode(pwd,"UTF-8")+"&APPNAME=CampusNet&PRGNAME=LOGINCHECK&ARGUMENTS=clino%2Cusrname%2Cpass%2Cmenuno%2Cpersno%2Cbrowser%2Cplatform&clino=000000000000001&menuno=000344&persno=00000000&browser=&platform=";
			//AnmeldeRequest Senden
			thisRequest[1] = new RequestObject("https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi", RequestObject.METHOD_POST, postdata);
			//Restliche Requests werden aus der Antwort ausgelesen..
    		
			//Requests abscicken
			newBrowser.execute(thisRequest);
		} catch (Exception e) {
			Log.e(LOG_TAG,"FEHLER: " +e.getMessage());
		}
    }
    
    
    
   



	
	


	public class HTTPSBrowser extends AsyncTask<RequestObject, Integer, AnswerObject>  {
    	ProgressDialog dialog;
    	protected void onPreExecute() {
    		//ProgressDialog anfertigen und anzeigen
    		dialog=ProgressBarDialogFactory
    				.createProgressDialog(TuCanMobileActivity.this,getResources().getString(R.string.ui_login));
    		dialog.show();
        }
    	
    	@Override
		protected void onProgressUpdate(Integer... values) {
    		//Fortschritt Berechnen und anzeigen
    		int Progress= (int) ((((double) values[0]+1)/(double) values[1])*100);
    		Log.i(LOG_TAG,values[0]+" von "+values[1]+ " ergibt: "+Progress);
			dialog.setProgress(Progress);
		}

		@Override
    	protected AnswerObject doInBackground(RequestObject... requestInfo) {
			
    		AnswerObject answer = new AnswerObject("", "", null,null);
    		for(int i = 0;i<requestInfo.length;i++){
    			//requestInfo[i] kann null sein, da das Array mit 2 null-Objekten übergeben wird und dort erst
    			//später Redirects hinein geschrieben werden
    			//TODO: requestInfo in ArrayList umwandeln ?
    			if(requestInfo[i]!= null){
    				BrowseMethods Browser=new BrowseMethods();
    				//Requests letztendlich abschicken
    				answer=Browser.browse(requestInfo[i]);      				
    			}
    			else{
    				break;
    			}
    			//Letztes Objekt
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
    				Log.i(LOG_TAG,"Zu viele Redirects");
    			}
    			publishProgress(new Integer[]{i,requestInfo.length});
    		}
    		
    		return  answer;
    	}
    	
    	
    	protected void onPostExecute(AnswerObject result) {
    		
    		dialog.setMessage(getResources().getString(R.string.ui_calc));
    		
    		
    		//TODO: Update to JSoup
    		Pattern findUser = Pattern.compile("<span\\s+class=\"loginDataName\"\\s+id=\"loginDataName\"><b>Name<span\\s+class=\"colon\">:</span>\\s+</b>(.*?)</span>");
    		Matcher findUsermatcher = findUser.matcher(result.getHTML());
    		boolean found=false;
    		String User="";
    		
    		Document doc = Jsoup.parse(result.getHTML());
    		Element UserSpan = doc.select("span#loginDataName").first();
    		if(UserSpan==null){
    			dialog.dismiss();
    			Toast wrongLoginNotif = Toast.makeText(TuCanMobileActivity.this, "Login fehlerhaft", Toast.LENGTH_LONG);
    			wrongLoginNotif.show();
    		}
    		else {
    			dialog.dismiss();
    			String lcURLString=result.getLastCalledURL();
    			try {
					URL lcURL = new URL (lcURLString);
					SessionArgument = lcURL.getQuery().split("ARGUMENTS=")[1].split(",")[0];
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			User=Jsoup.parse(UserSpan.html().split(":")[1]).text();
    			CheckBox remember = (CheckBox) findViewById(R.id.checkBox1);
    			if(remember.isChecked()){
    				final SharedPreferences einstellungen = MainPreferences.getSettings(TuCanMobileActivity.this);
    				SharedPreferences.Editor editor = einstellungen.edit();
    				editor.putString("tuid", usrnameField.getText().toString());
    				editor.putString("pw", pwdField.getText().toString());
    				editor.putString("Cookie", result.getCookieManager().getCookieHTTPString(TucanMobile.TUCAN_HOST));
    				Log.i(LOG_TAG,SessionArgument);
    				editor.putString("Session", SessionArgument);
    				editor.commit();
    				
    			}
        		final Intent i = new Intent(TuCanMobileActivity.this,MainMenu.class);
        		i.putExtra("Cookie", result.getCookieManager().getCookieHTTPString("www.tucan.tu-darmstadt.de"));
        		i.putExtra("URL", result.getLastCalledURL());
        		startActivity(i);
    		}
    		/*
    		while(findUsermatcher.find()){
    			User=findUsermatcher.group(1);
    			Log.i(LOG_TAG,"Match:"+findUsermatcher.group(1));
    			found=true;
    		}
    		if(found==true){
    			dialog.dismiss();
    			String lcURLString=result.getLastCalledURL();
    			try {
					URL lcURL = new URL (lcURLString);
					SessionArgument = lcURL.getQuery().split("ARGUMENTS=")[1].split(",")[0];
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    			CheckBox remember = (CheckBox) findViewById(R.id.checkBox1);
    			if(remember.isChecked()){
    				final SharedPreferences einstellungen = MainPreferences.getSettings(TuCanMobileActivity.this);
    				SharedPreferences.Editor editor = einstellungen.edit();
    				editor.putString("tuid", usrnameField.getText().toString());
    				editor.putString("pw", pwdField.getText().toString());
    				editor.putString("Cookie", result.getCookieManager().getCookieHTTPString(TucanMobile.TUCAN_HOST));
    				Log.i(LOG_TAG,SessionArgument);
    				editor.putString("Session", SessionArgument);
    				editor.commit();
    				
    			}
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
    		*/
    	}
    }
    
    public void onRequestisAnswered(){
    	
    }
	@Override
	public void onPostExecute(AnswerObject result) {
		Document doc = Jsoup.parse(result.getHTML());
		try{
			String UserName = doc.select("span#loginDataName").text().split(":")[1];
			if(!UserName.equals("")){
				final Intent i = new Intent(TuCanMobileActivity.this,MainMenu.class);
	    		i.putExtra("Cookie", result.getCookieManager().getCookieHTTPString("www.tucan.tu-darmstadt.de"));
	    		i.putExtra("URL", result.getLastCalledURL());
	    		startActivity(i);
			}
			else {
				Toast.makeText(this, "Schneller Login fehlgeschlagen", Toast.LENGTH_LONG);
				onClickSendLogin(null);
			}
		}
		catch(Exception e) {
			Log.i(LOG_TAG,"Fehler: "+e.getMessage());
			Toast.makeText(this, "Schneller Login fehlgeschlagen", Toast.LENGTH_LONG);
			onClickSendLogin(null);
		}
		
	}
}