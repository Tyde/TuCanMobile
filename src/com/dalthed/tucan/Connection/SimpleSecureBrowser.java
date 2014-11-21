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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.util.ConfigurationChangeStorage;

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

	/**
	 * Der {@link BrowserAnswerReciever}, welcher den
	 * {@link SimpleSecureBrowser} aufgerufen hat. Meistens ist dies auch eine
	 * {@link Activity}
	 */
	public BrowserAnswerReciever outerCallingRecieverActivity;
	/**
	 * Der lade-{@link Dialog}, welcher während des Ladevorgangs gezeigt wird,
	 * jedoch bei manchen Events von aussen auch abgebrochen werden muss
	 */
	public ProgressDialog dialog;
	/**
	 * Bei <code>true</code> wird ein HTTPS request restartet, anderenfalls nur
	 * HTTP
	 */
	public boolean HTTPS = true;
	/**
	 * <i>Depricated: </i> nutze stattdessen: {@link #getStatus()}
	 */
	@Deprecated
	boolean finished = false;
	/**
	 * List Adapter that has been set on the activity
	 */
	public ConfigurationChangeStorage mConfigurationStorage;

	/**
	 * SimpleSecureBrowser ist ein AsyncTask welcher die RequestObjects passend
	 * abschickt und zurückgibt. Muss aus einer SimpleWebListActivity gestartet
	 * werden. Nachdem die Daten angekommen sind, wird die onPostExecute der
	 * aufrufenden SimpleWebListActivity aufgerufen.
	 * 
	 * @param callingActivity
	 *            aufrufender {@link BrowserAnswerReciever}
	 */
	public SimpleSecureBrowser(BrowserAnswerReciever callingActivity) {
		outerCallingRecieverActivity = callingActivity;

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
            outerCallingRecieverActivity.runOnUI(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(TucanMobile.getAppContext(), "Keine Internetverbindung", Toast.LENGTH_LONG).show();
                }
            });


		}
		return answer;
	}

	/**
	 * Zeige Dialog
	 */
	public void showDialog() {
		onPreExecute();
	}

	@Override
	protected void onPreExecute() {

		Activity parentActivityHandler = getparentActivityHandler();

		// parentActivityHandler.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (parentActivityHandler != null && !TucanMobile.TESTING) {
			String loading = getparentActivityHandler().getResources().getString(
					R.string.ui_load_data);
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

	/**
	 * 
	 * @return {@link Activity} Object of {@link BrowserAnswerReciever}, if it
	 *         is an instance of that, otherwise <code>null</code>
	 */
	private Activity getparentActivityHandler() {
		if (outerCallingRecieverActivity instanceof Activity) {
			return (Activity) outerCallingRecieverActivity;
		}
		return null;

	}

	@Override
	protected void onPostExecute(AnswerObject result) {

		Activity parentActivityHandler = getparentActivityHandler();
		try {
			if (dialog != null) {
				dialog.setTitle(parentActivityHandler.getResources().getString(R.string.ui_calc));
			}
			outerCallingRecieverActivity.onPostExecute(result);
			
			if (dialog != null  && dialog.isShowing())
				dialog.dismiss();
		} catch (IllegalArgumentException e) {
            if(parentActivityHandler!=null) {
                outerCallingRecieverActivity.runOnUI(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TucanMobile.getAppContext(), "Bei dem Drehen des Bildschirmes ist ein Fehler aufgetreten", Toast.LENGTH_LONG).show();
                    }
                });


                //ACRA.getErrorReporter().handleSilentException(e);
                parentActivityHandler.finish();
            }
		}

	}

	/**
	 * Renews the given {@link Activity} {@link Context}, if a new App Instance
	 * had to be created
	 * 
	 * @param context
	 *            {@link BrowserAnswerReciever} object
	 */
	public void renewContext(BrowserAnswerReciever context) {
		outerCallingRecieverActivity = context;
	}

}
