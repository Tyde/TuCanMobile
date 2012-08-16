package com.dalthed.tucan.tests;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.junit.Test;

import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.helpers.FastSwitchHelper;
import com.dalthed.tucan.scraper.SingleEventScraper;
import com.dalthed.tucan.ui.FragmentSingleEvent;

public class SingleEventTest extends BasicTest {

	@Test
	public void singleEventTest() throws IOException, LostSessionException {
		String URLStringtoCall = dtef.getErrorURL(1188, "FragmentSingleEvent");
		FragmentSingleEvent fakeSingleEvent = new FragmentSingleEvent();
		
		String html = Jsoup.connect(URLStringtoCall)
				.cookie("canView", "16ede40c878aee38d0882b3a6b2642c0ae76dafb").get().html();
		AnswerObject result = new AnswerObject(html, "", cm, URLStringtoCall);
		SingleEventScraper scraper = new SingleEventScraper(fakeSingleEvent, result, false, null, null, null);
		scraper.scrapeAdapter(0);
		
	}
}
