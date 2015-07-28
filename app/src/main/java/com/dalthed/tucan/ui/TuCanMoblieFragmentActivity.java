package com.dalthed.tucan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.acraload.LoadAcraResults;
import com.dalthed.tucan.helpers.FastSwitchHelper;
import com.dalthed.tucan.preferences.MainPreferences;

/**
 * Created by yttyd_000 on 27.07.2015.
 */
public class TuCanMoblieFragmentActivity extends AppCompatActivity implements AdapterViewCompat.OnItemClickListener {
    private static final int DEBUG_MENU_ID = 4855569;
    private ActionBar acBar;
    private FastSwitchHelper fsh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        fsh = new FastSwitchHelper(this,toolbar);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (TucanMobile.DEBUG) {
            menu.add(Menu.NONE, DEBUG_MENU_ID, Menu.NONE, "Debug");
        }
        getMenuInflater().inflate(R.menu.loginmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.loginmenu_opt_setpreferences:
                //Intent settingsACTIVITY = new Intent(getBaseContext(), MainPreferences.class);
                //startActivity(settingsACTIVITY);
                //TODO: Preferences Fragment
                return true;
            case R.id.loginmenu_opt_changelog:
                ChangeLog cl = new ChangeLog(this);
                cl.getFullLogDialog().show();
                return true;
            case R.id.loginmenu_opt_close:
                finish();
                return true;
            case android.R.id.home:
                //fsh.startHomeIntent();
                //TODO: Go to Home Fragment
                return true;
            case DEBUG_MENU_ID:
                //Intent acraInten = new Intent(getBaseContext(), LoadAcraResults.class);
                //startActivity(acraInten);
                //TODO: Start Debug Menu
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterViewCompat<?> parent, View view, int position, long id) {
        //Navigation Item has been clicked

    }
}
