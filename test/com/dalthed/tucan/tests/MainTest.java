package com.dalthed.tucan.tests;

import java.io.IOException;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertTrue;

import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.Connection.AnswerObject;
import com.dalthed.tucan.Connection.CookieManager;
import com.dalthed.tucan.exceptions.LostSessionException;
import com.dalthed.tucan.scraper.RegisterExamsScraper;
import com.dalthed.tucan.ui.RegisterExams;
import com.xtremelabs.robolectric.RobolectricTestRunner;

public class MainTest extends BasicTest {
	

	

	
	@Test
	public void testRegisterExamsScraper() throws IOException, LostSessionException {

		String URLStringtoCall = dtef.getErrorURL(896, "RegisterExams");
		RegisterExams fakeExams = new RegisterExams();
		String html = Jsoup.connect(URLStringtoCall)
				.cookie("canView", "16ede40c878aee38d0882b3a6b2642c0ae76dafb").get().html();
		AnswerObject result = new AnswerObject(html, "", cm, URLStringtoCall);
		RegisterExamsScraper examScraper = new RegisterExamsScraper(fakeExams, result,
				URLStringtoCall, cm);
		// "Welche methode soll ausgeführt werden?"
		// "0: scrapeAnswer(0)"
		// "1: startRegistration()"
		// "2: getRegisterDialog()"
		int methodid = 1;

		switch (methodid) {
		case 0:
			examScraper.scrapeAdapter(0);
			break;
		case 1:
			examScraper.startRegistration();
			break;
		case 2:
			examScraper.getRegisterdialog();
			break;
		default:
			break;
		}

	}

}
