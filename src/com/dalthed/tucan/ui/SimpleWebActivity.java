package com.dalthed.tucan.ui;

import org.acra.ErrorReporter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.SimpleSecureBrowser;
import com.dalthed.tucan.acraload.LoadAcraResults;
import com.dalthed.tucan.preferences.MainPreferences;

public abstract class SimpleWebActivity extends SherlockActivity implements ActionBar.OnNavigationListener {
	public SimpleSecureBrowser callResultBrowser;
	protected Boolean HTTPS=true;
	protected Boolean navigateList = false;
	public abstract void onPostExecute(AnswerObject result);
	protected ActionBar acBar = null;
	protected int navigationItem = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		acBar = getSupportActionBar();
		if(TucanMobile.DEBUG && getIntent().hasExtra("HTTPS")){
			HTTPS=getIntent().getExtras().getBoolean("HTTPS");
		}
		super.onCreate(savedInstanceState);
		if(this.navigateList){
			String[] lOptions = getResources().getStringArray(R.array.mainmenu_options);
			Context context = acBar.getThemedContext();
			ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.mainmenu_options, R.layout.sherlock_spinner_item);
			list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
			acBar.setDisplayShowTitleEnabled(false);
			acBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			acBar.setListNavigationCallbacks(list, this);
			acBar.setSelectedNavigationItem(navigationItem);
		}
	}


	public void sendHTMLatBug(String html){
		ErrorReporter.getInstance().putCustomData("html", html);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.loginmenu, menu);
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


	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.ActionBar.OnNavigationListener#onNavigationItemSelected(int, long)
	 */
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		return false;
	}
	
	
	
	
}
