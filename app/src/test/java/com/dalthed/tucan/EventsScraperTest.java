package com.dalthed.tucan;

import android.widget.SpinnerAdapter;

import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.scraper.EventsScraper;

import org.jsoup.Jsoup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Map;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class EventsScraperTest extends TestBase {

    public EventsScraperTest() {
        this.resourcesBaseName = "Events";
    }

    @Test
    public void testSpinner() {

        for (Map.Entry<String, String> entry : sourcesMap.entrySet()) {
            String id = entry.getKey();
            AnswerObject answer = new AnswerObject(entry.getValue(),"",new CookieManager(),"");
            EventsScraper scraper = new EventsScraper(RuntimeEnvironment.application,answer);
            SpinnerAdapter spinnerAdapter = scraper.spinnerAdapter();
            System.out.println(scraper.SemesterOptionName);
        }
    }
}
