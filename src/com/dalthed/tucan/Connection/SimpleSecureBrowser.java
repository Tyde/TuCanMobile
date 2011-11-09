package com.dalthed.tucan.Connection;

import com.dalthed.tucan.R;

import com.dalthed.tucan.ui.FragmentWebActivity;
import com.dalthed.tucan.ui.SimpleWebActivity;
import com.dalthed.tucan.ui.SimpleWebListActivity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import android.os.AsyncTask;
/**
 * SimpleSecureBrowser ist ein AsyncTask welcher die RequestObjects passend abschickt und zurückgibt.
 * Muss aus einer SimpleWebListActivity gestartet werden. Nachdem die Daten angekommen sind, wird
 * die onPostExecute der aufrufenden SimpleWebListActivity aufgerufen.
 * @author Tyde
 *
 */
public class SimpleSecureBrowser extends AsyncTask<RequestObject, Integer, AnswerObject> {
	public SimpleWebListActivity outerCallingListActivity;
	public SimpleWebActivity outerCallingActivity;
	public FragmentWebActivity outerCallingFragmentActivity;
	public ProgressDialog dialog;
	
	boolean finished = false;
	/**
	 * Die Activity muss übergeben werden, damit der Browser die Methode onPostExecute aufrufen kann
	 * @param callingActivity
	 */
	public SimpleSecureBrowser (SimpleWebListActivity callingActivity) {
		outerCallingListActivity=callingActivity;
		outerCallingFragmentActivity=null;
		outerCallingActivity=null;
	}
	
	public SimpleSecureBrowser (FragmentWebActivity callingActivity) {
		outerCallingListActivity=null;
		outerCallingFragmentActivity=callingActivity;
		outerCallingActivity=null;
	}
	public SimpleSecureBrowser (SimpleWebActivity callingActivity) {
		outerCallingListActivity=null;
		outerCallingFragmentActivity=null;
		outerCallingActivity=callingActivity;
	}
	@Override
	protected AnswerObject doInBackground(RequestObject... requestInfo) {
		AnswerObject answer = new AnswerObject("", "", null,null);
		RequestObject significantRequest = requestInfo[0];
		BrowseMethods Browser=new BrowseMethods();
		answer=Browser.browse(significantRequest); 
		return answer;
	}

	@Override
	protected void onPreExecute() {
		
		Activity parentActivityHandler = getparentActivityHandler();
		
		//parentActivityHandler.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		dialog = ProgressDialog.show(parentActivityHandler,"",
					//parentActivityHandler.getResources().getString(R.string.ui_load_data),true);
				"Lade",true);
		
	}
	private Activity getparentActivityHandler() {
		Activity parentActivityHandler;
		if(outerCallingListActivity==null && outerCallingFragmentActivity== null){
			parentActivityHandler = outerCallingActivity;
		}
		else if(outerCallingFragmentActivity == null) {
			
			parentActivityHandler = outerCallingListActivity;
		}
		else {
			parentActivityHandler = outerCallingFragmentActivity;
		}
		return parentActivityHandler;
	}

	@Override
	protected void onPostExecute(AnswerObject result) {
		
		Activity parentActivityHandler = getparentActivityHandler();
		dialog.setTitle(parentActivityHandler.getResources().getString(R.string.ui_calc));
		if(outerCallingListActivity==null && outerCallingFragmentActivity== null){
			outerCallingActivity.onPostExecute(result);
		}
		else if(outerCallingFragmentActivity == null) {
			
			outerCallingListActivity.onPostExecute(result);
		}
		else {
			outerCallingFragmentActivity.onPostExecute(result);
		}
		
		if(dialog.isShowing() && dialog!=null)
			dialog.dismiss();
		
		
		
	}
	

}
