/**
 *	This file is part of TuCan Mobile.
 *
 *	TuCan Mobile is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	TuCan Mobile is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with TuCan Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dalthed.tucan.Connection;

import java.net.ConnectException;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListActivity;

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
		try {
			answer = Browser.browse(significantRequest);
		} catch (ConnectException e) {
			if(callingActivity instanceof Activity) {
				Toast.makeText((Activity) callingActivity, "Keine Internetverbindung", Toast.LENGTH_LONG).show();
			}
			
		}
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
