package com.dalthed.tucan.ui;

import org.acra.ErrorReporter;

import com.dalthed.tucan.R;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.preferences.MainPreferences;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public abstract class SimpleWebActivity extends Activity {
	public abstract void onPostExecute(AnswerObject result);

	public void sendHTMLatBug(String html){
		ErrorReporter.getInstance().putCustomData("html", html);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.loginmenu, menu);
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
		default:
			return super.onOptionsItemSelected(item);
		}

	}
}
