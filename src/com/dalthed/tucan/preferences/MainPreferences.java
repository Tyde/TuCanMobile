package com.dalthed.tucan.preferences;

import com.dalthed.tucan.R;

import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MainPreferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.addPreferencesFromResource(R.xml.main_pref);

	}

	public static final SharedPreferences getSettings(final ContextWrapper ctx) {
		return ctx.getSharedPreferences(ctx.getPackageName() + "_preferences",
				MODE_PRIVATE);

	}

}
