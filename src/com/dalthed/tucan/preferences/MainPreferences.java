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

package com.dalthed.tucan.preferences;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.view.animation.DecelerateInterpolator;
import android.widget.SeekBar;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TuCanMobileActivity;
import com.dalthed.tucan.helpers.AuthenticationManager;
import com.dalthed.tucan.ui.ChangeLog;
import com.dalthed.tucan.widget.WidgetProvider;

public class MainPreferences extends PreferenceActivity {
	
	private final MainPreferences mainPrefs = this; //workaround

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.addPreferencesFromResource(R.xml.main_pref);
		
		Preference logoutPreference = (Preference) findPreference("logout");
		logoutPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				logout();
				Intent backtostartIntent = new Intent(MainPreferences.this,TuCanMobileActivity.class);
				backtostartIntent.putExtra("loggedout", true);
				startActivity(backtostartIntent);
				return true;
			}
		});
//		if(!isLoggedIn()){ // if user not logged in hide logout...
//			PreferenceCategory category = (PreferenceCategory) findPreference("pref_category_misc");
//			category.removePreference(logoutPreference);
//		}
		
		
		Preference changelogPreference = (Preference) findPreference("changelog");
		
		changelogPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				ChangeLog cl = new ChangeLog(mainPrefs);
			    cl.getFullLogDialog().show();
				return true;
			}
		});
		
		
		
		final Preference widgetTransparency = findPreference("widget_transparency");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			//set transparency text
			int t = Math
					.round((getWidgetTransparency() / 255f) * 1000) / 10;
			widgetTransparency
					.setSummary(mainPrefs
							.getString(
									R.string.settings_transparency_summary,
									t));			
			widgetTransparency
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						@Override
						public boolean onPreferenceClick(Preference preference) {
							// add a slider...
							final SeekBar sb = new SeekBar(mainPrefs);
							sb.setMax(255);
							sb.setInterpolator(new DecelerateInterpolator());
							sb.setProgress((int) getWidgetTransparency());
							sb.setPadding(20, 30, 20, 30);
							new AlertDialog.Builder(mainPrefs)
									.setTitle(R.string.settings_transparency)
									.setView(sb)
									.setPositiveButton(android.R.string.ok,
											new OnClickListener() {

												@Override
												public void onClick(DialogInterface dialog, int which) {
													setWidgetTransparency(sb.getProgress());
													int t = Math
															.round((sb.getProgress() / 255f) * 1000) / 10;
													widgetTransparency
															.setSummary(mainPrefs
																	.getString(
																			R.string.settings_transparency_summary,
																			t));
												}
											})
									.setNegativeButton(android.R.string.cancel,
											null).show();
							return true;
						}
					});

		} else {
			// remove transparency from settings
			PreferenceCategory category = (PreferenceCategory) findPreference("pref_category_widget");
			category.removePreference(widgetTransparency);
		}

	}

	public static final SharedPreferences getSettings(final ContextWrapper ctx) {
		return ctx.getSharedPreferences(ctx.getPackageName() + "_preferences",
				MODE_PRIVATE);

	}
	
	private void logout(){
		AuthenticationManager.getInstance().deleteAccount();
	}
	
	private void setWidgetTransparency(int transparency){
		final SharedPreferences altPrefs = getSharedPreferences("WIDGET", MODE_PRIVATE);
		SharedPreferences.Editor editor = altPrefs.edit();
		editor.putInt("transparency", transparency);
		editor.commit();
		MainPreferences.forceWidgetRedraw(this);
	}
	
	private int getWidgetTransparency(){
		final SharedPreferences altPrefs = getSharedPreferences("WIDGET", MODE_PRIVATE);
		return altPrefs.getInt("transparency", 64);
	}
	
	/**
	 * triggers onUpdate
	 * @param pkg
	 */
	public static void forceWidgetRedraw(Context pkg){
		Intent intent = new Intent();
		intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
		
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(pkg);
		ComponentName thisWidget = new ComponentName(pkg,
				WidgetProvider.class);
		
		int[] ids = appWidgetManager.getAppWidgetIds(thisWidget);
		
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		pkg.sendBroadcast(intent);
	}

}
