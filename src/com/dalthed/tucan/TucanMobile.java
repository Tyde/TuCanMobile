package com.dalthed.tucan;

import android.app.Application;
import android.content.Context;

public class TucanMobile extends Application {
	private static Context Appcontext;
	@Override
	public void onCreate() {
		TucanMobile.Appcontext=getApplicationContext();
	}
	
	public static Context getAppContext(){
		return Appcontext;
	}

}
