package com.dalthed.tucan.preferences;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TuCanMobileActivity;
import com.dalthed.tucan.TucanMobile;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

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
