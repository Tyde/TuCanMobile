package com.dalthed.tucan.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.acra.ErrorReporter;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.actionbarsherlock.ActionBarSherlock.OnOptionsItemSelectedListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.BrowserAnswerReciever;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.helpers.FastSwitchHelper;
import com.dalthed.tucan.preferences.MainPreferences;

/**
 * SimpleWebListActivity notwendig für SimpleSecureBrowser
 * 
 * @author Tyde
 * 
 */
public abstract class SimpleWebListActivity extends SherlockListActivity implements
		ActionBar.OnNavigationListener,BrowserAnswerReciever {
	public SimpleSecureBrowser callResultBrowser;
	protected static final String LOG_TAG = "TuCanMobile";
	protected Boolean HTTPS = true;
	
	
	protected ActionBar acBar = null;
	protected FastSwitchHelper fsh;
	
	protected void onCreate(Bundle savedInstanceState,Boolean navigateList,int navigationItem) {
		acBar = getSupportActionBar();
		if (TucanMobile.DEBUG && getIntent().hasExtra("HTTPS")) {
			HTTPS = getIntent().getExtras().getBoolean("HTTPS");
		}
		super.onCreate(savedInstanceState);
		fsh = new FastSwitchHelper(this,navigateList,acBar,navigationItem);
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		onCreate(savedInstanceState, false,0);
	}

	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.loginmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public static void sendHTMLatBug(String html) {
		ErrorReporter.getInstance().putCustomData("html", html);
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

	public Object onRetainNonConfigurationInstance() {
		if (callResultBrowser != null) {
			callResultBrowser.dialog.dismiss();
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
		
		return fsh.startFastSwitchIntent(itemPosition);
		
		
		
	}

	
}
