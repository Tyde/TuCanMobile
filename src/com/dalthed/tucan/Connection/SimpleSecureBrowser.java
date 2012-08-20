package com.dalthed.tucan.Connection;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;

import com.dalthed.tucan.ui.FragmentWebActivity;
import com.dalthed.tucan.ui.SimpleWebActivity;
import com.dalthed.tucan.ui.SimpleWebListActivity;
import com.dalthed.tucan.util.ConfigurationChangeStorage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import android.os.AsyncTask;
import android.widget.ListAdapter;

/**
 * SimpleSecureBrowser ist ein AsyncTask welcher die RequestObjects passend
 * abschickt und zurückgibt. Muss aus einer SimpleWebListActivity gestartet
 * werden. Nachdem die Daten angekommen sind, wird die onPostExecute der
 * aufrufenden SimpleWebListActivity aufgerufen.
 * 
 * @author Tyde
 * 
 */
public class SimpleSecureBrowser extends AsyncTask<RequestObject, Integer, AnswerObject> {

	public BrowserAnswerReciever outerCallingRecieverActivity;
	public ProgressDialog dialog;
	public boolean HTTPS = true;
	boolean finished = false;
	/**
	 * List Adapter that has been set on the activity
	 */
	public ConfigurationChangeStorage mConfigurationStorage;

	public SimpleSecureBrowser(BrowserAnswerReciever callingActivity) {
		outerCallingRecieverActivity = callingActivity;

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

	@Override
	protected void onPreExecute() {

		Activity parentActivityHandler = getparentActivityHandler();

		// parentActivityHandler.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (parentActivityHandler != null && !TucanMobile.TESTING) {
			String loading = getparentActivityHandler().getResources().getString(R.string.ui_load_data);
			dialog = ProgressDialog.show(parentActivityHandler, "",
			// parentActivityHandler.getResources().getString(R.string.ui_load_data),true);
					loading, true);
			dialog.setCancelable(true);
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

				public void onCancel(DialogInterface dialog) {
					cancel(true);

				}
			});
		}

	}

	private Activity getparentActivityHandler() {
		if (outerCallingRecieverActivity instanceof Activity) {
			return (Activity) outerCallingRecieverActivity;
		}
		return null;

	}

	@Override
	protected void onPostExecute(AnswerObject result) {

		Activity parentActivityHandler = getparentActivityHandler();
		dialog.setTitle(parentActivityHandler.getResources().getString(R.string.ui_calc));
		
		outerCallingRecieverActivity.onPostExecute(result);

		if (dialog.isShowing() && dialog != null)
			dialog.dismiss();

	}
	
	

}
