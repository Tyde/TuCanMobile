package com.dalthed.tucan;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.sax.StartElementListener;
import android.util.Log;

//@ReportsCrashes(formKey = "dGxJeFNVZk5YQXRmaXV6WVZfVHYzdWc6MQ")
/**
 * @author Tyde
 *
 */
@ReportsCrashes(formKey = "", // will not be used
formUri = "http://daniel-thiem.de/ACRA/insertsql.php")
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
	public final static Boolean DEBUG = true;
	/**
	 * App is/isn't in Testing mode; App cannot work properly, if it is in Testing mode
	 */
	public final static Boolean TESTING = true;
	/**
	 * Tag for Log
	 */
	public static final String LOG_TAG = "TuCanMobile";

	@Override
	public void onCreate() {
		//BugReporting does not Work in testing mode
		if(!TucanMobile.TESTING){
			ACRA.init(this);
		}
		
		
		super.onCreate();
		TucanMobile.Appcontext = getApplicationContext();
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
