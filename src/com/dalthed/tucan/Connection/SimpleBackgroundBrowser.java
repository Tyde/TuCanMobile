package com.dalthed.tucan.Connection;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListActivity;

import android.R;

import android.app.Activity;
import android.os.AsyncTask;

public class SimpleBackgroundBrowser extends AsyncTask<RequestObject, Integer, AnswerObject> {
	public boolean HTTPS = true;
	private BackgroundBrowserReciever callingActivity;
	private ActionBar acBar;

	public SimpleBackgroundBrowser(BackgroundBrowserReciever callingActivity, ActionBar acBar) {
		this.callingActivity = callingActivity;
		this.acBar=acBar;
	}

	@Override
	protected AnswerObject doInBackground(RequestObject... requestInfo) {
		AnswerObject answer = new AnswerObject("", "", null, null);
		RequestObject significantRequest = requestInfo[0];
		BrowseMethods Browser = new BrowseMethods();
		Browser.HTTPS = this.HTTPS;
		answer = Browser.browse(significantRequest);
		return answer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		if(callingActivity.getwindowFeatureCalled()) {
			if(callingActivity instanceof SherlockActivity) {
				((SherlockActivity) callingActivity).setSupportProgressBarIndeterminateVisibility(true);
			}
			else if(callingActivity instanceof SherlockListActivity){
				((SherlockListActivity) callingActivity).setSupportProgressBarIndeterminateVisibility(true);
			}
			else if(callingActivity instanceof SherlockFragmentActivity) {
				((SherlockFragmentActivity) callingActivity).setSupportProgressBarIndeterminateVisibility(true);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(AnswerObject result) {
		if(callingActivity.getwindowFeatureCalled()) {
			if(callingActivity instanceof SherlockActivity) {
				((SherlockActivity) callingActivity).setSupportProgressBarIndeterminateVisibility(false);
			}
			else if(callingActivity instanceof SherlockListActivity){
				((SherlockListActivity) callingActivity).setSupportProgressBarIndeterminateVisibility(false);
			}
			else if(callingActivity instanceof SherlockFragmentActivity) {
				((SherlockFragmentActivity) callingActivity).setSupportProgressBarIndeterminateVisibility(false);
			}
		}
		callingActivity.onBackgroundBrowserFinalized(result);
	}
	

}
