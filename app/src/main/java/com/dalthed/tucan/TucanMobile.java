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

package com.dalthed.tucan;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

import com.dalthed.tucan.helpers.AuthenticationManager;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender.Method;
import org.acra.sender.HttpSender.Type;
import org.apache.commons.lang3.StringEscapeUtils;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

//@ReportsCrashes(formKey = "dGxJeFNVZk5YQXRmaXV6WVZfVHYzdWc6MQ")
/**
 * @author Tyde
 *
 */
@ReportsCrashes(formKey = "", // will not be used
	httpMethod = Method.PUT,
	reportType = Type.JSON,
	formUri = "http://dttyde.de:5984/acra-tucan/_design/acra-storage/_update/report",
	formUriBasicAuthLogin = "tucanApp",
    formUriBasicAuthPassword = "thordielrbl")
//@ReportsCrashes(formUri = "http://www.bugsense.com/api/acra?api_key=f08ba688", formKey="")
public class TucanMobile extends Application {
	private static Context Appcontext; 
	/**
	 * The Host String of Tucan without protocol
	 */
	public final static String TUCAN_HOST = "www.tucan.tu-darmstadt.de";
	/**
	 * The used Protocol
	 */
	public final static String TUCAN_PROT = "https://";
	/**
	 * Name of the extra in Intents in which Cookies are forwarded
	 */
	public final static String EXTRA_COOKIE = "Cookie";
	/**
	 * Name of the URL Intent-Extra
	 */
	public final static String EXTRA_URL = "URL";
	/**
	 * Name of the URL Intent-Extra
	 */
	public final static String EXTRA_USERNAME = "UserName";
	/**
	 * Name of the cache, where ActionBar fastswitch links are safed
	 */
	public final static String LINK_FILE_NAME = "link_cache";
	/**
	 * Make App Crash (Bug-Reporting testing)
	 */
	public final static Boolean CRASH = false;
	/**
	 * App is/isn't in Debug mode
	 */
	public final static Boolean DEBUG = false;
	/**
	 * App is/isn't in Testing mode; App cannot work properly, if it is in Testing mode
	 */
	public final static Boolean TESTING = false;
	/**
	 * Tag for Log
	 */
	public static final String LOG_TAG = "TuCanMobile";

	/**
	 * Pattern for nspbTrennung
	 */
	private static final Pattern nbspPat = Pattern.compile("&nbsp;");
	@Override
	public void onCreate() {
		//BugReporting does not Work in testing mode
		if(!TucanMobile.TESTING){
			ACRA.init(this);
		}
		AuthenticationManager.init(this);
		
		
		super.onCreate();
		TucanMobile.Appcontext = getApplicationContext();
	}
	/**
	 * Gibt bei einem String wie "04-00-0126-vu&nbsp;Mathematik 1 (f&uuml;r ET)" "Mathematik 1 (f&uuml;r ET)" zurück
	 * @param evNameString
	 * @return
	 */
	public static String getEventNameByString(String evNameString) {
		String[] evNameAr = nbspPat.split(evNameString);
		
		if(evNameAr.length==2){
			
			return StringEscapeUtils.unescapeHtml4(evNameAr[1]);
		}
		else {
			return evNameString;
		}
	}
	
	public static Context getAppContext() {
		return Appcontext;
	}
	/**
	 * Writes a note on the SD. Only Used for Development purposes
	 * @param sFileName Filname
	 * @param sBody content
	 * @param mContext App Context
	 */
	public static void generateNoteOnSD(String sFileName, String sBody,Context mContext) {
		try {
			File root = new File(Environment.getExternalStorageDirectory(),
					"Notes");
			if (!root.exists()) {
				root.mkdirs();
			}
			File gpxfile = new File(root, sFileName);
			FileWriter writer = new FileWriter(gpxfile);
			writer.append(sBody);
			writer.flush();
			writer.close();
			//Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			String importError = e.getMessage();
			Log.e("TuCanMobile", importError);
		}
	}
	
	public static void alertOnTucanDown(final Context context,String error) {
		Dialog tucanDownDialog = new AlertDialog.Builder(context).setTitle("")
				.setMessage(error)
				.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Intent goToLogin = new Intent(context, TuCanMobileActivity.class);
						goToLogin.putExtra("loggedout", true);
						context.startActivity(goToLogin);
					}
				}).create();
	}

}
