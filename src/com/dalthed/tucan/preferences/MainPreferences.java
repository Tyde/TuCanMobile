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

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TuCanMobileActivity;

public class MainPreferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.addPreferencesFromResource(R.xml.main_pref);
		
		Preference logoutPreference = (Preference) findPreference("logout");
		logoutPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				final SharedPreferences altPrefs = getSharedPreferences("LOGIN", MODE_PRIVATE);
				SharedPreferences.Editor editor = altPrefs.edit();
				editor.putString("tuid", "");
				editor.putString("pw", "");
				editor.putString("Cookie", "");
				editor.putString("Session", "");
				editor.commit();
				Intent backtostartIntent = new Intent(MainPreferences.this,TuCanMobileActivity.class);
				backtostartIntent.putExtra("loggedout", true);
				startActivity(backtostartIntent);
				return true;
			}
		});

	}

	public static final SharedPreferences getSettings(final ContextWrapper ctx) {
		return ctx.getSharedPreferences(ctx.getPackageName() + "_preferences",
				MODE_PRIVATE);

	}

}
