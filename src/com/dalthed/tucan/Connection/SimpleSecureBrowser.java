package com.dalthed.tucan.Connection;

import org.acra.ErrorReporter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

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
	 * jedoch bei manchen Events von außen auch abgebrochen werden muss
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
		answer = Browser.browse(significantRequest);
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

			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
		} catch (IllegalArgumentException e) {
			ErrorReporter.getInstance().handleSilentException(e);

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
