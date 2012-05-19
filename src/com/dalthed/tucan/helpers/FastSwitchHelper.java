package com.dalthed.tucan.helpers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.adapters.FastSwitchAdapter;
import com.dalthed.tucan.ui.Events;
import com.dalthed.tucan.ui.Exams;
import com.dalthed.tucan.ui.MainMenu;
import com.dalthed.tucan.ui.Messages;
import com.dalthed.tucan.ui.Schedule;
import com.dalthed.tucan.ui.VV;

public class FastSwitchHelper {
	public String[] linkarray = null;
	protected static final String LOG_TAG = "TuCanMobile";
	public String cached_Session, cached_Cookie;
	private Context context;
	protected Boolean navigateList = false;
	public int navigationItem = 0;

	protected HashMap<Integer, Class> ActivitiesToStart = new HashMap<Integer, Class>() {
		{
			put(0, VV.class);
			put(1, Schedule.class);
			put(2, Events.class);
			put(3, Exams.class);
			put(4, Messages.class);
		}
	};

	public FastSwitchHelper(Context context, Boolean navigateList, ActionBar acBar,
			int navigationItem) {
		this.context = context;
		this.navigateList = navigateList;
		this.navigationItem = navigationItem;
		if (navigateList && createLinkArray() && context instanceof ActionBar.OnNavigationListener) {
			Context ac_context = acBar.getThemedContext();
			FastSwitchAdapter dropAdapter = new FastSwitchAdapter(context,context.getResources().getStringArray(R.array.mainmenu_options));
			
			acBar.setDisplayShowTitleEnabled(false);
			acBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			acBar.setListNavigationCallbacks(dropAdapter, (ActionBar.OnNavigationListener) context);
			acBar.setSelectedNavigationItem(navigationItem);
			acBar.setDisplayHomeAsUpEnabled(true);
			dropAdapter.setSubtitle("test");

		}
	}

	public boolean createLinkArray() {
		try {

			FileInputStream fis = this.context.openFileInput(TucanMobile.LINK_FILE_NAME);
			StringBuffer strFile = new StringBuffer("");
			int ch;
			while ((ch = fis.read()) != -1) {
				strFile.append((char) ch);

			}
			fis.close();
			String[] filePieces = strFile.toString().split("<<");
			cached_Cookie = filePieces[1];
			cached_Session = filePieces[2];
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

	public boolean startFastSwitchIntent(int itemPosition) {
		if (itemPosition != navigationItem) {
			try {
				// Log.i(LOG_TAG, "in a Intent");

				Intent navigateIntent = new Intent(context, ActivitiesToStart.get(itemPosition));
				navigateIntent.putExtra("URL", linkarray[itemPosition]);
				navigateIntent.putExtra("Cookie", cached_Cookie);
				navigateIntent.putExtra("Session", cached_Session);
				navigateIntent.putExtra("UserName", "");
				context.startActivity(navigateIntent);
			} catch (ArrayIndexOutOfBoundsException e) {
				Log.e(LOG_TAG, "Array out of Bounds for switching. Tried: " + itemPosition
						+ ", but linkarray has only " + linkarray.length + " length");
				return false;
			}

			return true;
		} else {
			return false;
		}
	}

	public void startHomeIntent() {
		Intent homeIntent = new Intent(context, MainMenu.class);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		homeIntent.putExtra("Cookie", cached_Cookie);
		homeIntent.putExtra("Session", cached_Session);
		homeIntent.putExtra("URL",
				"https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi?APPNAME=CampusNet&PRGNAME=MLSSTART&ARGUMENTS="
						+ cached_Session + ",-N000019");
		context.startActivity(homeIntent);
	}

}
