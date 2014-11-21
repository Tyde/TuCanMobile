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

package com.dalthed.tucan.ui;

import org.acra.ErrorReporter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.BrowserAnswerReciever;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.acraload.LoadAcraResults;
import com.dalthed.tucan.preferences.MainPreferences;
@Deprecated
public abstract class SimpleWebActivity extends SherlockActivity implements
		ActionBar.OnNavigationListener, BrowserAnswerReciever {
	public SimpleSecureBrowser callResultBrowser;
	protected Boolean HTTPS = true;
	protected Boolean navigateList = false;

	protected ActionBar acBar = null;
	protected int navigationItem = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		acBar = getSupportActionBar();
		if (TucanMobile.DEBUG && getIntent().hasExtra("HTTPS")) {
			HTTPS = getIntent().getExtras().getBoolean("HTTPS");
		}
		super.onCreate(savedInstanceState);
		if (this.navigateList) {
			String[] lOptions = getResources().getStringArray(R.array.mainmenu_options);
			Context context = acBar.getThemedContext();
			ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context,
					R.array.mainmenu_options, R.layout.sherlock_spinner_item);
			list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
			acBar.setDisplayShowTitleEnabled(false);
			acBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			acBar.setListNavigationCallbacks(list, this);
			acBar.setSelectedNavigationItem(navigationItem);
		}
	}

	public void sendHTMLatBug(String html) {
		if (!TucanMobile.TESTING) {
			ErrorReporter.getInstance().putCustomData("html", html);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.loginmenu, menu);
		
		if (TucanMobile.DEBUG) {
			menu.add(Menu.NONE, 9941, Menu.NONE, "Test");
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.loginmenu_opt_setpreferences:
			Intent settingsACTIVITY = new Intent(getBaseContext(), MainPreferences.class);
			startActivity(settingsACTIVITY);
			return true;
		case R.id.loginmenu_opt_changelog:
			ChangeLog cl = new ChangeLog(this);
		    cl.getFullLogDialog().show();
			return true;
		case R.id.loginmenu_opt_close:
			finish();
			return true;
		case 9941:
			// Toast.makeText(this, "jadoiwjdi", Toast.LENGTH_LONG).show();
			Log.i(TucanMobile.LOG_TAG,"Komischerweise ist er hier");
			Intent debugIntent = new Intent(getBaseContext(), LoadAcraResults.class);
			startActivity(debugIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}
	
	protected Boolean restoreResultBrowser() {
		if (getLastNonConfigurationInstance() != null) {
			if (getLastNonConfigurationInstance() instanceof SimpleSecureBrowser) {
				SimpleSecureBrowser oldBrowser = (SimpleSecureBrowser) getLastNonConfigurationInstance();
				 callResultBrowser = oldBrowser;
				 if (!(oldBrowser.getStatus()
	                        .equals(AsyncTask.Status.FINISHED))) {
					
					 callResultBrowser.dialog.show();
					 
				 } else {
					 this.retainConfiguration(oldBrowser.mConfigurationStorage);
				 }
			}
			return true;
		}
		return false;
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		if (callResultBrowser != null) {
			callResultBrowser.mConfigurationStorage = saveConfiguration();
			callResultBrowser.dialog.dismiss();
			return callResultBrowser;
		}

		return super.onRetainNonConfigurationInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.ActionBar.OnNavigationListener#
	 * onNavigationItemSelected(int, long)
	 */
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		return false;
	}



}
