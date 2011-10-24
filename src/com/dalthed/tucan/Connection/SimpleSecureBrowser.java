package com.dalthed.tucan.Connection;

import com.dalthed.tucan.R;

import com.dalthed.tucan.ui.SimpleWebListActivity;


import android.app.ProgressDialog;

import android.os.AsyncTask;
/**
 * SimpleSecureBrowser ist ein AsyncTask welcher die RequestObjects passend abschickt und zurückgibt.
 * Muss aus einer SimpleWebListActivity gestartet werden. Nachdem die Daten angekommen sind, wird
 * die onPostExecute der aufrufenden SimpleWebListActivity aufgerufen.
 * @author Tyde
 *
 */
public class SimpleSecureBrowser extends AsyncTask<RequestObject, Integer, AnswerObject> {
	protected SimpleWebListActivity outerCallingActivity;
	ProgressDialog dialog;
	/**
	 * Die Activity muss übergeben werden, damit der Browser die Methode onPostExecute aufrufen kann
	 * @param callingActivity
	 */
	public SimpleSecureBrowser (SimpleWebListActivity callingActivity) {
		super();
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
		dialog = ProgressDialog.show(outerCallingActivity,"",
				outerCallingActivity.getResources().getString(R.string.ui_load_data),true);
	}

	@Override
	protected void onPostExecute(AnswerObject result) {
		dialog.setTitle(outerCallingActivity.getResources().getString(R.string.ui_calc));
		outerCallingActivity.onPostExecute(result);
		dialog.dismiss();
	}
	

}
