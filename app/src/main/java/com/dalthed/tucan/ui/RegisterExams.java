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

package com.dalthed.tucan.ui;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.dalthed.tucan.R;
import com.dalthed.tucan.TuCanMobileActivity;
import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.connection.AnswerObject;
import com.dalthed.tucan.connection.CookieManager;
import com.dalthed.tucan.connection.RequestObject;
import com.dalthed.tucan.connection.SimpleSecureBrowser;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.exceptions.TucanDownException;
import com.dalthed.tucan.scraper.BasicScraper;
import com.dalthed.tucan.scraper.RegisterExamsScraper;
import com.dalthed.tucan.util.ConfigurationChangeStorage;

public class RegisterExams extends SimpleWebListActivity {
    private String URLStringtoCall;
    private CookieManager localCookieManager;

    private static final String LOG_TAG = "TuCanMobile";

    int mode = 0;
    private RegisterExamsScraper scrape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exams);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.register_exams_alert_title))
                .setMessage(getString(R.string.register_exams_alert_message))
                .setPositiveButton(getString(R.string.register_exams_alert_accept),null)
                .show();

        // Webhandling Start
        String CookieHTTPString = getIntent().getExtras().getString("Cookie");
        URLStringtoCall = getIntent().getExtras().getString("URL");

        URL URLtoCall;
        if (!restoreResultBrowser()) {
            try {
                URLtoCall = new URL(URLStringtoCall);

                localCookieManager = new CookieManager();
                localCookieManager.generateManagerfromHTTPString(URLtoCall.getHost(),
                        CookieHTTPString);
                callResultBrowser = new SimpleSecureBrowser(this);
                if (TucanMobile.DEBUG) {
                    callResultBrowser.HTTPS = this.HTTPS;
                }
                RequestObject thisRequest = new RequestObject(URLStringtoCall, localCookieManager,
                        RequestObject.METHOD_GET, "");

                callResultBrowser.execute(thisRequest);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }

    }

    public void onPostExecute(AnswerObject result) {
        if (scrape == null) {
            scrape = new RegisterExamsScraper(this, result, URLStringtoCall, localCookieManager);
        } else {
            scrape.setNewAnswer(result);
        }
        try {
            switch (mode) {
                case 0:
                    setListAdapter(scrape.scrapeAdapter(mode));
                    break;

                case 1:
                    mode = scrape.getRegisterdialog();
                    break;
                case 3:
                    mode = scrape.startRegistration();
                    break;

            }

        } catch (LostSessionException e) {
            Intent BackToLoginIntent = new Intent(this, TuCanMobileActivity.class);
            BackToLoginIntent.putExtra("lostSession", true);
            startActivity(BackToLoginIntent);
        } catch (TucanDownException e) {
            TucanMobile.alertOnTucanDown(this, e.getMessage());
        }

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (scrape.examSelection.get(position) == 2 || scrape.examSelection.get(position) == 3) {
            if (scrape.examSelection.get(position) == 2)
                mode = 1;
            else if (scrape.examSelection.get(position) == 3)
                mode = 1;
            SimpleSecureBrowser callOverviewBrowser = new SimpleSecureBrowser(this);
            RequestObject callstatuschange = new RequestObject(TucanMobile.TUCAN_PROT
                    + TucanMobile.TUCAN_HOST + scrape.registerLink.get(position),
                    localCookieManager, RequestObject.METHOD_GET, "");
            callOverviewBrowser.execute(callstatuschange);
        }
    }

    @Override
    public ConfigurationChangeStorage saveConfiguration() {
        ConfigurationChangeStorage cStore = new ConfigurationChangeStorage();
        cStore.adapters.add(getListAdapter());
        cStore.addScraper(scrape);
        return cStore;
    }

    @Override
    public void retainConfiguration(ConfigurationChangeStorage conf) {
        setListAdapter(conf.adapters.get(0));
        BasicScraper retainedScraper = conf.getScraper(0, this);
        if (retainedScraper instanceof RegisterExamsScraper) {
            scrape = (RegisterExamsScraper) retainedScraper;
        }
    }

}
