package com.dalthed.tucan.ui;

import com.dalthed.tucan.R;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.preferences.MainPreferences;

import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
/**
 * SimpleWebListActivity notwendig für SimpleSecureBrowser
 * @author Tyde
 *
 */
public abstract class SimpleWebListActivity extends ListActivity {
	/**
	 * Wird aufgerufen, wenn SimpleSecureBrowser fertig ist.
	 * @param result
	 */
	public abstract void onPostExecute(AnswerObject result) ;
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
