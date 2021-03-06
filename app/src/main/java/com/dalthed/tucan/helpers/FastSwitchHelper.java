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

package com.dalthed.tucan.helpers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;

import com.actionbarsherlock.app.ActionBar;
import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.adapters.FastSwitchAdapter;
import com.dalthed.tucan.scraper.MainMenuScraper;
import com.dalthed.tucan.ui.Events;
import com.dalthed.tucan.ui.Exams;
import com.dalthed.tucan.ui.MainMenu;
import com.dalthed.tucan.ui.Messages;
import com.dalthed.tucan.ui.Schedule;
import com.dalthed.tucan.ui.VV;

public class FastSwitchHelper {
	protected static final String LOG_TAG = "TuCanMobile";
	public String[] linkarray = null;
	public String cached_Session, cached_Cookie;
	public int navigationItem = 0;
	protected Boolean navigateList = false;
	protected SparseArray<Class> ActivitiesToStart = new SparseArray<Class>() {
		{
			append(0, VV.class);
			append(1, Schedule.class);
			append(2, Events.class);
			append(3, Exams.class);
			append(4, Messages.class);
		}
	};
	private Context context;
	private ActionBar acBar;
	private FastSwitchAdapter dropAdapter;

	public FastSwitchHelper(Context context, Boolean navigateList, ActionBar acBar,
			int navigationItem) {
		this.context = context;
		this.navigateList = navigateList;
		this.navigationItem = navigationItem;
		this.acBar = acBar;
		if (navigateList && createLinkArray() && context instanceof ActionBar.OnNavigationListener) {
			Context ac_context = acBar.getThemedContext();
			dropAdapter = new FastSwitchAdapter(context,context.getResources().getStringArray(R.array.mainmenu_options));
			
			acBar.setDisplayShowTitleEnabled(false);
			acBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			acBar.setListNavigationCallbacks(dropAdapter, (ActionBar.OnNavigationListener) context);
			acBar.setSelectedNavigationItem(navigationItem);
			acBar.setDisplayHomeAsUpEnabled(true);
			

		}
	}
	public void setSubtitle (CharSequence subtitle) {
		dropAdapter.setSubtitle(subtitle.toString());
		
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
				dropAdapter.setSubtitle(null);
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
		Log.i(LOG_TAG, "Er sollte nach oben");
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		homeIntent.putExtra("Cookie", cached_Cookie);
		homeIntent.putExtra("Session", cached_Session);

//		Old code is commented out and added new code below that
//		homeIntent.putExtra("URL",
//				"https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi?APPNAME=CampusNet&PRGNAME=MLSSTART&ARGUMENTS="
//						+ cached_Session + ",-N000019");
		if (!MainMenuScraper.isEnglish) {
			homeIntent.putExtra("URL",
					"https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi?APPNAME=CampusNet&PRGNAME=MLSSTART&ARGUMENTS="
							+ cached_Session + ",-N000019");
		} else {
			homeIntent.putExtra("URL",
					"https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi?APPNAME=CampusNet&PRGNAME=MLSSTART&ARGUMENTS="
							+ cached_Session + ",-N00035");
		}
		context.startActivity(homeIntent);
	}

}
