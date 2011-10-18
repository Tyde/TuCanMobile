package com.dalthed.tucan;

import android.app.Application;
import android.content.Context;

public class TucanMobile extends Application {
	private static Context Appcontext;
	public final static String TUCAN_HOST = "www.tucan.tu-darmstadt.de";
	public final static String TUCAN_PROT = "https://";
	@Override
	public void onCreate() {
		TucanMobile.Appcontext=getApplicationContext();
	}
	
	public static Context getAppContext(){
		return Appcontext;
	}

}
