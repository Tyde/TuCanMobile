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

import org.acra.ACRA;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.BrowserAnswerReciever;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.helpers.FastSwitchHelper;
import com.dalthed.tucan.preferences.MainPreferences;

public abstract class FragmentWebActivity extends SherlockFragmentActivity implements ActionBar.OnNavigationListener, BrowserAnswerReciever {
	public SimpleSecureBrowser callResultBrowser;
	protected Boolean HTTPS = true;



	protected ActionBar acBar = null;
	protected FastSwitchHelper fsh;

	protected void onCreate(Bundle savedInstanceState, Boolean navigateList, int navigationItem) {
		acBar = getSupportActionBar();
		if (TucanMobile.DEBUG && getIntent().hasExtra("HTTPS")) {
			HTTPS = getIntent().getExtras().getBoolean("HTTPS");
		}
		super.onCreate(savedInstanceState);
		fsh = new FastSwitchHelper(this, navigateList, acBar, navigationItem);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		onCreate(savedInstanceState, false, 0);
	}

	public void sendHTMLatBug(String html) {
		if(!TucanMobile.TESTING){
			ACRA.getErrorReporter().putCustomData("html", html);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.loginmenu, menu);
		return super.onCreateOptionsMenu(menu);
	} 

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.loginmenu_opt_setpreferences:
			Intent settingsACTIVITY = new Intent(getBaseContext(), MainPreferences.class);
			startActivity(settingsACTIVITY);
			return true;
		case R.id.loginmenu_opt_close:
			finish();
			return true;
		case android.R.id.home:
			fsh.startHomeIntent();
		default:
			return super.onOptionsItemSelected(item);
		}

	}
	

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.ActionBar.OnNavigationListener#onNavigationItemSelected(int, long)
	 */
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		return fsh.startFastSwitchIntent(itemPosition);
	}
	

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		if (callResultBrowser != null) {
			callResultBrowser.mConfigurationStorage = saveConfiguration();
			callResultBrowser.dialog.dismiss();
			return callResultBrowser;
		}

		return super.onRetainCustomNonConfigurationInstance();
	}

	protected Boolean restoreResultBrowser() {
		if (getLastCustomNonConfigurationInstance() != null) {
			if (getLastCustomNonConfigurationInstance() instanceof SimpleSecureBrowser) {
				SimpleSecureBrowser oldBrowser = (SimpleSecureBrowser) getLastCustomNonConfigurationInstance();
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

}
