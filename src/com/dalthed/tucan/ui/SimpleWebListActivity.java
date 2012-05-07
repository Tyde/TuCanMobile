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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.preferences.MainPreferences;

/**
 * SimpleWebListActivity notwendig für SimpleSecureBrowser
 * 
 * @author Tyde
 * 
 */
public abstract class SimpleWebListActivity extends SherlockListActivity implements
		ActionBar.OnNavigationListener {
	public SimpleSecureBrowser callResultBrowser;
	protected static final String LOG_TAG = "TuCanMobile";
	protected Boolean HTTPS = true;
	protected Boolean navigateList = false;
	private Boolean doIntent = false;
	protected ActionBar acBar = null;
	protected int navigationItem = 0;

	protected HashMap<Integer, Class> ActivitiesToStart = new HashMap<Integer, Class>() {
		{
			put(0, VV.class);
			put(1, Schedule.class);
			put(2, Events.class);
			put(3, Exams.class);
			put(4, Messages.class);
		}
	};
	private String[] linkarray = null;
	private String cached_Session, cached_Cookie;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		acBar = getSupportActionBar();
		if (TucanMobile.DEBUG && getIntent().hasExtra("HTTPS")) {
			HTTPS = getIntent().getExtras().getBoolean("HTTPS");
		}
		super.onCreate(savedInstanceState);
		if (this.navigateList && createLinkArray()) {
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

	/**
	 * Wird aufgerufen, wenn SimpleSecureBrowser fertig ist.
	 * 
	 * @param result
	 */
	public abstract void onPostExecute(AnswerObject result);

	private boolean createLinkArray() {
		try {
			FileInputStream fis = openFileInput(TucanMobile.LINK_FILE_NAME);
			StringBuffer strFile = new StringBuffer("");
			int ch;
			while ((ch = fis.read()) != -1) {
				strFile.append((char) ch);

			}
			fis.close();
			String[] filePieces = strFile.toString().split("<<");
			cached_Cookie = filePieces[1];
			cached_Session = filePieces[2];
			Log.i(LOG_TAG,"AllLinks:"+filePieces[0]);
			Log.i(LOG_TAG,"Ganze Datei:"+strFile.toString());
			linkarray = filePieces[0].split(">>");

		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		if (linkarray.length > 0) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.loginmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public void sendHTMLatBug(String html) {
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
		Log.i(LOG_TAG,"Item Postion:"+ itemPosition+" itemID: "+itemId+" navItem: "+navigationItem);
		if(itemPosition!=navigationItem){
			try{
				Log.i(LOG_TAG, "in a Intent");
				Intent navigateIntent = new Intent(this,ActivitiesToStart.get(itemPosition));
				navigateIntent.putExtra("URL", linkarray[itemPosition]);
				navigateIntent.putExtra("Cookie", cached_Cookie);
				navigateIntent.putExtra("Session", cached_Session);
				navigateIntent.putExtra("UserName", "");
				startActivity(navigateIntent);
			}
			catch (ArrayIndexOutOfBoundsException e) {
				Log.e(LOG_TAG, "Array out of Bounds for switching. Tried: "+itemPosition+", but linkarray has only "+linkarray.length+" length");
				return false;
			}
			
			return true;
		}
		else{
			return false;
		}
		
		
	}

}
