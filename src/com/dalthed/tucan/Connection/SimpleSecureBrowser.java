package com.dalthed.tucan.Connection;

import com.dalthed.tucan.R;

import com.dalthed.tucan.ui.SimpleWebListActivity;


import android.app.ProgressDialog;

import android.os.AsyncTask;

public class SimpleSecureBrowser extends AsyncTask<RequestObject, Integer, AnswerObject> {
	protected SimpleWebListActivity outerContext;
	ProgressDialog dialog;
	public SimpleSecureBrowser (SimpleWebListActivity context) {
		super();
		outerContext=context;
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
		dialog = ProgressDialog.show(outerContext,"",
				outerContext.getResources().getString(R.string.ui_load_data),true);
	}

	@Override
	protected void onPostExecute(AnswerObject result) {
		dialog.setTitle(outerContext.getResources().getString(R.string.ui_calc));
		outerContext.onPostExecute(result);
		dialog.dismiss();
	}
	

}
