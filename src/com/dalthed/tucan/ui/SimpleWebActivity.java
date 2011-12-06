package com.dalthed.tucan.ui;

import org.acra.ErrorReporter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.acraload.LoadAcraResults;
import com.dalthed.tucan.preferences.MainPreferences;

public abstract class SimpleWebActivity extends Activity {
	public SimpleSecureBrowser callResultBrowser;
	protected Boolean HTTPS=true;
	public abstract void onPostExecute(AnswerObject result);
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(TucanMobile.DEBUG && getIntent().hasExtra("HTTPS")){
			HTTPS=getIntent().getExtras().getBoolean("HTTPS");
		}
		super.onCreate(savedInstanceState);
	}


	public void sendHTMLatBug(String html){
		ErrorReporter.getInstance().putCustomData("html", html);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.loginmenu, menu);
		if(TucanMobile.DEBUG){
			menu.add(Menu.NONE, 9941, Menu.NONE, "Test");
		}
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.loginmenu_opt_setpreferences:
			Intent settingsACTIVITY = new Intent(getBaseContext(),
					MainPreferences.class);
			startActivity(settingsACTIVITY);
			return true;
		case R.id.loginmenu_opt_close:
			finish();
			return true;
		case 9941:
			//Toast.makeText(this, "jadoiwjdi", Toast.LENGTH_LONG).show();
			Intent debugIntent = new Intent(getBaseContext(),LoadAcraResults.class);
			startActivity(debugIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return callResultBrowser;
	}
	
	
}
