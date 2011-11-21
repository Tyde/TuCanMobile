package com.dalthed.tucan;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

//@ReportsCrashes(formKey = "dGxJeFNVZk5YQXRmaXV6WVZfVHYzdWc6MQ")
@ReportsCrashes(formKey = "", // will not be used
formUri = "http://daniel-thiem.de/ACRA/insertsql.php")
//@ReportsCrashes(formUri = "http://www.bugsense.com/api/acra?api_key=f08ba688", formKey="")
public class TucanMobile extends Application {
	private static Context Appcontext;
	public final static String TUCAN_HOST = "www.tucan.tu-darmstadt.de";
	public final static String TUCAN_PROT = "https://";
	public GoogleAnalyticsTracker mAnalyticsTracker;
	public final static Boolean CRASH = false;

	@Override
	public void onCreate() {
		
		
		ACRA.init(this);
		
		super.onCreate();
		TucanMobile.Appcontext = getApplicationContext();
	}

	public static Context getAppContext() {
		return Appcontext;
	}

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

}
